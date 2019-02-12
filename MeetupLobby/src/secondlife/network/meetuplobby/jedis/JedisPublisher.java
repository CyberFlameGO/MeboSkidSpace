package secondlife.network.meetuplobby.jedis;

import redis.clients.jedis.Jedis;

public class JedisPublisher {

    private final JedisController controller;
    private final String channel;
    private final boolean urgent;

    public JedisPublisher(JedisController controller, String channel, boolean urgent) {
        this.controller = controller;
        this.channel = channel;
        this.urgent = urgent;
    }

    public JedisPublisher(JedisController controller, String channel) {
        this(controller, channel, false);
    }

    public void write(String message) {
        Jedis jedis = null;
        try {
            jedis = controller.getPool().getResource();

            if (controller.hasPassword()) {
                jedis.auth(controller.getPassword());
            }

            if (urgent) {
                jedis.rpush(channel, message);
            } else {
                jedis.publish(channel, message);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
