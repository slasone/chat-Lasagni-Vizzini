package progetto5_chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/* 
 * Thread di recezione messaggi da parte del server. Instanza il socket
 */
public class ClientInputThread extends Thread{

    private Socket socket;
    private Client clientOutput;
    private BufferedReader reader;

        public ClientInputThread(Socket socket, Client client) throws IOException {
            this.socket = socket;
            this.clientOutput = client;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void run() {
            try {
                while(true){
                    String ricevuto = receive();
                    System.out.println(ricevuto);
                    if(ricevuto.equals("BYE!")) break;
                }

            reader.close();
            
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }

        public String receive () throws IOException{
            String message = reader.readLine();
            return message;
        }
}
