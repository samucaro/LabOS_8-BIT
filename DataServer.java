import java.util.ArrayList;
import java.util.HashMap;

public class DataServer {

    HashMap<String, ArrayList<Messagges>> chats; //importante per tenere traccia di topic e messaggi inviati ad ogniuno di essi
    int contatoreID;
    
    public DataServer(HashMap<String, ArrayList<Messagges>> c) {
        this.chats = c;
        this.contatoreID = 0;
    }
   
    public synchronized void addMessage(String contenuto, String topic, String dataOra) throws InterruptedException{
        contatoreID++;
        Messagges message = new Messagges(this.contatoreID, contenuto, dataOra);
        if(this.chats.containsKey(topic)) //esiste già un topic con questo nome
            this.chats.get(topic).add(message);
        else { 
            ArrayList<Messagges> first = new ArrayList<Messagges>();
            first.add(message);
            this.chats.put(topic, first);
        }
    }
    
    public synchronized void addTopic(String newTopic){
        this.chats.put(newTopic, new ArrayList<Messagges>());
    }
    
    // Metodo sincronizzato per leggere i messaggi di un topic
    public synchronized ArrayList<Messagges> getChats(String topic) {
        return this.chats.getOrDefault(topic, new ArrayList<Messagges>());
    }

    public HashMap<String, ArrayList<Messagges>> getChats() {
        return chats;
    }

    public int getContatoreID() {
        return contatoreID;
    }
}