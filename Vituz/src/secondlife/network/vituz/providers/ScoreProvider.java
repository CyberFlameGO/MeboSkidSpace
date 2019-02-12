package secondlife.network.vituz.providers;

import org.bukkit.entity.Player;

public interface ScoreProvider {

    String[] getScores(Player player);
}