import java.util.ArrayList;
import java.util.HashMap;

public class DataServer {

    HashMap<String, ArrayList<Messagges>> chats; //importante per tenere traccia di topic e messaggi inviati ad ogniuno di essi
    int contatoreID;
    boolean db_writing;//flag che ci dice quando possiamo scrivere
    int num_sub;
    
    public DataServer(HashMap<String, ArrayList<Messagges>> c) {
        this.chats = c;
        this.contatoreID = 0;
        this.db_writing=false;
        this.num_sub=0;
    }

    public synchronized void acquire_read_Lock(){
        while (db_writing) { 
            //System.out.println("sono in attesa");
            try {
                wait();
                
            } catch (InterruptedException e) {
            } 
        }
        num_sub++;
    }

    public synchronized void release_read_Lock(){
        num_sub--;
        if(num_sub==0)
            notify();
    }

    public synchronized void acquire_write_Lock(){
        while(db_writing || num_sub!=0){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        db_writing=true;
    }
    
    public synchronized void release_write_Lock(){
       db_writing=false;
       
       notifyAll();
    }
        
   
    public void addMessage(String contenuto, String topic, String dataOra) throws InterruptedException{
        contatoreID++;
        Messagges message = new Messagges(this.contatoreID, contenuto, dataOra);
        if(this.chats.containsKey(topic)) {//esiste già un topic con questo nome
            this.chats.get(topic).add(message);
            
        }

        else { 
            ArrayList<Messagges> first = new ArrayList<Messagges>();
            first.add(message);
            this.chats.put(topic, first);
        }
    }

    public boolean deleteMessage(String topic, int idMessage){
        boolean deleted = false;
        Messagges m;
        if(idMessage <= this.chats.get(topic).get(this.chats.get(topic).size()-1).id && idMessage > 0){
            for (int i = 0; i < this.chats.get(topic).size(); i++) {
            m = this.chats.get(topic).get(i);
            if(m.id == idMessage) {
                this.chats.get(topic).remove(i);
                deleted = true;
            }
        }
        }
            return deleted;
    
    }
    
    public void addTopic(String newTopic){
        this.chats.put(newTopic, new ArrayList<Messagges>());
    }
    
    // Metodo sincronizzato per leggere i messaggi di un topic
    public ArrayList<Messagges> getChats(String topic) {
        return this.chats.getOrDefault(topic, new ArrayList<Messagges>());
    }

    public HashMap<String, ArrayList<Messagges>> getChats() {
        return chats;
    }

    public int getContatoreID() {
        return contatoreID;
    }
    
}