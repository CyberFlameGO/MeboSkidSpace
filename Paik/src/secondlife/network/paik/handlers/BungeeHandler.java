package secondlife.network.paik.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import secondlife.network.paik.Paik;
import secondlife.network.paik.utilties.Handler;
import secondlife.network.paik.utilties.events.BungeeReceivedEvent;
import secondlife.network.paik.utilties.events.ModListRetrieveEvent;

import java.util.Map;

public class BungeeHandler extends Handler implements PluginMessageListener {

	public BungeeHandler(Paik plugin) {
		super(plugin);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("BungeeCord")) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subChannel = in.readUTF();

		if(subChannel.equals("ForgeMods")) {
			try {
				Map<String, String> mods = (Map<String, String>) new JSONParser().parse(in.readUTF());
				ModListRetrieveEvent event = new ModListRetrieveEvent(player, mods);
				this.plugin.getServer().getPluginManager().callEvent(event);
			} catch(ParseException e) {
				e.printStackTrace();
			}

			return;
		}

		short len = in.readShort();
		byte[] messageBytes = new byte[len];
		in.readFully(messageBytes);

		ByteArrayDataInput dis = ByteStreams.newDataInput(messageBytes);
		String data = dis.readUTF();
		Long systemTime = Long.parseLong(data.split(":")[0]);

		BungeeReceivedEvent event = new BungeeReceivedEvent(player, subChannel, data.replace(systemTime + ":", ""), message, systemTime > System.currentTimeMillis());
		this.plugin.getServer().getPluginManager().callEvent(event);
	}
}
