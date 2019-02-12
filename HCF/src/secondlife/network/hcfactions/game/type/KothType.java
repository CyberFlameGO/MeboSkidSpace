package secondlife.network.hcfactions.game.type;

import org.bukkit.entity.Player;
import secondlife.network.hcfactions.factions.type.games.EventFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.game.events.GameManager;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.timers.GameHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.concurrent.TimeUnit;

public class KothType implements GameManager {

	public static long default_cap_millis = TimeUnit.MINUTES.toMillis(10L);
	public static long control_time_announce = TimeUnit.SECONDS.toMillis(15L);

    @Override
    public void tick(GameHandler game, EventFaction eventFaction) {
        CaptureZone captureZone = ((KothFaction) eventFaction).getCaptureZone();
        
        captureZone.updateScoreboardRemaining();

        if(captureZone.getCappingPlayer() != null && (captureZone.getCuboid() == null || !captureZone.getCuboid().contains(captureZone.getCappingPlayer()) || captureZone.getCappingPlayer().isDead() || !captureZone.getCappingPlayer().isValid())) {
            captureZone.setCappingPlayer(null);
        }

        long remainingMillis = captureZone.getRemainingCaptureMillis();
        
        if(remainingMillis <= 0L) {
        	GameHandler.getGameHandler().handleWinner(captureZone.getCappingPlayer());
            GameHandler.stopCooldown();
            return;
        }

        if(remainingMillis == captureZone.getDefaultCaptureMillis()) return;

        int remainingSeconds = (int) (remainingMillis / 1000L);
        
        if(remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Msg.sendMessage("&eSomeone is controlling &d" + captureZone.getDisplayName() + ". &d(" + HCFUtils.KOTH_FORMAT.format(remainingMillis) + ")");
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, GameHandler game) {
        Msg.sendMessage("&8&m---------------------------------");
        Msg.sendMessage("&8\u2588&e\u2588\u2588\u2588\u2588\u2588\u2588\u2588&8\u2588");
        Msg.sendMessage("&e\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
        Msg.sendMessage("&e\u2588&6\u2588&e\u2588&6\u2588&e\u2588&6\u2588&e\u2588&6\u2588&e\u2588");
        Msg.sendMessage("&e\u2588&6\u2588\u2588\u2588\u2588\u2588\u2588\u2588&e\u2588       &a&l" + eventFaction.getName());
        Msg.sendMessage("&e\u2588&6\u2588&b\u2588&6\u2588&b\u2588&6\u2588&b\u2588&6\u2588&e\u2588 &7has been started. &a(" + HCFUtils.KOTH_FORMAT.format(game.getRemaining()) + ")");
        Msg.sendMessage("&e\u2588&6\u2588\u2588\u2588\u2588\u2588\u2588\u2588&e\u2588");
        Msg.sendMessage("&e\u2588\u2588\u2588&7\u2588\u2588\u2588&e\u2588\u2588\u2588");
        Msg.sendMessage("&e\u2588\u2588\u2588\u2588&7\u2588&e\u2588\u2588\u2588\u2588");
        Msg.sendMessage("&8\u2588&e\u2588\u2588\u2588&7\u2588&e\u2588\u2588\u2588&8\u2588");
        Msg.sendMessage("&8&m---------------------------------");
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        player.sendMessage(Color.translate("&eYou are now in control of &d" + captureZone.getDisplayName() + "."));
        return true;
    }

    @Override
    public void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        player.sendMessage(Color.translate("&eYou are no longer in control of &d" + captureZone.getDisplayName() + "."));

        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if(remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > control_time_announce) {
            Msg.sendMessage("&d" + player.getName() + " &ehas lost control of &d" + captureZone.getDisplayName() + ". &d(" + captureZone.getScoreboardRemaining() + ")");
        }
    }

    @Override
    public void stopTiming() {}
}
