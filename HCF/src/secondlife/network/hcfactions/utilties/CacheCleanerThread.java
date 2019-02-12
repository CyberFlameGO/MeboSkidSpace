package secondlife.network.hcfactions.utilties;

import com.google.common.cache.Cache;
import org.bukkit.plugin.Plugin;
import secondlife.network.vituz.utilties.Msg;

public class CacheCleanerThread extends Thread {
    
    private long time;
    private Plugin plugin;
    private Cache cache;
    
    public CacheCleanerThread(long time, Plugin plugin, Cache cache) {
        this.time = time;
        this.plugin = plugin;
        this.cache = cache;
    }
    
    public CacheCleanerThread(Plugin plugin, Cache cache) {
        this(5000L, plugin, cache);
    }
    
    @Override
    public void run() {
        do {
            try {
                Thread.sleep(this.time);
            } catch (InterruptedException e) {
                Msg.logConsole("&eInterrupted " + e);
                break;
            }

            this.cache.cleanUp();
        } while(this.plugin.isEnabled());
    }
}
