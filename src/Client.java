import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.username = username;
        }catch (IOException e){
            closeConnection(socket, bufferedReader, printWriter);
        }
    }

    public void sendMessage(){
        try{
            printWriter.println(username);

            Scanner scanner = new Scanner(System.in);
            while(true){
                String message = scanner.nextLine();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                printWriter.println(dtf.format(now)+ " - " +username + ": " + message);
            }
        }catch (Exception e){
            closeConnection(socket, bufferedReader, printWriter);
        }
    }

    public void listenMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;
                while(true){
                    try{
                        messageFromChat = bufferedReader.readLine();
                        System.out.println(messageFromChat);
                    }catch (IOException e){
                        closeConnection(socket, bufferedReader, printWriter);
                    }
                }
            }
        }).start();
    }

    public void closeConnection(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(printWriter != null) {
                printWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username");
        String username = scanner.nextLine();
        Socket socket = null;
        try {
            socket = new Socket("localhost", 1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Client client = new Client(socket, username);
        client.listenMessage();
        client.sendMessage();
    }
}
