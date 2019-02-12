package secondlife.network.hcfactions.factions.type.system;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.Map;

public class RoadFaction extends ClaimableFaction implements ConfigurationSerializable {

    public RoadFaction() {
        super("Road");
    }

    /*public RoadFaction(UUID uuid) {
        super("Road", uuid);
    }*/

    public RoadFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return HCFConfiguration.roadColor + "Road";
    }

    @Override
    public void printDetails(CommandSender sender) {
        sender.sendMessage(HCFUtils.BIG_LINE);
        sender.sendMessage(getDisplayName(sender));
        sender.sendMessage(Color.translate("&eLocation: &dNone"));
        sender.sendMessage(HCFUtils.BIG_LINE);
    }
}
