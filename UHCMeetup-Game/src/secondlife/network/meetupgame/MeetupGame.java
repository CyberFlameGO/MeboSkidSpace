package secondlife.network.meetupgame;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.meetupgame.managers.*;
import secondlife.network.meetupgame.providers.NametagsProvider;
import secondlife.network.meetupgame.providers.ScoreboardProvider;
import secondlife.network.meetupgame.providers.TabProvider;
import secondlife.network.meetupgame.scenario.ScenarioListeners;
import secondlife.network.meetupgame.tasks.VoteTask;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.providers.tab.VituzTab;
import secondlife.network.vituz.utilties.ConfigFile;
import secondlife.network.vituz.utilties.ServerUtils;
import secondlife.network.vituz.utilties.command.VituzCommandHandler;

/**
 * Created by Marko on 23.07.2018.
 */

@Getter
public class MeetupGame extends JavaPlugin {

    @Getter
    private static MeetupGame instance;

    private BorderManager borderManager;
    private GameManager gameManager;
    private GlassManager glassManager;
    private InventoryManager inventoryManager;
    private KitsManager kitsManager;
    private ScenarioManager scenarioManager;
    private SpectatorManager spectatorManager;
    private VanishManager vanishManager;
    private VoteManager voteManager;

    private ConfigFile kits;

    @Override
    public void onEnable() {
        instance = this;

        kits = new ConfigFile(this, "kits.yml");

        registerManagers();
        registerListeners();
        registerProviders();
        registerOther();

        new VoteTask();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Announce");
    }

    @Override
    public void onDisable() {
        MeetupUtils.setMotd("&cSetup");
        kits.save();
        MeetupUtils.deleteWorld();
    }

    private void registerManagers() {
        borderManager = new BorderManager(this);
        gameManager = new GameManager(this);
        glassManager = new GlassManager(this);
        inventoryManager = new InventoryManager(this);
        kitsManager = new KitsManager(this);
        scenarioManager = new ScenarioManager(this);
        spectatorManager = new SpectatorManager(this);
        vanishManager = new VanishManager(this);
        voteManager= new VoteManager(this);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ScenarioListeners(), this);

        ServerUtils.getClassesInPackage(this, "secondlife.network.meetupgame.listeners").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private void registerProviders() {
        VituzTab.setLayoutProvider(new TabProvider());
        VituzNametag.registerProvider(new NametagsProvider());
        VituzScoreboard.setConfiguration(ScoreboardProvider.create());
        VituzCommandHandler.loadCommandsFromPackage(this, "secondlife.network.meetupgame.commands");
    }

    private void registerOther() {
        World uhc = Bukkit.getWorld("world");
        uhc.setGameRuleValue("doMobSpawning", "false");
        uhc.setGameRuleValue("doDaylightCycle", "false");
        uhc.setGameRuleValue("naturalRegeneration", "false");
        uhc.setGameRuleValue("doFireTick", "false");
        uhc.setGameRuleValue("difficulty", "0");
        uhc.setTime(0);
    }
}
