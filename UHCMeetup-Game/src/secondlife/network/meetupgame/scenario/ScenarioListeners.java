package secondlife.network.meetupgame.scenario;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.managers.ScenarioManager;
import secondlife.network.meetupgame.scenario.type.*;
import secondlife.network.meetupgame.states.GameState;

/**
 * Created by Marko on 15.07.2018.
 */
public class ScenarioListeners implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("No Clean")) {
            NoCleanScenario.handleDeath(event.getEntity(), event.getEntity().getKiller());
        }

        if(isActive("Time Bomb")) {
            TimeBombScenario.handleDeath(event.getDrops(), event.getEntity());
        }

        /*if(isActive("WebCage")) {
            WebCageScenario.handleDeath(event.getEntity());
        }*/
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            return;
        }
        
        if(isActive("Fireless")) {
            FirelessScenario.handleEntityDamage(event.getEntity(), event.getCause(), event);
        }

        if(isActive("No Clean")) {
            NoCleanScenario.handleEntityDamage(event.getEntity(), event);
        }

        if(isActive("NoFallDamage")) {
            NoFallDamageScenario.handleEntityDamage(event.getEntity(), event.getCause(), event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Long Shots")) {
            LongShotsScenario.handleEntityDamageByEntity(event.getEntity(), event.getDamager(), event);
        }

        if(isActive("No Clean")) {
            NoCleanScenario.handleEntityDamageByEntity(event.getEntity(), event.getDamager(), event);
        }

        if(isActive("Switcheroo")) {
            SwitcherooScenario.handleDamageByEntity(event.getEntity(), event.getDamager());
        }

        if(isActive("Do Not Disturb")) {
            DoNotDisturbScenario.handleEntityDamageByEntity(event.getEntity(), event.getDamager(), event);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Nine Slot")) {
            NineSlotScenario.handlePickup(event.getPlayer(), event.getItem(), event);
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            return;
        }

        Player player = (Player) event.getView().getPlayer();

        if(isActive("Bowless")) {
            BowlessScenario.handleCraft(player, event.getRecipe(), event.getInventory(), event);
        }

        if(isActive("Rodless")) {
            RodlessScenario.handleCraft(player, event.getRecipe(), event.getInventory(), event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Bowless")) {
            BowlessScenario.handleInteract(event.getPlayer(), event.getItem());
        }

        if(isActive("Rodless")) {
            RodlessScenario.handleInteract(event.getPlayer(), event.getItem());
        }

        if(isActive("Soup")) {
            SoupScenario.handleInteract(event.getPlayer(), event.getItem(), event.getAction(), event);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Horseless")) {
            HorselessScenario.handlePlayerInteractEntity(event.getRightClicked(), event);
        }
    }
    
    private boolean isActive(String name) {
        if(ScenarioManager.getByName(name).isEnabled()) {
            return true;
        }
        
        return false;
    }
}
