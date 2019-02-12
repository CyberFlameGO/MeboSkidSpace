package secondlife.network.hcfactions.factions.claim;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.vituz.utilties.cuboid.Cuboid;

import java.util.UUID;

@Getter
@Setter
public class ClaimSelection implements Cloneable {

    private UUID uuid;
    private World world;

    private long lastUpdateMillis;
    private Location pos1;
    private Location pos2;

    public ClaimSelection(World world) {
        this.uuid = UUID.randomUUID();
        this.world = world;
    }
    
    public int getPrice(PlayerFaction playerFaction, boolean selling) {
        return pos1 == null || pos2 == null ? 0 : ClaimHandler.calculatePrice(new Cuboid(pos1, pos2), playerFaction.getClaims().size(), selling);
    }

    public ClaimZone toClaim(Faction faction) {
        return pos1 == null || pos2 == null ? null : new ClaimZone(faction, pos1, pos2);
    }
    
    public void setPos1(Location location) {
        this.pos1 = location;
        this.lastUpdateMillis = System.currentTimeMillis();
    }

    public void setPos2(Location location) {
        this.pos2 = location;
        this.lastUpdateMillis = System.currentTimeMillis();
    }
    
    public boolean hasBothPositionsSet() {
        return pos1 != null && pos2 != null;
    }
}