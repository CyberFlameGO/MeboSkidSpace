package secondlife.network.bungee.handlers;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;
import secondlife.network.bungee.utils.Message;

import java.io.*;
import java.text.DecimalFormat;

public class PluginMessageHandler extends Handler implements Listener {

	public PluginMessageHandler(Bungee plugin) {
		super(plugin);
		
		ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	}
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		if(event.isCancelled()) return;
		
		try {
			if(event.getTag().equalsIgnoreCase(Bungee.incomingAnnounceChannel)) {
				event.setCancelled(true);
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

				String channel = in.readUTF();
				if(!channel.equals("Meetup")) return;

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());
				String server = in.readUTF();

				broadcast(player, server);
			}

			if(event.getTag().equalsIgnoreCase(Bungee.incomingBungeeBroadcastChannel)) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
				
				String channel = in.readUTF();
				String message = in.readUTF();

				switch(channel) {
					case "BroadcastChannel":
						Message.sendMessage(Color.translate(message));
						break;
					case "BroadcastPChannel":
						String permission = in.readUTF();

						Message.sendMessage(Color.translate(message), permission);
						break;
				}
			}

			if(event.getTag().equalsIgnoreCase(Bungee.incomingFilterChannel)) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

				String channel = in.readUTF();

				if(!channel.equals("Filtered")) return;

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());
				String server = in.readUTF();
				String message = in.readUTF();

				broadcastFilter(player, server, message);
			}
			
			if(event.getTag().equalsIgnoreCase(Bungee.incomingCommandChannel)) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
				
				String channel = in.readUTF();
				
				if(!channel.equals("BungeeCommands")) return;
				
				String command = in.readUTF();
				
				ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
			}

			if(event.getTag().equalsIgnoreCase(Bungee.incomingAlertsChannel)) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

				String channel = in.readUTF();

				if(channel.equals("AlertsChannel")) {
					ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());

					String check = in.readUTF();
					String location = in.readUTF();
					int ping = in.readInt();
					double tps = in.readDouble();

					alert(player, check, location, ping, tps);
				}
			}

			if(event.getTag().equalsIgnoreCase(Bungee.incomingBanChannel)) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

				String channel = in.readUTF();

				if(!channel.equals("BanChannel")) return;

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());

				ban(player);
			}

			if(event.getTag().equalsIgnoreCase(Bungee.incomingPermissionsChannel)) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

				String channel = in.readUTF();

				if(!channel.equals("PermissionsChannel")) return;

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());
				String permission = in.readUTF();

				player.setPermission(permission, true);
			}

			if(event.getTag().equalsIgnoreCase(Bungee.incomingAuthChannel)) {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

				String channel = in.readUTF();

				if(!channel.equals("AuthChannel")) return;

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());

				PlayerHandler.bridge.add(player.getUniqueId());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void broadcast(ProxiedPlayer player, String server) {
		AnnounceHandler.handle(player, server);
		
		TextComponent clicktoconnect = new TextComponent(Color.translate("&8[&4Alert&8] &d" + player.getDisplayName() + "&7: &b" + server + " &bis starting soon! &bClick here to join &f" + server));
		
		clicktoconnect.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/play " + server));
		clicktoconnect.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to join " + server).create()));
		
		for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()) {
			if(!online.getServer().getInfo().getName().equalsIgnoreCase("Hub")) {
				online.sendMessage(clicktoconnect);
			}
		}
	}
	
	public static void broadcastFilter(ProxiedPlayer player, String server, String message) {
		TextComponent clicktoconnect = new TextComponent(Color.translate("&8(&6Filtered&8) (&6" + server + "&8) &6" + player.getName() + "&7: &f" + message));
		
		for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()) {
			if(online.hasPermission("secondlife.staff")) {
				if(!SilentHandler.silent.contains(online.getUniqueId())) {
					online.sendMessage(clicktoconnect);
				}
			}
		}
	}

	public void alert(ProxiedPlayer player, String check, String location, int ping, double tps) {
		TextComponent a = new TextComponent(Color.translate("&8[&4!&8] &8(&c" + player.getServer().getInfo().getName().toUpperCase() + "&8) &4" + player.getName() + " &cfailed &4" + check.toUpperCase()));
		a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.translate("&c Player: &4" + player.getName() + "\n &cCheck: &4" + check + "\n &cLocation: &4" + location + "\n &cPing: &4" + ping + "\n &cTPS: &4" + new DecimalFormat("##.##").format(tps))).create()));

		for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()) {
			if(online.hasPermission("secondlife.staff")) {
				if(!SilentHandler.silent.contains(online.getUniqueId())) {
					online.sendMessage(a);
				}
			}
		}
	}

	public void ban(ProxiedPlayer player) {
		Message.sendMessage(Color.translate("&c&m----------------------------------------"));
		Message.sendMessage(Color.translate("&ePaik detected &c" + player.getName() + " &echeating!"));
		Message.sendMessage(Color.translate("&c&m----------------------------------------"));
	}

	public static void sendPremiumInfo(String name, boolean isPremium) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {
			out.writeUTF("Auth");
			out.writeUTF(name);
			out.writeBoolean(isPremium);

			ProxyServer.getInstance().getServerInfo("Hub").sendData(Bungee.outgoingPremiumChannel, stream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
