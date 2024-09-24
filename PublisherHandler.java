import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PublisherHandler implements Runnable {
    
    DataServer dataStructure;
    Socket socket;
    ArrayList<Messagges> clientMessages = new ArrayList<Messagges>();
    String topic;

    public PublisherHandler(Socket s,DataServer c, String topic) {
        this.dataStructure=c;
        this.socket=s;
        this.topic = topic;
    }

    public void run() {
        try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                this.dataStructure.acquire_write_Lock();
                if(!dataStructure.getChats().containsKey(topic))
                    dataStructure.addTopic(topic);
                this.dataStructure.release_write_Lock();
                
                while (true) {
                    String parola = in.readLine(); 
                    if (parola == null) {
                        System.out.println("Connessione chiusa dal client");
                        break;
                    }
                    String[] parole = parola.split(" "); 
                    


                    switch(parole[0]) {
                        case "send": 
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                            String formattedDateTime = now.format(formatter);
                            this.dataStructure.acquire_write_Lock(); //acquisiamo il lock in scrittura
                            //String varifica=in.readLine();
                            dataStructure.addMessage(parola.substring(5), this.topic, formattedDateTime);
                            clientMessages.add(new Messagges(dataStructure.getContatoreID(), parola.substring(5), formattedDateTime));
                            this.dataStructure.release_write_Lock(); //rilasciamo il lock in scrittura
                            break;

                        case "list":
                            if(this.clientMessages.size()==0){
                                pw.println("Non hai ancora pubblicato nessun messaggio in questo topic");
                                pw.flush();
                            }
                            else{
                                String messaggiIntero1 = "";
                                for(Messagges m : this.clientMessages) {
                                    messaggiIntero1 += m.toString();
                                }
                                pw.println(messaggiIntero1); 
                                pw.flush();
                            }
                            break;
                            
                        case "listall":
                            this.dataStructure.acquire_read_Lock();
                            if(this.dataStructure.chats.get(this.topic).size()==0){
                                pw.println("Nessun messaggio presente nel topic");
                                pw.flush();
                            }    
                            else{
                                String messaggiIntero2 = "";
                                for(Messagges m : this.dataStructure.chats.get(this.topic)) {
                                    messaggiIntero2 += m.toString() + "\n";
                                }
                                pw.println(messaggiIntero2); 
                                pw.flush();
                            }
                            this.dataStructure.release_read_Lock();
                            break;
                            
                        case "quit":
                            pw.println("quit");
                            pw.flush();
                            return;
                        
                        default:
                            pw.println("ERRORE: hai usato un comando non disponibile ");
                            pw.flush();                
                    }   
             
                }
            }
    
        catch(IOException e) {
            System.err.println("PublishersHandler: IOException caught: " + e);
            e.printStackTrace();
        }catch(InterruptedException e){
            System.err.println("Nicola fa schifo: " + e);
            e.printStackTrace();
        }
    }

}



