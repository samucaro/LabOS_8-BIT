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
        while(true) { 
               
                String row = scan.nextLine();
                if(row.equalsIgnoreCase("exit")) {
                    scan.close();
                    break;
                }
                
                String[] comand = row.split(" ");
                
    
                switch (comand[0]) {
                    case "linstall":
                        this.dataStructure.acquire_read_Lock();
                                String messaggiIntero2 = "";
                                for(Messagges m : this.dataStructure.chats.get(this.topic)) {
                                    messaggiIntero2 += m.toString();
                                }
                                System.out.println(messaggiIntero2);
                                this.dataStructure.release_read_Lock();
                                break;
    
                    case "delete":
                    this.dataStructure.acquire_write_Lock();
                        if(comand.length == 2) {
                            try {
                                int idToDelete = Integer.parseInt(comand[1]);
                                if(this.dataStructure.deleteMessage(this.topic, idToDelete) == false)
                                    System.out.println("L' id specificato non è presente");
                                this.dataStructure.release_write_Lock();

                            }//il metodo delete() di dataserver ora restituisce un booleano perchè non sempre può essere portata a termine
                            //ti spiego meglio domani
                            catch(NumberFormatException e) {
                                System.out.println("Dopo il comando <delete> deve essere inserito un numero non una parola o un carattere");
                            }
                            
                        }
                        else {
                            System.out.println("Bisogna specificare anche l' ID dopo il comando <delete>");
                        }
                        break;

                    case "end":
                        return;

                    default:
                        System.out.println("Comando inesistente");
                }
            }
            scan.close();
        this.dataStructure.release_write_Lock();
        System.out.println("Fine Sessione iterattiva lato server");
    }
}
