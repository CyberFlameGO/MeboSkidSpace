package secondlife.network.hcfactions.factions.type.system;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;

import java.util.Map;

public class SpawnFaction extends ClaimableFaction implements ConfigurationSerializable {

    public SpawnFaction() {
        super("Spawn");

        this.setSafezone(true);
    }

    /*public SpawnFaction(UUID uuid) {
        super("Spawn", uuid);

        this.setSafezone(true);
    }*/

    public SpawnFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public boolean isDeathban() {
        return false;
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return HCFConfiguration.spawnColor + "Spawn";
    }
}
