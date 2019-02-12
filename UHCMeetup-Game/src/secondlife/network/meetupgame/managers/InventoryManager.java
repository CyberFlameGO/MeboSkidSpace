package secondlife.network.meetupgame.managers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.utilties.Manager;
import secondlife.network.vituz.utilties.Color;

import java.util.Comparator;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
public class InventoryManager extends Manager {

    private Inventory scenarioInventory;

    public InventoryManager(MeetupGame plugin) {
        super(plugin);

        scenarioInventory = Bukkit.createInventory(null, 18, Color.translate("&eScenarios"));
    }

    public void handleUpdateScenarios() {
        scenarioInventory.clear();

        ScenarioManager.getScenarios()
                .stream().sorted(Comparator.comparing(Scenario::getName))
                .forEach(scenario -> scenarioInventory.addItem(plugin.getScenarioManager().getItem(scenario)));
    }
}
