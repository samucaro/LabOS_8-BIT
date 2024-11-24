import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    Socket s;
    DataServer dataStructure;
    Thread handlerThread;

    public ClientHandler(Socket s, DataServer ds) {
        this.s = s;
        this.dataStructure=ds;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));//Qui arriva l'input da tastiera del client
            PrintWriter output = new PrintWriter(s.getOutputStream(), true);
            while(true){
                String type = in.readLine();//Qui il clientHandler preleva il messaggio del client
                String parts[] = type.split(" ");
                if (!Thread.interrupted()) {
                if(parts.length == 2) {
                    /* crea un nuovo thread per lo specifico socket */
                    if(parts[0].equalsIgnoreCase("publish")) { 
                        handlerThread = new Thread(new PublisherHandler(s, dataStructure, parts[1]));
                        handlerThread.start();
                        
                        try{
                            handlerThread.join();
                        }
                        catch(InterruptedException e) {

                        }
                        break;
                    }
                    else if(parts[0].equalsIgnoreCase("Subscribe")) { 
                        this.dataStructure.acquire_read_Lock();
                        if(this.dataStructure.getChats().keySet().contains(parts[1])){ 
                            this.dataStructure.release_read_Lock();  
                            handlerThread = new Thread(new SubscriberHandler(s, dataStructure, parts[1]));
                            handlerThread.start();
                            try{
                                handlerThread.join();
                            }
                            catch(InterruptedException e) {
    
                            }
                            break;    
                        }else{
                            this.dataStructure.release_read_Lock(); 
                            output.println("Topic doesn't exist");
                            output.flush();
                        }     
                              
                    }                           
                    else {
                        output.println("Wrong command, try again : ");
                    }
                } 
                else if(parts[0].equalsIgnoreCase("Show")) {
                    String topic = "Topics :";
                    this.dataStructure.acquire_read_Lock();
                    if(!this.dataStructure.getChats().keySet().isEmpty()){
                        for (String string : this.dataStructure.getChats().keySet()) {
                            topic = topic + "\n     - " + string;
                        }
                    }
                    this.dataStructure.release_read_Lock();
                    output.println(topic);
                    output.flush();  
                }
                else if(parts[0].equalsIgnoreCase("quit")) {
                    output.println("quit");
                    output.flush();
                    break;
                }
                else {
                    output.println("Wrong command, try again : ");                           
                }
            }
            else{
                    if(handlerThread!=null){
                        handlerThread.interrupt();
                        break;
                    }else {
                        break;
                    }
            }
                 
            }
            output.println("quit");
            output.flush();
            s.close();
            System.out.println("Client Closed");
        }
        catch (IOException e) {
            System.err.println("ClientHandler: IOException caught: " + e);
            e.printStackTrace();
        }

    }
}