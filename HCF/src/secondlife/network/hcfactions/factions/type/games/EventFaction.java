package secondlife.network.hcfactions.factions.type.games;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.claim.ClaimHandler;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.game.GameType;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.cuboid.Cuboid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class EventFaction extends ClaimableFaction {

    public EventFaction(String name) {
        super(name);

        this.setDeathban(true);
    }

    public EventFaction(String name, UUID uuid) {
        super(name, uuid);
        
        this.setDeathban(true); 
    }

    public EventFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(Faction faction) {
        if(this.getName().equalsIgnoreCase("eotw")) {
            return Color.translate("&4" + this.getName());
        }

        if(this.getName().equalsIgnoreCase("glowstone")) {
            return Color.translate("&6" + this.getName() + " Mountain");
        }
        
        return Color.translate("&9&l" + getName() + "  &e" + this.getGameType().getDisplayName());
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        if(this.getName().equalsIgnoreCase("eotw")) {
            return Color.translate("&4" + this.getName());
        }

        if(this.getName().equalsIgnoreCase("glowstone")) {
            return Color.translate("&6" + this.getName() + " Mountain");
        }

        return Color.translate("&9&l" + this.getName());
    }
    
	public String getScoreboardName() {
        if(this.getName().equalsIgnoreCase("eotw")) {
            return Color.translate("&4" + this.getName());
        }

        if(this.getName().equalsIgnoreCase("glowstone")) {
            return Color.translate("&6" + this.getName() + " Mountain");
        }

        return Color.translate("&9&l" + this.getName());
	}

    public void setClaim(Cuboid cuboid, CommandSender sender) {
        removeClaims(getClaims(), sender);

        Location min = cuboid.getMinimumPoint();
        min.setY(ClaimHandler.MIN_CLAIM_HEIGHT);

        Location max = cuboid.getMaximumPoint();
        max.setY(ClaimHandler.MAX_CLAIM_HEIGHT);

        addClaim(new ClaimZone(this, min, max), sender);
    }

    public abstract GameType getGameType();

    public abstract List<CaptureZone> getCaptureZones();
}
