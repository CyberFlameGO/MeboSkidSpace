package secondlife.network.hcfactions.factions.utils.struction;

import lombok.Getter;
import org.bukkit.ChatColor;
import secondlife.network.hcfactions.HCFConfiguration;

@Getter
public enum Relation {

    MEMBER(3), ALLY(2), ENEMY(1);

    private int value;

    Relation(int value) {
        this.value = value;
    }

    public String getDisplayName() {
        switch (this) {
        case ALLY:
            return toChatColour() + "alliance";
        default:
            return toChatColour() + name().toLowerCase();
        }
    }

    public ChatColor toChatColour() {
        switch (this) {
        case MEMBER:
            return HCFConfiguration.teammateColor;
        case ALLY:
            return HCFConfiguration.allyColor;
        case ENEMY:
        default:
            return ChatColor.YELLOW;
        }
    }

    public ChatColor toTabChatColour() {
        switch (this) {
            case MEMBER:
                return HCFConfiguration.teammateColor;
            case ALLY:
                return HCFConfiguration.allyColor;
            case ENEMY:
            default:
                return ChatColor.LIGHT_PURPLE;
        }
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isAlly() {
        return this == ALLY;
    }

    public boolean isEnemy() {
        return this == ENEMY;
    }
}
