package secondlife.network.meetupgame.managers;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.utilties.Manager;
import secondlife.network.vituz.utilties.inventory.InventoryUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
public class KitsManager extends Manager {

    private List<String> kits = new ArrayList<>();
    private int count = 0;

    public KitsManager(MeetupGame plugin) {
        super(plugin);

        handleSetKits();
    }

    private void handleSetKits() {
        try {
            plugin.getKits().load(plugin.getKits().getFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        plugin.getKits().getKeys(false).forEach(kit -> {
            if(plugin.getKits().contains(kit + ".inventory") && (plugin.getKits().contains(kit + ".armor"))) {
                kits.add(kit);
            }
        });
    }

    public void handleGiveKit(Player player) {
        if(count == 20) {
            count = 1;
        }

        try {
            String items = plugin.getKits().getString(kits.get(count) + ".inventory");
            String armor = plugin.getKits().getString(kits.get(count) + ".armor");

            player.getInventory().setContents(InventoryUtils.fromBase64(items).getContents());
            player.getInventory().setArmorContents(InventoryUtils.itemStackArrayFromBase64(armor));

            player.updateInventory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        count = count + 1;
    }
}
