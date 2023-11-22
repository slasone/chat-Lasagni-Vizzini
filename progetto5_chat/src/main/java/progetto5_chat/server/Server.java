package progetto5_chat.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ClientsHandler clientsHandler = new ClientsHandler();

    public void startserver(){
        try{

            ServerSocket serverSocket = new ServerSocket(6789);
            System.out.println("-SERVER STARTED-");

            for(;;){
                
                Socket socket = serverSocket.accept();
                System.out.println("Client connesso: " + socket);
                ServerClientConnectionThread serverClientThread = new ServerClientConnectionThread(socket, clientsHandler);

                this.clientsHandler.addClient(serverClientThread);

                serverClientThread.start();

            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("ERRORE durante istanza del server");
        }
    }

    public static void main(String[] args){
        Server server = new Server();
        server.startserver();
    }
}
