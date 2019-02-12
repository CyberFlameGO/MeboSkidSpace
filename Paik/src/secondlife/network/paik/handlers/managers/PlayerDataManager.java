package secondlife.network.paik.handlers.managers;

import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager extends Handler {
    
    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerDataManager(Paik plugin) {
        super(plugin);
    }

    public void addPlayerData(Player player) {
        this.playerDataMap.put(player.getUniqueId(), new PlayerData(this.plugin));
    }
    
    public void removePlayerData(Player player) {
        this.playerDataMap.remove(player.getUniqueId());
    }

    public boolean hasPlayerData(Player player) {
        return this.playerDataMap.containsKey(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId());
    }
}
