
package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class KillStreakHandler extends Handler implements Listener {

	public KillStreakHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getEntity().getKiller() instanceof Player)) return;
		
		Player killed = event.getEntity();
		Player killer = killed.getKiller();

		event.setDroppedExp(0);

		HCFData data = HCFData.getByName(killer.getName());
		HCFData ddata = HCFData.getByName(killed.getName());

		data.setKillStreak(data.getKillStreak() + 1);
		ddata.setKillStreak(0);

		killer.setLevel(killer.getLevel() + 1);
		killed.setLevel(0);
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
				
		switch(event.getNewLevel()) {
		case 3:
			ItemStack apple = new ItemStack(Material.GOLDEN_APPLE, 3);
			player.getInventory().addItem(apple);
			
			player.sendMessage(Color.translate("&8[&6*&8]&c " + player.getName() + "&e got &63 Golden Apple &efrom their killstreak of &c3 &e."));
			break;
		case 6:
			ItemStack slowness = new ItemStack(Material.POTION, 1, (short) 16426);
			ItemStack poison = new ItemStack(Material.POTION, 1, (short) 16388);
			player.getInventory().addItem(slowness, poison);
			
			Msg.sendMessage("&8[&6*&8]&c " + player.getName() + "&e got &61 Splash Slowness &e& &6Poison &efrom their killstreak of &c6 &e.");
			break;
		case 10:
			ItemStack invisible = new ItemStack(Material.POTION, 1, (short) 16430);
			player.getInventory().addItem(invisible);
			
			Msg.sendMessage("&8[&6*&8]&c " + player.getName() + "&e got &61 Splash Invisibility &efrom their killstreak of &c10 &e.");
			break;
		case 12:
			ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
			player.getInventory().addItem(gapple);
			
			Msg.sendMessage("&8[&6*&8]&c " + player.getName() + "&e got &61 God Apple &efrom their killstreak of &c12 &e.");
			break;
		case 15:
			ItemStack strength = new ItemStack(Material.POTION, 1, (short) 16425);
			player.getInventory().addItem(strength);
			
			Msg.sendMessage("&8[&6*&8]&c " + player.getName() + "&e got &61 Splash Strength II &efrom their killstreak of &c15 &e.");
			break;
		case 30:
			ItemStack sharpness = new ItemStack(Material.DIAMOND_SWORD, 1);
			sharpness.addEnchantment(Enchantment.DAMAGE_ALL, 2);
			ItemMeta meta = sharpness.getItemMeta();
			meta.setDisplayName(Color.translate("&8[&cKillstreak Sword&8]"));
			sharpness.setItemMeta(meta);
			player.getInventory().addItem(sharpness);
			
			Msg.sendMessage("&8[&6*&8]&c " + player.getName() + "&e got &6Sword sharpness2 fire1 &efrom their killstreak of &c30 &e.");
			break;
		}
	}

}
