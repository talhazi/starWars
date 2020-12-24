package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadCast;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(BombDestroyerEvent.class, callBackBombDestroyerEvent -> {
            TerminateBroadCast terminateBroadCast = new TerminateBroadCast();
            try {
                Thread.sleep(duration);
                sendBroadcast(terminateBroadCast);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        });

        subscribeBroadcast(TerminateBroadCast.class,callBackTerminateBroadCast->{
            terminate();
            diary.SetLandoTerminate();
        });
    }
}
