package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.system.SpawnFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.timers.StuckHandler;
import secondlife.network.vituz.utilties.Color;

public class FactionStuckCommand extends SubCommand {

	public FactionStuckCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "stuck" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
        
		if(RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()) instanceof SpawnFaction) {
			sender.sendMessage(Color.translate("&cYou cannot use this command at &aSpawn &cclaim."));
			return;
		}

        if(player.getWorld().getEnvironment() != Environment.NORMAL) {
            sender.sendMessage(Color.translate("&cYou can only use this command from the overworld."));
            return;
        }
		
        if(StuckHandler.isActive(player)) {
            sender.sendMessage(Color.translate("&cYour Stuck timer is already active."));
        } else {
			StuckHandler.applyCooldown(player);
        }
    }
}
