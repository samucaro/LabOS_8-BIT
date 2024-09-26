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
        System.out.println("sei nel thread inspect");
        this.dataStructure.acquire_write_Lock(topic);
        while(true) { 
                String row = scan.nextLine();
                if(row.equalsIgnoreCase("end")) {
                    //scan.close();
                    System.out.print("sei uscito dalla sessione iterattiva");
                    break;
                }
                
                String[] comand = row.split(" ");
                //System.out.println(comand[0]);
    
                switch (comand[0]) {
                    case "listall":
                                String messaggiIntero2 = "";
                                for(Messagges m : this.dataStructure.chats.get(this.topic)) {
                                    messaggiIntero2 += m.toString();
                                }
                                System.out.println(messaggiIntero2);
                                break;
    
                    case "delete":
                        if(comand.length == 2) {
                            try {
                                int idToDelete = Integer.parseInt(comand[1]);
                                if(this.dataStructure.deleteMessage(this.topic, idToDelete) == false)
                                    System.out.println("L' id specificato non è presente");
                                
                            }
                            catch(NumberFormatException e) {
                                System.out.println("Dopo il comando <delete> deve essere inserito un numero non una parola o un carattere");
                                
                            }
                            
                        }
                        else {
                            System.out.println("Bisogna specificare anche l' ID dopo il comando <delete>");
                        }
                        break;

                    default:
                        System.out.println("Comando inesistente");
                }
        }
            
        this.dataStructure.release_write_Lock(topic);
        System.out.println("Fine Sessione iterattiva lato server");
    }
}
