package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionDepositCommand extends SubCommand {

	public FactionDepositCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "deposit", "d" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(args.length < 2) {
        	sender.sendMessage(Color.translate("&cUsage: /f deposit <amount|all>"));
        	return;
        }
        
        if(playerFaction == null) {
            sender.sendMessage(HCFUtils.NO_FACTION);
            return;
        }
        
        HCFData data = HCFData.getByName(player.getName());

        Integer amount;
        
        if(args[1].equalsIgnoreCase("all")) {
            amount = data.getBalance();
        } else {
            if((amount = (JavaUtils.tryParseInt(args[1]))) == null) {
                sender.sendMessage(Color.translate("&cInvalid Number!"));
                return;
            }
        }

        if(amount <= 0) {
            sender.sendMessage(Color.translate("&cAmount must be positive!"));
            return;
        }

        if(data.getBalance() < amount) {
            sender.sendMessage(Color.translate("&cYou need at least &l" + "$" + JavaUtils.format(amount) + " &cto do this, you only have &l$" + JavaUtils.format(data.getBalance())));
            return;
        }

        data.setBalance(data.getBalance() - amount);

        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.broadcast(Color.translate(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + " &ehas deposited &d" + "$" + JavaUtils.format(amount) + " &einto the faction balance!"));
    }
}
