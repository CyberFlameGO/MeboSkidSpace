package secondlife.network.meetuplobby.jedis;

import lombok.Getter;
import redis.clients.jedis.JedisPool;

public class JedisController {

    @Getter private final String address;
    @Getter private final String password;
    @Getter private final int port;
    @Getter private final JedisPool pool;

    public JedisController(String address, String password, int port) {
        this.address = address;
        this.password = password;
        this.port = port;
        this.pool = new JedisPool(address, port);
    }

    public JedisController(String address) {
        this(address, null, 6379);
    }

    public boolean hasPassword() {
        return password != null;
    }

}
