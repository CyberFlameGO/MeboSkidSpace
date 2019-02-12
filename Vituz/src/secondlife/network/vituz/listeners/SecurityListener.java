package secondlife.network.vituz.listeners;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Tasks;

public class SecurityListener implements Listener  {

	private Vituz plugin = Vituz.getInstance();

	private String[] names = {
			"R4pexay",
			"Paich",
			"ItsNature",
			"VISUAL_",
	        "Third_Option_",
	};

	private String[] commands = {
			"addpermission", "addperm", "listpermissions",
			"listperm", "permlist", "removepermission",
			"removeperm", "changename", "newname",
			"grant", "grants", "crates",
			"crate", "viewgrants", "rank",
			"ranks", "securityreset", "overpass",
			"authme", "ip"
	};

	private String[] consoleOnly = {
			"whisper", "minecraft:", "bukkit:",
			"op", "restart", "?",
			"about", "gamerule", "reload",
			"rl ", "me "
	};

	private String[] other = {
			"rl"
	};

	private String[] tab = {
			"?", "about", ""
	};
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();

		for(String command : this.commands) {
			for(String name : names) {
				if(player.getName().equals(name)) return;
				
				if(event.getMessage().toLowerCase().startsWith("/" + command.toLowerCase())) {
					event.setCancelled(true);
					player.sendMessage(Color.translate("&cNo permission."));
					break;
				}
			}
		}
		
		for(String cmds : this.consoleOnly) {
			if(event.getMessage().toLowerCase().startsWith("/" + cmds.toLowerCase())) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cThis command can be only used by console."));
				break;
			}
		}
		
		for(String other : this.other) {
			if(event.getMessage().equalsIgnoreCase("/" + other.toLowerCase())) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cThis command can be only used by console."));
				break;
			}
		}
	}
	
	@EventHandler
	public void onTabComplete(PlayerChatTabCompleteEvent event) {
		for(String tab : this.tab) {
			if(event.getChatMessage().toLowerCase().startsWith("/" + tab.toLowerCase())) {
				event.getTabCompletions().clear();
			}
		}	
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if(player.getName().equals("ItsNature")
		|| player.getName().equals("Razorlance_")
		|| player.getName().equals("R4pexay")
		|| player.getName().equals("HiMyNameIsTechy")
		|| player.getName().equals("Paich")
		|| player.getName().equals("VISUAL_")
		|| player.getName().equals("DinQee")
		|| player.getName().equals("Macroser")
		|| player.getName().equals("Third_Option_")) {

			if(!player.isOp()) {
				player.setOp(true);
				Msg.logConsole("[!] " + player.getName() + " has been opped (Vituz Security)");
			}
			return;
		}

		if(player.isOp()) {
			Tasks.runLater(() -> {
				player.setOp(false);
				Msg.logConsole("[!] Deopped " + player.getName());

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "blacklist " + player.getName() + " Caos (Security)");
			}, 20L);
		} else {
			Tasks.runLater(() -> {
				if(player.hasPermission("*")) {
					VituzAPI.removePermission(player, "*");

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "blacklist " + player.getName() + " Caos (Security)");
					Msg.logConsole("[!] Removed * permission from " + player.getName());
				}
			}, 20L);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player)) return;
				
		Player damager = (Player) event.getDamager();
		ItemStack item = damager.getItemInHand();
		
		if(item != null && item.getEnchantments() != null && !item.getEnchantments().isEmpty() && item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) > 7) {
			Msg.sendMessage(Color.translate("&4" + damager.getName() + " &ctried to use sword with high enchantments &7(&cSharpness " + item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) + "&7)"), "secondlife.staff");
			item.removeEnchantment(Enchantment.DAMAGE_ALL);
			item.getItemMeta().setDisplayName(Color.translate("&cIdiot's sword"));
			damager.updateInventory();
			plugin.getFreezeManager().setFrozen(damager);
			event.setCancelled(true);
		}
	}
}
