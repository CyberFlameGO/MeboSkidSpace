package secondlife.network.hcfactions.factions.utils.struction;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum RegenStatus {

    FULL(ChatColor.GREEN.toString() + '\u25B6'),
    REGENERATING(ChatColor.GOLD.toString() + '\u21ea'),
    PAUSED(ChatColor.RED.toString() + '\u25a0');

    private String symbol;

    RegenStatus(String symbol) {
        this.symbol = symbol;
    }
}
