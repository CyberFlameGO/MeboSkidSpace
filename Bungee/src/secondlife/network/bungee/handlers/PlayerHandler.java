package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;
import secondlife.network.bungee.utils.Message;
import secondlife.network.bungee.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerHandler extends Handler implements Listener {

	public static int blockLengh = 16;
	public static int block = 16;
	public static boolean enabled = false;
	public static List<String> bots = new ArrayList<>();
	public static boolean spam = false;
	public static int blocked = 0;
	public static boolean edo = false;
	public static List<UUID> bridge = new ArrayList<>();

	public PlayerHandler(Bungee plugin) {
		super(plugin);

		this.resetSpam();
		this.resetFully();

		ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	}

	@EventHandler(priority=-64)
	public void onLog(PreLoginEvent event) {
		String name = event.getConnection().getName();

		if(edo && name.length() >= blockLengh) {
			event.setCancelled(true);
			blocked++;
			return;
		}

		if(name.length() == blockLengh) {
			if(enabled || spam) {
				event.setCancelled(true);
				blocked++;
				return;
			}

			if(!bots.contains(name)) {
				bots.add(name);
			}

			if(bots.size() > 100) {
				spam = true;
				Message.sendMessage(Color.translate("&cAntiBot has ben automatically &aenabled"), "secondlife.op");
				bots.clear();
			}
		}
	}

	@EventHandler(priority=64)
	public void onPreLogin(PreLoginEvent event) {
		if(event.isCancelled()) return;

		String name = event.getConnection().getName();

		if(StringUtils.isPremium(name)) {
			event.getConnection().setOnlineMode(true);
		}

		PluginMessageHandler.sendPremiumInfo(event.getConnection().getName(), event.getConnection().isOnlineMode() ? true : false);
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		if(bridge.contains(event.getPlayer().getUniqueId())) bridge.remove(event.getPlayer().getUniqueId());

		// TODO remove
		//bridge.add(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority=64)
	public void onServerConnect(ServerConnectEvent event) {
		if(event.isCancelled()) return;

		PluginMessageHandler.sendPremiumInfo(event.getPlayer().getName(), event.getPlayer().getPendingConnection().isOnlineMode() ? true : false);
	}

	public void resetSpam() {
		ProxyServer.getInstance().getScheduler().schedule(Bungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(spam) {
					ProxyServer.getInstance().getConsole().sendMessage("!!! BLOCKED " + blocked + " CONNECTIONS !!!");
					ProxyServer.getInstance().getConsole().sendMessage("!!! BLOCKED " + blocked + " CONNECTIONS !!!");
					ProxyServer.getInstance().getConsole().sendMessage("!!! BLOCKED " + blocked + " CONNECTIONS !!!");
					blocked = 0;
					spam = false;
					Message.sendMessage(Color.translate("&cAntiBot has ben automatically &cdisabled"), "secondlife.op");
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	public void resetFully() {
		ProxyServer.getInstance().getScheduler().schedule(Bungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				enabled = false;
			}
		}, 5, 5, TimeUnit.MINUTES);
	}

	@EventHandler
	public void onPlayerJoin(ServerSwitchEvent event) {
		ProxiedPlayer player = event.getPlayer();

		if(!player.hasPermission("secondlife.staff")) return;

		ServerInfo server = player.getServer().getInfo();

		if(server.getName().equalsIgnoreCase("UHC-1") || server.getName().equalsIgnoreCase("UHC-2")) {
			player.connect(server);
		}

		Message.sendMessage("&9[Staff] &f" + player.getName() + " &bhas joined &f" + server.getName() + "&b.", "secondlife.staff");
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ProxiedPlayer player = event.getPlayer();

		if(!player.hasPermission("secondlife.staff")) return;

		Message.sendMessage("&9[Staff] &f" + player.getName() + " &bhas left the server.", "secondlife.staff");
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		if(!(event.getSender() instanceof ProxiedPlayer)) return;

		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		ServerInfo server = player.getServer().getInfo();

		String message = event.getMessage().toLowerCase();

		if(!bridge.contains(player.getUniqueId()) && message.startsWith("/")) {
			if(message.startsWith("/l ")
					|| message.startsWith("/login")
					|| message.startsWith("/reg ")
					|| message.startsWith("/register")
					|| message.startsWith("/auth")
					|| message.startsWith("/securityregister")
					|| message.startsWith("/code")) return;
			player.sendMessage(Color.translate("&dPlease authenticate before using commands!"));
			event.setCancelled(true);
		}

		if(server.getName().equalsIgnoreCase("Hub")) {
			if(event.getMessage().equalsIgnoreCase("/HCF")
					|| event.getMessage().equalsIgnoreCase("/Factions")
					|| event.getMessage().equalsIgnoreCase("/UHC")
					|| event.getMessage().equalsIgnoreCase("/Minigames")
					|| event.getMessage().equalsIgnoreCase("/Practice")
					|| event.getMessage().equalsIgnoreCase("/KitMap")) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cPlease connect to that server trough hub! To go to the hub use /hub or /lobby"));
			}
		}
	}
}