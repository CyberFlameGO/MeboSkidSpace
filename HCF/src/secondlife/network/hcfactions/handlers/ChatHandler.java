package secondlife.network.hcfactions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.events.FactionChatEvent;
import secondlife.network.hcfactions.factions.utils.struction.ChatChannel;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.Collection;
import java.util.Set;

public class ChatHandler extends Handler implements Listener {
		
	public ChatHandler(HCF plugin) {
		super(plugin);
				
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
	    if(event.isCancelled()) return;

        String message = event.getMessage();
        Player player = event.getPlayer();
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
        ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        Set<Player> recipients = event.getRecipients();
        
        if(chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE || chatChannel == ChatChannel.CAPTAIN) {
            if(isGlobalChannel(message)) { 
                message = message.substring(1, message.length()).trim();
                
                event.setMessage(message);
            } else {
                Collection<Player> online = playerFaction.getOnlinePlayers();
                
                if(chatChannel == ChatChannel.ALLIANCE) {
                    Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    
                    for(PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }

                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));

                Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
                return;
            }
        }

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
			recipient.sendMessage(this.getFormat(player, playerFaction, message, recipient));
		}
		
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		
		console.sendMessage(this.getFormat(player, playerFaction, message, console));
    }
    
	private String getFormat(Player player, PlayerFaction playerFaction, String message, CommandSender viewer) {
		String tag = playerFaction == null ? ChatColor.YELLOW + "*" : playerFaction.getDisplayName(viewer);
		PlayerFaction playersFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
		
		if(playersFaction != null) {
			if(player.isOp()) {
				return Color.translate("&7[" + tag + "&7]" + player.getDisplayName() + " &6» &f" + message);
			} else {
				return Color.translate("&7[" + tag + "&7]" + player.getDisplayName() + " &6» &f") + message;
			}
		} else {
			if(player.isOp()) {
				return Color.translate(player.getDisplayName() + " &6» &f" + message);
			} else {
				return Color.translate(player.getDisplayName() + " &6» &f") + message;
			}
		}
	}

    private boolean isGlobalChannel(String input) {
        int length = input.length();
        
        if(length <= 1 || !input.startsWith("!")) return false;

        for(int i = 1; i < length; i++) {
            char character = input.charAt(i);
            
            if(character == ' ') continue;
            
            if(character == '/') {
                return false;
            } else {
                break;
            }
        }

        return true;
    }
}
