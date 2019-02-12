package secondlife.network.hcfactions.timers;

import com.google.common.base.Optional;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.utils.events.FactionPlayerClaimEnterEvent;
import secondlife.network.hcfactions.factions.utils.events.FactionPlayerJoinEvent;
import secondlife.network.hcfactions.factions.utils.events.FactionPlayerLeaveEvent;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.UUID;

public class SpawnTagHandler extends Handler implements Listener {

	public static HashMap<UUID, Long> cooldown;

	public SpawnTagHandler(HCF plugin) {
		super(plugin);

		cooldown = new HashMap<UUID, Long>();

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onFactionJoin(FactionPlayerJoinEvent event) {
		Optional<Player> optional = event.getPlayer();
		
		if(optional.isPresent()) {
			Player player = optional.get();
									
			if(isActive(player)) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cYou can't join factions while your &c&lSpawn Tag&c timer is active."));
			}
		}
	}

	@EventHandler
	public void onFactionLeave(FactionPlayerLeaveEvent event) {
		if(event.isForce()) return;

		Optional<Player> optional = event.getPlayer();
		if(optional.isPresent()) {
			Player player = optional.get();
									
			if(isActive(player)) {
				event.setCancelled(true);

				CommandSender sender = event.getSender();
				
				if(sender == player) {
					sender.sendMessage(Color.translate("&cYou can't kick &l" + player.getName() + " &cas their &c&lSpawn Tag &ctimer is active."));
				} else {
					sender.sendMessage(Color.translate("&cYou can't leave factions whilst your &c&lSpawn Tag&c timer is active"));
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerClaimEnterMonitor(FactionPlayerClaimEnterEvent event) {
		Player player = event.getPlayer();
				
		if(isActive(player)) {
			Faction toFaction = event.getToFaction();
			Faction fromFaction = event.getFromFaction();

			if(!fromFaction.isSafezone() && toFaction.isSafezone()) {
				event.setCancelled(true);
			}
		}
	}

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	if(event.isCancelled()) return;
    	
        Player attacker = HCFUtils.getFinalAttacker(event, true);
       
        if(attacker != null && (event.getEntity()) instanceof Player) {
            Player attacked = (Player) event.getEntity();
            
            boolean weapon = event.getDamager() instanceof Arrow;
           
            if(!weapon) {
                ItemStack stack = attacker.getItemInHand();
                weapon = (stack != null && EnchantmentTarget.WEAPON.includes(stack));
            }

            applyTagger(attacked);
            applyOther(attacker);
        }
    }

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		stopCooldown(player);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPreventClaimEnterMonitor(FactionPlayerClaimEnterEvent event) {		
		if((event.getEnterCause() == FactionPlayerClaimEnterEvent.EnterCause.TELEPORT) && (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone())) stopCooldown(event.getPlayer());
	}

	public static boolean isActive(Player player) {
		return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
	}

	public static void applyBard(Player player) {
		if(!cooldown.containsKey(player.getUniqueId())) {
			player.sendMessage(Color.translate("&eYou have been spawn-tagged for &c30 &eseconds!"));
		}
		
		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 30 * 1000);
	}

	public static void applyTagger(Player player) {
		if(HCFData.getByName(player.getName()).isEvent()) return;

		if (!cooldown.containsKey(player.getUniqueId())) {
			player.sendMessage(Color.translate("&eYou have been spawn-tagged for &c30 &eseconds!"));
		}
		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 30 * 1000);
	}

	public static void applyOther(Player damager) {
		if(HCFData.getByName(damager.getName()).isEvent()) return;

		if(!cooldown.containsKey(damager.getUniqueId())) {
			damager.sendMessage(Color.translate("&eYou have been spawn-tagged for &c30 &eseconds!"));
		}

		cooldown.put(damager.getUniqueId(), System.currentTimeMillis() + 30 * 1000);
	}

	public static void stopCooldown(Player player) {
		cooldown.remove(player.getUniqueId());
	}

	public static long getMillisecondsLeft(Player player) {
		if(cooldown.containsKey(player.getUniqueId())) {
			return Math.max(cooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}
}