package secondlife.network.hcfactions.factions.commands.staff;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.FactionManager;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class FactionSetDTRRegenCommand extends SubCommand {

	public FactionSetDTRRegenCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "setdtrregen", "dtrregen", "regen" };
		this.permission = Permission.STAFF_PLUS_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if (args.length < 3) {
            player.sendMessage(Color.translate("&cUsage: /f setdtrregen <player|faction> <regen>"));
            return;
        }

        long newRegen = JavaUtils.parse(args[2]);

        if(newRegen < 0L) {
        	player.sendMessage(Color.translate("&cInvalid Number."));
            return;
        }

        if(newRegen > FactionManager.max_dtr_regen_millis) {
            player.sendMessage(Color.translate("&cYou cannot set factions DTR regen above &l" + FactionManager.max_dtr_regen_words + "&c."));
            return;
        }

        Faction faction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

        if(faction == null) {
        	player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
            return;
        }

        if(!(faction instanceof PlayerFaction)) {
            player.sendMessage(Color.translate("&cThis type of faction does not use DTR!"));
            return;
        }

        PlayerFaction playerFaction = (PlayerFaction) faction;
        
        long previousRegenRemaining = playerFaction.getRemainingRegenerationTime();
        playerFaction.setRemainingRegenerationTime(newRegen);

        Command.broadcastCommandMessage(sender, Color.translate("&eSet DTR regen of &d" + faction.getName() + (previousRegenRemaining > 0L ? " &efrom &d" + DurationFormatUtils.formatDurationWords(previousRegenRemaining, true, true) : "") + " &eto &d" + DurationFormatUtils.formatDurationWords(newRegen, true, true)));
    }
}
