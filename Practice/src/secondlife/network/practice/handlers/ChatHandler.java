package secondlife.network.practice.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.PlayerKit;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.Handler;
import secondlife.network.vituz.handlers.ChatControlHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.ArrayList;
import java.util.UUID;

public class ChatHandler extends Handler implements Listener {

    public static ArrayList<UUID> chat;

    public ChatHandler(Practice plugin) {
        super(plugin);

        chat = new ArrayList<UUID>();

        Bukkit.getPluginManager().registerEvents(this, this.getInstance());
    }

    public static void disable() {
        chat.clear();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        String message = event.getMessage();
        Player player = event.getPlayer();

        event.setCancelled(true);

        if(ChatControlHandler.muted && !player.hasPermission(Permission.STAFF_PERMISSION)) {
            event.setCancelled(true);

            player.sendMessage(Color.translate("&cChat is currently muted."));
            return;
        }

        if(ChatControlHandler.delay > 0 && !player.hasPermission(Permission.STAFF_PERMISSION)) {
            if(ChatControlHandler.isActive(player)) {
                event.setCancelled(true);
                player.sendMessage(Color.translate("&cYou can't use chat for another &l" + StringUtils.getRemaining(ChatControlHandler.getMillisecondsLeft(player), true) + "&c."));
                return;
            }

            ChatControlHandler.applyCooldown(player);
        }

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

        if (party != null) {
            if (message.startsWith("!") || message.startsWith("@")) {
                event.setCancelled(true);

                String finalMessage = CC.PRIMARY + "[Party] " + CC.PRIMARY + player.getName() + ChatColor.RESET + ": " +
                        message.replaceFirst("!", "").replaceFirst("@", "");

                party.broadcast(finalMessage);
                return;
            }
        } else {
            PlayerKit kitRenaming = this.plugin.getEditorManager().getRenamingKit(player.getUniqueId());

            if (kitRenaming != null) {
                kitRenaming.setDisplayName(ChatColor.translateAlternateColorCodes('&', message));
                event.setCancelled(true);
                event.getPlayer().sendMessage(
                        CC.PRIMARY + "Set kit " + CC.SECONDARY + kitRenaming.getIndex() + CC.PRIMARY + "'s name to "
                                + CC.SECONDARY + kitRenaming.getDisplayName());

                this.plugin.getEditorManager().removeRenamingKit(event.getPlayer().getUniqueId());
                return;
            }
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
