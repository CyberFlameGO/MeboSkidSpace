package secondlife.network.victions.providers;

import org.bukkit.entity.Player;
import secondlife.network.victions.Victions;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.providers.ScoreProvider;
import secondlife.network.vituz.providers.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.providers.scoreboard.TitleGetter;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;
import subside.plugins.koth.adapter.KothClassic;
import subside.plugins.koth.adapter.KothHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 18.07.2018.
 */
public class ScoreboardProvider implements ScoreProvider {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration sc = new ScoreboardConfiguration();

        sc.setTitleGetter(new TitleGetter("&5&lSecondLife"));
        sc.setScoreGetter(new ScoreboardProvider());

        return sc;
    }

    @Override
    public String[] getScores(Player player) {
        List<String> board = new ArrayList<>();

        FactionsData data = FactionsData.getByName(player.getName());

        if(data == null) {
            return null;
        }

        if(isActive(player)) {
            board.add("&7&m----------------------");
        }

        if(data.isHomeActive(player)) {
            board.add("&9&lHome: &9" + StringUtils.getRemaining(data.getHomeMillisecondsLeft(player), true));
        }

        if(data.isLogoutActive(player)) {
            board.add("&4&lLogout: &f" + StringUtils.getRemaining(data.getLoogutMillisecondsLeft(player), true));
        }

        if(data.isPearlActive(player)) {
            board.add("&e&lEnderpearl: &f" + StringUtils.getRemaining(data.getPearlMillisecondsLeft(player), true));
        }

        if(Victions.getInstance().getPlayerManager().isSpawnTagActive(player)) {
            board.add("&c&lSpawn Tag: &f" + StringUtils.getRemaining(Victions.getInstance().getPlayerManager().getSpawnTagMillisecondsLeft(player), false));
        }

        if(data.isNightVision()) {
            board.add("&b&lNight Vision: &aEnabled");
        }

        if(data.isJellyLegs()) {
            board.add("&e&lJelly Legs: &aEnabled");
        }

        if(data.isFactionFly()) {
            board.add("&2&lFaction Fly: &aEnabled");
        }

        if(!KothHandler.getInstance().getRunningKoths().isEmpty()) {
            for(int i = 0; i < KothHandler.getInstance().getRunningKoths().size(); i++) {
                KothClassic koth = (KothClassic) KothHandler.getInstance().getRunningKoths().get(i);
                String time = koth.getTimeObject().getTimeLeftFormatted();

                board.add("&9&l" + koth.getKoth().getName() + ": &f" + time);
            }
        }

        if(isActive(player)) {
            board.add("&1&7&m----------------------");
        }

        return board.stream().map(Color::translate).toArray(String[]::new);
    }

    private boolean isActive(Player player) {
        FactionsData data = FactionsData.getByName(player.getName());

        if(data.isHomeActive(player)
                || data.isLogoutActive(player)
                || data.isPearlActive(player)
                || Victions.getInstance().getPlayerManager().isSpawnTagActive(player)
                || data.isNightVision()
                || data.isJellyLegs()
                || !KothHandler.getInstance().getRunningKoths().isEmpty()
                || data.isFactionFly()) {
            return true;
        }

        return false;
    }
}
