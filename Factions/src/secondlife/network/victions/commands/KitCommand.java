package secondlife.network.victions.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import secondlife.network.victions.Victions;
import secondlife.network.victions.kit.Kit;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

public class KitCommand {

	private static final String NO_KIT = Color.translate("&cThat kit doesn't exist!");
	private static Victions plugin = Victions.getInstance();

	private final static String[] HELP_MESSAGE = new String[] {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.YELLOW + "Kit Commands:",
			ChatColor.LIGHT_PURPLE + "(*) /kit create <name> " + ChatColor.GRAY + "- Create kit",
			ChatColor.LIGHT_PURPLE + "(*) /kit delete <name> " + ChatColor.GRAY + "- Delete kit",
			ChatColor.LIGHT_PURPLE + "(*) /kit enable <name> " + ChatColor.GRAY + "- Enable kit",
			ChatColor.LIGHT_PURPLE + "(*) /kit icon <name> " + ChatColor.GRAY + "- Set icon",
			ChatColor.LIGHT_PURPLE + "(*) /kit setinv <name> " + ChatColor.GRAY + "- Set inventory",
			ChatColor.LIGHT_PURPLE + "(*) /kit getinv <name> " + ChatColor.GRAY + "- Get inventory",
			ChatColor.LIGHT_PURPLE + "(*) /kit save save " + ChatColor.GRAY + "- Save kits",

			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
	};

	@Command(names = "kit")
	public static void handleUsage(Player player) {
		if(player.hasPermission(Permission.OP_PERMISSION)) {
			player.sendMessage(HELP_MESSAGE);
			player.sendMessage("");
		} else {
			player.sendMessage(Color.translate("&cUsage: /kit <name>"));
		}

		StringBuilder builder = new StringBuilder();

		plugin.getKitManager().getKits().forEach(kits -> {
			if(builder.length() > 0) {
				builder.append("&f, ");
			}

			builder.append("&d").append(kits.getName());
		});

		player.sendMessage(Color.translate("&eCurrent Kits: " + builder.toString()));
	}

	@Command(names = "kit")
	public static void handleGive(Player player, @Parameter(name = "name") String name) {
		FactionsData data = FactionsData.getByName(player.getName());
		Kit toGive = plugin.getKitManager().getKit(name);

		if(toGive == null) {
			player.sendMessage(NO_KIT);
			return;
		}

		if(data.isKitActive(player, toGive)) {
			player.sendMessage(Color.translate("&cYou can't use " + toGive.getName() + " &cfor another &l" + StringUtils.formatInt(toGive.getDelay()) + "&c!"));
			return;
		}

		toGive.applyToPlayer(player);
	}

	@Command(names = "kit create", permissionNode = "secondlife.op")
	public static void handleCreate(Player player, @Parameter(name = "name") String name) {
		Kit kit = plugin.getKitManager().getKit(name);

		if(kit == null) {
			plugin.getKitManager().createKit(name);
			player.sendMessage(ChatColor.GREEN + "Successfully created kit " + name + ".");
		} else {
			player.sendMessage(ChatColor.RED + "That kit already exists!");
		}
	}

	@Command(names = {"kit enable", "kit disable"}, permissionNode = "secondlife.op")
	public static void handleToggle(Player player, @Parameter(name = "name") String name) {
		Kit kit = plugin.getKitManager().getKit(name);

		if(kit != null) {
			kit.setEnabled(!kit.isEnabled());
			player.sendMessage(kit.isEnabled() ? ChatColor.GREEN + "Successfully enabled kit " + name + "." :
					ChatColor.RED + "Successfully disabled kit " + name + ".");
		} else {
			player.sendMessage(KitCommand.NO_KIT);
		}
	}

	@Command(names = {"kit setinv", "kit setinventory"}, permissionNode = "secondlife.op")
	public static void handleSetInventory(Player player, @Parameter(name = "name") String name) {
		Kit kit = plugin.getKitManager().getKit(name);

		if(kit != null) {
			if(player.getGameMode() == GameMode.CREATIVE) {
				player.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
			} else {
				player.updateInventory();

				kit.setContents(player.getInventory().getContents());
				kit.setArmor(player.getInventory().getArmorContents());

				player.sendMessage(ChatColor.GREEN + "Successfully set kit contents for " + name + ".");
			}
		} else {
			player.sendMessage(KitCommand.NO_KIT);
		}
	}

	@Command(names = {"kit getinv", "kit getinventory"}, permissionNode = "secondlife.op")
	public static void handleGetInventory(Player player, @Parameter(name = "name") String name) {
		Kit kit = plugin.getKitManager().getKit(name);

		if(kit != null) {
			player.getInventory().setContents(kit.getContents());
			player.getInventory().setArmorContents(kit.getArmor());
			player.updateInventory();
			player.sendMessage(ChatColor.GREEN + "Successfully retrieved kit contents from " + name + ".");
		} else {
			player.sendMessage(KitCommand.NO_KIT);
		}
	}

	@Command(names = {"kit save"}, permissionNode = "secondlife.op")
	public static void handleGetInventory(Player player) {
		plugin.getKitManager().loadKits();
		player.sendMessage(ChatColor.GREEN + "Successfully reloaded the kits.");
	}

	@Command(names = {"kit setdelay", "kit delay"}, permissionNode = "secondlife.op")
	public static void handleGetInventory(Player player, @Parameter(name = "name") String name, @Parameter(name = "delay") int seconds) {
		Kit kit = plugin.getKitManager().getKit(name);

		if(kit == null) {
			player.sendMessage(NO_KIT);
			return;
		}

		kit.setDelay(seconds);
		player.sendMessage(Color.translate("&eYou have set kit delay of &d" + kit.getName() + " &eto &d" + kit.getDelay() + "&e."));
	}

	/*@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return false;

		Player player = (Player) sender;

		FactionsData data = FactionsData.getByName(player.getName());
		Kit kit = this.plugin.getKitManager().getKit(args[1]);

		if(args.length == 0) {
			if(player.hasPermission(Permission.OP_PERMISSION)) {
				player.sendMessage(HELP_MESSAGE);
				player.sendMessage("");
			} else {
				player.sendMessage(Color.translate("&cUsage: /kit <name>"));
			}

			StringBuilder builder = new StringBuilder();

			plugin.getKitManager().getKits().forEach(kits -> {
				if(builder.length() > 0) {
					builder.append("&f, ");
				}

				builder.append("&d" + kits.getName());
			});

			player.sendMessage(Color.translate("&eCurrent Kits: " + builder.toString()));
		} else {
			if(args[0].equalsIgnoreCase("create")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return false;
				}

				if(args.length < 2) {
					sender.sendMessage(this.HELP_MESSAGE);
					return true;
				}

				if(kit == null) {
					this.plugin.getKitManager().createKit(args[1]);
					sender.sendMessage(ChatColor.GREEN + "Successfully created kit " + args[1] + ".");
				} else {
					sender.sendMessage(ChatColor.RED + "That kit already exists!");
				}
			} else if(args[0].equalsIgnoreCase("delete")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return false;
				}

				if(args.length < 2) {
					sender.sendMessage(this.HELP_MESSAGE);
					return true;
				}

				if(kit != null) {
					this.plugin.getKitManager().deleteKit(args[1]);
					sender.sendMessage(ChatColor.GREEN + "Successfully deleted kit " + args[1] + ".");
				} else {
					sender.sendMessage(KitCommand.NO_KIT);
				}
			} else if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return false;
				}

				if(args.length < 2) {
					sender.sendMessage(this.HELP_MESSAGE);
					return true;
				}

				if(kit != null) {
					kit.setEnabled(!kit.isEnabled());
					sender.sendMessage(kit.isEnabled() ? ChatColor.GREEN + "Successfully enabled kit " + args[1] + "." :
							ChatColor.RED + "Successfully disabled kit " + args[1] + ".");
				} else {
					sender.sendMessage(KitCommand.NO_KIT);
				}
			} else if(args[0].equalsIgnoreCase("setinv")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return false;
				}

				if(args.length < 2) {
					sender.sendMessage(this.HELP_MESSAGE);
					return true;
				}

				if(kit != null) {
					if(player.getGameMode() == GameMode.CREATIVE) {
						sender.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
					} else {
						player.updateInventory();

						kit.setContents(player.getInventory().getContents());
						kit.setArmor(player.getInventory().getArmorContents());

						sender.sendMessage(ChatColor.GREEN + "Successfully set kit contents for " + args[1] + ".");
					}
				} else {
					sender.sendMessage(KitCommand.NO_KIT);
				}
			} else if(args[0].equalsIgnoreCase("getinv")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return false;
				}

				if(args.length < 2) {
					sender.sendMessage(this.HELP_MESSAGE);
					return true;
				}

				if(kit != null) {
					player.getInventory().setContents(kit.getContents());
					player.getInventory().setArmorContents(kit.getArmor());
					player.updateInventory();
					sender.sendMessage(ChatColor.GREEN + "Successfully retrieved kit contents from " + args[1] + ".");
				} else {
					sender.sendMessage(KitCommand.NO_KIT);
				}
			} else if(args[0].equalsIgnoreCase("save")) {
				if(!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return false;
				}

				this.plugin.getKitManager().loadKits();
				sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the kits.");
			} else if(args[0].equalsIgnoreCase("setdelay")) {
				if (!player.hasPermission(Permission.OP_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return false;
				}

				if (args.length < 3) {
					sender.sendMessage(this.HELP_MESSAGE);
					return true;
				}

				if(kit == null) {
					player.sendMessage(NO_KIT);
					return false;
				}

				if(!NumberUtils.isInteger(args[2])) {
					sender.sendMessage(ChatColor.RED + "Invalid time!");
					return false;
				}

				int delay = Integer.parseInt(args[2]);

				kit.setDelay(delay);
				player.sendMessage(Color.translate("&eYou have set kit delay of &d" + kit.getName() + " &eto &d" + kit.getDelay() + "&e."));
			} else {
				Kit toGive = plugin.getKitManager().getKit(args[0]);

				if(toGive == null) {
					player.sendMessage(NO_KIT);
					return false;
				}

				if(data.isKitActive(player, toGive)) {
					player.sendMessage(Color.translate("&cYou can't use " + toGive.getName() + " &cfor another &l" + StringUtils.formatInt(toGive.getDelay()) + "&c!"));
					return false;
				}

				toGive.applyToPlayer(player);
			}
		}

		return true;
	}*/
}
