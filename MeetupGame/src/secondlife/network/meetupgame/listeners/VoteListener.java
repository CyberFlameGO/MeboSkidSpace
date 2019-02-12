package secondlife.network.meetupgame.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.player.PlayerData;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.ScenarioManager;

/**
 * Created by Marko on 12.06.2018.
 */
public class VoteListener implements Listener {

    private MeetupGame plugin = MeetupGame.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        ItemStack stack = event.getCurrentItem();

        if(stack == null || stack.getType() == Material.AIR || !stack.hasItemMeta()) return;
        if(stack.getItemMeta() == null) return;

        if(inventory != null) {
            if(inventory.getTitle().equals(plugin.getInventoryManager().getVoteInventory().getTitle())) {
                event.setCancelled(true);

                String optionName = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
                Scenario scenario = ScenarioManager.getByScenario(optionName);

                if(scenario != null) {
                    if(plugin.getVoteManager().getScenarioVotes().containsKey(scenario.getName())) {
                        return;
                    }

                    plugin.getVoteManager().getScenarioVotes().put(scenario.getName(), plugin.getVoteManager().getScenarioVotes().get(scenario.getName()));
                    plugin.getVoteManager().getUsers().add(player.getUniqueId());
                }
            }
        }
    }
}
