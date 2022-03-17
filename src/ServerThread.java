import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerThread implements Runnable{

    public static ArrayList<ServerThread> serverThreads = new ArrayList<>();
    public static CopyOnWriteArrayList<String> usernames = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<String> history = new CopyOnWriteArrayList<String>();
    public static ArrayList<String> bannedWords = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String clientUsername;

    public ServerThread(Socket socket) {
        try {
            bannedWords.add("covid");
            bannedWords.add("war");
            bannedWords.add("sex");
            this.socket = socket;
            this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            boolean exists = checkUsernames(clientUsername);
            if(exists == false){
                serverThreads.add(this);
                usernames.add(clientUsername);
                sendMessageToAllUsers(clientUsername + " has connected");
                printWriter.println("Welcome " + clientUsername);
                for(String s : history){
                    printWriter.println(s);
                }
                //history.add(clientUsername + " has connected");

            }
        } catch (IOException e){
            closeConnection(socket, bufferedReader, printWriter);
        }
    }

    public boolean checkUsernames(String username){
        for(String s : usernames){
            if(s.equalsIgnoreCase(username)){
                this.printWriter.println("Username already exists");
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        String messageFromClient;

        while(true){
            try{
                messageFromClient = bufferedReader.readLine();
                String split[] = messageFromClient.split(" ");
                String newString = "";
                for(String s: split){
                    for(String s1: bannedWords){
                        if(s.equalsIgnoreCase(s1)){
                            s = '*'+s.substring(1,s.length()-1)+'*';
                        }else{

                        }
                    }
                     newString += s + " ";
                }
                history.add(newString);
                sendMessageToAllUsers(newString);
            }catch (IOException e){
                closeConnection(socket, bufferedReader, printWriter);
                break;
            }
        }
    }
    public void sendMessageToAllUsers(String message){
        for(ServerThread serverThread : serverThreads){
            try{
                if (!serverThread.clientUsername.equals(clientUsername)){
                serverThread.printWriter.println(message);
                }else{
                    //serverThread.printWriter.println("Welcome");
                }
            }catch (Exception e){
                closeConnection(socket, bufferedReader, printWriter);
            }
        }
    }
    public void removeClient(){
        serverThreads.remove(this);
        sendMessageToAllUsers(clientUsername + " has disconnected");
    }

    public void closeConnection(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter){
        removeClient();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(printWriter != null) {
                printWriter.close();
            }if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
