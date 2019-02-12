package secondlife.network.victions;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.victions.handler.CustomMovemomentHandler;
import secondlife.network.victions.managers.*;
import secondlife.network.victions.providers.ScoreboardProvider;
import secondlife.network.victions.tasks.FactionsTask;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.utilties.ConfigFile;
import secondlife.network.vituz.utilties.ServerUtils;
import secondlife.network.vituz.utilties.command.VituzCommandHandler;

/**
 * Created by Marko on 14.07.2018.
 */

@Getter
public class Victions extends JavaPlugin {

    @Getter
    private static Victions instance;

    private EntityLimiterManager entityLimiterManager;
    private FactionsManager factionsManager;
    private GlassManager glassManager;
    private KitManager kitManager;
    private MobStackManager mobStackManager;
    private PlayerManager playerManager;
    private PotionLimitManager potionLimitManager;
    private SellWandManager sellWandManager;
    private TeleportManager teleportManager;

    private ConfigFile mainConfig, kitsConfig;

    @Override
    public void onEnable() {
        instance = this;

        mainConfig = new ConfigFile(this, "config.yml");
        kitsConfig = new ConfigFile(this, "kits.yml");

        VictionsAPI.hook();

        registerManagers();
        registerListeners();
        registerProviders();

        new FactionsTask();
        ClubSpigot.INSTANCE.addMovementHandler(new CustomMovemomentHandler());
    }

    @Override
    public void onDisable() {
        kitManager.saveKits();
        mobStackManager.handleOnDisable();
    }

    private void registerManagers() {
        entityLimiterManager = new EntityLimiterManager(this);
        factionsManager = new FactionsManager(this);
        glassManager = new GlassManager(this);
        kitManager = new KitManager(this);
        mobStackManager = new MobStackManager(this);
        playerManager = new PlayerManager(this);
        potionLimitManager = new PotionLimitManager(this);
        sellWandManager = new SellWandManager(this);
        teleportManager = new TeleportManager(this);
    }

    private void registerProviders() {
        VituzScoreboard.setConfiguration(ScoreboardProvider.create());
        VituzCommandHandler.loadCommandsFromPackage(this, "secondlife.network.victions.commands");
    }

    private void registerListeners() {
        ServerUtils.getClassesInPackage(this, "secondlife.network.victions.listeners").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
