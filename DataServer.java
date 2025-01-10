import java.util.ArrayList;
import java.util.HashMap;

public class DataServer {

    HashMap<String, ArrayList<Message>> chats; // importante per tenere traccia di topic e messaggi inviati ad ogniuno
                                               // di essi
    HashMap<String, ArrayList<SubscriberHandler>> subs;
    HashMap<String, Lock> topicCondition;
    int contatoreID;
    boolean db_writing;
    int num_sub;

    private class Lock {

        boolean topicWriting;
        int numSub;

        public Lock(boolean topicWriting, int num_sub) {
            this.topicWriting = topicWriting;
            this.numSub = num_sub;
        }

        public Lock() {
            this.topicWriting = false;
            this.numSub = 0;
        }

        public void incrementNumSub() {
            numSub++;
        }

        public void decrementNumSub() {
            numSub--;
        }

    }

    public DataServer(HashMap<String, ArrayList<Message>> c) {
        this.chats = c;
        this.contatoreID = 0; // serve per garantire l'univocita' dei messaggi
        this.topicCondition = new HashMap<String, Lock>(); // permette di acquisire il lock solo su un topic specifico
        this.subs = new HashMap<String, ArrayList<SubscriberHandler>>();
    }

    public synchronized void acquire_read_Lock() {
        while (db_writing) {
            // System.out.println("sono in attesa");
            try {
                wait();

            } catch (InterruptedException e) {
            }

        }
        num_sub++;
    }

    public synchronized void release_read_Lock() {
        num_sub--;
        if (num_sub == 0)
            notify();
    }

    public synchronized void acquire_write_Lock() {
        while (db_writing || num_sub != 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        db_writing = true;
    }

    public synchronized void release_write_Lock() {
        db_writing = false;

        notifyAll();
    }

    public synchronized void acquire_read_Lock(String topic) {
        while (topicCondition.get(topic).topicWriting) {
            // System.out.println("sono in attesa");
            try {
                wait();

            } catch (InterruptedException e) {
            }
        }
        topicCondition.get(topic).incrementNumSub();
    }

    public synchronized void release_read_Lock(String topic) {
        topicCondition.get(topic).decrementNumSub();

        if (topicCondition.get(topic).numSub == 0)
            notify();
    }

    public synchronized void acquire_write_Lock(String topic) {
        while (topicCondition.get(topic).topicWriting || topicCondition.get(topic).numSub != 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        topicCondition.get(topic).topicWriting = true;
    }

    public synchronized void release_write_Lock(String topic) {
        topicCondition.get(topic).topicWriting = false;

        notifyAll();
    }

    public void addMessage(String contenuto, String topic, String dataOra) throws InterruptedException {
        contatoreID++;
        Message message = new Message(this.contatoreID, contenuto, dataOra);
        this.chats.get(topic).add(message);

    }

    public boolean deleteMessage(String topic, int idMessage) {
        boolean deleted = false;
        Message m;
        if (idMessage <= this.chats.get(topic).get(this.chats.get(topic).size() - 1).id && idMessage > 0) {
            for (int i = 0; i < this.chats.get(topic).size(); i++) {
                m = this.chats.get(topic).get(i);
                if (m.id == idMessage) {
                    this.chats.get(topic).remove(i);
                    deleted = true;
                }
            }
        }
        return deleted;
    }

    // Metodo per aggiungere un nuovo topic alla risorsa condivisa e alla struttura
    // subs
    public void addTopic(String newTopic) {
        this.chats.put(newTopic, new ArrayList<Message>());
        this.topicCondition.put(newTopic, new Lock());
        this.subs.put(newTopic, new ArrayList<SubscriberHandler>());
    }

    // Metodo sincronizzato per leggere i messaggi di un topic
    public ArrayList<Message> getChats(String topic) {
        return this.chats.getOrDefault(topic, new ArrayList<Message>());
    }

    public HashMap<String, ArrayList<Message>> getChats() {
        return chats;
    }

    public ArrayList<SubscriberHandler> getSubs(String topic) {
        return this.subs.get(topic);
    }

    public int getContatoreID() {
        return contatoreID;
    }

    public void removeSubscriber(String topic, SubscriberHandler subscriber) {
        ArrayList<SubscriberHandler> subscribers = this.subs.get(topic);
        if (subscribers != null) {
            subscribers.remove(subscriber);
        }
    }
}