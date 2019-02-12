package secondlife.network.hcfactions.commands.arguments;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.RegenStatus;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class RegenCommand extends BaseCommand {

	public RegenCommand(HCF plugin) {
		super(plugin);

		this.command = "regen";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
		
		if(playerFaction == null) {
			sender.sendMessage(HCFUtils.NO_FACTION);
			return;
		}

		RegenStatus regenStatus = playerFaction.getRegenStatus();
		
		switch(regenStatus) {
		case FULL:
			sender.sendMessage(Color.translate("&cYour faction currently has full DTR!"));
			return;
		case PAUSED:
			sender.sendMessage(Color.translate("&cYour faction is currently on DTR freeze for another &l" + DurationFormatUtils.formatDurationWords(playerFaction.getRemainingRegenerationTime(), true, true) + "&c!"));
			return;
		case REGENERATING:
			sender.sendMessage(Color.translate("&cYour faction currently has &l" + regenStatus.getSymbol() + ' ' + playerFaction.getDeathsUntilRaidable() + "&c DTR and is regenerating at a rate of &l" + HCFConfiguration.dtrIncrementBetweenUpdate + "&c every &l" + HCFConfiguration.dtrWordsBetweenUpdate + "&c. Your ETA for maximum DTR is &l" + DurationFormatUtils.formatDurationWords(getRemainingRegenMillis(playerFaction), true, true) + "&c!"));
			return;
		}

		player.sendMessage(Color.translate("&cError while checking your regen status please contact an Administrator!"));
	}

	public long getRemainingRegenMillis(PlayerFaction faction) {
		long millisPassedSinceLastUpdate = System.currentTimeMillis() - faction.getLastDtrUpdateTimestamp();
		double dtrRequired = faction.getMaximumDeathsUntilRaidable() - faction.getDeathsUntilRaidable();
		
		return (long) ((HCFConfiguration.dtrUpdate / HCFConfiguration.dtrIncrementBetweenUpdate) * dtrRequired) - millisPassedSinceLastUpdate;
	}
}
