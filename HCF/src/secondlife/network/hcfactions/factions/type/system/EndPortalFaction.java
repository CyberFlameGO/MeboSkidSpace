package secondlife.network.hcfactions.factions.type.system;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;

import java.util.Map;

public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable {

    public EndPortalFaction() {
        super("EndPortal");

        this.setSafezone(true);
    }

    /*public EndPortalFaction(UUID uuid) {
        super("EndPortal", uuid);

        this.setSafezone(true);
    }*/

    public EndPortalFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.GREEN + this.getName().replace("EndPortal", "End Portal");
    }

    @Override
    public boolean isDeathban() {
        return false;
    }
}
