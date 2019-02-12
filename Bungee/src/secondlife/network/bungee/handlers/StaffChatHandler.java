package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffChatHandler extends Handler implements Listener {

    public static List<UUID> staff = new ArrayList<>();
	
	public StaffChatHandler(Bungee plugin) {
		super(plugin);

		ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		
		if(staff.contains(player.getUniqueId())) {
			staff.remove(player.getUniqueId());
		}	
	}
	
	@EventHandler
	public void onChat(ChatEvent event) {
		if(event.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) event.getSender();
			ServerInfo server = player.getServer().getInfo();
			
			if(event.getMessage().startsWith("/")) return;
			
			if(staff.contains(player.getUniqueId())) {
				if(player.hasPermission("secondlife.staff")) {
					for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
						if(online.hasPermission("secondlife.staff")) {
							if(!SilentHandler.silent.contains(online.getUniqueId())) {
								online.sendMessage(Color.translate("&5[Staff Chat] &7[" + server.getName() + "] &d" + player.getName() + "&7: &f" + event.getMessage()));
								//online.sendMessage(Color.translate("&8(&dStaff Chat&8) &8(&d" + server.getName() + "&8) &d" + player.getName() + "&7: &f" + event.getMessage()));
								event.setCancelled(true);
							}
						}
					}
				} else {
					staff.remove(player.getUniqueId());
				}
			}
		}
	}
}