package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Color;
import secondlife.network.bungee.utils.Handler;
import secondlife.network.bungee.utils.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marko on 09.04.2018.
 */
public class AutoMessageHandler extends Handler implements Listener {

    public static ArrayList<String> msglist = new ArrayList<>();
    public static Random random = new Random();

    public AutoMessageHandler(Bungee plugin) {
        super(plugin);

        setup();

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    public void setup() {
        List<String> messages = Bungee.configuration.getStringList("auto_message.active_messages");

        for(String msg : messages) {
            String message = Bungee.configuration.getString("auto_message.messages." + msg + ".message");

            msglist.add(Color.translate("&8[&5&l" + '✇' + "&8] &d" + message));
        }

        ProxyServer.getInstance().getScheduler().schedule(Bungee.getInstance(), () -> {
            Message.sendMessage("");
            Message.sendMessage(Color.translate(msglist.get(Math.abs(random.nextInt() % msglist.size()))));
            Message.sendMessage("");
        },2, 2, TimeUnit.MINUTES);
    }
}
