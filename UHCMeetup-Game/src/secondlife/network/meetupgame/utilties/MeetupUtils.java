package secondlife.network.meetupgame.utilties;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.states.GameState;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Marko on 23.07.2018.
 */
public class MeetupUtils {

    public static boolean deleteFile(File file) {
        if(file.isDirectory()) {
            for(File subfile : file.listFiles()) {
                if(!deleteFile(subfile)) {
                    return false;
                }
            }
        }

        return file.delete();
    }

    public static int getNextBorder() {
        int border = GameManager.getGameData().getBorder();

        if(border == 150) {
            border = 100;
        } else if(border == 100) {
            border = 50;
        } else if(border == 50) {
            border = 25;
        } else if(border == 25) {
            border = 10;
        }

        return border;
    }

    public static void deleteWorld() {
        World world = Bukkit.getWorld("world");

        if(world != null) {
            Bukkit.getServer().unloadWorld(world, false);

            deleteFile(world.getWorldFolder());
        }
    }

    public static List<Location> getSphere(Location centerBlock, int radius, boolean hollow) {
        List<Location> circleBlocks = new ArrayList<Location>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {

                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {

                        Location l = new Location(centerBlock.getWorld(), x, y, z);

                        circleBlocks.add(l);

                    }

                }
            }
        }

        return circleBlocks;
    }

    public static void loadSpectatorInventory(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS).name("&aAlive Players").build());
        player.getInventory().setItem(1, new ItemBuilder(Material.WATCH).name("&bRandom Teleport").build());
        player.getInventory().setItem(8, new ItemBuilder(Material.REDSTONE).name("&cGo to lobby").build());

        if(player.hasPermission(Permission.STAFF_PERMISSION)) {
            player.getInventory().setItem(4, new ItemBuilder(Material.PACKED_ICE).name("&dFreeze Player").build());
        }

        player.updateInventory();
    }

    public static void loadVoteInventory(Player player) {
        player.getInventory().setItem(0, new ItemBuilder(Material.BOOK).name("&aVote for scenarios").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.REDSTONE).name("&cGo to lobby").build());
        player.getInventory().setItem(8, new ItemBuilder(Material.WATCH).name("&3Edit Settings").build());

        player.updateInventory();
    }

    public static void setMotd(String motd) {
        (((CraftServer) Bukkit.getServer()).getHandle().getServer()).setMotd(Color.translate(motd));
    }

    public static ItemStack getGoldenHead() {
        return new ItemBuilder(Material.GOLDEN_APPLE).data(1).name("&6Golden Head").build();
    }

    public static boolean isPlayerInSpecMode(Player player) {
        return MeetupGame.getInstance().getSpectatorManager().getSpectators().containsKey(player.getUniqueId());
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
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        player.updateInventory();
    }

    public static Location getScatterLocation() {
        Random r = new Random();

        int x = r.nextInt(150 * 2) - 150;
        int z = r.nextInt(150 * 2) - 150;

        return new Location(Bukkit.getWorld("world"), x, (Bukkit.getWorld("world").getHighestBlockYAt(x, z) + 1), z);
    }

    public static boolean isState() {
        GameData data = GameManager.getGameData();

        if(!data.getGameState().equals(GameState.PLAYING)) {
            return true;
        }

        return false;
    }

    public static void announce(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Meetup");
            out.writeUTF(player.getName());
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(MeetupGame.getInstance(), "Announce", b.toByteArray());
    }
}
