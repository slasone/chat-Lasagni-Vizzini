package progetto5_chat.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Thread che gestisce la connessione con un client
 */
public class ServerClientConnectionThread extends Thread {
    
    private Socket socket;
    private ClientsHandler clientsHandler;
    private DataOutputStream outVersoClinet;
    private BufferedReader inDalClient;



    private String clientName;

    public ServerClientConnectionThread(Socket socket, ClientsHandler clientsHandler) throws IOException {
        this.socket = socket;
        this.clientsHandler = clientsHandler;
        this.outVersoClinet = new DataOutputStream(socket.getOutputStream());
        this.inDalClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientName = "";
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {
        // in attesa che il client mandi qualcosa
        try {

            login();
            inviaListaClientConnessi();
            boolean connected = true;
            while (connected) {
                // scambiare messaggi
                String msg = this.inDalClient.readLine();
                System.out.println("messaggio:" + msg);

                //devo capire che tipo di messaggio è
                String [] splitted = msg.split(" ");
                
                boolean result;

                switch(splitted[0]){   
                    //se è un messaggio da inviare a tutti
                    case "/all":
                        msg = "[" + this.clientName + "/ALL]" + msg.substring(5, msg.length());
                        result = this.clientsHandler.sendMessageToAll(this.clientName, msg);
                        if (result == false) errorNobodyConnected();
                        else confMsg();      
                        break;

                    //se è un messaggio per chiudere la connessione 
                    case "/dsn": 
                        connected = false;
                        break;

                    //se è un messaggio da inviare a uno solo
                    default: 
                        String destination = splitted[0].substring(1, splitted[0].length());
                        msg = "[" + this.clientName + "]" + msg.substring((msg.indexOf(" ")+1), msg.length());
                        result = this.clientsHandler.sendMessageToOne(this.clientName, destination, msg);
                        if (result == false) errorNoDestination();
                        else confMsg();        
                        break;
                }

            }


            disconnect();
            this.clientsHandler.quit(this.clientName);
            shutDown();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Invia messaggio al client
    public void inviaMessaggio(String msg) throws IOException {
        this.outVersoClinet.writeBytes(msg+"\n");
    }

    //Chiusura connessione
    public void shutDown () throws IOException{
        inDalClient.close();
        outVersoClinet.close();
    }

    //Lista di tutti i client connessi in quel momento
    public void inviaListaClientConnessi () throws IOException {
        String msg = "[SERVER] Lista client connessi:\n";
        inviaMessaggio(msg + this.clientsHandler.listOfConnected());
    }

    //Invia messaggio a tutti in caso di disconnessione di un client
    public void disconnect () throws IOException{
        inviaMessaggio("BYE!");
        this.clientsHandler.sendMessageToAll(this.clientName, "[SERVER] " + this.clientName + " si e' disconnesso\n");
    }

    //Messaggio conferma
    public void confMsg () throws IOException{
        inviaMessaggio("[SERVER] Sending completed\n");
    }

    //Errore nome già presente
    public void errorName () throws IOException{
        inviaMessaggio("[SERVER] ERROR: Name already used. Try again:");
    }

    //Tutte le azioni svolte nel login
    public void login () throws IOException {
        // chiedere di inviare un nome
        inviaMessaggio("[SERVER] USERNAME: ");

        //Controlla se nome già presente
        String name;
        boolean trovato;

        do{
        name = this.inDalClient.readLine();
        trovato = this.clientsHandler.checkName(name); 
        if(trovato){
            errorName();
        }
        }while(trovato);

        //Associa nome a Client
        this.clientName = name;

        System.out.println(this.clientName);
        inviaMessaggio("[SERVER] Login confirmed\n");

        //Manda messaggio a tutti di avvenuta connessione
        this.clientsHandler.sendMessageToAll(this.clientName, "[SERVER] " + this.clientName + " si e' connesso\n");

        instructions();
    }

    //Istruzioni per la comunicazione
    public void instructions () throws IOException{
        inviaMessaggio("\n-ISTRUZIONI PER LA COMUNICAZIONE-");
        inviaMessaggio("/nomeDestinatario --> Messaggio singolo");
        inviaMessaggio("/all --> Messaggio a tutti");
        inviaMessaggio("/dsn --> Chiusura connessione\n");

    }

    //ERRORE in caso di mancanza del destinatario del messaggio
    public void errorNoDestination () throws IOException{
        inviaMessaggio("[SERVER] ERROR: Destination not found!\n");
    }

    //ERRORE in caso ci sia solo il client mittente connesso
    public void errorNobodyConnected () throws IOException{
        inviaMessaggio("[SERVER] ERROR: Nobody else is connected!\n");
    }
}
