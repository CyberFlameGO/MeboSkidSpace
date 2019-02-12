package secondlife.network.hcfactions.factions.type.games;

import java.util.Map;
import java.util.UUID;

public abstract class CapturableFaction extends EventFaction {

    public CapturableFaction(String name) {
        super(name);
    }

    public CapturableFaction(String name, UUID uuid) {
        super(name, uuid);
    }

    public CapturableFaction(Map<String, Object> map) {
        super(map);
    }
}
