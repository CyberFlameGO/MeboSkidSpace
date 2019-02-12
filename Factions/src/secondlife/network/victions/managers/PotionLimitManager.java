package secondlife.network.victions.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.victions.Victions;
import secondlife.network.victions.utilities.Manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 18.07.2018.
 */

@Getter
public class PotionLimitManager extends Manager {

    private List<PotionLimit> potionLimits = new ArrayList<>();

    public PotionLimitManager(Victions plugin) {
        super(plugin);

        handleSetup();
    }

    private void handleSetup() {
        ConfigurationSection section = plugin.getMainConfig().getConfigurationSection("potion-limiter");

        section.getKeys(false).forEach(type -> {
            if(section.getInt(type + ".level") != -1) {
                PotionLimit potionLimit = new PotionLimit();

                potionLimit.setType(PotionEffectType.getByName(type));
                potionLimit.setLevel(section.getInt(type + ".level"));
                potionLimit.setExtended(section.getBoolean(type + ".extended"));

                potionLimits.add(potionLimit);
            }
        });
    }

    @Getter
    @Setter
    public class PotionLimit {
        private PotionEffectType type;
        private int level;
        private boolean extended;
    }
}
