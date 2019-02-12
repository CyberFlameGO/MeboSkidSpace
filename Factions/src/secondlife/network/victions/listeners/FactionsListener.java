package secondlife.network.victions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.victions.VictionsAPI;
import secondlife.network.victions.VictionsConfig;
import secondlife.network.victions.utilities.FactionsUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Marko on 18.07.2018.
 */
public class FactionsListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Material material = player.getLocation().getBlock().getType();

        String[] commands = new String[] {
                "home", "sethome", "warp",
                "warps",
        };

        for(String command : commands) {
            if(material.equals(Material.STATIONARY_WATER)
                    || material.equals(Material.WATER)
                    || material.equals(Material.LAVA)
                    || material.equals(Material.STATIONARY_LAVA)) {
                if(event.getMessage().toLowerCase().startsWith("/" + command.toLowerCase())) {
                    event.setCancelled(true);
                    player.sendMessage(Color.translate("&cYou can't use this command in lava/water!"));
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) {
            Block block = event.getLocation().getBlock();

            if(block.getType().equals(Material.COBBLESTONE)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Material item = event.getRecipe().getResult().getType();

        if(item.equals(Material.HOPPER)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if((event.getEntity() instanceof TNTPrimed) || (event.getEntity() instanceof ExplosiveMinecart)) {
            if(!VictionsConfig.isTntExplosion()) {
                event.setCancelled(true);
            }

            if(!VictionsConfig.isTnTExplosionDamage()) {
                event.blockList().clear();
            }
        }

        if(event.getEntity() instanceof Creeper) {
            if(!VictionsConfig.isCreeperExplosion()) {
                event.setCancelled(true);
            }

            if(!VictionsConfig.isCreeperExplosionDamage()) {
                event.blockList().clear();
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            if((event.getDamager() instanceof TNTPrimed) || (event.getDamager() instanceof ExplosiveMinecart)) {
                if(!VictionsConfig.isTntExplosion()) {
                    event.setCancelled(true);
                }

            }

            if(event.getDamager() instanceof Creeper) {
                if(!VictionsConfig.isCreeperExplosion()) {
                    event.setCancelled(true);
                }

            }
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        World world = event.getBlock().getWorld();
        Block blockFrom = event.getBlock();
        Block blockTo = event.getBlock();

        int blockX = blockTo.getX();
        int blockY = blockTo.getY();
        int blockZ = blockTo.getZ();

        boolean isLava = (blockFrom.getTypeId() == 10) || (blockFrom.getTypeId() == 11);

        if(VictionsConfig.isWaterSponge()) {
            if(blockFrom.getType().equals(Material.WATER) || blockFrom.getType().equals(Material.STATIONARY_WATER)) {
                int radius = 3;

                for(int x = -radius; x <= radius; x++) {
                    for(int y = -radius; y <= radius; y++) {
                        for(int z = -radius; z <= radius; z++) {
                            Block block = world.getBlockAt(blockX + x, blockY + y, blockZ + z);

                            if(block.getType().equals(Material.SPONGE)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }

        if(VictionsConfig.isLavaSponge()) {
            if(blockFrom.getType().equals(Material.LAVA) || blockFrom.getType().equals(Material.STATIONARY_LAVA)) {
                int radius = 3;

                for(int x = -radius; x <= radius; x++) {
                    for(int y = -radius; y <= radius; y++) {
                        for(int z = -radius; z <= radius; z++) {
                            Block block = world.getBlockAt(blockX + x, blockY + y, blockZ + z);

                            if(block.getType().equals(Material.SPONGE)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }

        if(isLava) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) {
            return;
        }

        ItemStack item = event.getPlayer().getItemInHand();

        int size = 1;
        int amount = 2;

        if((item.getType() == Material.DIAMOND_PICKAXE) && (item.getItemMeta().hasLore()) && (item.getItemMeta().getLore().contains("Breaks blocks in 3x3"))) {
            for(int x = event.getBlock().getX() - size; x <= event.getBlock().getX() + size; x++) {
                for(int y = event.getBlock().getY() - size; y <= event.getBlock().getY() + size; y++) {
                    for(int z = event.getBlock().getZ() - size; z <= event.getBlock().getZ() + size; z++) {
                        Location loc = new Location(event.getBlock().getWorld(), x, y, z);

                        if(loc.getWorld().getEnvironment() == World.Environment.NETHER) {
                            return;
                        }

                        if((FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), loc, "BUILD", false)) && (VictionsAPI.getWorldGuard().canBuild(event.getPlayer(), loc)) && (isBreakable(loc.getBlock().getType()))) {
                            loc.getBlock().breakNaturally();
                        }
                    }
                }
            }
        }

        if((item.getType() == Material.DIAMOND_PICKAXE) && (item.getItemMeta().hasLore()) && (item.getItemMeta().getLore().contains("Breaks blocks in 5x5"))) {
            for(int x = event.getBlock().getX() - amount; x <= event.getBlock().getX() + amount; x++) {
                for(int y = event.getBlock().getY() - amount; y <= event.getBlock().getY() + amount; y++) {
                    for(int z = event.getBlock().getZ() - amount; z <= event.getBlock().getZ() + amount; z++) {
                        Location loc = new Location(event.getBlock().getWorld(), x, y, z);

                        if(loc.getWorld().getEnvironment() == World.Environment.NETHER) {
                            return;
                        }

                        if((FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), loc, "BUILD", false)) && (VictionsAPI.getWorldGuard().canBuild(event.getPlayer(), loc)) && (isBreakable(loc.getBlock().getType()))) {
                            loc.getBlock().breakNaturally();
                        }
                    }
                }
            }
        }

        if((item.getType() == Material.DIAMOND_HOE) && (item.getItemMeta().hasLore()) && (item.getItemMeta().getLore().contains("You don't need to pickup sugarcane"))) {
            if(event.getBlock().getType() == Material.SUGAR_CANE_BLOCK) {
                event.setCancelled(true);

                Location currLoc = event.getBlock().getLocation();

                while(currLoc.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
                    currLoc = new Location(currLoc.getWorld(), currLoc.getBlockX(), currLoc.getBlockY() + 1, currLoc.getBlockZ());
                }

                currLoc = new Location(currLoc.getWorld(), currLoc.getBlockX(), currLoc.getBlockY() - 1, currLoc.getBlockZ());

                while(currLoc.getBlockY() >= event.getBlock().getY()) {
                    currLoc.getBlock().setType(Material.AIR);

                    handleGiveItem(event.getPlayer(), Material.SUGAR_CANE);

                    currLoc = new Location(currLoc.getWorld(), currLoc.getBlockX(), currLoc.getBlockY() - 1, currLoc.getBlockZ());
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
        Location location = block.getLocation();
        FLocation Floc = new FLocation(location);

        if(Board.getInstance().getFactionAt(Floc).isWilderness() || fplayer.isInOwnTerritory() || fplayer.isAdminBypassing()) {
            if(block.getType() == Material.SPONGE) {
                int blockX = block.getX();
                int blockY = block.getY();
                int blockZ = block.getZ();

                int radius = 3 - 1;

                for(int x = -radius; x <= radius; x++) {
                    for(int y = -radius; y <= radius; y++) {
                        for(int z = -radius; z <= radius; z++) {
                            Block blockAt = block.getWorld().getBlockAt(blockX + x, blockY + y, blockZ + z);

                            if(blockAt.getType() == Material.WATER || blockAt.getType() == Material.STATIONARY_WATER) {
                                blockAt.setType(Material.AIR);
                            }

                            if(blockAt.getType() == Material.LAVA || blockAt.getType() == Material.STATIONARY_LAVA) {
                                blockAt.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        handleEntityDeath(event.getEntity(), event.getDrops());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if(VictionsAPI.isPvPEnabled(player) || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(block.getType().equals(Material.ANVIL)) {
            block.setType(Material.ANVIL);
        }
    }

    private boolean isBreakable(Material material) {
        if(material.equals(Material.CHEST)
                || material.equals(Material.TRAPPED_CHEST)
                || material.equals(Material.BEDROCK)
                || material.equals(Material.WATER)
                || material.equals(Material.LAVA)
                || material.equals(Material.MOB_SPAWNER)) {
            return false;
        }

        return true;
    }

    private void handleGiveItem(Player player, Material m) {
        if(player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack(m));
        } else if(getSlot(player, m) != -1) {
            player.getInventory().addItem(new ItemStack(m));
        } else {
            player.getWorld().dropItem(player.getLocation(), new ItemStack(m));
        }
    }

    private int getSlot(Player player, Material material) {
        for(int i = 0; i < player.getInventory().getSize(); i++) {
            if((player.getInventory().getItem(i).getType().equals(material)) && (player.getInventory().getItem(i).getAmount() < player.getInventory().getItem(i).getMaxStackSize())) {
                return i;
            }
        }

        return -1;
    }

    private Material[] shit = {
            Material.RED_ROSE,
            Material.BOW,
            Material.SULPHUR,

            Material.CHAINMAIL_BOOTS,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_LEGGINGS,

            Material.GOLD_BOOTS,
            Material.GOLD_CHESTPLATE,
            Material.GOLD_HELMET,
            Material.GOLD_LEGGINGS,

            Material.LEATHER_BOOTS,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_HELMET,
            Material.LEATHER_LEGGINGS
    };

    private void handleEntityDeath(Entity entity, List<ItemStack> drops) {
        if(!(entity instanceof Player)) {
            drops.forEach(drop -> Stream.of(shit).forEach(shit -> {
                if(drop.getType().equals(shit)) {
                    drops.remove(drop);
                }
            }));
        }

        if(entity instanceof Creeper) {
            if(FactionsUtils.random(1, 3) == 1) drops.add(new ItemStack(Material.TNT));
        }

        int emerald  = FactionsUtils.random(1, 10);

        if(entity instanceof Silverfish) {
            if(emerald >= 9) {
                drops.add(new ItemStack(Material.EMERALD, 3));
            } else if(emerald >= 6) {
                drops.add(new ItemStack(Material.EMERALD, 2));
            } else {
                drops.add(new ItemStack(Material.EMERALD));
            }
        }
    }
}
