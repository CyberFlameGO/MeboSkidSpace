package secondlife.network.victions.managers;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import secondlife.network.victions.Victions;
import secondlife.network.victions.utilities.Manager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 18.07.2018.
 */

@Getter
public class SellWandManager extends Manager {

    private Map<String, Integer> prices = new HashMap<>();

    public SellWandManager(Victions plugin) {
        super(plugin);

        handleSetup();
    }

    private void handleSetup() {
        ConfigurationSection section = plugin.getMainConfig().getConfigurationSection("prices");

        section.getKeys(false).forEach(type -> {
            int price = plugin.getMainConfig().getInt("prices." + type);

            prices.put(type, price);
        });
    }
}
