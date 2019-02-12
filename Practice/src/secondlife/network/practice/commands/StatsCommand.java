package secondlife.network.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.utilties.ItemBuilder;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 23.06.2018.
 */
public class StatsCommand extends Command {

    private Practice plugin = Practice.getInstance();

    public StatsCommand() {
        super("stats");

        setUsage(CC.RED + "Usage: /stats <player>");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length == 0) {
            getStatsInventory(player, player);
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if(Msg.checkOffline(sender, args[0])) return false;

            getStatsInventory(player, target);
        }

        return false;
    }

    public Inventory getStatsInventory(Player player, Player target) {
        Inventory inventory = Bukkit.createInventory(null, 18, CC.PRIMARY + "Stats of " + target.getName());

        PracticeData data = PracticeData.getByName(target.getName());

        ItemBuilder otherBuilder = new ItemBuilder(Material.NETHER_STAR);

        otherBuilder.name("&dOther Statistics");

        List<String> otherLore = new ArrayList<>();

        otherLore.add(Msg.BIG_LINE);
        otherLore.add("&dPremium Matches:");
        otherLore.add(" &eElo: &d" + data.getPremiumElo());
        otherLore.add(" &eWins: &d" + data.getPremiumWins());
        otherLore.add(" &eLosses: &d" + data.getPremiumLosses());
        otherLore.add(" &ePlayed: &d" + data.getPremiumMatchesPlayed());
        otherLore.add("&dEvents:");
        otherLore.add(" &dOITC:");
        otherLore.add("   &eWins: &d" + data.getOitcEventWins());
        otherLore.add("   &eLosses: &d" + data.getOitcEventLosses());
        otherLore.add("   &eKills: &d" + data.getOitcEventKills());
        otherLore.add("   &eDeaths: &d" + data.getOitcEventDeaths());
        otherLore.add(" &dParkour:");
        otherLore.add("   &eWins: &d" + data.getParkourEventWins());
        otherLore.add("   &eLosses: &d" + data.getParkourEventLosses());
        otherLore.add(" &dSumo:");
        otherLore.add("   &eWins: &d" + data.getSumoEventWins());
        otherLore.add("   &eLosses: &d" + data.getSumoEventLosses());
        otherLore.add(Msg.BIG_LINE);

        otherBuilder.lore(otherLore);
        inventory.setItem(0, otherBuilder.build());

        for(Kit kit : plugin.getKitManager().getKits()) {
            ItemBuilder builder = new ItemBuilder(kit.getIcon().getType());

            builder.name("&d" + kit.getName());
            builder.durability(kit.getIcon().getDurability());

            List<String> lore = new ArrayList<>();

            String kitName = kit.getName();

            lore.add(Msg.BIG_LINE);
            lore.add("&eElo: &d" + data.getElo(kitName));
            lore.add("&eParty Elo: &d" + data.getPartyElo(kitName));
            lore.add("&eWins: &d" + data.getWins(kitName));
            lore.add("&eLosses: &d" + data.getLosses(kitName));
            lore.add(Msg.BIG_LINE);

            builder.lore(lore);

            inventory.addItem(builder.build());
        }

        player.openInventory(inventory);

        return inventory;
    }
}
