package secondlife.network.victions.managers;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import secondlife.network.victions.Victions;
import secondlife.network.victions.utilities.Manager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 18.07.2018.
 */

@Getter
public class EntityLimiterManager extends Manager {

    private Map<EntityType, Boolean> disabledEntities = new HashMap<>();

    public EntityLimiterManager(Victions plugin) {
        super(plugin);

        handleSetup();
    }

    private void handleSetup() {
        ConfigurationSection section = plugin.getMainConfig().getConfigurationSection("potion-limiter");

        section.getKeys(false).forEach(type -> {
            EntityType entityType = EntityType.valueOf(type);
            boolean enabled = section.getBoolean(type);

            disabledEntities.put(entityType, enabled);
        });
    }
}
