package secondlife.network.hcfactions.factions;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.system.*;
import secondlife.network.hcfactions.factions.utils.events.FactionRenameEvent;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.*;

@Getter
@Setter
public abstract class Faction implements ConfigurationSerializable {

    @Getter
    private static Set<Faction> factions = new HashSet<>();

    private UUID uniqueID;
    private String name;

    private long creationMillis = System.currentTimeMillis();
    private long lastRenameMillis;

    private double dtrLossMultiplier = 1.0;
    private double deathbanMultiplier = 1.0;
    private boolean safezone;

    public Faction(String name) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;

        factions.add(this);
    }

    public Faction(String name, UUID uniqueID) {
        this.name = name;
        this.uniqueID = uniqueID;

        if(uniqueID == null) this.uniqueID = UUID.randomUUID();

        factions.add(this);
    }

    public Faction(Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.name = (String) map.get("name");
        this.creationMillis = Long.parseLong((String) map.get("creationMillis"));
        this.lastRenameMillis = Long.parseLong((String) map.get("lastRenameMillis"));
        this.deathbanMultiplier = (Double) map.get("deathbanMultiplier");
        this.safezone = (Boolean) map.get("safezone");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("uniqueID", uniqueID.toString());
        map.put("name", name);
        map.put("creationMillis", Long.toString(creationMillis));
        map.put("lastRenameMillis", Long.toString(lastRenameMillis));
        map.put("deathbanMultiplier", deathbanMultiplier);
        map.put("safezone", safezone);

        return map;
    }

    public Faction() {}

    public static Faction getByName(String name) {
        for (Faction faction : getFactions()) {
            if (faction.getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
                return faction;
            }
        }

        return null;
    }

    public static Faction getByUuid(UUID uuid) {
        for (Faction faction : getFactions()) {
            if (faction.getUniqueID().equals(uuid)) {
                return faction;
            }
        }

        return null;
    }

    public boolean setName(String name) {
        return this.setName(name, Bukkit.getConsoleSender());
    }

    public boolean setName(String name, CommandSender sender) {
        if(this.name != null && this.name.equals(name)) return false;

        FactionRenameEvent event = new FactionRenameEvent(this, sender, this.name, name);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) return false;

        this.lastRenameMillis = System.currentTimeMillis();
        this.name = name;

        return true;
    }

    public Relation getFactionRelation(Faction faction) {
        if(faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;

            if(playerFaction == this) return Relation.MEMBER;
            if(playerFaction.getAllied().contains(uniqueID)) return Relation.ALLY;
        }

        return Relation.ENEMY;
    }

    public Relation getRelation(CommandSender sender) {
        return sender instanceof Player ? getFactionRelation(RegisterHandler.getInstancee().getFactionManager().getPlayerFaction((Player) sender)) : Relation.ENEMY;
    }

    public String getDisplayName(CommandSender sender) {
        return (this.safezone ? Color.translate("&a") : this.getRelation(sender).toChatColour()) + this.name;
    }

    public String getDisplayName(Faction other) {
        if(name.equalsIgnoreCase("eotw")) {
            name = Color.translate("&4" + name);
        }

        if(name.equalsIgnoreCase("glowstone")) {
            name = Color.translate("&6" + name + " Mountain");
        }

        return this.getFactionRelation(other).toChatColour() + this.name;
    }

    public String getTabDisplayName(CommandSender sender) {
        if(name.equalsIgnoreCase("eotw")) {
            name = Color.translate("&4" + name);
        }

        if(name.equalsIgnoreCase("glowstone")) {
            name = Color.translate("&6" + name + " Mountain");
        }


        return (safezone ? Color.translate("&a") : getRelation(sender).toTabChatColour()) + name;
    }

    public void printDetails(CommandSender sender) {
        sender.sendMessage(HCFUtils.BIG_LINE);
        sender.sendMessage(getDisplayName(sender));
        sender.sendMessage(HCFUtils.BIG_LINE);
    }

    public boolean isDeathban() {
        return !safezone && deathbanMultiplier > 0.0D;
    }

    public void setDeathban(boolean deathban) {
        if(deathban != isDeathban()) this.deathbanMultiplier = deathban ? 1.0 : 0.0; 
    }

    public String getType() {
        if (this instanceof EndPortalFaction) {
            return "END_PORTAL_FACTION";
        } else if (this instanceof PlayerFaction) {
            return "PLAYER_FACTION";
        } else if (this instanceof RoadFaction) {
            return "ROAD_FACTION";
        } else if (this instanceof SpawnFaction) {
            return "SPAWN_FACTION";
        } else if (this instanceof WarzoneFaction) {
            return "WARZONE_FACTION";
        } else if (this instanceof WildernessFaction) {
            return "WILDERNESS_FACTION";
        } else if (this instanceof KothFaction) {
            return "KOTH_FACTION";
        }

        return null;
    }

}
