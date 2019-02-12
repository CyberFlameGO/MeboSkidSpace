package secondlife.network.practice.commands.management;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.arena.Arena;
import secondlife.network.practice.runnable.ArenaCommandRunnable;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.Set;

public class ArenaCommand extends Command {

	private static final String NO_ARENA = CC.RED + "That arena doesn't exist!";
	private final Practice plugin = Practice.getInstance();

	private final static String[] HELP_MESSAGE = new String[] {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			CC.PRIMARY + "Arena Commands:",
			CC.SECONDARY + "(*) /arena create <name> " + ChatColor.GRAY + "- Create arena",
			CC.SECONDARY + "(*) /arena delete <name> " + ChatColor.GRAY + "- Delete arena",
			CC.SECONDARY + "(*) /arena a <name> " + ChatColor.GRAY + "- Set first spawn point",
			CC.SECONDARY + "(*) /arena b <name> " + ChatColor.GRAY + "- Set second spawn point",
			CC.SECONDARY + "(*) /arena abed <name> " + ChatColor.GRAY + "- Set first bed point",
			CC.SECONDARY + "(*) /arena bbed <name> " + ChatColor.GRAY + "- Set second bed point",
			CC.SECONDARY + "(*) /arena min <name> " + ChatColor.GRAY + "- Set min of arena",
			CC.SECONDARY + "(*) /arena max <name> " + ChatColor.GRAY + "- Set max of arena",
			CC.SECONDARY + "(*) /arena enable <name> " + ChatColor.GRAY + "- Enable arena",
			CC.SECONDARY + "(*) /arena generate <name> <amount> " + ChatColor.GRAY + "- Generate multiple arenas",
			CC.SECONDARY + "(*) /arena list list " + ChatColor.GRAY + "- See all arenas",
			CC.SECONDARY + "(*) /arena save save " + ChatColor.GRAY + "- Save arenas",
			CC.SECONDARY + "(*) /arena manage manage " + ChatColor.GRAY + "- Manage arenas",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
	};

	public ArenaCommand() {
		super("arena");

		setDescription("Manage server arenas.");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Permission.OP_PERMISSION)) return true;

		if(args.length < 2) {
			sender.sendMessage(HELP_MESSAGE);
			return true;
		}

		Player player = (Player) sender;
		Arena arena = this.plugin.getArenaManager().getArena(args[1]);

		switch(args[0].toLowerCase()) {
			case "create":
				if(arena == null) {
					this.plugin.getArenaManager().createArena(args[1]);
					sender.sendMessage(CC.GREEN + "Successfully created arena " + args[1] + ".");
				} else {
					sender.sendMessage(CC.RED + "That arena already exists!");
				}
				break;
			case "delete":
				if(arena != null) {
					this.plugin.getArenaManager().deleteArena(args[1]);
					sender.sendMessage(CC.GREEN + "Successfully deleted arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "a":
				if(arena != null) {
					Location location = player.getLocation();

					if(args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
						location.setX(location.getBlockX() + 0.5D);
						location.setY(location.getBlockY() + 3.0D);
						location.setZ(location.getBlockZ() + 0.5D);
					}

					arena.setA(CustomLocation.fromBukkitLocation(location));
					sender.sendMessage(CC.GREEN + "Successfully set position A for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "b":
				if(arena != null) {
					Location location = player.getLocation();

					if(args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
						location.setX(location.getBlockX() + 0.5D);
						location.setY(location.getBlockY() + 3.0D);
						location.setZ(location.getBlockZ() + 0.5D);
					}

					arena.setB(CustomLocation.fromBukkitLocation(location));
					sender.sendMessage(CC.GREEN + "Successfully set position B for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "abed":
				if(arena != null) {
					Block block = player.getTargetBlock((Set<Material>) null, 5);

					if(block == null) {
						player.sendMessage(Color.translate("&cYou must be looking at bed."));
						return false;
					}

					if(block.getType() != Material.BED) {
						player.sendMessage(Color.translate("&cYou must be looking at bed."));
						return false;
					}

					arena.setABed(block);
					sender.sendMessage(CC.GREEN + "Successfully set position ABed for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "bbed":
				if(arena != null) {
					Block block = player.getTargetBlock((Set<Material>) null, 5);

					if(block == null) {
						player.sendMessage(Color.translate("&cYou must be looking at bed."));
						return false;
					}

					if(block.getType() != Material.BED) {
						player.sendMessage(Color.translate("&cYou must be looking at bed."));
						return false;
					}

					arena.setBBed(block);
					sender.sendMessage(CC.GREEN + "Successfully set position BBed for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "min":
				if(arena != null) {
					arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
					sender.sendMessage(CC.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "max":
				if(arena != null) {
					arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
					sender.sendMessage(CC.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "disable":
			case "enable":
				if(arena != null) {
					arena.setEnabled(!arena.isEnabled());
					sender.sendMessage(arena.isEnabled() ? CC.GREEN + "Successfully enabled arena " + args[1] + "." :
							CC.RED + "Successfully disabled arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "generate":
				if(args.length == 3) {
					int arenas = Integer.parseInt(args[2]);

					this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
					this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
				} else {
					sender.sendMessage(CC.RED + "Usage: /arena generate <arena> <arenas>");
				}
				break;
			case "list":
				sender.sendMessage(Color.translate("&eCurrent arenas:"));

				for(Arena arenas : Practice.getInstance().getArenaManager().getArenas().values()) {
					sender.sendMessage(Color.translate(" &7- &d" + arenas.getName()));
				}
				break;
			case "save":
				this.plugin.getArenaManager().reloadArenas();
				sender.sendMessage(CC.GREEN + "Successfully reloaded the arenas.");
				break;
			case "manage":
				this.plugin.getArenaManager().openArenaSystemUI(player);
				break;
			default:
				sender.sendMessage(this.HELP_MESSAGE);
				break;
		}

		return true;
	}
}
