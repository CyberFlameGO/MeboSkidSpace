package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class StatsCommand extends BaseCommand {

	public StatsCommand(HCF plugin) {
		super(plugin);
		
		this.command = "stats";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		PlayerFaction faction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player.getName());
		HCFData data = HCFData.getByName(player.getName());
		
		if (args.length == 0) {
			sender.sendMessage(HCFUtils.BIG_LINE);
			sender.sendMessage(Color.translate(" &9&lYour Stats"));
			sender.sendMessage(Color.translate(""));
			sender.sendMessage(Color.translate(" &9&lStatistics"));
			
			if(faction != null) {
				sender.sendMessage(Color.translate("   &7* &bCurrent Faction: &d" + faction.getName()));
			} else {
				sender.sendMessage(Color.translate("   &7* &bCurrent Faction: &dNone"));
			}
			
			sender.sendMessage(Color.translate("   &7* &bKills: &d" + data.getKills()));
			sender.sendMessage(Color.translate("   &7* &bDeaths: &d" + data.getDeaths()));
			sender.sendMessage(Color.translate("   &7* &bBalance: &d" + data.getBalance() + "$"));
			sender.sendMessage(HCFUtils.BIG_LINE);
			return;
		} else {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(Msg.checkOffline(sender, args[0])) return;
			
			PlayerFaction targetFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(target.getName());
			HCFData tdata = HCFData.getByName(target.getName());
			
			sender.sendMessage(HCFUtils.BIG_LINE);
			sender.sendMessage(Color.translate(" &d" + target.getName() + "'s &9&lstats."));
			sender.sendMessage(Color.translate(""));
			sender.sendMessage(Color.translate(" &9&lStatistics"));
			if(faction != null) {
				if(targetFaction.getName() != null) {
					sender.sendMessage(Color.translate("   &7* &bCurrent Faction: &d" + targetFaction.getName()));
				} else {
					sender.sendMessage(Color.translate("   &7* &bCurrent Faction: &dNone"));
				}
			} else {
				sender.sendMessage(Color.translate("   &7* &bCurrent Faction: &dNone"));
			}
			sender.sendMessage(Color.translate("   &7* &bKills: &d" + tdata.getKills()));
			sender.sendMessage(Color.translate("   &7* &bDeaths: &d" + tdata.getDeaths()));
			sender.sendMessage(Color.translate("   &7* &bBalance: &d" + tdata.getBalance() + "$"));
			sender.sendMessage(HCFUtils.BIG_LINE);
		}
		
	}
}
