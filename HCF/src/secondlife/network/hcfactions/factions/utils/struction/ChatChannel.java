package secondlife.network.hcfactions.factions.utils.struction;

import lombok.Getter;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.vituz.utilties.Color;

import java.util.Locale;

@Getter
public enum ChatChannel {

    FACTION("Faction"), ALLIANCE("Alliance"), CAPTAIN("Captain"), PUBLIC("Public");

    private String name;

    ChatChannel(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        String prefix;
        switch (this) {
        case FACTION:
            prefix = HCFConfiguration.teammateColor.toString();
            break;
        case ALLIANCE:
            prefix = HCFConfiguration.allyColor.toString();
            break;
        case CAPTAIN:
        	prefix = HCFConfiguration.captainColor.toString();
        	break;
        case PUBLIC:
        default:
            prefix = HCFConfiguration.enemyColor.toString();
            break;
        }

        return prefix + name;
    }

    public String getShortName() {
        switch (this) {
        case FACTION:
            return "FC";
        case CAPTAIN:
        	return "CC";
        case ALLIANCE:
            return "AC";
        case PUBLIC:
        default:
            return "PC";
        }
    }

    public static ChatChannel parse(String id) {
        return parse(id, PUBLIC);
    }

    public static ChatChannel parse(String id, ChatChannel def) {
        id = id.toLowerCase(Locale.ENGLISH);
        
        switch (id) {
        case "f":
        case "faction":
        case "fc":
        case "fac":
        case "fact":
            return ChatChannel.FACTION;
        case "a":
        case "alliance":
        case "ally":
        case "ac":
            return ChatChannel.ALLIANCE;
        case "c":
        case "captain":
        case "ca":
        case "cap":
            return ChatChannel.CAPTAIN;            
        case "p":
        case "pc":
        case "g":
        case "gc":
        case "global":
        case "pub":
        case "publi":
        case "public":
            return ChatChannel.PUBLIC;
        default:
            return def == null ? null : def.getRotation();
        }
    }

    public ChatChannel getRotation() {
        switch (this) {
        case FACTION:
            return PUBLIC;
        case CAPTAIN:
        	return FACTION;
        case PUBLIC:
            return HCFConfiguration.maxAllysPerFaction > 0 ? ALLIANCE : FACTION;
        case ALLIANCE:
            return FACTION;
        default:
            return PUBLIC;
        }
    }

    public String getRawFormat(Player player) {
        switch(this) {
        case FACTION:
            return Color.translate("&3(Faction) " + player.getName() + ": &e" + "%2$s");
        case ALLIANCE:
            return Color.translate("&9(Alliance) " + player.getName() + ": &e" + "%2$s");
        case CAPTAIN:
        	return Color.translate("&d(Captain) " + player.getName() + ": &e" + "%2$s");
        default:
            throw new IllegalArgumentException("Cannot get the raw format for public chat channel");
        }
    }
}
