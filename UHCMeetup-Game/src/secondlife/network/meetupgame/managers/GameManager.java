package secondlife.network.meetupgame.managers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.type.NineSlotScenario;
import secondlife.network.meetupgame.states.GameState;
import secondlife.network.meetupgame.states.PlayerState;
import secondlife.network.meetupgame.tasks.BorderTask;
import secondlife.network.meetupgame.tasks.BorderTimeTask;
import secondlife.network.meetupgame.tasks.GameTask;
import secondlife.network.meetupgame.tasks.StartingTask;
import secondlife.network.meetupgame.utilties.Manager;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
public class GameManager extends Manager {

    @Getter
    private static GameData gameData = new GameData();

    private List<Material> whitelistedBlocks = new ArrayList<>();

    public GameManager(MeetupGame plugin) {
        super(plugin);

        handleSetWhitelistedBlocks();
        handleLoadChunks();
    }

    public void handleStart() {
        GameManager.getGameData().setGameState(GameState.PLAYING);
        MeetupUtils.setMotd("&aPlaying");

        Scenario scenario = plugin.getVoteManager().getHighestVote();
        scenario.setEnabled(true);

        new BorderTask();
        new GameTask();

        Msg.sendMessage("&d" + scenario.getName() + " &ehas been chosen as this game's scenario with &d" + plugin.getVoteManager().getVotes().get(scenario) + " votes&e.");
        Msg.sendMessage("&4&lThis is a solo game. Any form of teaming isn't allowed and is punishable at staff's disrection.");

        if(ScenarioManager.getByName("Nine Slot").isEnabled()) {
            Tasks.runTimer(() -> Bukkit.getOnlinePlayers().forEach(NineSlotScenario::cleanInventory), 20L, 20L);
        }

        if(ScenarioManager.getByName("Soup").isEnabled()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.getInventory().addItem(new ItemStack(Material.RED_MUSHROOM, 64));
                player.getInventory().addItem(new ItemStack(Material.BROWN_MUSHROOM, 64));
                player.getInventory().addItem(new ItemStack(Material.BOWL, 64));
            });
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            MeetupData data = MeetupData.getByName(player.getName());
            data.setPlayed(data.getPlayed() + 1);
            data.setPlayerState(PlayerState.PLAYING);
            Vituz.getInstance().getHorseManager().unsitPlayer(player);

            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
        });

        gameData.setCanBorderTime(true);
        gameData.setScenario(scenario.getName());
        gameData.setRemaining(plugin.getGameManager().getAlivePlayers());
        gameData.setInitial(plugin.getGameManager().getAlivePlayers());

        new BorderTimeTask();

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getBorderManager().handleStartSeconds(), 1200L);
    }

    public void handleStarting() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            MeetupUtils.clearPlayer(player);
            plugin.getKitsManager().handleGiveKit(player);
        });

        GameManager.getGameData().setGameState(GameState.STARTING);
        MeetupUtils.setMotd("&eStarting");

        new StartingTask();
    }

    public int getAlivePlayers() {
        int i = 0;

        for(MeetupData data : MeetupData.getMeetupDatas().values()) {
            if(data.getPlayerState().equals(PlayerState.PLAYING)) {
                i++;
            }
        }

        return i;
    }

    private void handleSetWhitelistedBlocks() {
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
        whitelistedBlocks.add(Material.WEB);
        whitelistedBlocks.add(Material.ANVIL);
        whitelistedBlocks.add(Material.DEAD_BUSH);
        whitelistedBlocks.add(Material.RED_MUSHROOM);
        whitelistedBlocks.add(Material.HUGE_MUSHROOM_1);
        whitelistedBlocks.add(Material.HUGE_MUSHROOM_2);
    }

    private void handleLoadChunks() {
        MeetupUtils.setMotd("&cSetup");

        Tasks.runLater(() -> {
            for(int x = -180; x < 180; x++) {
                for(int z = -180; z < 180; z++) {
                    Location location = new Location(Bukkit.getWorld("world"), x, 60, z);

                    if(!location.getChunk().isLoaded()) {
                        location.getWorld().loadChunk(x, z);
                    }
                }
            }

            VituzAPI.setMaxPlayers(60);
        }, 100L);

        Tasks.runLater(() -> {
            MeetupUtils.setMotd("&eVoting");
            GameManager.getGameData().setGenerated(true);

            Tasks.runLater(() -> {
                if(Bukkit.getOnlinePlayers().size() == 0) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                }
            }, 12000L);
        }, 200L);
    }
}
