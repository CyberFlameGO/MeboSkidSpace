package secondlife.network.victions.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import secondlife.network.victions.Victions;

public class MobStackListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() != null) {
            LivingEntity entity = event.getEntity();
            if(entity.getType() != EntityType.PLAYER && entity.getType() != EntityType.VILLAGER) {
                Victions.getInstance().getMobStackManager().handleUnstackOne(entity, ChatColor.RED);
            }
        }
    }
}
