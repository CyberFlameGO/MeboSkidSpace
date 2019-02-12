package secondlife.network.meetupgame.managers;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.ScenarioManager;
import secondlife.network.meetupgame.state.GameState;
import secondlife.network.meetupgame.utilities.Manager;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.ItemBuilder;

/**
 * Created by Marko on 11.06.2018.
 */

@Getter
public class InventoryManager extends Manager {

    private Inventory voteInventory;

    public InventoryManager(MeetupGame plugin) {
        super(plugin);

        voteInventory = Bukkit.createInventory(null, 9, Color.translate("Vote for scenarios"));
    }

    public void loadInventory(Player player, GameState state) {
        if(state.equals(GameState.VOTING)) {
            player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("&d&lVote for scenarios").build());
        } else if(state.equals(GameState.WAITING)) {

        }
    }

    public void updateVoteInventory() {
        voteInventory.clear();

        for(Scenario scenario : ScenarioManager.getScenarios()) {
            voteInventory.addItem(ScenarioManager.getScenarioItem(scenario));
        }
    }
}
