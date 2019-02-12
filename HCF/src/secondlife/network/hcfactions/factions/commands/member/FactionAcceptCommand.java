package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.ChatChannel;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;

public class FactionAcceptCommand extends SubCommand {

	public FactionAcceptCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "accept", "join" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f join <player|faction>"));
            return;
        }

        if(RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player) != null) {
            player.sendMessage(HCFUtils.ALREADY_IN_FACTION);
            return;
        }

        Faction faction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

        if(faction == null) {
            player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
            return;
        }

        if(!(faction instanceof PlayerFaction)) {
            player.sendMessage(Color.translate("&cYou cannot join system factions!"));
            return;
        }

        PlayerFaction targetFaction = (PlayerFaction) faction;

        if(targetFaction.getMembers().size() >= HCFConfiguration.maxMembers) {
            player.sendMessage(Color.translate("&d" + faction.getDisplayName(sender) + " &eis currently full!"));
            player.sendMessage(Color.translate("&cFaction limits are &d" + HCFConfiguration.maxMembers + "&c!"));
            return;
        }

        if(!targetFaction.getInvitedPlayerNames().contains(player.getName())) {
            player.sendMessage(Color.translate("&d" + faction.getDisplayName(sender) + " &ehas not invited you."));
            return;
        }

        if(targetFaction.addMember(player, player, player.getName(), new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
            targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + " &ehas joined the faction.");
        }

        VituzNametag.reloadPlayer(player);
        VituzNametag.reloadOthersFor(player);
    }
}
