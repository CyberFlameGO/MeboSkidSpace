package secondlife.network.paik;

import org.bukkit.entity.Player;
import secondlife.network.paik.client.EnumClientType;
import secondlife.network.paik.handlers.data.PlayerData;

public class PaikAPI {
    
    public static boolean isCheatBreaker(Player player) {
        PlayerData playerData = Paik.getInstance().getPlayerDataManager().getPlayerData(player);
        return playerData != null && playerData.getClient() == EnumClientType.CHEAT_BREAKER;
    }
    
    public static int getPing(Player player) {
        PlayerData playerData = Paik.getInstance().getPlayerDataManager().getPlayerData(player);

        if(playerData != null) return (int) playerData.getPing();

        return 0;
    }
}
