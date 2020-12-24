package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeactivationEvent implements Event<Integer> {
    private int durationToR2D2 =0;

    public DeactivationEvent() {
    }


    public int getTime() {
        return durationToR2D2;
    }
}

