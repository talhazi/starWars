package bgu.spl.mics.application.passiveObjects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Diary {
    private AtomicInteger totalAttacks = new AtomicInteger(0);
    private long StartProgram;
    private long LeiaTerminate;
    private long HanSoloFinish;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long R2D2Terminate;
    private long LandoTerminate;
    AtomicBoolean Indicator;
    private static Diary instance = null;   // for making sure it singleton

    public Diary() {
        StartProgram = -1;
        LeiaTerminate = -1;
        HanSoloFinish = -1;
        HanSoloTerminate = -1;
        C3POTerminate = -1;
        C3POFinish = -1;
        R2D2Deactivate = -1;
        R2D2Terminate = -1;
        LandoTerminate = -1;
        Indicator=new AtomicBoolean(false);
    }

    private static class DiaryHolder {
        private static Diary instance = new Diary();
    }

    public static Diary getInitialization() // Singleton
    {
        if (instance == null)
            instance = DiaryHolder.instance;
        return instance;
    }

    public static Diary getInstance() {
        return DiaryHolder.instance;
    }

    public void SetStartProgram() {
        StartProgram = System.currentTimeMillis();
    }

    public void SetHanSoloFinish() {
        HanSoloFinish = System.currentTimeMillis() - StartProgram;
    }

    public void SetLeiaTerminate() {
        LeiaTerminate = System.currentTimeMillis()- StartProgram;
    }

    public void SetHanSoloTerminate() {
        HanSoloTerminate = System.currentTimeMillis()- StartProgram;
    }

    public void SetC3POTerminate() {
        C3POTerminate = System.currentTimeMillis()- StartProgram;
    }

    public void SetC3POFinish() {
        C3POFinish = System.currentTimeMillis()- StartProgram;
    }

    public void SetR2D2Deactivate() {
        R2D2Deactivate = System.currentTimeMillis()- StartProgram;
    }

    public void SetR2D2Terminate() {
        R2D2Terminate = System.currentTimeMillis()- StartProgram;
    }

    public void SetLandoTerminate() {
        LandoTerminate = System.currentTimeMillis()- StartProgram;
    }

    public void incrementTotalAttacks() {
        int oldVal, newVal;
        do {
            oldVal = totalAttacks.intValue();
            newVal = totalAttacks.intValue() + 1;
        }
        while (!totalAttacks.compareAndSet(oldVal, newVal));
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public AtomicInteger getNumberOfAttacks() {
        return totalAttacks;
    }

    public void resetNumberAttacks() {
        totalAttacks = new AtomicInteger(0);
    }

    public void printOutput(String filename) {
        GsonBuilder builder = new GsonBuilder();
        builder = builder.setPrettyPrinting();
        Map<String, Object> diaryToPrint = new HashMap<>();
        Gson gson = builder.create();
        diaryToPrint.put("totalAttacks", totalAttacks);
        diaryToPrint.put("HanSoloFinished", HanSoloFinish);
        diaryToPrint.put("C3POFinish", C3POFinish);
        diaryToPrint.put("R2D2Deactivate", R2D2Deactivate);
        diaryToPrint.put("LeiaTerminate", LeiaTerminate);
        diaryToPrint.put("HanSoloTerminate", HanSoloTerminate);
        diaryToPrint.put("C3POTerminate", C3POTerminate);
        diaryToPrint.put("R2D2Terminate", R2D2Terminate);
        diaryToPrint.put("LandoTerminate", LandoTerminate);

        try {
            FileWriter file = new FileWriter(filename); // Making Json in the place we get .
            file.write(gson.toJson(diaryToPrint));
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIndicator(){
        if (Indicator.compareAndSet(false,false))
            Indicator.set(true);
        else
            synchronized (Indicator)
            {
                 Indicator.notifyAll();
            }
    }
    public AtomicBoolean getIndicator(){
        return Indicator;
    }
}