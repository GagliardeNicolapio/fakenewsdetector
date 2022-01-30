package Model.Components;

import java.util.ArrayList;

public class Alert {
    private ArrayList<String> messages;

    public Alert(){
        this.messages = new ArrayList<>();
    }

    public void setMessages(ArrayList<String> list){
        this.messages = list;
    }

    public void addMessage(String msg){
        this.messages.add(msg);
    }

    public ArrayList<String> getMessages() {
        return messages;
    }
}
