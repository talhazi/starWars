package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("HanSolo");
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, callbackAttackEvent -> {
            List<Integer> serachForResource = resources.getTheAtk((callbackAttackEvent.getAttack())); // What we need
            List<String> StringResources=new LinkedList<>(); // What we have
            for (Integer x: serachForResource){
            String y=String.valueOf(x);
                StringResources.add(y);
            }
            boolean tryToComplete=false;
            while (!tryToComplete) {
                 tryToComplete=resources.getEwok(StringResources);
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

            subscribeBroadcast(LeiaFinish.class, callBackLeiaFinish ->{
                diary.SetHanSoloFinish();
                sendBroadcast(new HanSoloFinishedBroadCast());
            });

        subscribeBroadcast(TerminateBroadCast.class,callBackTerminateBroadCast->{
            terminate();
            diary.SetHanSoloTerminate();
        });
        this.setConfrimInitialization();
        diary.setIndicator();
    }
}