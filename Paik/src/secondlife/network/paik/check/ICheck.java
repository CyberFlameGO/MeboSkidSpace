package secondlife.network.paik.check;

import org.bukkit.entity.Player;

public interface ICheck<T> {
    
    void handleCheck(Player player, T packet);
    
    Class<? extends T> getType();
}
