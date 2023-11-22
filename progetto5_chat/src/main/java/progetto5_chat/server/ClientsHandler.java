package progetto5_chat.server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe che si preocccupa di smistare i messaggi ai client
 */
public class ClientsHandler {


    public ArrayList<ServerClientConnectionThread> allConnectedClients = new ArrayList<>();

    //AGGIUNGE UN CLIENT ALLA LISTA
    public void addClient(ServerClientConnectionThread thread) {
        this.allConnectedClients.add(thread);
    }

    //INVIA MESSAGGIO A TUTTI 
    public boolean sendMessageToAll(String fromClientName, String message) {

        if(allConnectedClients.size() == 1) return false;

        for (ServerClientConnectionThread thread : allConnectedClients) {
            if (!thread.getClientName().equals(fromClientName))
                try {
                    thread.inviaMessaggio(message + "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

        return true;
    }

    //INVIA MESSAGGIO A UNA SOLA PERSONA TRAMITE CONTROLLO NOME
    public boolean sendMessageToOne(String fromClientName, String toClientName, String message) {
        boolean trovato = false;
        for (ServerClientConnectionThread thread : allConnectedClients) {
            if (thread.getClientName().equals(toClientName))
                try {
                    trovato = true;
                    thread.inviaMessaggio(message + "\n");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return trovato;
    }

    //CHIUSURA CLIENT, CICLO PER TROVARE IL THREAD CORRISPONDENTE, CHIUDE
    public void quit(String clientName){
        for (ServerClientConnectionThread thread : allConnectedClients) {
            if (thread.getClientName().equals(clientName))
                allConnectedClients.remove(thread);
        }
    }

    public String listOfConnected (){
        String list = "";
        int i = 0;
        for (ServerClientConnectionThread thread : allConnectedClients) {
            list += "- " + thread.getClientName() + " -";
            i++;
            if(i == 3){
                list += "\n";
                i = 0;
            }
        }
        list += "\n";
        return list;
    }

    public boolean checkName (String name){
        
        System.out.println("entrato con nome: " + name);
        //Controllo se nome già presente
        for (ServerClientConnectionThread thread : allConnectedClients) {
            if(thread.getClientName().equals(name)) return true;
        }

        System.out.println("finito ciclo");
        return false;
    }
}
