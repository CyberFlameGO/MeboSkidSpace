package secondlife.network.practice.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.handlers.DatabaseHandler;
import secondlife.network.vituz.utilties.ItemBuilder;
import secondlife.network.vituz.utilties.Msg;

import java.util.*;

/**
 * Created by joeleoli on 22.06.2018.
 * gang
 */

@Getter
public class Leaderboards implements Runnable {

    private Map<Kit, LeaderboardEntry[]> leaderboards = new HashMap<>();
    private Inventory leaderboardInventory;

    private Comparator<LeaderboardEntry> TOP_ELO = Comparator.comparingInt(LeaderboardEntry::getElo).reversed();

    public Leaderboards() {
        leaderboardInventory = Bukkit.createInventory(null, 18, CC.PRIMARY + "Leaderboards");

        Collection<Kit> kits = Practice.getInstance().getKitManager().getKits();

        for (Kit kit : kits) {
            if (kit.isRanked()) {
                this.leaderboards.put(kit, new LeaderboardEntry[10]);
            }
        }
    }

    @Override
    public void run() {
        Msg.logConsole("&e&lLeaderboard update STARTED!");

        FindIterable<Document> fetched = DatabaseHandler.practiceProfiles.find();
        Iterator<Document> iterator = fetched.iterator();
        Map<Kit, List<LeaderboardEntry>> leaderboardsEntries = new HashMap<>();
        Map<Kit, LeaderboardEntry[]> toReplace = new HashMap<>();

        Practice.getInstance().getKitManager().getKits().forEach(kit -> {
            if (kit.isRanked()) {
                leaderboardsEntries.put(kit, new ArrayList<>());
            }
        });

        while (iterator.hasNext()) {
            Document current = iterator.next();

            if (current.containsKey("player_elo")) {

                JsonArray eloArray = new JsonParser().parse(current.getString("player_elo")).getAsJsonArray();

                Map<String, Integer> eloMap = new HashMap<>();

                eloArray.forEach(eloElement -> {
                    JsonObject eloObject = eloElement.getAsJsonObject();

                    if (!eloObject.has("kit_name")) {
                        return;
                    }

                    String kitName = eloObject.get("kit_name").getAsString();
                    eloMap.put(kitName, eloObject.get("kit_elo").getAsInt());
                });

                String name = current.getString("realName");
                if(name == null) name = current.getString("name");

                for(Kit kit: Practice.getInstance().getKitManager().getKits()){
                    if(kit.isEnabled() && kit.isRanked()){
                        int elo = eloMap.getOrDefault(kit.getName(), PracticeData.DEFAULT_ELO);
                        leaderboardsEntries.computeIfAbsent(kit, k -> new ArrayList<>()).add(new LeaderboardEntry(name, elo));
                    }
                }
            }
        }

        leaderboardsEntries.forEach((kit, entryList) -> {
            entryList.sort(TOP_ELO);
            int size = Math.min(entryList.size(), 10);
            List<LeaderboardEntry> publish = entryList.subList(0, size);
            toReplace.put(kit, publish.toArray(new LeaderboardEntry[size]));
        });

        this.leaderboards = toReplace;

        leaderboardInventory.clear();

        List<Document> documents = (List<Document>) DatabaseHandler.practiceProfiles.find().limit(10).sort(new BasicDBObject("premium_elo", Integer.valueOf(-1))).into(new ArrayList());

        ItemBuilder topPremium = new ItemBuilder(Material.NETHER_STAR).name("&dPremium Elo &e| Top 10");
        int index = 1;

        List<String> tLore = new ArrayList<>();

        tLore.add(Msg.BIG_LINE);

        for(Document document : documents) {
            String name = document.getString("realName");
            if(name == null) name = document.getString("name");
            int premiumElo = document.getInteger("premium_elo").intValue();

            tLore.add("&e#" + index++ + ": &d" + name + " &e(" + premiumElo + ")");
        }

        tLore.add(Msg.BIG_LINE);
        topPremium.lore(tLore);

        leaderboardInventory.addItem(topPremium.build());

        for (Map.Entry<Kit, LeaderboardEntry[]> entry : this.leaderboards.entrySet()) {
            ItemBuilder builder = new ItemBuilder(entry.getKey().getIcon().getType());
            builder.name("&d" + entry.getKey().getName() + " &e| Top 10");

            List<String> lore = new ArrayList<>();

            lore.add(Msg.BIG_LINE);

            if(entry.getValue().length == 0) {
                lore.add("&eNone");
            } else {
                int count = 1;

                for (LeaderboardEntry leaderboardEntry : entry.getValue()) {
                    lore.add("&e#" + count++ + ": &d" + leaderboardEntry.getName() + " &e(" + leaderboardEntry.getElo() + ")");
                }
            }

            lore.add(Msg.BIG_LINE);

            builder.durability(entry.getKey().getIcon().getDurability());
            builder.lore(lore);

            leaderboardInventory.addItem(builder.build());
        }

        Msg.logConsole("&a&lFinished updating leaderboard!");
    }
}
