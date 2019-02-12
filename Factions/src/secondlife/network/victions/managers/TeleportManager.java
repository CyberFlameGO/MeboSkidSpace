package secondlife.network.victions.managers;

import lombok.Getter;
import secondlife.network.victions.Victions;
import secondlife.network.victions.utilities.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Marko on 28.07.2018.
 */

@Getter
public class TeleportManager extends Manager {

    private Map<UUID, UUID> tpaUsers = new HashMap<>();
    private Map<UUID, UUID> tpaHereUsers = new HashMap<>();

    public TeleportManager(Victions plugin) {
        super(plugin);
    }
}
