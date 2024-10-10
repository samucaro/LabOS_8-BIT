public class Message {

    int id;
    String contents;
    String dataOra;

    public Message(int id, String content, String dataOra){
        this.id = id;
        this.contents = content;
        this.dataOra=dataOra;
    }
    
    public String toString(){
        return "ID: "+ this.id + "\n" +
        "Testo: " + this.contents + "\n" +
        "Data: " + dataOra+"\n";
    }
}