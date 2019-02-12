package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class KillallCommand extends BaseCommand {

	public KillallCommand(Vituz plugin) {
		super(plugin);

		this.command = "killall";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /killall <all|mobs|animals|items>"));
		} else {
			if(args[0].equalsIgnoreCase("all")) {	
				int total = 0;
				
				for(World world : Bukkit.getWorlds()) {
					for(Entity entity : world.getEntities()) {
						if(entity instanceof Monster || entity instanceof Animals || entity instanceof Item) {
							entity.remove();
							total++;
						}
					}
				}
				
				sender.sendMessage(Color.translate("&eYou have killed all entities."));
				sender.sendMessage(Color.translate("&eTotal&7: &d" + total));
			} else if(args[0].equalsIgnoreCase("mobs") || args[0].equalsIgnoreCase("mob")) {	
				int total = 0;
				
				for(World world : Bukkit.getWorlds()) {
					for(Entity entity : world.getEntities()) {
						if(entity instanceof Monster) {
							entity.remove();
							total++;
						}
					}
				}
				
				sender.sendMessage(Color.translate("&eYou have killed all mobs."));
				sender.sendMessage(Color.translate("&eTotal&7: &d" + total));
			} else if(args[0].equalsIgnoreCase("animals") || args[0].equalsIgnoreCase("animal")) {		
				int total = 0;
				
				for(World world : Bukkit.getWorlds()) {
					for(Entity entity : world.getEntities()) {
						if(entity instanceof Animals) {
							entity.remove();
							total++;
						}
					}
				}
				
				sender.sendMessage(Color.translate("&eYou have killed all animals."));
				sender.sendMessage(Color.translate("&eTotal&7: &d" + total));
			} else if(args[0].equalsIgnoreCase("items") || args[0].equalsIgnoreCase("item")) {		
				int total = 0;
				
				for(World world : Bukkit.getWorlds()) {
					for(Entity entity : world.getEntities()) {
						if(entity instanceof Item) {
							entity.remove();
							total++;
						}
					}
				}
				
				sender.sendMessage(Color.translate("&eYou have killed all items."));
				sender.sendMessage(Color.translate("&eTotal&7: &d" + total));
			}
		}
	}
}