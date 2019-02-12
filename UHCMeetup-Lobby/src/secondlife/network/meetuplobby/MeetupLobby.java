package secondlife.network.meetuplobby;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.meetuplobby.managers.InventoryManager;
import secondlife.network.meetuplobby.providers.NametagsProvider;
import secondlife.network.meetuplobby.providers.ScoreboardProvider;
import secondlife.network.meetuplobby.providers.TabProvider;
import secondlife.network.meetuplobby.utilties.MeetupUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.providers.tab.VituzTab;
import secondlife.network.vituz.utilties.ConfigFile;
import secondlife.network.vituz.utilties.ServerUtils;
import secondlife.network.vituz.utilties.command.VituzCommandHandler;

/**
 * Created by Marko on 22.07.2018.
 */

@Getter
public class MeetupLobby extends JavaPlugin {

    @Getter
    private static MeetupLobby instance;

    private InventoryManager inventoryManager;
    private ConfigFile config;

    @Override
    public void onEnable() {
        instance = this;

        config = new ConfigFile(this, "config.yml");

        registerManagers();
        registerListeners();
        registerProviders();

        MeetupUtils.setupLoading();
    }

    @Override
    public void onDisable() {
        config.save();
    }

    private void registerManagers() {
        inventoryManager = new InventoryManager(this);
    }

    private void registerListeners() {
        ServerUtils.getClassesInPackage(this, "secondlife.network.meetuplobby.listeners").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
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
        VituzCommandHandler.loadCommandsFromPackage(this, "secondlife.network.meetuplobby.commands");
    }
}
