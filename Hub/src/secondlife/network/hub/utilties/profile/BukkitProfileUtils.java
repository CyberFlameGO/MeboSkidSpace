package secondlife.network.hub.utilties.profile;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.LinkedList;
import java.util.UUID;

public class BukkitProfileUtils {

    @Getter
    @Setter
    public static boolean enabled = true;

    public static LinkedList<OfflinePlayer> getPlayers() {
        LinkedList<OfflinePlayer> players = new LinkedList<>();

        for(File file : getByPlayersDataFolder().listFiles()) {
            if(file.getName().endsWith(".dat")) {
                String uuidstring = file.getName().substring(0, file.getName().length() - 4);

                try {
                    players.add(Bukkit.getOfflinePlayer(UUID.fromString(uuidstring)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return players;
    }
    
    public static File getByPlayerFile(OfflinePlayer player) {
        return new File(getByPlayersDataFolder(), player.getUniqueId().toString() + ".dat");
    }
    
    private static File getByPlayersDataFolder() {
        return new File(Bukkit.getWorld("world").getWorldFolder(), "playerdata");
    }
}
