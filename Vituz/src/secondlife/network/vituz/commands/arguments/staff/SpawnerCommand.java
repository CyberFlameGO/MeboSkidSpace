package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.Set;

public class SpawnerCommand extends BaseCommand {

	public SpawnerCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "spawner";
		this.permission = Permission.ADMIN_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /spawner <spawnerType>"));
		} else {
			try {
				EntityType.valueOf(args[0].toUpperCase());
			} catch (Exception e) {
				player.sendMessage(Color.translate("&cSpawner " + args[0] + " doesn't exists."));
                return;
			}
			
            Block block = player.getTargetBlock((Set<Material>) null, 5);
            
            if(block == null) {
            	player.sendMessage(Color.translate("&cYou must be looking at spawner."));
            	return;
            }
            
            if(block.getType() != Material.MOB_SPAWNER) {
            	player.sendMessage(Color.translate("&cYou must be looking at spawner."));
            	return;
            }
            
            EntityType entityType = EntityType.valueOf(args[0].toUpperCase());
            
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            
            spawner.setSpawnedType(entityType);
            spawner.update();
            
            player.sendMessage(Color.translate("&eYou have updated spawner to &d" + entityType + "&e."));
		}
		
		return;
	}
}