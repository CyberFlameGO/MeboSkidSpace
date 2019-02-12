package secondlife.network.meetuplobby.utilties;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.vituz.Vituz;

/**
 * Created by Marko on 23.07.2018.
 */
public class MeetupUtils {

    public static String loading = "Loading";

    public static int getStats(String mongoValue, String name) {
        Document document = (Document) Vituz.getInstance().getDatabaseManager()
                .getMeetupProfiles().find(Filters.eq("name", name.toLowerCase())).first();

        if(document != null && document.containsKey(mongoValue)) {
            return document.getInteger(mongoValue);
        }

        return 0;
    }

    public static void setStats(String mongoValue, String name, int value) {
        Document document = (Document) Vituz.getInstance().getDatabaseManager()
                .getMeetupProfiles().find(Filters.eq("name", name.toLowerCase())).first();

        if(document != null && document.containsKey(mongoValue)) {
            document.put(mongoValue, value);

            Vituz.getInstance().getDatabaseManager().getMeetupProfiles().replaceOne(Filters.eq("name", name.toLowerCase()), document, new UpdateOptions().upsert(true));
        }
    }

    public static void clearPlayer(Player player) {
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.closeInventory();
        player.setGameMode(GameMode.ADVENTURE);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        player.updateInventory();
    }

    public static void setupLoading() {
        new BukkitRunnable() {
            public void run() {
                if(loading == "Loading") {
                    loading = "Loading.";
                } else if(loading == "Loading.") {
                    loading = "Loading..";
                } else if(loading == "Loading..") {
                    loading = "Loading...";
                } else if(loading == "Loading...") {
                    loading = "Loading";
                }
            }
        }.runTaskTimerAsynchronously(MeetupLobby.getInstance(), 20L, 20L);
    }
}
