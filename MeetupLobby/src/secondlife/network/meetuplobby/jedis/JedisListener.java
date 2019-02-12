package secondlife.network.meetuplobby.jedis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

import java.util.List;

public abstract class JedisListener {

    public abstract void respond(String channel, JsonObject data);

    private final JedisController controller;
    private final String channel;

    protected JedisListener(JedisController controller, String channel) {
        this.controller = controller;
        this.channel = channel;

        listen();
    }

    private void listen() {
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    Jedis jedis = null;
                    try {
                        jedis = controller.getPool().getResource();

                        if (controller.hasPassword()) {
                            jedis.auth(controller.getPassword());
                        }

                        List<String> messages = jedis.blpop(0, channel);
                        respond(messages.get(0), new JsonParser().parse(messages.get(1)).getAsJsonObject());
                    } finally {
                        if (jedis != null) {
                            jedis.close();
                        }
                    }
                }
            }
        }.start();
    }
}
