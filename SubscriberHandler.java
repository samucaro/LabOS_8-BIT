import java.io.*;
import java.net.Socket;
import java.net.*;

public class SubscriberHandler implements Runnable {
    
    Socket socket;
    DataServer dataStructure;
    String topic;

    public SubscriberHandler(Socket s, DataServer ds, String topic){
        this.socket = s;
        this.dataStructure = ds;
        this.topic = topic;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
           
            while(true) {
                String parola = in.readLine(); 
                if (!Thread.interrupted()) {
                    if (parola == null) {
                        System.out.println("Connessione chiusa dal client");
                        break;
                    } 
                    else {
                        String[] parole=parola.split(" ");  
                        switch (parole[0]) {
                            case "listall":
                                this.dataStructure.acquire_read_Lock();
                                String messaggiIntero2 = "";
                                for(Messagges m : this.dataStructure.chats.get(this.topic)) {
                                messaggiIntero2 += m.toString();
                                }
                                pw.println(messaggiIntero2); 
                                pw.flush();
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
                else {
                    pw.println("quit");
                    pw.flush();
                    break; 
                }   
            }
        }
        catch (IOException e) {
            //System.err.println("SubsrcibeHandler: IOException caught: " + e);
            //e.printStackTrace();
        }
    }
}
