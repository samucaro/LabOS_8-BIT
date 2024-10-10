import java.util.Scanner;

public class InspectHandler implements Runnable{

    DataServer dataStructure;
    String topic;

    public InspectHandler(DataServer dataStructure, String topic) {
        this.dataStructure = dataStructure;
        this.topic = topic;
    }

    @Override
     public void run() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Interactive session started\n");
        this.dataStructure.acquire_write_Lock(topic);
        while(true) { 
                String row = scan.nextLine();
                if(row.equalsIgnoreCase("end")) {
                    //scan.close();
                    System.out.print("Interactive session ended\n");
                    break;
                }
                
                String[] comand = row.split(" ");
                //System.out.println(comand[0]);
    
                switch (comand[0]) {
                    case "listall":
                                String messaggiIntero2 = "";
                                for(Message m : this.dataStructure.getChats(this.topic)) {
                                    messaggiIntero2 += m.toString();
                                }
                                System.out.println(messaggiIntero2);
                                break;
    
                    case "delete":
                        if(comand.length == 2) {
                            try {
                                int idToDelete = Integer.parseInt(comand[1]);
                                if(this.dataStructure.deleteMessage(this.topic, idToDelete) == false)
                                    System.out.println("ID not found\n");
                                
                            }
                            catch(NumberFormatException e) {
                                System.out.println("Error Content-Type: <delete> <int>\n");
                                
                            }
                            
                        }
                        else {
                            System.out.println("Error Content-Type: <delete> <int>\n");
                        }
                        break;

                    default:
                        System.out.println("Not valid command\n");
                }
        }   
        this.dataStructure.release_write_Lock(topic);
    }
}
