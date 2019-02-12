package secondlife.network.uhc.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.RequestManager;
import secondlife.network.vituz.utilties.Color;

@Getter
@AllArgsConstructor
public class PartyRequest {
	
    private Party party;
    private Player recipient;

    public void handleDeny() {
        recipient.sendMessage(Color.translate("&eYou have denied the party invite."));
        party.getOwner().sendMessage(Color.translate("&d" + recipient.getName() + " &ehas denied the party invite."));
    }

    public static void handleRequestTimer(Player player, Party party) {
        new BukkitRunnable() {
            public void run() {
                PartyRequest partyRequest = RequestManager.getByPlayer(player);

                if(partyRequest != null) {
                    RequestManager.timedOutRequest(player);

                    player.sendMessage(Color.translate("&eYour party request from &d" + party.getOwner().getName() + " &ehas expiried."));

                    party.getOwner().sendMessage(Color.translate("&eParty invite to &d" + player.getName() + "&ehas expiried."));
                }

            }
        }.runTaskLater(UHC.getInstance(), 350L);
    }
}
