package progetto5_chat.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    private Socket socket;
    private ClientInputThread inputThread;

    String nomeServer = "localhost";
    int portaServer = 6789;
    Socket mySocket;
    
    BufferedReader tastiera;
    String strRicevutaDalServer;
    DataOutputStream outVersoServer;
    
    String msg;
    String tipoMsg;
    String toClient;

    public Socket connetti(){
        try {
            tastiera = new BufferedReader(new InputStreamReader(System.in));
            mySocket = new Socket(nomeServer,portaServer);

            inputThread = new ClientInputThread(mySocket, this);

            outVersoServer = new DataOutputStream(mySocket.getOutputStream());

        } catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("errore durante la connesione ");
            System.exit(1);
        }
        return mySocket;
    }

    public void comunica(){

        try {
            //INSERIRE NOME 
            System.out.println(inputThread.receive());

            msg = tastiera.readLine(); //USERNAME

            outVersoServer.writeBytes(msg + "\n");

            
            System.out.println(inputThread.receive());

            inputThread.start();


            while(true){

                //CHIEDE TIPO DI MESSAGGIO DA INVIARE
                msg = tastiera.readLine();
                
                outVersoServer.writeBytes(msg + "\n");
            
                if (msg == "/dsn") {
                    break;
                }
        
            }

            inputThread.join();
            closeConnection();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("errore durante la connesione con il server!");
            System.exit(1);
        }
    }

    public void closeConnection() throws IOException{
        outVersoServer.close();
        
        socket.close();
    }

    public static void main(String[] args){
        Client client = new Client();
        client.connetti();
        client.comunica();
    }

    
}
