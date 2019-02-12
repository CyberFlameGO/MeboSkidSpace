package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotWorldConfig;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class ViewdistanceCommand extends BaseCommand {

	public ViewdistanceCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "viewdistance";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(Color.translate("&e&lView Distance&7:"));
			
			for(World world : Bukkit.getWorlds()) {
				SpigotWorldConfig swc = ((CraftWorld) world).getHandle().spigotConfig;

				player.sendMessage(Color.translate("&e" + world.getName() + " &c" + swc.viewDistance));
			}
		} else {
			int distance = Integer.parseInt(args[0]);

			if(distance < 0 || distance > 16) {
				player.sendMessage(Color.translate("&cNumber must be between 0-16"));
				return;
			}

			for(World world : Bukkit.getWorlds()) {
				SpigotWorldConfig swc = ((CraftWorld) world).getHandle().spigotConfig;
				
				swc.viewDistance = distance;
			}

			new BukkitRunnable() {
				public void run() {
					for(Player online : Bukkit.getOnlinePlayers()) {
						online.spigot().setViewDistance(distance);
					}
				}
			}.runTaskAsynchronously(this.getPlugin());

			player.sendMessage(Color.translate("&eView distance set to &c" + args[0]));
		}
	}
}