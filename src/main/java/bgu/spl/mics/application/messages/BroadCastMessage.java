package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class BroadCastMessage implements Broadcast {
    private String id;
    private int time;

    public int getTime() {
        return time;
    }
    public BroadCastMessage(int time) {
        this.time = time;
        id="TimeService";
    }


    public String getId() {
        return id;
    }
}

