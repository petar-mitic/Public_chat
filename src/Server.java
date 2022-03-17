import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (true) {
                System.out.println("Server ocekuje konekcije");
                Socket socket = serverSocket.accept();
                System.out.println("Server primio konekciju");
                Thread serverThread = new Thread(new ServerThread(socket));
                serverThread.start();
            }
        }catch (IOException e){
            try {
                if(serverSocket != null){
                    serverSocket.close();
                }
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
