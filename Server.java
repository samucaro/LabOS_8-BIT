import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Server {

    
    
    public static void main(String[] args) {
        
        DataServer dataStructure = new DataServer(new HashMap<String, ArrayList<Messagges>>());
        
        if (args.length < 1) {
            System.err.println("Usage: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Scanner userInput = new Scanner(System.in);

        try {
            ServerSocket server = new ServerSocket(port);
            /*
             * deleghiamo a un altro thread la gestione di tutte le connessioni; nel thread
             * principale ascoltiamo solo l'input da tastiera dell'utente (in caso voglia
             * chiudere il programma)
             */
            Thread serverThread = new Thread(new SocketListener(server, dataStructure));
            serverThread.start(); 

            String command = "";

            /*
             * Gestione delle istruzioni che si possono chiamare dal server (show, inspect, quit)
             */
            while (!command.equals("quit")) {
                command = userInput.nextLine();
                if(command.equals("show")){
                    String topic = "Topics :";
                    dataStructure.acquire_read_Lock();
                    if(!dataStructure.getChats().keySet().isEmpty()){
                        for (String string : dataStructure.getChats().keySet()) {
                            topic = topic + "\n     - " + string;
                        }
                    }
                    dataStructure.release_read_Lock();
                    System.out.println(topic); 
                }
                else if(command.equals("inspect")){
                    command = userInput.nextLine();
                    dataStructure.acquire_read_Lock();
                        if(dataStructure.chats.keySet().contains(command)){ 
                            dataStructure.release_read_Lock();  
                            Thread inspectThread = new Thread(new InspectHandler(dataStructure, command));
                            inspectThread.start();
                            try{
                                inspectThread.join();
                            }
                            catch(InterruptedException e) {
    
                            }                            
                        }
                        else{
                            dataStructure.release_read_Lock(); 
                            System.out.println("Topic doesn't exist");
                        }
                }
                
            }
            

            try {
                serverThread.interrupt();
                /* attendi la terminazione del thread */
                serverThread.join();
            } catch (InterruptedException e) {
                /*
                 * se qualcuno interrompe questo thread nel frattempo, terminiamo
                 */
                return;
            }
            System.out.println("Main thread terminated.");
        } catch (IOException e) {
            System.err.println("IOException caught: " + e);
            e.printStackTrace();
        } finally {
            userInput.close();
        }
    }
}
