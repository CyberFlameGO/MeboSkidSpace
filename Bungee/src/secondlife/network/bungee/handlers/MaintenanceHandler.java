package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;

/**
 * Created by Marko on 09.04.2018.
 */
public class MaintenanceHandler extends Handler implements Listener {

    public MaintenanceHandler(Bungee plugin) {
        super(plugin);

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        if(!Bungee.configuration.getBoolean("whitelisted")) return;

        ServerPing response = event.getResponse();
        ServerPing.Protocol protocol = new ServerPing.Protocol("Maintenance", response.getVersion().getProtocol() - 1);

        response.setVersion(protocol);
        event.setResponse(response);
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if(!Bungee.configuration.getBoolean("whitelisted")) return;

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(event.getConnection().getUniqueId());

        if(player == null) return;
        if(player.hasPermission("secondlife.op")) return;

        event.setCancelReason(Color.translate("&cThe network is currently undergoing maintenance, check back soon!"));
        event.setCancelled(true);
    }
}
