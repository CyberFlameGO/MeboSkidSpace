package secondlife.network.paik;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.paik.commands.PaikCommand;
import secondlife.network.paik.commands.sub.*;
import secondlife.network.paik.handlers.*;
import secondlife.network.paik.handlers.LogsHandler;
import secondlife.network.paik.handlers.managers.AlertsManager;
import secondlife.network.paik.handlers.managers.PlayerDataManager;
import secondlife.network.paik.utilties.DirectoryUtils;
import secondlife.network.paik.utilties.command.CommandFramework;

@Getter
@Setter
public class Paik extends JavaPlugin {

    @Getter private static Paik instance;

    private PlayerDataManager playerDataManager;
    private AlertsManager alertsManager;
    private double rangeVl = 30.0;

    private CommandFramework framework;

    public void onEnable() {
        instance = this;

        this.registerHandlers();
        this.registerManagers();
        this.registerListeners();
        this.registerCommands();
    }
    
    public boolean isAntiCheatEnabled() {
        return MinecraftServer.getServer().tps1.getAverage() > 19.0 && MinecraftServer.LAST_TICK_TIME + 100L > System.currentTimeMillis();
    }
    
    private void registerHandlers() {
        ClubSpigot.INSTANCE.addPacketHandler(new CustomPacketHandler(this));
        ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler(this));

        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeHandler(this));
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
    
    private void registerManagers() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "AutoBan");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Alerts");

        DirectoryUtils.registerDirectory();

        this.framework = new CommandFramework(this);
        this.alertsManager = new AlertsManager(this);
        this.playerDataManager = new PlayerDataManager(this);
    }
    
    private void registerListeners() {
        new PlayerHandler(this);
        new ModListHandler(this);
        new LogsHandler(this);
    }
    
    private void registerCommands() {
        new PaikCommand();
        new LogsCommand();
        new ToggleCommand();
        new PaikBanCommand();
        new RangeCommand();
        new PaikFunCommand();
    }
}
