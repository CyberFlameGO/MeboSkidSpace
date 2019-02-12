package secondlife.network.meetupgame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

/**
 * Created by Marko on 28.03.2018.
 */
public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        event.setCancelled(true);

        if(Vituz.getInstance().getChatControlManager().isMuted() && !player.hasPermission(Permission.STAFF_PERMISSION)) {
            event.setCancelled(true);

            player.sendMessage(Color.translate("&cChat is currently muted."));
            return;
        }

        if(Vituz.getInstance().getChatControlManager().getDelay() > 0 && !player.hasPermission(Permission.STAFF_PERMISSION)) {
            if(Vituz.getInstance().getChatControlManager().isActive(player)) {
                event.setCancelled(true);
                player.sendMessage(Color.translate("&cYou can't use chat for another &l" + StringUtils.getRemaining(Vituz.getInstance().getChatControlManager().getMillisecondsLeft(player), true) + "&c."));
                return;
            }

            Vituz.getInstance().getChatControlManager().applyCooldown(player);
        }

        if(MeetupUtils.isPlayerInSpecMode(player) && !player.hasPermission(Permission.STAFF_PERMISSION)) {
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(MeetupUtils.isPlayerInSpecMode(online)) {
                    online.sendMessage(this.getSpectatorFormat(player, event.getMessage()));
                }
            }

            return;
        }

        for(Player recipient : event.getRecipients()) {
            recipient.sendMessage(this.getFormat(player, event.getMessage()));
        }

        Msg.logConsole(this.getFormat(player, event.getMessage()));
    }

    private String getFormat(Player player, String message) {
        if(player.isOp()) {
            return Color.translate(player.getDisplayName() + " &6» &f" + message);
        } else {
            return Color.translate(player.getDisplayName() + " &6» &f") + message;
        }
    }

    private String getSpectatorFormat(Player player, String message) {
        if(!player.hasPermission(Permission.STAFF_PERMISSION)) {
            if(MeetupUtils.isPlayerInSpecMode(player)) {
                return Color.translate("&9[Spectator Chat] &7" + player.getDisplayName() + " &6» &f") + message;
            }
        }

        return null;
    }
}
