package secondlife.network.hub.managers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hub.Hub;
import secondlife.network.hub.utilties.HubUtils;
import secondlife.network.hub.utilties.Manager;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Marko on 28.03.2018.
 */

@Getter
public class SelectorManager extends Manager {

    private Inventory serverSelector;
    private Inventory uhcSelector;

    private String selectorName;
    private String selectorUhcName;

    public SelectorManager(Hub plugin) {
        super(plugin);

        selectorName = Color.translate("&dChoose a server to play...");
        selectorUhcName = Color.translate("&dChoose a uhc to play...");

        serverSelector = Bukkit.createInventory(null, 45, selectorName);
        uhcSelector = Bukkit.createInventory(null, 9, selectorName);

        handleInventory();
    }

    private void handleInventory() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            //serverSelector.setItem(11, handleUpdate(Material.GOLDEN_APPLE, "UHC", 0));
            uhcSelector.setItem(2, handleUpdate(Material.GOLDEN_APPLE, "UHC-1", 0));
            uhcSelector.setItem(6, handleUpdate(Material.GOLDEN_APPLE, "UHC-2", 0));

            serverSelector.setItem(11, handleUHC());
            serverSelector.setItem(15, handleUpdate(Material.DIAMOND_SWORD, "KitMap", 0));
            serverSelector.setItem(29, handleUpdate(Material.LAVA_BUCKET, "UHCMeetup", 0));
            serverSelector.setItem(33, handleUpdate(Material.TNT, "Factions", 0));
        }, 20L, 20L);

        IntStream.range(0, serverSelector.getSize()).forEach(i -> {
            if(serverSelector.getItem(i) == null) {
                serverSelector.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&f ").durability(7).build());
            }
        });

        IntStream.range(0, uhcSelector.getSize()).forEach(i -> {
            if(uhcSelector.getItem(i) == null) {
                uhcSelector.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&f ").durability(7).build());
            }
        });
    }

    private static ItemStack handleUHC() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.GOLDEN_APPLE);

        itemBuilder.name("&a&lUHC Selector");

        int uhc1 = ServerData.getByName("UHC-1") != null ? ServerData.getByName("UHC-1").getOnlinePlayers() : 0;
        int uhc2 = ServerData.getByName("UHC-2") != null ? ServerData.getByName("UHC-2").getOnlinePlayers() : 0;
        int uhcGlobal = uhc1 + uhc2;


        // UHC-1 12 16 20 24

        // UHC-2 14 18 22

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&fTermins");
        lore.add(" &fUHC-1: &a12:00, 16:00, 20:00, 00:00");
        lore.add(" &fUHC-2: &a14:00, 18:00, 22:00");
        lore.add("");
        lore.add("&fUHC-1: &a" + uhc1);
        lore.add("&fUHC-2: &a" + uhc2);
        lore.add("");
        lore.add("&fUHC-Global: &a" + uhcGlobal);
        lore.add("");

        itemBuilder.lore(lore);

        return itemBuilder.build();
    }

    private static ItemStack handleUpdate(Material material, String name, int durabillity) {
        ServerData data = ServerData.getByName(name.equals("UHCMeetup") ? "UHCMeetup-Lobby" : name);

        ItemBuilder itemBuilder = new ItemBuilder(material);
        List<String> lore = new ArrayList<>();

        if(data != null) {
            if(data.isOnline() && !data.isWhitelisted()) {
                itemBuilder.name("&a&l" + name);
            } else if(data.isOnline() && data.isWhitelisted()) {
                itemBuilder.type(Material.PAPER);
                itemBuilder.name("&e&l" + name);
            } else {
                itemBuilder.type(Material.REDSTONE_BLOCK);
                itemBuilder.name("&c&l" + name);
            }

            lore.add("");

            if(data.isOnline()) {
                if(data.isWhitelisted()) {
                    lore.add("&fPlayers: &e&l" + (name.equals("UHCMeetup") ? HubUtils.getMeetupCount() : data.getOnlinePlayers()) + "/" + data.getMaximumPlayers());
                    lore.add("&fStatus: " + data.getTranslatedStatus());
                    lore.add("");
                    lore.add("&fThis server is whitelisted!");
                } else {
                    lore.add("&fPlayers: &a&l" + (name.equals("UHCMeetup") ? HubUtils.getMeetupCount() : data.getOnlinePlayers()) + "/" + data.getMaximumPlayers());
                    lore.add("&fStatus: " + data.getTranslatedStatus());
                    lore.add("");
                    lore.add("&fClick to join queue!");
                }
            } else {
                lore.add("");
                lore.add("&cThis server is offline.");
                lore.add("&cFor information about");
                lore.add("&cthe server can be found");
                lore.add("&cat &lforum.secondlife.network");
            }

            lore.add("");
        } else {
            itemBuilder.type(Material.REDSTONE_BLOCK);
            itemBuilder.name("&c&l" + name);

            lore.add("");
            lore.add("&cThis server is offline.");
            lore.add("&cFor information about");
            lore.add("&cthe server can be found");
            lore.add("&cat &lforum.secondlife.network");
            lore.add("");
        }

        itemBuilder.lore(lore);
        itemBuilder.durability(durabillity);

        return itemBuilder.build();
    }
}
