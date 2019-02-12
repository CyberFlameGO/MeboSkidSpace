package secondlife.network.hcfactions.game.events;

import org.bukkit.entity.Player;
import secondlife.network.hcfactions.factions.type.games.EventFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.timers.GameHandler;

public interface GameManager {

    void tick(GameHandler gameHandler, EventFaction eventFaction);

    void onContest(EventFaction eventFaction, GameHandler gameHandler);

    boolean onControlTake(Player player, CaptureZone captureZone);

    void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction);

    void stopTiming();
}
