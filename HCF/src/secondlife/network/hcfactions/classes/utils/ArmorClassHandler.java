package secondlife.network.hcfactions.classes.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.classes.Archer;
import secondlife.network.hcfactions.classes.Bard;
import secondlife.network.hcfactions.classes.Miner;
import secondlife.network.hcfactions.classes.Rogue;
import secondlife.network.hcfactions.classes.utils.events.ArmorClassEquipEvent;
import secondlife.network.hcfactions.classes.utils.events.ArmorClassUnequipEvent;
import secondlife.network.hcfactions.utilties.Handler;

import java.util.*;

public class ArmorClassHandler extends Handler implements Listener {

	public static Map<UUID, ArmorClass> equippedClassMap = new HashMap<>();
	public static List<ArmorClass> classes = new ArrayList<>();

	public ArmorClassHandler(HCF plugin) {
		super(plugin);
		
		classes.add(new Archer());
		classes.add(new Bard());
		classes.add(new Miner());
		classes.add(new Rogue());

		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
		
		for(ArmorClass armor : classes) {
			if(armor instanceof Listener) {
				Bukkit.getPluginManager().registerEvents((Listener) armor, plugin);
			}
		}
	}

	public static void disable() {
		classes.clear();
		equippedClassMap.clear();
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		setEquippedClass(event.getEntity(), null);
	}

	public static Collection<ArmorClass> getClasses() {
		return classes;
	}

	public static ArmorClass getEquippedClass(Player player) {
		synchronized(equippedClassMap) {
			return equippedClassMap.get(player.getUniqueId());
		}
	}

	public static boolean hasClassEquipped(Player player, ArmorClass armor) {
		return getEquippedClass(player) == armor;
	}
	
	public static void setEquippedClass(Player player, ArmorClass armor) {
		if(armor == null) {
			ArmorClass equipped = equippedClassMap.remove(player.getUniqueId());
			
			if(equipped != null) {
				equipped.onUnequip(player);
				
				Bukkit.getPluginManager().callEvent(new ArmorClassUnequipEvent(player, equipped));
			}
		} else if(armor.onEquip(player) && armor != getEquippedClass(player)) {
			equippedClassMap.put(player.getUniqueId(), armor);
			
			Bukkit.getPluginManager().callEvent(new ArmorClassEquipEvent(player, armor));
		}
	}
}
