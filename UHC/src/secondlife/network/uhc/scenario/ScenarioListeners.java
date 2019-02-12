package secondlife.network.uhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.managers.GameManager;
import secondlife.network.uhc.managers.ScenarioManager;
import secondlife.network.uhc.scenario.type.*;
import secondlife.network.uhc.state.GameState;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;

/**
 * Created by Marko on 15.07.2018.
 */
public class ScenarioListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Limitations")) {
            LimitationsScenario.handleJoin(event.getPlayer());
        }

        if(isActive("Seasons")) {
            VituzNametag.reloadPlayer(event.getPlayer());
            VituzNametag.reloadOthersFor(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("BareBones")) {
            BareBonesScenario.handleDeath(event.getDrops());
        }

        if(isActive("BestPvE")) {
            BestPVEScenario.handleDeath(event.getEntity());
        }

        if(isActive("Diamondless")) {
            DiamondlessScenario.handleDeath(event.getDrops());
        }

        if(isActive("Golden Retriever")) {
            GoldenRetrieverScenario.handleDeath(event.getDrops());
        }

        if(isActive("Goldless")) {
            GoldlessScenario.handleDeath(event.getDrops());
        }

        if(isActive("Ironless")) {
            IronlessScenario.handleDeath(event.getDrops());
        }

        if(isActive("No Clean")) {
            NoCleanScenario.handleDeath(event.getEntity(), event.getEntity().getKiller());
        }

        if(isActive("Time Bomb")) {
            TimeBombScenario.handleDeath(event.getDrops(), event.getEntity());
        } else {
            if(GameManager.getGameState().equals(GameState.PLAYING)) {
                UHCUtils.spawnHead(event.getEntity());
            }
        }

        if(isActive("WebCage")) {
            WebCageScenario.handleDeath(event.getEntity());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("BestPvE")) {
            BestPVEScenario.handleEntityDamage(event.getEntity(), event.getCause());
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
    public void onEntityDeath(EntityDeathEvent event) {
        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Cut Clean")) {
            CutCleanScenario.handleEntityDeath(event.getEntity(), event.getDrops());
        }

        if(isActive("Double Ores")) {
            DoubleOresScenario.handleEntityDeath(event.getDrops(), event.getEntity());
        }

        if(isActive("Triple Ores")) {
            TripleOresScenario.handleEntityDeath(event.getDrops(), event.getEntity());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Blood Diamonds")) {
            BloodDiamondsScenario.handleBreak(event.getPlayer(), event.getBlock());
        }

        if(isActive("Cut Clean")) {
            CutCleanScenario.handleBreak(event.getPlayer(), event.getBlock(), event);
        }

        if(isActive("Diamondless")) {
            DiamondlessScenario.handleBreak(event.getBlock(), event);
        }

        if(isActive("Double Exp")) {
            DoubleExpScenario.handleBreak(event.getPlayer(), event.getBlock());
        }

        if(isActive("Double Ores")) {
            DoubleOresScenario.handleBreak(event.getPlayer(), event.getBlock(), event);
        }

        if(isActive("Goldless")) {
            GoldlessScenario.handleBreak(event.getBlock(), event);
        }

        if(isActive("Ironless")) {
            IronlessScenario.handleBreak(event.getBlock(), event);
        }

        if(isActive("Limitations")) {
            LimitationsScenario.handleBreak(event.getPlayer(), event.getBlock(), event);
        }

        if(isActive("Limited Enchants")) {
            LimitedEnchantsScenario.handleBreak(event.getPlayer(), event.getBlock(), event);
        }

        if(isActive("Ore Frenzy")) {
            OreFrenzyScenario.handleBreak(event.getPlayer(), event.getBlock(), event);
        }

        if(isActive("Risky Retrieval")) {
            RiskyRetrievalScenario.handleBreak(event.getPlayer(), event.getBlock(), event);
        }

        if(isActive("Timber")) {
            TimberScenario.handleBreak(event.getPlayer(), event.getBlock());
        }

        if(isActive("Triple Ores")) {
            TripleOresScenario.handleBreak(event.getPlayer(), event.getBlock(), event);
        }

        if(isActive("Vanilla+")) {
            VanillaPlusScenario.handleBreak(event.getBlock());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
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
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Limited Enchants")) {
            LimitedEnchantsScenario.handlePlace(event.getPlayer(), event.getBlock(), event);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(Bukkit.spigot().getTPS().length < 18 && Bukkit.getOnlinePlayers().size() > 350) {
            event.getBlock().getDrops().removeIf(is -> is.getType() == Material.SAPLING);
        }

        if(Math.random() * 100.0D <= UHC.getInstance().getGameManager().getAppleRate() + .0D) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
        }

        if(isActive("Lucky Leaves")) {
            LuckyLeavesScenario.handleDecay(event.getBlock());
        }

        if(isActive("Vanilla+")) {
            VanillaPlusScenario.handleDecay(event.getBlock());
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
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

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        Player player = (Player) event.getView().getPlayer();

        if(isActive("Bowless")) {
            BowlessScenario.handleCraft(player, event.getRecipe(), event.getInventory(), event);
        }

        if(isActive("Limited Enchants")) {
            LimitedEnchantsScenario.handleCraft(player, event.getCurrentItem(), event);
        }

        if(isActive("Rodless")) {
            RodlessScenario.handleCraft(player, event.getRecipe(), event.getInventory(), event);
        }

        if(isActive("Swordless")) {
            SwordlessScenario.handleCraft(event.getCurrentItem(), event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
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
    public void onEnchantItem(EnchantItemEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("No Enchants")) {
            NoEnchantsScenario.handleEnchantItem(event.getEnchanter(), event);
        }

        if(isActive("Cold Weapons")) {
            ColdWeaponsScenario.handleEnchantItem(event.getEnchantsToAdd());
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Gone Fishing")) {
            GoneFishingScenario.handlePrepareItemCraft(event.getRecipe().getResult(), event.getInventory());
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        if(isActive("Horseless")) {
            HorselessScenario.handlePlayerInteractEntity(event.getRightClicked(), event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if(!GameManager.getGameState().equals(GameState.PLAYING)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if(isActive("Cold Weapons")) {
            ColdWeaponsScenario.handleInventoryClick(player, event.getInventory(), event.getSlotType(), event.getCurrentItem(), event);
        }

        if(isActive("No Enchants")) {
            NoEnchantsScenario.handleInventoryClick(player, event.getInventory(), event.getSlotType(), event);
        }
    }
    
    private boolean isActive(String name) {
        if(ScenarioManager.getByName(name).isEnabled()) {
            return true;
        }
        
        return false;
    }
}
