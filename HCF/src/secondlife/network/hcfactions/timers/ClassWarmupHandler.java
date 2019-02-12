package secondlife.network.hcfactions.timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.classes.utils.ArmorClass;
import secondlife.network.hcfactions.classes.utils.ArmorClassHandler;
import secondlife.network.hcfactions.utilties.Handler;

import java.util.*;

public class ClassWarmupHandler extends Handler implements Listener {

	public static Map<UUID, ArmorClass> classWarmups = new HashMap<UUID, ArmorClass>();
	public static HashMap<UUID, Long> cooldown;
	public static ArrayList<UUID> preWarmups = new ArrayList<>();

	public ClassWarmupHandler(HCF plugin) {
		super(plugin);

		cooldown = new HashMap<UUID, Long>();

		new BukkitRunnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					attemptEquip(player);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 20L, 20L);

		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

	public static void disable() {
		if(!classWarmups.isEmpty()) {
			classWarmups.clear();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.attemptEquip(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(!cooldown.containsKey(event.getPlayer().getUniqueId())) return;

		cooldown.remove(event.getPlayer().getUniqueId());
	}

	/*@EventHandler
	public void onEquipmentSet(EquipmentSetEvent event) {
		HumanEntity humanEntity = event.getHumanEntity();
	
		if(!(humanEntity instanceof Player)) return;
		
		this.attemptEquip((Player) humanEntity);
	}*/

	private void attemptEquip(Player player) {
		ArmorClass current = ArmorClassHandler.getEquippedClass(player);
		
		if(current != null) {
			if(current.isApplicableFor(player)) return;

			ArmorClassHandler.setEquippedClass(player, null);
			preWarmups.remove(player.getUniqueId());
		} else if((current = classWarmups.get(player.getUniqueId())) != null) {
			if(current.isApplicableFor(player)) return;

			clearCooldown(player);
			preWarmups.remove(player.getUniqueId());
		}

		Collection<ArmorClass> armorClasses = ArmorClassHandler.getClasses();
		for(ArmorClass armorClass : armorClasses) {
			if(armorClass.isApplicableFor(player)) {
				classWarmups.put(player.getUniqueId(), armorClass);
				preWarmups.add(player.getUniqueId());

				applyCooldown(player, armorClass.warmupDelay);

				new BukkitRunnable() {
					public void run() {
						ArmorClass pvpClass = classWarmups.remove(player.getUniqueId());

						if(preWarmups.contains(player.getUniqueId())) {
							ArmorClassHandler.setEquippedClass(player, pvpClass);

							preWarmups.remove(player.getUniqueId());
						}
					}
				}.runTaskLater(this.getInstance(), armorClass.warmupDelay * 20);
				break;
			}
		}
	}

	public static boolean isActive(Player player) {
		return cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId());
	}

	public static void applyCooldown(Player player, int seconds) {
		cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000));
	}

	public static void clearCooldown(Player player) {
		cooldown.remove(player.getUniqueId());
	}

	public static long getMillisecondsLeft(Player player) {
		if(cooldown.containsKey(player.getUniqueId())) {
			return Math.max(cooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}
}
