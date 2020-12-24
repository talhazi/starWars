package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import java.util.LinkedList;
import java.util.List;

/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    long C3PO;


    public C3POMicroservice() {
        super("C3PO");
        C3PO = 0;
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, callbackAttackEvent -> {
            List<Integer> serachForResource = resources.getTheAtk((callbackAttackEvent.getAttack())); // What we need
            List<String> StringResources = new LinkedList<>(); // What we have
            for (Integer x : serachForResource) {
                String y = String.valueOf(x);
                StringResources.add(y);
            }
            boolean tryToComplete = false;
            while (!tryToComplete) {
                tryToComplete = resources.getEwok(StringResources);
                if (tryToComplete) {
                    try {
                        Thread.sleep(callbackAttackEvent.getAttack().getDuration());
                        complete(callbackAttackEvent, true);
                        diary.incrementTotalAttacks();
                        resources.notifyForThisEwoks(StringResources);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        subscribeBroadcast(TerminateBroadCast.class, callBackTerminateBroadCast -> {
            terminate();
            diary.SetC3POTerminate();
        });

        subscribeBroadcast(HanSoloFinishedBroadCast.class, callbackHanSoloBroadCast -> {
            diary.SetC3POFinish();
            sendEvent(new DeactivationEvent());
        });
        this.setConfrimInitialization();
        diary.setIndicator();
    }
}



