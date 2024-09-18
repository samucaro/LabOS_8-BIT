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
            
            while (true) {
                String parola = in.readLine(); 
                if (parola == null) {
                    System.out.println("Connessione chiusa dal client");
                    break;
                }
                String[] parole = parola.split(" "); 

                switch(parole[0]) {
                    case "sent": 
                        
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String formattedDateTime = now.format(formatter);
                        dataStructure.addMessage(parola.substring(5), this.topic, formattedDateTime);
                        clientMessages.add(new Messagges(dataStructure.getContatoreID(), parola.substring(5), formattedDateTime));
                        System.out.println("invio sent");
                        pw.println("sent: hai selezionato il comando sent"); 
                        pw.flush();
                        break;

                    case "list":
                        String messaggiIntero1 = "";
                        for(Messagges m : this.clientMessages) {
                            messaggiIntero1 += m.toString();
                        }
                        pw.println(messaggiIntero1); 
                        pw.flush();
                        break;

                    case "listAll":
                        String messaggiIntero2 = "";
                        for(Messagges m : this.dataStructure.chats.get(this.topic)) {
                            messaggiIntero2 += m.toString();
                        }
                        pw.println(messaggiIntero2); 
                        pw.flush();
                        break;
                        
                    case "quit":
                        System.out.println("Scollegamento come publisher");
                        return;
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


