package secondlife.network.vituz.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

@Getter
@Setter
public class PlayerMessageEvent extends Event implements Cancellable {

	public static HandlerList handlers = new HandlerList();
	public boolean cancelled = false;
	
	public Player sender;
	public Player recipient;
	public String message;
	public boolean isReply;

	public PlayerMessageEvent(Player sender, Player recipient, String message, boolean isReply) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
		this.isReply = isReply;
	}

	public void send() {
		//if(!sender.hasPermission(Permission.STAFF_PERMISSION) && Vituz.getPlugin().getChatControlManager().isFiltered(this.sender, PlayerData.getByName(this.sender.getName()), this.message)) return;

		Vituz.getInstance().getEssentialsManager().getLastReplied().put(sender.getUniqueId(), recipient.getUniqueId());
		Vituz.getInstance().getEssentialsManager().getLastReplied().put(recipient.getUniqueId(), sender.getUniqueId());
		
		this.sender.sendMessage(Color.translate("&e(To " + this.recipient.getDisplayName() + "&e) &f") + this.message);
		this.recipient.sendMessage(Color.translate("&e(From " + this.sender.getDisplayName() + "&e) &f") + this.message);
		
		Bukkit.getOnlinePlayers().forEach(online -> {
			if(PlayerData.getByName(online.getName()).isSocialSpy() && online.hasPermission(Permission.STAFF_PERMISSION)) {
				online.sendMessage(Color.translate("&9(SS) " + this.sender.getDisplayName() + " &bto " + VituzAPI.getPrefix(recipient) + recipient.getDisplayName() + " &9* &f" + this.message));
			}
		});
	}

	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
