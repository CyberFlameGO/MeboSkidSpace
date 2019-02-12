package secondlife.network.meetupgame.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.player.PlayerData;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.ScenarioManager;
import secondlife.network.meetupgame.state.GameState;
import secondlife.network.meetupgame.state.PlayerState;
import secondlife.network.meetupgame.utilities.Manager;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 11.06.2018.
 */

@Getter
@Setter
public class GameManager extends Manager {

    private int initial = 0;
    private String winner = "";
    private GameState gameState = GameState.VOTING;
    private List<Material> whitelistedBlocks = new ArrayList<>();

    public GameManager(MeetupGame plugin) {
        super(plugin);

        whitelistedBlocks.add(Material.LOG);
        whitelistedBlocks.add(Material.LOG_2);
        whitelistedBlocks.add(Material.WOOD);
        whitelistedBlocks.add(Material.LEAVES);
        whitelistedBlocks.add(Material.LEAVES_2);
        whitelistedBlocks.add(Material.WATER);
        whitelistedBlocks.add(Material.STATIONARY_WATER);
        whitelistedBlocks.add(Material.LAVA);
        whitelistedBlocks.add(Material.STATIONARY_LAVA);
        whitelistedBlocks.add(Material.LONG_GRASS);
        whitelistedBlocks.add(Material.YELLOW_FLOWER);
        whitelistedBlocks.add(Material.COBBLESTONE);
        whitelistedBlocks.add(Material.CACTUS);
        whitelistedBlocks.add(Material.SUGAR_CANE_BLOCK);
        whitelistedBlocks.add(Material.DOUBLE_PLANT);
        whitelistedBlocks.add(Material.OBSIDIAN);
        whitelistedBlocks.add(Material.SNOW);
        whitelistedBlocks.add(Material.YELLOW_FLOWER);
        whitelistedBlocks.add(Material.RED_ROSE);
        whitelistedBlocks.add(Material.BROWN_MUSHROOM);
        whitelistedBlocks.add(Material.RED_MUSHROOM);
        whitelistedBlocks.add(Material.HUGE_MUSHROOM_1);
        whitelistedBlocks.add(Material.HUGE_MUSHROOM_2);
    }

    public int getAlivePlayers() {
        int i = 0;

        for(PlayerData data : PlayerData.getPlayerDatas()) {
            if(data.getPlayerState().equals(PlayerState.PLAYING)) {
                i++;
            }
        }

        return i;
    }

    public void startGame() {
        String scenarioName = plugin.getVoteManager().getHighestVote();
        ScenarioManager.getByScenario(scenarioName).setEnabled(true);

        Msg.sendMessage("&d" + scenarioName + " &ehas been chosen as this game's scenario with &d" + plugin.getVoteManager().getScenarioVotes().get(scenarioName) + " votes&e.");

        // TODO BorderTask (100, 75, 50, 25)
    }
}
