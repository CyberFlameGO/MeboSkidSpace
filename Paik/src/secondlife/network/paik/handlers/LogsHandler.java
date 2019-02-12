package secondlife.network.paik.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.Handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marko on 05.05.2018.
 */
public class LogsHandler extends Handler implements Listener {

    public static File logsDirecotry;
    public static DecimalFormat dc;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
    public static Date date = new Date();

    public LogsHandler(Paik plugin) {
        super(plugin);

        logsDirecotry = new File(plugin.getDataFolder(), "logs");
        dc = new DecimalFormat("##.##");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlayerData stats = plugin.getPlayerDataManager().getPlayerData(player);

        if(stats != null && !stats.getLogs().isEmpty()) {
            File log = new File(logsDirecotry, player.getName() + ".txt");

            if(!log.exists()) {
                try {
                    log.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileWriter fw = new FileWriter(log, false);
                BufferedWriter bw = new BufferedWriter(fw);

                for(String string : stats.getLogs()) {
                    bw.write(string);
                    bw.newLine();
                }

                bw.close();
                fw.close();
                stats.getLogs().clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void log(Player player, String message, String check, String location, int ping, String tps) throws IOException {
        PlayerData stats = Paik.getInstance().getPlayerDataManager().getPlayerData(player);

        if(stats == null) return;

        String line = sdf.format(date) + " " + player.getName() + " " + check.toUpperCase() + " " + message + " LOCATION: " + location + " PING: " + ping + " TPS: " + tps;

        stats.getLogs().add(line);
    }
}
