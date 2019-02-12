package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;
import java.util.List;

public class RepairCommand extends BaseCommand {

	public RepairCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "repair";
		this.forPlayerUseOnly = true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(VituzAPI.getServerName().equals("Factions")) {
			if(player.hasPermission("secondlife.repair")) {
				if(args.length == 0) {
					player.sendMessage(Color.translate("&cUsage: /repair <hand|all>"));
				} else {
					if(args[0].equalsIgnoreCase("hand")) {
						if(!player.hasPermission("secondlife.repair.hand")) {
							player.sendMessage(Msg.NO_PERMISSION);
							return;
						}

						ItemStack item = player.getItemInHand();
						Material material = Material.getMaterial(item.getTypeId());

						if(material == null || material == Material.AIR || material == Material.POTION || material == Material.GOLDEN_APPLE || material.isBlock() || material.getMaxDurability() < 1) {
							player.sendMessage(Color.translate("&cYou must hold item if you want to enchant items."));
							return;
						}

						if(item.getDurability() == 0) {
							player.sendMessage(Color.translate("&cItem in your hand is already repaired."));
							return;
						}

						item.setDurability((short) 0);

						player.sendMessage(Color.translate("&eYou have repaired item in your hand."));
					} else if(args[0].equalsIgnoreCase("all")) {
						if(!player.hasPermission("secondlife.repair.all")) {
							player.sendMessage(Msg.NO_PERMISSION);
							return;
						}

						List<ItemStack> items = new ArrayList<ItemStack>();

						for(ItemStack item : player.getInventory().getContents()) {
							if(item != null && item.getType() != Material.POTION && item.getType() != Material.GOLDEN_APPLE && !item.getType().isBlock() && item.getType().getMaxDurability() > 1 && item.getDurability() != 0) {
								items.add(item);
							}
						}

						for(ItemStack armor : player.getInventory().getArmorContents()) {
							if(armor != null && armor.getType() != Material.AIR) {
								items.add(armor);
							}
						}

						if(items.isEmpty()) {
							player.sendMessage(Color.translate("&cNo items to repair."));
							return;
						}

						for(ItemStack item : items) {
							item.setDurability((short) 0);
						}

						player.sendMessage(Color.translate("&eYou have repaired items in your inventory."));
					} else {
						player.sendMessage(Color.translate("&cUsage: /repair <hand|all>"));
					}
				}
			} else {
				player.sendMessage(Msg.NO_PERMISSION);
			}
		} else {
			if(player.hasPermission(Permission.ADMIN_PERMISSION)) {
				if(args.length == 0) {
					player.sendMessage(Color.translate("&cUsage: /repair <hand|all>"));
				} else {
					if(args[0].equalsIgnoreCase("hand")) {
						ItemStack item = player.getItemInHand();
						Material material = Material.getMaterial(item.getTypeId());

						if(material == null || material == Material.AIR || material == Material.POTION || material == Material.GOLDEN_APPLE || material.isBlock() || material.getMaxDurability() < 1) {
							player.sendMessage(Color.translate("&cYou must hold item if you want to enchant items."));
							return;
						}

						if(item.getDurability() == 0) {
							player.sendMessage(Color.translate("&cItem in your hand is already repaired."));
							return;
						}

						item.setDurability((short) 0);

						player.sendMessage(Color.translate("&eYou have repaired item in your hand."));
					} else if(args[0].equalsIgnoreCase("all")) {
						List<ItemStack> items = new ArrayList<ItemStack>();

						for(ItemStack item : player.getInventory().getContents()) {
							if(item != null && item.getType() != Material.POTION && item.getType() != Material.GOLDEN_APPLE && !item.getType().isBlock() && item.getType().getMaxDurability() > 1 && item.getDurability() != 0) {
								items.add(item);
							}
						}

						for(ItemStack armor : player.getInventory().getArmorContents()) {
							if(armor != null && armor.getType() != Material.AIR) {
								items.add(armor);
							}
						}

						if(items.isEmpty()) {
							player.sendMessage(Color.translate("&cNo items to repair."));
							return;
						}

						for(ItemStack item : items) {
							item.setDurability((short) 0);
						}

						player.sendMessage(Color.translate("&eYou have repaired items in your inventory."));
					} else {
						player.sendMessage(Color.translate("&cUsage: /repair <hand|all>"));
					}
				}
			}
		}
	}
}
