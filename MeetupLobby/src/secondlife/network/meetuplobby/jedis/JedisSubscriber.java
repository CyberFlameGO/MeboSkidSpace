package secondlife.network.meetuplobby.jedis;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public abstract class JedisSubscriber {

    public abstract JedisPubSub getConnection();

    private final JedisController controller;
    private final String channel;
    private final Jedis jedis;
    @Getter private JedisPubSub savedConnection;

    protected JedisSubscriber(JedisController controller, String channel) {
        this.controller = controller;
        this.channel = channel;
        this.jedis = new Jedis(controller.getAddress(), controller.getPort());

        authenticate();
        connect();
    }

    public void close() {
        if (savedConnection != null) {
            savedConnection.unsubscribe();
        }
    }

    private void authenticate() {
        if (controller.hasPassword()) {
            jedis.auth(controller.getPassword());
        }
    }

    private void connect() {
        savedConnection = getConnection();
        new Thread() {
            @Override
            public void run() {
                if (savedConnection != null) {
                    jedis.subscribe(savedConnection, channel);
                }
            }
        }.start();
    }
}
