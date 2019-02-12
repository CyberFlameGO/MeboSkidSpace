package secondlife.network.hcfactions.factions.commands.captain;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.concurrent.TimeUnit;

public class FactionTagCommand extends SubCommand {

    private static long FACTION_RENAME_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(15L);
    private static String FACTION_RENAME_DELAY_WORDS = DurationFormatUtils.formatDurationWords(FACTION_RENAME_DELAY_MILLIS, true, true);

    public FactionTagCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "tag", "rename" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
	
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f tag <name>"));
            return;
        }
        
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(playerFaction.getMember(player.getName()).getRole() != Role.LEADER) {
            player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        String newName = args[1];

        if(newName.length() < HCFConfiguration.factionNameMinCharacters) {
            player.sendMessage(Color.translate("&cFaction names must have at least &l" + HCFConfiguration.factionNameMinCharacters + " &ccharacters!"));
            return;
        }

        if(newName.length() > HCFConfiguration.factionNameMaxCharacters) {
            player.sendMessage(Color.translate("&cFaction names cannot be longer than &l" + HCFConfiguration.factionNameMaxCharacters + " &ccharacters!"));
            return;
        }

        if(!JavaUtils.isAlphanumeric(newName)) {
            player.sendMessage(Color.translate("&cFaction names may only be alphanumeric!"));
            return;
        }
        
		if(!sender.hasPermission(Permission.OP_PERMISSION)) {
			if(newName.equalsIgnoreCase("EOTW")) {
				player.sendMessage(Color.translate("&cInvalid name!"));
				return;
			}
		}

        if(RegisterHandler.getInstancee().getFactionManager().getFaction(newName) != null) {
            player.sendMessage(Color.translate("&c&l" + newName + " &calready exists!"));
            return;
        }

        long difference = (playerFaction.getLastRenameMillis() - System.currentTimeMillis()) + FACTION_RENAME_DELAY_MILLIS;

        if(!player.isOp() && difference > 0L) {
            player.sendMessage(Color.translate("&cThere is a faction rename delay of &l" + FACTION_RENAME_DELAY_WORDS + "&c. Therefore you need to wait another &l" + DurationFormatUtils.formatDurationWords(difference, true, true) + " &cto rename your faction."));
            return;
        }

        playerFaction.setName(args[1], sender);
        return;
    }
}
