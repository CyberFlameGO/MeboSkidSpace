package secondlife.network.hcfactions.factions.commands.leader;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionDemoteCommand extends SubCommand {

	public FactionDemoteCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "demote" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length < 2) {
			sender.sendMessage(Color.translate("&cUsage: /f demote <player>"));
			return;
		}

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

		if(playerFaction == null) {
			sender.sendMessage(HCFUtils.NO_FACTION);
			return;
		}

		if(playerFaction.getMember(player.getName()).getRole() != Role.LEADER) {
			sender.sendMessage(HCFUtils.INVALID_ROLE);
			return;
		}

		FactionMember targetMember = playerFaction.getMember(args[1]);

		if(targetMember == null) {
			sender.sendMessage(Color.translate("&cThat player is not in your faction!"));
			return;
		}

		Role role = targetMember.getRole();
		if(role == Role.MEMBER) {
			sender.sendMessage(Color.translate("&cThat player is already the lowest rank possible!"));
			return;
		}

		if(role == Role.CAPTAIN) {
			role = Role.MEMBER;
		} else if(role == Role.COLEADER) {
			role = Role.CAPTAIN;
		}

		targetMember.setRole(role);
		
		playerFaction.broadcast(Relation.MEMBER.toChatColour() + targetMember.getName() + " &ehas been demoted to a faction &d" + role.name().toLowerCase() + "&e!");
	}

}
