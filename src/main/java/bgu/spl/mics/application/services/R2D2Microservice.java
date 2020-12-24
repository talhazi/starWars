package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class, callBackDeactivationEvent -> {
            BombDestroyerEvent eventToPush = new BombDestroyerEvent();
            try {
                Thread.sleep(duration);
                diary.SetR2D2Deactivate();
                sendEvent(eventToPush);
            }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

        });
        subscribeBroadcast(TerminateBroadCast.class,callBackTerminateBroadCast->{
            terminate();
            diary.SetR2D2Terminate();
        });
    }
}
