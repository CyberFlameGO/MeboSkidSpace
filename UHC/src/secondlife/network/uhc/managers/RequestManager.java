package secondlife.network.uhc.managers;

import lombok.Getter;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.party.Party;
import secondlife.network.uhc.party.PartyRequest;
import secondlife.network.uhc.utilties.Manager;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;

import java.util.HashMap;
import java.util.Map;

public class RequestManager extends Manager {

	@Getter
	private static Map<Player, PartyRequest> requests = new HashMap<>();
	
    public RequestManager(UHC plugin) {
		super(plugin);
    }

    public static void handleSendRequest(Player sender, Player player, Party party) {
		if(requests.containsKey(player)) {
			sender.sendMessage(Color.translate("&cThat player has already party request."));
			return;
		}
		
		if(PartyManager.getByPlayer(player) != null) {
			sender.sendMessage(Color.translate("&cThat player is already in a party."));
			return;
		}
		
		requests.put(player, new PartyRequest(party, player));

		player.sendMessage(Color.translate("&eYou have &areceived&e a party request from &d" + party.getOwner().getName() + "&e."));

		ActionMessage actionMessage = new ActionMessage();
		actionMessage.addText("&eClick ");
		actionMessage.addText("&a&lYES").setClickEvent(ActionMessage.ClickableType.RunCommand, "/party accept").addHoverText(Color.translate("&eClick this to join their party."));
		actionMessage.addText(" &eor click ");
		actionMessage.addText("&c&lNO").setClickEvent(ActionMessage.ClickableType.RunCommand, "/party deny").addHoverText(Color.translate("&eClick this to deny their party invite."));
		actionMessage.sendToPlayer(player);

		party.broadcast("&d" + party.getOwner().getName() + " &ehas invited &d" + player.getName() + " &eto the party.");

		PartyRequest.handleRequestTimer(player, party);
	}

    public static PartyRequest getByPlayer(Player player) {
        return requests.get(player);
    }

    public static void declined(Player player) {
        requests.remove(player).handleDeny();
    }

    public static void timedOutRequest(Player player) {
        requests.remove(player);
    }
}
