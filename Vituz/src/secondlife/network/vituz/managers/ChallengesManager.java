package secondlife.network.vituz.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.ChallengeData;
import secondlife.network.vituz.utilties.ChallengeUtils;
import secondlife.network.vituz.utilties.Manager;
import secondlife.network.vituz.utilties.WoolUtil;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChallengesManager extends Manager {

    private  String[] weeks = {
            "Week 1",
            "Week 2",
            "Week 3",
            "Week 4",
    };

    private String[] colors = {
            "Purple", "Blue", "Light Gray",
            "Gray", "Pink", "Green",
            "Light Blue", "Orange",
            "Red", "Dark Red", "Yellow",
            "Dark Green", "Reset Color"
    };

    public ChallengesManager(Vituz plugin) {
        super(plugin);
    }

    public Inventory getInventory(Player player) {
        ChallengeData data = ChallengeData.getByName(player.getName());
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "" + ChatColor.BOLD + "Weeks");

        Stream.of(weeks).forEach(weeks -> {
            ChatColor color;

            if(weeks.contains("Week 1")) {
                color = ChatColor.GREEN;
            } else {
                color = ChatColor.GOLD;
            }

            inventory.addItem(new ItemBuilder(Material.WOOL)
                    .durability(WoolUtil.convertChatColorToWoolData(color)).name(color + weeks)
                    .lore(Arrays.asList(
                            "&7&m------------------------------",
                            color + "Click to see all weekly challenges.",
                            "&7&m------------------------------"))
                    .build());
        });

        IntStream.range(0, inventory.getSize()).forEach(i -> {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&5&lNone").durability(7).build());
            }
        });

        return inventory;
    }

    public Inventory getChallengeWeek1(Player player) {
        ChallengeData data = ChallengeData.getByName(player.getName());
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "" + ChatColor.BOLD + "Week 1");

        inventory.addItem(new ItemBuilder(Material.WOOL)
                .durability(data.isW1_1() ? WoolUtil.convertChatColorToWoolData(ChatColor.GREEN)
                        : WoolUtil.convertChatColorToWoolData(ChatColor.DARK_RED))
                .name(data.isW1_1() ? "&aChallenge 1 (Completed)"
                        : "&4Challenge 1")
                .lore(Arrays.asList(
                        "&7&m------------------------------",
                        "&dStay up till 4 AM.",
                        "&dReward: " + ChallengeUtils.getPoints(player, 10) + " points",
                        "&7&m------------------------------"))
                .build());

        // Practice -> MatchHandler 251
        inventory.addItem(new ItemBuilder(Material.WOOL)
                .durability(data.isW1_2() ? WoolUtil.convertChatColorToWoolData(ChatColor.GREEN)
                        : WoolUtil.convertChatColorToWoolData(ChatColor.DARK_RED))
                .name(data.isW1_2() ? "&aChallenge 2 (Completed)"
                        : "&4Challenge 2")
                .lore(Arrays.asList(
                        "&7&m------------------------------",
                        "&dPlay 150 ranked matches on Practice.",
                        "&dReward: " + ChallengeUtils.getPoints(player, 10) + " points",
                        "&7&m------------------------------"))
                .build());

        // Practice -> MatchHandler 251
        inventory.addItem(new ItemBuilder(Material.WOOL)
                .durability(data.isW1_3() ? WoolUtil.convertChatColorToWoolData(ChatColor.GREEN)
                        : WoolUtil.convertChatColorToWoolData(ChatColor.DARK_RED))
                .name(data.isW1_3() ? "&aChallenge 3 (Completed)"
                        : "&4Challenge 3")
                .lore(Arrays.asList(
                        "&7&m------------------------------",
                        "&dWin total of 60 ranked matches on Practice.",
                        "&dReward: " + ChallengeUtils.getPoints(player, 10) + " points",
                        "&7&m------------------------------"))
                .build());

        // UHC -> GameCommand 440
        inventory.addItem(new ItemBuilder(Material.WOOL)
                .durability(data.isW1_4() ? WoolUtil.convertChatColorToWoolData(ChatColor.GREEN)
                        : WoolUtil.convertChatColorToWoolData(ChatColor.DARK_RED))
                .name(data.isW1_4() ? "&aChallenge 4 (Completed)"
                        : "&4Challenge 4")
                .lore(Arrays.asList(
                        "&7&m------------------------------",
                        "&dPlay 10 UHC Games.",
                        "&dReward: " + ChallengeUtils.getPoints(player, 10) + " points",
                        "&7&m------------------------------"))
                .build());

        // UHC -> StatsHandler 181
        inventory.addItem(new ItemBuilder(Material.WOOL)
                .durability(data.isW1_5() ? WoolUtil.convertChatColorToWoolData(ChatColor.GREEN)
                        : WoolUtil.convertChatColorToWoolData(ChatColor.DARK_RED))
                .name(data.isW1_5() ? "&aChallenge 5 (Completed)"
                        : "&4Challenge 5")
                .lore(Arrays.asList(
                        "&7&m------------------------------",
                        "&dKill 5 players in a single UHC.",
                        "&dReward: " + ChallengeUtils.getPoints(player, 5) + " points",
                        "&7&m------------------------------"))
                .build());

        IntStream.range(0, inventory.getSize()).forEach(i -> {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&5&lNone").durability(7).build());
            }
        });

        return inventory;
    }
}
