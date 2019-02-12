package secondlife.network.hcfactions.utilties.redis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import secondlife.network.hcfactions.HCF;
import secondlife.network.vituz.utilties.Msg;

public class UUIDUtils {
	
    private static Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private static Map<String, UUID> nameToUUID = new ConcurrentHashMap<>();
    
    public static void hook() {
        Redis.runRedisCommand(redis -> {
        	Map<String, String> cache = redis.hgetAll("uuidcache");
        	Iterator<Map.Entry<String, String>> iterator = cache.entrySet().iterator();
        	
            while(iterator.hasNext()) {
            	Map.Entry<String, String> cacheEntry = iterator.next();
            	
                UUID uuid = UUID.fromString(cacheEntry.getKey());
                String name = cacheEntry.getValue();
                
                uuidToName.put(uuid, name);
                nameToUUID.put(name.toLowerCase(), uuid);
            }
            
            return null;
        });
        
        Bukkit.getPluginManager().registerEvents(new UUIDHandler(), HCF.getInstance());
    }
    
    public static boolean ensure(UUID uuid) {
        if(String.valueOf(name(uuid)).equals("null")) {
        	Msg.logConsole("&4" + uuid + " &cdidn't have a cached name.");
            return false;
        }
        
        return true;
    }
    
    public static void update(UUID uuid, String name) {
        uuidToName.put(uuid, name);
       
        for(Map.Entry<String, UUID> entry : new HashMap<>(nameToUUID).entrySet()) {
            if(entry.getValue().equals(uuid)) {
                nameToUUID.remove(entry.getKey());
            }
        }
        
        nameToUUID.put(name.toLowerCase(), uuid);
        
        new BukkitRunnable() {
            public void run() {
                Redis.runRedisCommand(redis -> {
                    redis.hset("uuidcache", uuid.toString(), name);
					return null;
				});
            }
        }.runTaskAsynchronously(HCF.getInstance());
    }
    
    public static UUID uuid(String name) {
        return nameToUUID.get(name.toLowerCase());
    }
    
    public static String name(UUID uuid) {
        return uuidToName.get(uuid);
    }
}
