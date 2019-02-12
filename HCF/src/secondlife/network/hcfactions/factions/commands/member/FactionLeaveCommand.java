package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;

public class FactionLeaveCommand extends SubCommand {

	public FactionLeaveCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "leave" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(playerFaction.getMember(player.getName()).getRole() == Role.LEADER) {
        	player.sendMessage(Color.translate("&cYou can't just leave faction if you want to leave faction as leader please type /f disband or just give someone else leader. Example: &l/faction leader VISUAL_ GANGGANGNGNGANGNG!!!"));
            return;
        }

        if(playerFaction.removeMember(player, player, player.getName(), false)) {
            sender.sendMessage(Color.translate("&eYou have succsesfuly left the faction."));
            playerFaction.broadcast(Relation.ENEMY.toChatColour() + sender.getName() + " &ehas left the faction.");
        }

        VituzNametag.reloadPlayer(player);
        VituzNametag.reloadOthersFor(player);
    }
}
