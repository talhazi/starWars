package bgu.spl.mics.application.passiveObjects;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Map<String, Ewok> EwoksHash;
    private static Ewoks instance = null; // for making sure it singleton

    public Ewoks() {
        EwoksHash = new HashMap<>();
        instance = getInitialization();
    }


    public void loadEwok(Ewok[] Ewoks) {
        for (int i = 0; i < Ewoks.length; i++) {
            Ewoks[i].release();
            this.EwoksHash.put(String.valueOf(Ewoks[i].getSerialNumber()), Ewoks[i]);
        }
    }

    public void addEwok(Ewok ewok) {
        EwoksHash.put(String.valueOf(ewok.getSerialNumber()), ewok);
    }

    public static Ewoks getInstance() {
        return Ewoks.instance;
    }

    private static class EwoksInstance {
        private static Ewoks initialization = new Ewoks();
    }


    public static Ewoks getInitialization() {
        if (instance == null)
            instance = EwoksInstance.initialization;
        return instance;
    }


    // This function will make sure all the resources that done will get realeased .
    public void releaseEwoks(List<String> EwoksList) {
        int size = EwoksList.size();
        for (int i = 0; i < size; i++) {
            if (EwoksHash.containsKey(EwoksList.get(i))) {
                synchronized (EwoksHash.get(EwoksList.get(i))) {
                    EwoksHash.get(EwoksList.get(i)).release();
                }
            }
        }
    }

    public List<Integer> getTheAtk(Attack attack) {
        return attack.getSerials();
    }


    public boolean getEwok(List<String> serials) {
        serials.sort(Comparator.comparingInt(Integer::parseInt));
        int size = serials.size();

        for (int i = 0; i < size; i++) {
            if (!(EwoksHash.containsKey(serials.get(i)))) {
                releaseEwoks(serials);
                return false;
            }

            synchronized (EwoksHash.get(serials.get(i))) {
                if (!(EwoksHash.get(serials.get(i))).getStatus()) {
                    try {
                        EwoksHash.get(serials.get(i)).wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else
                    EwoksHash.get(serials.get(i)).acquire();
            }
        }
        return true;
    }

    public void notifyForThisEwoks(List<String> serials) {
        serials.sort(Comparator.comparingInt(Integer::parseInt));
        int size = serials.size();
        for (int i = 0; i < size; i++) {
            synchronized (EwoksHash.get(serials.get(i))) {
                EwoksHash.get(serials.get(i)).release();
                EwoksHash.get(serials.get(i)).notifyAll();
            }
        }
    }
}