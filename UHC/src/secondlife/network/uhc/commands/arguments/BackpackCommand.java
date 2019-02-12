package secondlife.network.uhc.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.managers.GameManager;
import secondlife.network.uhc.managers.PartyManager;
import secondlife.network.uhc.managers.ScenarioManager;
import secondlife.network.uhc.party.Party;
import secondlife.network.uhc.state.GameState;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.utilties.Color;

public class BackpackCommand extends BaseCommand {

	public BackpackCommand(UHC plugin) {
		super(plugin);

		this.command = "backpack";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			if(!PartyManager.isEnabled()) {
				sender.sendMessage(Color.translate("&cParties are currently disabled."));
				return;
			}
			
			if(!ScenarioManager.getByName("BackPacks").isEnabled()) {
				sender.sendMessage(Color.translate("&cYou can't use BackPacks while &lBackPacks&c scenario is disabled."));
				return;
			}
			
			if(!GameManager.getGameState().equals(GameState.PLAYING)) {
				sender.sendMessage(Color.translate("&cThe game isn't running."));
				return;
			}
			
			if(UHCUtils.isPlayerInSpecMode(player)) {
				sender.sendMessage(Color.translate("&cYou can't use this while you are in &lSpectaotr Mode&c."));
				return;
			}

			if (PartyManager.getByPlayer(player) != null) {
				Party party = PartyManager.getByPlayer(player.getPlayer());
				
				player.openInventory(party.getBackPack());
			}
		}
	}

}
