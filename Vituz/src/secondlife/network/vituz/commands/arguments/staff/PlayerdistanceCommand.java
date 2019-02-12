package secondlife.network.vituz.commands.arguments.staff;

import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotWorldConfig;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class PlayerdistanceCommand extends BaseCommand {

	public PlayerdistanceCommand(Vituz plugin) {
		super(plugin);

		this.command = "playerdistance";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(Color.translate("&e&lPlayer Distance&7:"));
			
			for(World world : Bukkit.getWorlds()) {
				SpigotWorldConfig swc = ((CraftWorld) world).getHandle().spigotConfig;

				player.sendMessage(Color.translate("&c" + world.getName() + " &c" + swc.playerTrackingRange));
			}
		} else {
			int distance = Integer.parseInt(args[0]);

			if(distance < 0 || distance > 64) {
				player.sendMessage(Color.translate("&cNumber must be between 0-64"));
				return;
			}

			for(World world : Bukkit.getWorlds()) {
				SpigotWorldConfig swc = ((CraftWorld) world).getHandle().spigotConfig;
				
				swc.playerTrackingRange = distance;
			}

			for(Player online : Bukkit.getOnlinePlayers()) {
				EntityTracker tracker = ((CraftWorld) online.getWorld()).getHandle().getTracker();
				EntityTrackerEntry trackerEntry = (EntityTrackerEntry) tracker.trackedEntities.get(online.getEntityId());
				trackerEntry.b = distance;
			}

			player.sendMessage(Color.translate("&ePlayer distance set to &c" + args[0]));
		}
	}
}
