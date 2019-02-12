package secondlife.network.overpass;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.overpass.commands.OverpassCommand;
import secondlife.network.overpass.handler.CustomMovementHandler;
import secondlife.network.overpass.managers.OverpassManager;
import secondlife.network.overpass.managers.PremiumManager;
import secondlife.network.overpass.tasks.LoginTask;
import secondlife.network.vituz.utilties.ServerUtils;
import secondlife.network.vituz.utilties.Tasks;
import secondlife.network.vituz.utilties.command.VituzCommandHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 10.05.2018.
 */

@Getter
public class Overpass extends JavaPlugin {

    @Getter
    private static Overpass instance;

    private OverpassManager overpassManager;

    @Override
    public void onEnable() {
        instance = this;

        Tasks.runAsyncTimer(LoginTask::new, 100L, 100L);
        ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

        registerManagers();
        registerListeners();
        registerProviders();

        Bukkit.getMessenger().registerIncomingPluginChannel(this, "Premium", new PremiumManager());
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Auth");
    }

    private void registerManagers() {
        overpassManager = new OverpassManager(this);
    }

    private void registerListeners() {
        ServerUtils.getClassesInPackage(this, "secondlife.network.overpass.listeners").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void registerProviders() {
        Collections.singletonList(new OverpassCommand()).forEach(command -> registerCommand(command, command.getName()));
        VituzCommandHandler.loadCommandsFromPackage(this, "secondlife.network.overpass.commands");
    }

    public void registerCommand(Command command, String name) {
        Map<String, Command> commands = new HashMap<>();
        commands.put(name, command);

        commands.entrySet().forEach(entry -> {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Overpass", entry.getValue());
        });
    }
}
