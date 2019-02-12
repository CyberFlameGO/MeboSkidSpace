package secondlife.network.info;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import secondlife.network.info.handlers.DataSubscriptionHandler;
import secondlife.network.info.handlers.ServerSubscriptionHandler;
import secondlife.network.info.jedis.JedisPublisher;
import secondlife.network.info.jedis.JedisSettings;
import secondlife.network.info.jedis.JedisSubscriber;
import secondlife.network.info.thread.UpdateThread;
import secondlife.network.vituz.VituzAPI;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
public class InfoPlugin extends JavaPlugin {

    @Getter
    private static InfoPlugin instance;

    private final String dedihost = "137.74.4.87";

    private JedisSettings settings;
    private JedisPublisher publisher;
    private JedisSubscriber subscriber;
    private JedisSubscriber firstSubscriber;
    private JedisPublisher firstPublisher;
    private MeetupServer meetupServer;

    @Override
    public void onEnable() {
        instance = this;

        loadStatus();
    }

    private void loadStatus() {
        meetupServer = new MeetupServer(VituzAPI.getServerName(), "None", 0, 0, 0, 0);

        settings = new JedisSettings(dedihost, 6379, getConfig().getString("DATABASE.AUTHENTICATION.PASSWORD"));

        firstSubscriber = new JedisSubscriber(JedisSubscriber.INDEPENDENT, settings, new ServerSubscriptionHandler());
        firstPublisher = new JedisPublisher(settings);
        firstPublisher.start();

        subscriber = new JedisSubscriber(JedisSubscriber.BUKKIT, settings, new DataSubscriptionHandler());
        publisher = new JedisPublisher(settings);
        publisher.start();

        new UpdateThread().start();
    }
}
