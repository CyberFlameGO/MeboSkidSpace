package secondlife.network.vituz.providers.threads;

import lombok.Getter;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.nametags.NametagUpdate;
import secondlife.network.vituz.providers.nametags.VituzNametag;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagThread extends Thread {

    @Getter private static Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    public NametagThread() {
        super("Vituz - Nametags Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {
            Iterator<NametagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while(pendingUpdatesIterator.hasNext()) {
                NametagUpdate pendingUpdate = pendingUpdatesIterator.next();

                try {
                    VituzNametag.applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(VituzAPI.nametagsTime * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}