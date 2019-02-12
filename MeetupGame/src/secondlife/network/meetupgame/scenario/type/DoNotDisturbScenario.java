package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Marko on 03.06.2018.
 */
public class DoNotDisturbScenario extends Scenario {

    private Map<UUID, UUID> hashMap = new HashMap();

    public DoNotDisturbScenario() {
        super("Do Not Disturb", Material.BED, "Once you hit a player your fight can't be interfered with", "Your tag lasts 30 seconds whenever you hit the player.");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if(hashMap.containsKey(damager.getUniqueId())) {
            if(!player.getUniqueId().equals(hashMap.get(damager.getUniqueId()))) {
                event.setCancelled(true);
                damager.sendMessage(Color.translate("&cYou can't hit that player, he isn't linked to you!"));
                return;
            }
        } else {
            hashMap.put(damager.getUniqueId(), player.getUniqueId());
            hashMap.put(player.getUniqueId(), damager.getUniqueId());

            new BukkitRunnable() {
                public void run() {
                    hashMap.remove(player.getUniqueId());
                    hashMap.remove(damager.getUniqueId());

                    if(player != null) {
                        player.sendMessage(Color.translate("&8[&6&lScenarios&8] &eYour &fDo Not Disturb&e status has been removed!"));
                    }

                    if(damager != null) {
                        damager.sendMessage(Color.translate("&8[&6&lScenarios&8] &eYour &fDo Not Disturb&e status has been removed!"));
                    }
                }
            }.runTaskLater(MeetupGame.getInstance(), 25 * 20L);
        }
    }
}
