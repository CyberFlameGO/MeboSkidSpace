package secondlife.network.hcfactions.utilties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersistableLocation implements ConfigurationSerializable, Cloneable {
	
    private Location location;
    private World world;
    private String worldName;
    private UUID worldUID;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PersistableLocation(Location location) {
        this.world = location.getWorld();
        this.worldName = this.world.getName();
        this.worldUID = this.world.getUID();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public PersistableLocation(World world, double x, double y, double z) {
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        float n = 0.0f;
        this.yaw = n;
        this.pitch = n;
    }

    public PersistableLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        float n = 0.0f;
        this.yaw = n;
        this.pitch = n;
    }

    public PersistableLocation(Map<String, String> map) {
        this.worldName = map.get("worldName");
        this.worldUID = UUID.fromString(map.get("worldUID"));
        
        Object o = map.get("x");
        
        if(o instanceof String) {
            this.x = Double.parseDouble((String) o);
        } else {
            this.x = (double) o;
        }
        
        o = map.get("y");
        
        if(o instanceof String) {
            this.y = Double.parseDouble((String) o);
        } else {
            this.y = (double) o;
        }
        
        o = map.get("z");
        
        if(o instanceof String) {
            this.z = Double.parseDouble((String) o);
        } else {
            this.z = (double) o;
        }
        
        this.yaw = Float.parseFloat(map.get("yaw"));
        this.pitch = Float.parseFloat(map.get("pitch"));
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        
        map.put("worldName", this.worldName);
        map.put("worldUID", this.worldUID.toString());
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        map.put("yaw", Float.toString(this.yaw));
        map.put("pitch", Float.toString(this.pitch));
        return map;
    }

    public World getWorld() {
        if(this.world == null) {
            this.world = Bukkit.getWorld(this.worldUID);
        }
        
        return this.world;
    }

    public void setWorld(World world) {
        this.worldName = world.getName();
        this.worldUID = world.getUID();
        this.world = world;
    }

    public Location getLocation() {
        if(this.location == null) {
            this.location = new Location(this.getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
        }
        
        return this.location;
    }

    public PersistableLocation clone() throws CloneNotSupportedException {
        try {
            return (PersistableLocation) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        return "PersistableLocation [worldName=" + this.worldName + ", worldUID=" + this.worldUID + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + this.yaw + ", pitch=" + this.pitch + ']';
    }
}
