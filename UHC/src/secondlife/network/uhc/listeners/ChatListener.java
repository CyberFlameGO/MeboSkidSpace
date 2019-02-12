package secondlife.network.uhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import secondlife.network.uhc.managers.PartyManager;
import secondlife.network.uhc.party.Party;
import secondlife.network.uhc.utilties.BaseListener;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatListener extends BaseListener implements Listener {
	
	public static List<UUID> chat = new ArrayList<>();
	
	public static void disable() {
		chat.clear();
	}

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

		if(chat.contains(player.getUniqueId())) {
			Party party = PartyManager.getByPlayer(player);

			if(party != null) {
				party.broadcast("&9[Team Chat] &d" + player.getName() + "&7: &f" + event.getMessage());
			}

			return;
		}
		
		if(UHCUtils.isPlayerInSpecMode(player) && !player.hasPermission(Permission.STAFF_PERMISSION)) {					
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(UHCUtils.isPlayerInSpecMode(online)) {	
					online.sendMessage(this.getSpectatorFormat(player, message));
				}
			}
			
			return;
		}
    
		for(Player recipient : event.getRecipients()) {
			recipient.sendMessage(this.getFormat(player, message, recipient));
		}
		
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		
		console.sendMessage(this.getFormat(player, message, console));
    }
    
	private String getSpectatorFormat(Player player, String message) {	
		if(!player.hasPermission(Permission.STAFF_PERMISSION)) {
			if(UHCUtils.isPlayerInSpecMode(player)) {
				return Color.translate("&9[Spectator Chat] &7" + player.getDisplayName() + " &6» &f") + message;
			}
		}
		
		return null;
	}
    
	private String getFormat(Player player, String message, CommandSender viewer) {		
		if(PartyManager.isEnabled()) {
			if(PartyManager.getByPlayer(player) != null) {
				if(player.isOp()) {
					return Color.translate("&7[" + PartyManager.getByPlayer(player).getTeamColor() + "Team #" + PartyManager.getByPlayer(player).getId() + "&7] " + player.getDisplayName() + " &6» &f" + message);
				} else {
					return Color.translate("&7[" + PartyManager.getByPlayer(player).getTeamColor() + "Team #" + PartyManager.getByPlayer(player).getId() + "&7] " + player.getDisplayName() + " &6» &f") + message;
				}
			} else {
				if(player.isOp()) {
					return Color.translate(player.getDisplayName() + " &6» &f" + message);
				} else {
					return Color.translate(player.getDisplayName() + " &6» &f") + message;
				}
			}
		} else {
			if(player.isOp()) {
				return Color.translate(player.getDisplayName() + " &6» &f" + message);
			} else {
				return Color.translate(player.getDisplayName() + " &6» &f") + message;
			}
		}
	}
}
