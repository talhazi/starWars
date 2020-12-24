package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadCastMessage;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    MessageBusImpl messageBus;
    MicroService HanSolo;
    AttackEvent eventMessage;
    Broadcast broadcastMessage;

    @BeforeEach
    void setUp() {
        messageBus=MessageBusImpl.getInstance();
        HanSolo=new HanSoloMicroservice();
        eventMessage=new AttackEvent(null);
        broadcastMessage= new BroadCastMessage(0);
    }

    @AfterEach
    void tearDown() {// Not Need
    }

    @Test
    void subscribeEvent() { // Not Need
    }

    @Test
    void subscribeBroadcast() {// Not Need
    }

    @Test
    void complete()
        {
            messageBus.subscribeEvent(AttackEvent.class, HanSolo);
            Event exEvent = new AttackEvent(null);
            Future<String> futureTest= messageBus.sendEvent(exEvent);
            assertNull(futureTest);
            messageBus.complete(exEvent,"Check 1 2");
            assertTrue(futureTest.isDone());

        }


    @Test
    void sendBroadcast() {
        MicroService C3PO = new C3POMicroservice();
        Broadcast exBroad = new BroadCastMessage(0);

        messageBus.register(HanSolo);
        messageBus.register(C3PO);
        messageBus.subscribeBroadcast(BroadCastMessage.class, HanSolo); // checking 1
        messageBus.subscribeBroadcast(BroadCastMessage.class, C3PO);// checking 2
        messageBus.sendBroadcast(exBroad);
        try {
            assertEquals(messageBus.awaitMessage(HanSolo), exBroad);
        } catch (InterruptedException exp1) {
            exp1.printStackTrace();
        }
        try {
            assertEquals(messageBus.awaitMessage(C3PO), exBroad);
        } catch (InterruptedException exp2) {
            exp2.printStackTrace();
        }
    }

    @Test
    void sendEvent() {
        MicroService C3PO = new C3POMicroservice();
        Event eventChecking = new AttackEvent(null); // checking AttackEvent for example.
        messageBus.subscribeEvent(AttackEvent.class,HanSolo);// checking Han Solo for example.
        messageBus.subscribeEvent(AttackEvent.class, C3PO);// checking C3po for example.
        messageBus.sendEvent(eventChecking);
        try {
            assertEquals(messageBus.awaitMessage(HanSolo), eventChecking); // checking if sent first time.
        } catch (InterruptedException exp1) {
            exp1.printStackTrace();
        }
        try {
            assertNotEquals(messageBus.awaitMessage(C3PO), eventChecking); // checking if sent second time.
        } catch (InterruptedException exp2) {
            exp2.printStackTrace();
        }
    }

    @Test
    void register() { // Not Need
    }

    @Test
    void unregister() { // Not Need
    }

    @Test
    void awaitMessage() {
        Event eventChecking = new AttackEvent(null); // checking AttackEvent for example.
        messageBus.subscribeEvent(AttackEvent.class, HanSolo);
        messageBus.sendEvent(eventChecking);
        try {
            assertEquals(messageBus.awaitMessage(HanSolo), eventChecking);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}