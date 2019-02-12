package secondlife.network.hcfactions.factions.commands.captain;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionWithdrawCommand extends SubCommand {

    public FactionWithdrawCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "withdraw", "w", "take" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
        	player.sendMessage(Color.translate("&cUsage: /f withdraw <all|amount>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        FactionMember factionMember = playerFaction.getMember(player.getName());

        if(factionMember.getRole() == Role.MEMBER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        int factionBalance = playerFaction.getBalance();
        Integer amount;

        if(args[1].equalsIgnoreCase("all")) {
            amount = factionBalance;
        } else {
            if((amount = (JavaUtils.tryParseInt(args[1]))) == null) {
            	player.sendMessage(Color.translate("&cInvalid money!"));
                return;
            }
        }

        if(amount <= 0) {
            player.sendMessage(Color.translate("&cAmount must be positive!"));
            return;
        }

        if(amount > factionBalance) {
        	player.sendMessage(Color.translate("&cThe faction doesn't have enough money to do this!"));
            return;
        }

        HCFData data = HCFData.getByName(player.getName());
        data.setBalance(data.getBalance() + amount);
        
        playerFaction.setBalance(factionBalance - amount);
        playerFaction.broadcast(Color.translate("&d" + sender.getName() + " &ehas withdrew &d$" + JavaUtils.format(amount) + " &efrom the faction balance!"));
        player.sendMessage(Color.translate("&eYou have withdrawn &d$" + JavaUtils.format(amount) + " &efrom the faction balance!"));
    }
}
