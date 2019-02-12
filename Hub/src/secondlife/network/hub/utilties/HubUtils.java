package secondlife.network.hub.utilties;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hub.Hub;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.item.ItemBuilder;

/**
 * Created by Marko on 28.03.2018.
 */
public class HubUtils {

    public static String loading = "Loading";

    public static void resetPlayer(Player player) {
        player.setCanPickupItems(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        player.getInventory().setHeldItemSlot(4);
        player.setGameMode(GameMode.ADVENTURE);

        player.getInventory().setItem(1, new ItemBuilder(Material.ENDER_PEARL).name("&b&lPearl Rider").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).name("&2&lServer Selector").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.INK_SACK).durability(8).name("&7Hide Players").build());
    }

    public static int getMeetupCount() {
        ServerData data1 = ServerData.getByName("UHCMeetup-1");
        ServerData data2 = ServerData.getByName("UHCMeetup-2");
        ServerData data3 = ServerData.getByName("UHCMeetup-3");
        ServerData data4 = ServerData.getByName("UHCMeetup-4");
        ServerData data5 = ServerData.getByName("UHCMeetup-5");
        ServerData data6 = ServerData.getByName("UHCMeetup-6");
        ServerData dataLobby = ServerData.getByName("UHCMeetup-Lobby");

        int count1 = data1 != null ? data1.getOnlinePlayers() : 0;
        int count2 = data2 != null ? data2.getOnlinePlayers() : 0;
        int count3 = data3 != null ? data3.getOnlinePlayers() : 0;
        int count4 = data4 != null ? data4.getOnlinePlayers() : 0;
        int count5 = data5 != null ? data5.getOnlinePlayers() : 0;
        int count6 = data6 != null ? data6.getOnlinePlayers() : 0;
        int count7 = dataLobby != null ? dataLobby.getOnlinePlayers() : 0;

        return count1 + count2 + count3 + count4 + count5 + count6 + count7;
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
        }.runTaskTimerAsynchronously(Hub.getInstance(), 20L, 20L);
    }

    public static int getPriority(Player player) {
        if(VituzAPI.getRankName(player.getName()).equalsIgnoreCase("TrialMod")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Mod")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Mod+")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("SeniorMod")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Admin")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("SeniorAdmin")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("PlatformAdmin")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Owner")) {
            return 0;
        } else if(VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Xenon")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Partner")
                || VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Media")) {
            return 1;
        } else if(VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Krypton")) {
            return 2;
        } else if(VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Titanium")) {
            return 3;
        } else if(VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Nitrogen")) {
            return 4;
        } else if(VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Hydrogen")) {
            return 5;
        }

        return 6;
    }
}
