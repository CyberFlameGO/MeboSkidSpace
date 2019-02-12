package secondlife.network.hub;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.hub.handler.CustomMovementHandler;
import secondlife.network.hub.managers.*;
import secondlife.network.hub.providers.NametagsProvider;
import secondlife.network.hub.providers.ScoreboardProvider;
import secondlife.network.hub.providers.TabProvider;
import secondlife.network.hub.utilties.HubUtils;
import secondlife.network.hub.utilties.profile.BukkitProfileStorage;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.providers.tab.VituzTab;
import secondlife.network.vituz.utilties.ConfigFile;
import secondlife.network.vituz.utilties.ServerUtils;
import secondlife.network.vituz.utilties.command.VituzCommandHandler;

/**
 * Created by Marko on 28.03.2018.
 */

@Getter
public class Hub extends JavaPlugin {

    @Getter
    private static Hub instance;

    private ConfigFile utilities;

    private AutoKickManager autoKickManager;
    private CountManager countManager;
    private HubManager hubManager;
    private MultiSpawnManager multiSpawnManager;
    private QueueManager queueManager;
    private SelectorManager selectorManager;
    private StaffSecurityManager staffSecurityManager;

    private BukkitProfileStorage storage;

    @Override
    public void onEnable() {
        instance = this;

        utilities = new ConfigFile(this, "utilities.yml");

        ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

        registerManagers();
        registerListeners();
        registerProviders();
    }

    private void registerManagers() {
        storage = new BukkitProfileStorage();
        autoKickManager = new AutoKickManager(this);
        countManager = new CountManager(this);
        hubManager = new HubManager(this);
        multiSpawnManager = new MultiSpawnManager(this);
        queueManager = new QueueManager(this);
        selectorManager = new SelectorManager(this);
        staffSecurityManager = new StaffSecurityManager(this);
    }

    private void registerListeners() {
        ServerUtils.getClassesInPackage(this, "secondlife.network.hub.listeners").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void registerProviders() {
        VituzNametag.registerProvider(new NametagsProvider());
        VituzTab.setLayoutProvider(new TabProvider());
        VituzScoreboard.setConfiguration(ScoreboardProvider.create());
        VituzCommandHandler.loadCommandsFromPackage(this, "secondlife.network.hub.commands");

        HubUtils.setupLoading();
    }
}
