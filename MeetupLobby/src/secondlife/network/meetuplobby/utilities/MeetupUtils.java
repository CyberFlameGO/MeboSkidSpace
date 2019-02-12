package secondlife.network.meetuplobby.utilities;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Marko on 10.06.2018.
 */
public class MeetupUtils {

    public static void resetPlayer(Player player) {
        player.setCanPickupItems(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);        player.getInventory().setHeldItemSlot(3);
        player.setGameMode(GameMode.ADVENTURE);
    }
}
