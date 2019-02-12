package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.antibot.AntiBotUtils;
import secondlife.network.bungee.antibot.BotAttack;
import secondlife.network.bungee.antibot.BotBoth;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AntiBotHandler extends Handler implements Listener {
	
	public static String[] st;
	public static int i;
	public static File f;
	public static Configuration config;
	public static int limit;
	public static int startup_multiplier;
	public static int startup_time;
	public static List<ProxiedPlayer> ignore = new ArrayList<>();
	public static String declining;
	public static String accepting;

	public AntiBotHandler(Bungee plugin) {
		super(plugin);
		
		i = 0;
		limit = 3;
		BotBoth.timeout = 7;
		startup_multiplier = 10;
		startup_time = 30;
		declining = Color.translate("&4[AB-VISUAL_] &cDeclining connection of &f%PLAYER%");
		accepting = Color.translate("&2[AB-VISUAL_] &aAccepting connection of &f%PLAYER%");
		
		st = new String[limit + 2];
		
		ProxyServer.getInstance().getPluginManager().registerListener(this.getPlugin(), this);
	}

	@EventHandler
	public void onProxyPing(ProxyPingEvent event) {
		String ip = event.getConnection().getAddress().getAddress().getHostAddress();
		
		if(!BotBoth.pings.contains(ip)) BotBoth.pings.add(ip);
	}

	@EventHandler(priority = -64)
	public void onPreLogin(PreLoginEvent event) {
		if(BotBoth.joins > limit * startup_multiplier) {
			int ping = 0;
			int nevv = 0;
			int length = 0;
			
			boolean pinging = false;
			boolean nevv2 = false;
			
			List<String> list = new ArrayList<>();
			String[] st;
			
			for(int x = (st = AntiBotHandler.st).length, i = 0; i < x; ++i) {
				String s = st[i];
				
				if(s != null) {
					if(Boolean.parseBoolean(s.split(" ")[0])) ++ping;
					if(Boolean.parseBoolean(s.split(" ")[1])) ++nevv;
					
					
					list.add(s.split(" ")[2]);
				}
			}
			
			String type = BotBoth.getNickType(list);
			
			if(type.equals("null")) {
				length = BotBoth.getLength(list);
				
				if(length != 0) type = "length";
			}
			
			if(ping > limit) pinging = true;
			if(nevv > limit) nevv2 = true;
			
			BotBoth.attacks.add(new BotAttack(System.currentTimeMillis(), pinging, nevv2, type, length));
			BotBoth.joins = 0;
		}
		
		String name = event.getConnection().getName();
		String ip = event.getConnection().getAddress().getAddress().getHostAddress();
		
		for(BotAttack a : BotBoth.attacks) {
			if(!event.isCancelled() && a.handleLogin(true, name, ip)) {
				AntiBotUtils.sendMessage(declining.replace("%PLAYER%", name));
				
				event.setCancelled(true);
				return;
			}
		}
		
		++BotBoth.joins;
		++i;
		
		if(i > limit + 1) i = 0;
	
		String s2 = String.valueOf(String.valueOf(BotBoth.pingedServer(ip))) + " " + BotBoth.isNew(name) + " " + name;
		st[i] = s2;
		
		if(BotBoth.isFakeNickname(name)) {
			event.setCancelled(true);
			return;
		}
		
		event.registerIntent(this.getPlugin());
		
		this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {}
				
				for(BotAttack a : BotBoth.attacks) {
					if(a.handleLogin(false, name, ip)) {
						event.setCancelled(true);
						event.completeIntent(Bungee.getInstance());
						return;
					}
				}
				
				event.completeIntent(Bungee.getInstance());
			}
		});
		
		if(!BotBoth.attacks.isEmpty()) AntiBotUtils.sendMessage(accepting.replace("%PLAYER%", name));
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), new Runnable() {
			public void run() {
				try {
					Thread.sleep(30000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(event.getPlayer().isConnected()) BotBoth.addPlayer(event.getPlayer().getName());
			}
		});
	}
}
