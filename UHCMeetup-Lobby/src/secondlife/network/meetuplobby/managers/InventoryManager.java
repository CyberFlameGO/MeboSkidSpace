package secondlife.network.meetuplobby.managers;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.meetuplobby.utilties.Manager;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
public class InventoryManager extends Manager {

    private Inventory leaderboardInventory, gameInventory;

    public InventoryManager(MeetupLobby plugin) {
        super(plugin);

        leaderboardInventory = Bukkit.createInventory(null, 27, Color.translate("&eLeaderboards"));
        gameInventory = Bukkit.createInventory(null, 18, Color.translate("&eSelect a game"));

        handleSetup();
    }

    private void handleSetup() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::handleGame, 0L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::handleLeaderboard, 0L, 20L * 60 * 5);
    }

    private void handleLeaderboard() {
        leaderboardInventory.clear();

        leaderboardInventory.setItem(11, getTopKillsItem());
        leaderboardInventory.setItem(12, getTopStreakItem());
        leaderboardInventory.setItem(13, getTopWinsItem());
        leaderboardInventory.setItem(14, getTopPlayedItem());
        leaderboardInventory.setItem(15, getTopEloItem());

        for(int i = 0; i < leaderboardInventory.getSize(); i++) {
            if(leaderboardInventory.getItem(i) == null) {
                leaderboardInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(7).build());
            }
        }
    }

    public void handleGame() {
        gameInventory.clear();

        ServerData.getServers()
                .stream().sorted(Comparator.comparing(ServerData::getName))
                .forEach(server -> {
                    if(server.isOnline() && server.getName().contains("UHCMeetup-") && !server.getName().equals("UHCMeetup-Lobby")) {
                        ItemBuilder builder = new ItemBuilder(Material.GOLDEN_APPLE);
                        builder.name("&d" + server.getName());

                        List<String> lore = new ArrayList<>();
                        lore.add(Msg.BIG_LINE);
                        lore.add("&eState: &d" + server.getMotd());
                        lore.add("&eOnline: &d" + server.getOnlinePlayers());
                        lore.add("");
                        lore.add("&eClick to join!");
                        lore.add(Msg.BIG_LINE);

                        builder.lore(lore);

                        gameInventory.addItem(builder.build());
                    }
                });
    }

    private ItemStack getItem(Material material, String textName, String mongoValue) {
        List<Document> documents = (List<Document>) Vituz.getInstance().getDatabaseManager().getMeetupProfiles().find().limit(10).sort(new BasicDBObject(mongoValue, Integer.valueOf(-1))).into(new ArrayList());

        ItemBuilder builder = new ItemBuilder(material).name("&d" + textName + " &e| Top 10");
        int index = 1;

        List<String> tLore = new ArrayList<>();

        tLore.add(Msg.BIG_LINE);

        for(Document document : documents) {
            String name = document.getString("realName");
            if(name == null) name = document.getString("name");
            int value = document.getInteger(mongoValue);

            tLore.add("&e#" + index++ + ": &d" + name + " &e(" + value + ")");
        }

        tLore.add(Msg.BIG_LINE);
        builder.lore(tLore);

        return builder.build();
    }

    private ItemStack getTopKillsItem() {
        return getItem(Material.DIAMOND_SWORD, "Total Kills", "kills");
    }

    private ItemStack getTopWinsItem() {
        return getItem(Material.NETHER_STAR, "Wins", "wins");
    }

    private ItemStack getTopPlayedItem() {
        return getItem(Material.BOOK, "Played", "played");
    }

    private ItemStack getTopEloItem() {
        return getItem(Material.PAPER, "Elo", "elo");
    }

    private ItemStack getTopStreakItem() {
        return getItem(Material.BEACON, "Top Killstreak", "highest_killstreak");
    }
}
