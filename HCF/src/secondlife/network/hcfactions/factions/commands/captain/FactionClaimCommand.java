package secondlife.network.hcfactions.factions.commands.captain;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.claim.ClaimHandler;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionClaimCommand extends SubCommand {

    public FactionClaimCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "claim" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player.getName());

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(playerFaction.isRaidable()) {
            player.sendMessage(Color.translate("&cYou can't claim land for your faction while raidable!"));
            return;
        }

        if(player.getInventory().contains(ClaimHandler.CLAIM_WAND)) {
            player.sendMessage(Color.translate("&cYou already have a claiming wand in your inventory!"));
            return;
        }

        if(!player.getInventory().addItem(ClaimHandler.CLAIM_WAND).isEmpty()) {
            player.sendMessage(Color.translate("&cYour inventory is full!"));
            return;
        }
    }
}
