package secondlife.network.hub.utilties.profile;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

public class BukkitProfileStorage {
    
    private Map<String, PlayerData> realplayers = new HashMap<>();
    
    public BukkitProfileStorage() {
        BukkitProfileUtils.getPlayers().forEach(player -> {
            if(player.getName() != null) {
                if(!hasRealPlayer(player)) {
                    addRealPlayer(player);
                } else {
                    if(player.getLastPlayed() >= realplayers.get(player.getName().toLowerCase()).getLastPlayed()) {
                        addRealPlayer(player);
                    }
                }
            }
        });

        Bukkit.getOnlinePlayers().forEach(this::addRealPlayer);
    }
    
    public boolean hasRealPlayer(OfflinePlayer player) {
        return realplayers.containsKey(player.getName().toLowerCase());
    }
    
    public void addRealPlayer(OfflinePlayer player) {
        realplayers.put(player.getName().toLowerCase(), new PlayerData(player));
    }
    
    public boolean isPlayerReal(OfflinePlayer player) {
        if(hasRealPlayer(player)) {
            PlayerData playerdata = realplayers.get(player.getName().toLowerCase());
            return playerdata.getName().equals(player.getName());
        }

        return false;
    }
    
    public String getRealPlayerValidName(OfflinePlayer player) {
        return realplayers.get(player.getName().toLowerCase()).getName();
    }

    @Getter
    private class PlayerData {
        private String name;
        private long lastPlayed;

        PlayerData(OfflinePlayer player) {
            name = player.getName();
            lastPlayed = player.getLastPlayed();
        }
    }
}
