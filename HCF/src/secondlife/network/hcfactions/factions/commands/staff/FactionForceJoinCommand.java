package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.ChatChannel;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class FactionForceJoinCommand extends SubCommand {

	public FactionForceJoinCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "forcejoin" };
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            sender.sendMessage(Color.translate("&cUsage: /f forcejoin <player>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction != null) {
        	sender.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        Faction faction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

        if(faction == null) {
        	sender.sendMessage(HCFUtils.FACTION_NOT_FOUND);
            return;
        }

        if(!(faction instanceof PlayerFaction)) {
            sender.sendMessage(Color.translate("&cYou can only join player factions!"));
            return;
        }

        playerFaction = (PlayerFaction) faction;
        
        if(playerFaction.addMember(player, player, player.getName(), new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
            playerFaction.broadcast("&d" + player.getName() + " &ehas forcefully joined the faction!");
        }
    }
}
