package secondlife.network.hcfactions.factions.type.system;

import org.bukkit.command.CommandSender;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.factions.Faction;

import java.util.Map;

public class WarzoneFaction extends Faction {

    public WarzoneFaction() {
        super("Warzone");
    }

    public WarzoneFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return HCFConfiguration.warzoneColor + "Warzone";
    }
}
