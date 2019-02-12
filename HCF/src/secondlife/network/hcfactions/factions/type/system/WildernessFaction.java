package secondlife.network.hcfactions.factions.type.system;

import org.bukkit.command.CommandSender;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.factions.Faction;

import java.util.Map;

public class WildernessFaction extends Faction {

    public WildernessFaction() {
        super("The Wilderness");
    }

    public WildernessFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return HCFConfiguration.wildernessColor + "The Wilderness";
    }
}
