import java.io.*;
import java.net.Socket;

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
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter clientOutput = new PrintWriter(socket.getOutputStream());
           
            while(true) {
                String input = clientInput.readLine(); 
                if (!Thread.interrupted()) {
                    if (input == null) {
                        System.out.println("Connection closed by Client\n");
                        break;
                    } 
                    else {
                        String[] strings = input.split(" "); 
                         
                        switch (strings[0]) {

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
                }
                else {
                    clientOutput.println("quit");
                    clientOutput.flush();
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
