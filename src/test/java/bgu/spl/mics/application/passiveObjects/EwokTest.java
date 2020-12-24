package bgu.spl.mics.application.passiveObjects;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    Ewok tester;

    @org.junit.jupiter.api.BeforeEach
    void setUp(int serialNumber) {
        tester=new Ewok(serialNumber,true);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void testAcquire() {
        tester.acquire();
        assertEquals(false,tester.getStatus());
    }

    @org.junit.jupiter.api.Test
    void release() {
        tester.release();
        assertEquals(true,tester.getStatus());
    }
}