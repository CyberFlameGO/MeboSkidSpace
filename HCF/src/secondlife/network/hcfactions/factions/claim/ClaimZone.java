package secondlife.network.hcfactions.factions.claim;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.vituz.utilties.cuboid.Cuboid;
import secondlife.network.vituz.utilties.cuboid.NamedCuboid;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Getter
@Setter
public class ClaimZone extends NamedCuboid implements Cloneable, ConfigurationSerializable {

    private static Random RANDOM = new Random();

    private UUID claimUniqueID;
    private UUID factionUUID;
    private Faction faction;
    private boolean loaded = false;

    public ClaimZone() {}

    public ClaimZone(Map<String, Object> map) {
        super(map);

        this.name = (String) map.get("name");
        this.claimUniqueID = UUID.fromString((String) map.get("claimUUID"));
        this.factionUUID = UUID.fromString((String) map.get("factionUUID"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();

        map.put("name", name);
        map.put("claimUUID", claimUniqueID.toString());
        map.put("factionUUID", factionUUID.toString());

        return map;
    }

    public ClaimZone(Faction faction, Location location) {
        super(location, location);
        
        this.name = generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    public ClaimZone(Faction faction, Location location1, Location location2) {
        super(location1, location2);
        
        this.name = generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    public ClaimZone(Faction faction, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        super(world, x1, y1, z1, x2, y2, z2);
        
        this.name = generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    public ClaimZone(Faction faction, Cuboid cuboid) {
        super(cuboid);
        
        this.name = generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    public ClaimableFaction getFaction() {
        if(!this.loaded && this.faction == null && this.factionUUID != null) {
            this.faction = RegisterHandler.getInstancee().getFactionManager().getFaction(this.factionUUID);
            this.loaded = true;
        }

        return (this.faction instanceof ClaimableFaction) ? ((ClaimableFaction)this.faction) : null;
    }

    private String generateName() {
        return String.valueOf(RANDOM.nextInt(899) + 100);
    }
    
    public String getFormattedName() {
        return getName() + ": (" + this.getWorldName() + ", " + this.getX1() + ", " + this.getY1() + ", " + this.getZ1() + ") - " + "(" + this.getWorldName() + ", " + this.getX2() + ", " + this.getY2() + ", " + this.getZ2() + ')';
    }
}
