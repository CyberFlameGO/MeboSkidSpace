package secondlife.network.victions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

/**
 * Created by Marko on 19.07.2018.
 */
public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        String message = event.getMessage();
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

        for(Player recipient : event.getRecipients()) {
            recipient.sendMessage(this.getFormat(player, message, recipient));
        }

        ConsoleCommandSender console = Bukkit.getConsoleSender();

        console.sendMessage(this.getFormat(player, message, console));
    }

    private String getFormat(Player player, String message, CommandSender viewer) {
        if(player.isOp()) {
            return Color.translate(player.getDisplayName() + " &6» &f" + message);
        } else {
            return Color.translate(player.getDisplayName() + " &6» &f") + message;
        }
    }
}
