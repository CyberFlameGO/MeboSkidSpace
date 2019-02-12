package secondlife.network.vituz.status.jedis;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JedisQueue {

    @NonNull private String channel;
    @NonNull private JedisAction action;
    @NonNull private JsonObject data;

}
