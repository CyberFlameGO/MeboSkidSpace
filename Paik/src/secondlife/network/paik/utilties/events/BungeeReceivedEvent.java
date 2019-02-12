package secondlife.network.paik.utilties.events;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class BungeeReceivedEvent extends PlayerEvent {

	private String channel;
	private String message;

	private byte[] messageBytes;
	private boolean isValid;

	public BungeeReceivedEvent(Player player, String channel, String message, byte[] messageBytes, boolean isValid) {
		super(player);
		
		this.channel = channel;
		this.message = message;
		this.messageBytes = messageBytes;
		this.isValid = isValid;
	}

}
