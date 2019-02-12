package secondlife.network.hcfactions.factions.commands.captain;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionPromoteCommand extends SubCommand {

    public FactionPromoteCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "promote", "captain", "officer", "mod", "moderator" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f promote <player>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player.getName());

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(playerFaction.getMember(player.getName()).getRole() != Role.LEADER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

		FactionMember targetMember = playerFaction.getMember(args[1]);

		if(targetMember == null) {
			player.sendMessage(Color.translate("&cThat player is not in your faction!"));
			return;
		}

		Role role = targetMember.getRole();
		if(role == Role.COLEADER) {
			player.sendMessage(Color.translate("&cThat player is already the highest rank possible!"));
			return;
		}

		if (role == Role.MEMBER) {
			role = Role.CAPTAIN;
		} else if (role == Role.CAPTAIN) {
			role = Role.COLEADER;
		}

		targetMember.setRole(role);
		playerFaction.broadcast("&7" + role.getAstrix() + "&2" + targetMember.getName() + " &ehas been assigned as a faction &d" + role.name().toLowerCase() + "&e.");
		return;
	}

}
