package secondlife.network.hcfactions.factions.commands.leader;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.game.events.eotw.EOTWHandler;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;

public class FactionDisbandCommand extends SubCommand {

	private ArrayList<String> kuldown = new ArrayList<String>();

	public FactionDisbandCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "disband" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(kuldown.contains(player.getName())) {
        	player.sendMessage(Color.translate("&cYou are on cooldown!"));
        	return;
        }
        
        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(playerFaction.isRaidable() && !HCFConfiguration.kitMap && !EOTWHandler.isEOTW()) {
            player.sendMessage(Color.translate("&cYou can't disband your faction while it is raidable!"));
            return;
        }

        if(playerFaction.getMember(player.getName()).getRole() != Role.LEADER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }
        
        playerFaction.broadcast("&c&l" + sender.getName() + " &chas disbanded the faction!!!");
        
        RegisterHandler.getInstancee().getFactionManager().removeFaction(playerFaction, sender);

        VituzNametag.reloadPlayer(player);
        VituzNametag.reloadOthersFor(player);
        
        if(!player.isOp()) {
        	kuldown.add(player.getName());
        	
            new BukkitRunnable() {
            	public void run() {
            		kuldown.remove(player.getName());
            	}
            }.runTaskLater(HCF.getInstance(), 600L);
        }
    }
}
