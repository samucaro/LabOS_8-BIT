import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PublisherHandler implements Runnable  {
    
    DataServer dataStructure;
    Socket socket;
    ArrayList<Message> clientMessages = new ArrayList<Message>();
    String topic;

    public PublisherHandler(Socket s,DataServer c, String topic) {
        this.dataStructure=c;
        this.socket=s;
        this.topic = topic;
    }

    public void run() {
        try {
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter clientOutput = new PrintWriter(socket.getOutputStream());

            this.dataStructure.acquire_write_Lock();
            if(!dataStructure.getChats().containsKey(topic))
                this.dataStructure.addTopic(topic);
            this.dataStructure.release_write_Lock();
                
            while (true) {
                String parola = clientInput.readLine(); 
                if (!Thread.interrupted()) {
                    if (parola == null) {
                        System.out.println("Connection closed by Client\n");
                        break;
                    }

                    String[] parole = parola.split(" ");
                    switch(parole[0]) {
                        case "send": 
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                            String formattedDateTime = now.format(formatter);
                            this.dataStructure.acquire_write_Lock(topic); //acquisiamo il lock in scrittura
                            dataStructure.addMessage(parola.substring(5), this.topic, formattedDateTime);
                            clientMessages.add(new Message(dataStructure.getContatoreID(), parola.substring(5), formattedDateTime));
                            this.dataStructure.release_write_Lock(topic); //rilasciamo il lock in scrittura
                            
                            this.dataStructure.acquire_read_Lock(topic);
                            for (SubscriberHandler sub : this.dataStructure.subs.get(this.topic)) {
                                PrintWriter subOutput = new PrintWriter(sub.getSocket().getOutputStream());
                                subOutput.println("New message in topic " + this.topic + ": " + parola.substring(5));
                                subOutput.flush();
                            }
                            this.dataStructure.release_read_Lock(topic);

                            break;

                        case "list":
                            if(this.clientMessages.size()==0){
                                clientOutput.println("No messages sent by the Client\n");
                                clientOutput.flush();
                            }
                            else {
                                String messaggiIntero = "";
                                for(Message m : this.clientMessages) {
                                    messaggiIntero += m.toString() + "\n";
                                }
                                clientOutput.println(messaggiIntero); 
                                clientOutput.flush();
                            }
                            break;
                                
                        case "listall":
                            this.dataStructure.acquire_read_Lock(topic);
                            if(this.dataStructure.getChats(this.topic).isEmpty()){
                                clientOutput.println("0 messages sent in this topic\n");
                                clientOutput.flush();
                            }    
                            else {
                                String message = "";
                                int countMessages = this.dataStructure.getChats(this.topic).size();
                                clientOutput.println(countMessages+ " messages sent in this topic\n");
                                clientOutput.flush();

                                for(Message m : this.dataStructure.chats.get(this.topic)) {
                                    message += m.toString() + "\n";
                                }
                                clientOutput.println(message); 
                                clientOutput.flush();
                            }
                            this.dataStructure.release_read_Lock(topic);
                            break;
                                
                        case "quit":
                            clientOutput.println("quit");
                            clientOutput.flush();
                            return;
                            
                        default:
                            clientOutput.println("Not valid command\n");
                            clientOutput.flush();                
                    }   
                }
                else {
                    clientOutput.println("quit");
                    clientOutput.flush();
                    break;
                }            
            }
        }
        catch(IOException e) {
            //System.err.println("PublishersHandler: IOException caught: " + e);
            //e.printStackTrace();
        }catch(InterruptedException e){
            System.err.println("Exception: " + e);
            e.printStackTrace();
        }
    }

}



