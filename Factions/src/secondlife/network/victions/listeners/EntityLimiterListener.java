package secondlife.network.victions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import secondlife.network.victions.Victions;

/**
 * Created by Marko on 18.07.2018.
 */
public class EntityLimiterListener implements Listener {

    private Victions plugin = Victions.getInstance();

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(!plugin.getEntityLimiterManager().getDisabledEntities().containsKey(event.getEntity().getType())) return;
        if(!plugin.getEntityLimiterManager().getDisabledEntities().get(event.getEntity().getType())) return;

        event.setCancelled(true);
    }
}
