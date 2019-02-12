package secondlife.network.meetupgame.listeners;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.managers.ScenarioManager;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.states.GameState;
import secondlife.network.meetupgame.utilties.EloUtils;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.*;

/**
 * Created by Marko on 23.07.2018.
 */
public class PlayerListener implements Listener {

    private MeetupGame plugin = MeetupGame.getInstance();

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!GameManager.getGameData().isGenerated()) {
            event.setKickMessage(Color.translate("&cPlease wait for map to generate!"));
            event.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if(!PlayerUtils.isMongoConnected()) return;

        MeetupData data = MeetupData.getByName(event.getName());

        if(!data.isLoaded()) {
            data.load();
        }

        if(!data.isLoaded()) {
            PlayerUtils.kick(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction().name().startsWith("RIGHT_")) {
            ItemStack item = event.getItem();

            if(item == null) {
                return;
            }

            switch (item.getType()) {
                case BOOK:
                    player.openInventory(plugin.getInventoryManager().getScenarioInventory());
                    break;
                case WATCH:
                    if(!GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
                        player.performCommand("settings");
                    }
                    break;
                case REDSTONE:
                    ServerUtils.sendToServer(player, "UHCMeetup-Lobby");
                    break;
                case PAPER:
                    player.performCommand("stats");
                    break;
                case MUSHROOM_SOUP:
                    if (player.getHealth() <= 19.0D && !player.isDead()) {
                        if(player.getHealth() < 20.0D || player.getFoodLevel() < 20) {
                            player.getItemInHand().setType(Material.BOWL);
                        }

                        player.setHealth(player.getHealth() + 7.0D > 20.0D ? 20.0D : player.getHealth() +
                                7.0D);
                        player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
                        player.setSaturation(12.8F);
                        player.updateInventory();
                    }
                    break;
                case WORKBENCH:
                    event.setCancelled(false);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    player.sendMessage(ChatColor.RED + "Crafting tables are disabled!");
                    break;
            }
        }
    }

    @EventHandler
    public void onHorseSetup(CreatureSpawnEvent event) {
        if(event.getEntityType() != EntityType.HORSE
                || event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            return;
        }

        Horse horse = (Horse) event.getEntity();

        horse.setAdult();
        horse.setAgeLock(true);

        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));

        horse.setTamed(true);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if(event.getItem().getType() != Material.GOLDEN_APPLE
                || !event.getItem().hasItemMeta()
                || !event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
            return;
        }

        Player player = event.getPlayer();

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
        player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
    }

    @EventHandler
    public void onEntityDamageByEntityBow(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player entity = (Player) event.getEntity();

        if(!(event.getDamager() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getDamager();

        if(!(arrow.getShooter() instanceof Player)) return;

        Player shooter = (Player) arrow.getShooter();

        if(entity.getName().equals(shooter.getName())) return;

        double health = Math.ceil(entity.getHealth() - event.getFinalDamage()) / 2.0D;

        if(health > 0.0D) {
            shooter.sendMessage(Color.translate("&d" + entity.getName() + " &eis now at &d" + health + Msg.HEART + "&e."));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        VituzNametag.reloadPlayer(player);
        VituzNametag.reloadOthersFor(player);

        switch (GameManager.getGameData().getGameState()) {
            case WINNER:
            case PLAYING: {
                Tasks.runLater(() -> {
                    plugin.getSpectatorManager().handleEnable(player);

                    Location location = new Location(Bukkit.getWorld("world"), 0, Bukkit.getWorld("world").getHighestBlockYAt(0, 0) + 15, 0);
                    player.teleport(location);

                    if(MeetupUtils.isPlayerInSpecMode(player)) {
                        Bukkit.getOnlinePlayers().forEach(on -> {
                           if(MeetupUtils.isPlayerInSpecMode(on)) {
                               player.hidePlayer(on);
                           }
                       });
                    }
                }, 1L);
                break;
            }

            case STARTING: {
                MeetupUtils.clearPlayer(player);
                player.teleport(MeetupUtils.getScatterLocation());

                Tasks.runLater(() -> Vituz.getInstance().getHorseManager().sitPlayer(player), 5L);

                if(player.hasPermission(Permission.DONOR_PERMISSION)) {
                    player.sendMessage(Color.translate("&eYou can use &d/announce&e to announce that game will start!"));
                }

                Msg.sendMessage("&d" + player.getName() + " &ehas joined the game &d(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
                Bukkit.getOnlinePlayers().forEach(online -> online.playSound(online.getLocation(), Sound.ORB_PICKUP, 1F, 1F));
                plugin.getKitsManager().handleGiveKit(player);
                break;
            }

            case VOTE: {
                if(Bukkit.getOnlinePlayers().size() < 6) {
                    int count = 6;
                    int online = Bukkit.getOnlinePlayers().size();

                    Msg.sendMessage("&eThe game need &d" + (count - online) + " &emore players for start.");
                }

                MeetupUtils.clearPlayer(player);
                player.teleport(MeetupUtils.getScatterLocation());
                MeetupUtils.loadVoteInventory(player);

                Tasks.runLater(() -> Vituz.getInstance().getHorseManager().sitPlayer(player), 5L);

                if(player.hasPermission(Permission.DONOR_PERMISSION)) {
                    player.sendMessage(Color.translate("&eYou can use &d/announce&e to announce that game will start!"));
                }

                Msg.sendMessage("&d" + player.getName() + " &ehas joined the game &d(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
                Bukkit.getOnlinePlayers().forEach(online -> online.playSound(online.getLocation(), Sound.ORB_PICKUP, 1F, 1F));
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MeetupData meetupData = MeetupData.getByName(player.getName());

        if(meetupData.isAlive() && GameManager.getGameData().getGameState().equals(GameState.PLAYING)) {
            int toLoseElo = EloUtils.giveElo(meetupData);

            if(meetupData.getElo() > toLoseElo) {
                meetupData.setElo(meetupData.getElo() - toLoseElo);
                player.sendMessage(Color.translate("&eYou have lost &d" + toLoseElo + " elo&e because you died."));
            }

            Msg.sendMessage("&c" + player.getName() + "&7[&f" + meetupData.getGameKills() + "&7] &ehas disconnected. (Died)!");
        }

        meetupData.save();

        Tasks.runLater(() -> {
            if(MeetupUtils.isPlayerInSpecMode(player)) {
                plugin.getSpectatorManager().handleDisable(player);
                plugin.getVanishManager().handleUnvanish(player);
            }
        }, 20L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null
                || event.getCurrentItem().getType() == Material.AIR
                || !event.getCurrentItem().hasItemMeta()
                || event.getCurrentItem().getItemMeta() == null) {
            return;
        }

        String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        String title = event.getClickedInventory().getTitle();
        Player player = (Player) event.getWhoClicked();

        if(title.contains("Scenarios")) {
            event.setCancelled(true);

            Scenario scenario = ScenarioManager.getByName(name);

            if(scenario != null) {
                if(plugin.getVoteManager().hasVoted(player)) {
                    plugin.getVoteManager().handleRemove(player, scenario);
                    return;
                }

                plugin.getVoteManager().handleAddVote(player, scenario);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        player.setHealth(20.0);
        player.teleport(player.getLocation());

        Tasks.runLater(() -> plugin.getSpectatorManager().handleEnable(player), 1L);

        event.setDroppedExp(0);

        MeetupData playerData = MeetupData.getByName(player.getName());
        playerData.setDeaths(playerData.getDeaths() + 1);

        int toLoseElo = EloUtils.giveElo(playerData);

        if(killer == null && playerData.getElo() > toLoseElo) {
            playerData.setElo(playerData.getElo() - toLoseElo);
            player.sendMessage(Color.translate("&eYou have lost &d" + toLoseElo + " elo&e because you died."));
        }

        if(killer != null) {
            MeetupData killerData = MeetupData.getByName(killer.getName());

            int toGiveElo = EloUtils.getElo(killerData, playerData);

            if(playerData.getElo() > toGiveElo) {
                playerData.setElo(playerData.getElo() - toGiveElo);
                player.sendMessage(Color.translate("&eYou have lost &d" + toGiveElo + " elo&e because you were killed by &d" + killer.getName() + "&e."));
            }

            killer.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, 12));

            killerData.setKills(killerData.getKills() + 1);
            killerData.setElo(killerData.getElo() + toGiveElo);

            killerData.setGameKills(killerData.getGameKills() + 1);
            killerData.setGameElo(killerData.getGameElo() + toGiveElo);

            killer.sendMessage(Color.translate("&eYou have gotten &d" + toGiveElo + " elo&e because you killed &d" + player.getName() + "&e."));

            if(killerData.getGameKills() > killerData.getHighestKillStreak()) {
                killerData.setHighestKillStreak(killerData.getGameKills());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(MeetupUtils.isState()) {
            event.setCancelled(true);
            return;
        }

        if(!plugin.getGameManager().getWhitelistedBlocks().contains(event.getBlock().getType())) {
            player.sendMessage(Color.translate("&cYou aren't allowed to break this block!"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if(MeetupUtils.isState()) {
            event.setCancelled(true);
            return;
        }

        int max = 90;
        if(event.getBlock().getY() > max) {
            event.setCancelled(true);

            if(player.getLocation().getY() > max + 2) {
                Location finalLocation = player.getLocation();
                finalLocation.setY(max + 1);

                if(finalLocation.getBlock().getRelative(0, 1, 0).isEmpty()) {
                    player.teleport(finalLocation);
                }

                player.sendMessage(Color.translate("&cSky basing isn't allowed!"));
            }
        }
    }

    @EventHandler
    public void onPlayerCraftItemEvent(CraftItemEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);
        event.setResult(Event.Result.DENY);

        player.sendMessage(Color.translate("&cCrafting items is not allowed!"));
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if(MeetupUtils.isState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if(MeetupUtils.isState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(MeetupUtils.isState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            if(MeetupUtils.isState()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if(MeetupUtils.isState()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getEntity() instanceof Horse) {
           return;
        }

        event.setCancelled(true);
    }
}
