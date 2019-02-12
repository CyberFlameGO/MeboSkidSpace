package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;

/**
 * Created by Marko on 09.04.2018.
 */
public class MotdHandler extends Handler implements Listener {

    public MotdHandler(Bungee plugin) {
        super(plugin);

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = 64)
    public void onServerListPing(ProxyPingEvent event) {
        if(Bungee.configuration.getString("server_motd") == null) return;

        ServerPing ping = event.getResponse();

        String motd = Color.translate(Bungee.configuration.getString("server_motd"));
        motd = motd.replace("{nl}", "\n");

        ping.setDescription(motd);
        event.setResponse(ping);
    }
}
