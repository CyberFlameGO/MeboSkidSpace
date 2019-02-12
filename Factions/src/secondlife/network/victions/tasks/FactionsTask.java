package secondlife.network.victions.tasks;

import com.massivecraft.factions.FPlayers;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.victions.Victions;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

/**
 * Created by Marko on 14.07.2018.
 */
public class FactionsTask extends BukkitRunnable {

    public FactionsTask() {
        runTaskTimerAsynchronously(Victions.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            FactionsData data = FactionsData.getByName(player.getName());

            if(data.isHomeActive(player)) {
                player.sendMessage(Color.translate("&eYou will be teleported in &d" + DurationFormatUtils.formatDurationWords(data.getHomeMillisecondsLeft(player), true, false) + "&e."));
            }

            if(data.isLogoutActive(player)) {
                player.sendMessage(Color.translate("&eYou will be logged out in &d" + DurationFormatUtils.formatDurationWords(data.getLoogutMillisecondsLeft(player), true, false) + "&e."));
            }

            if(player.getAllowFlight() && !player.hasPermission(Permission.STAFF_PERMISSION)) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage(Color.translate("&eYour flight has been disabled because you are in end."));
            }

            FPlayers.getInstance().getAllFPlayers().forEach(fPlayer -> {
                if(fPlayer != null && data.isFactionFly() && !fPlayer.getFaction().isWilderness() && !fPlayer.isInOwnTerritory()) {
                    data.setFactionFly(false);

                    fPlayer.getPlayer().setAllowFlight(false);
                    fPlayer.getPlayer().setFlying(false);
                    fPlayer.getPlayer().sendMessage(Color.translate("&dFaction Fly &edisabled due to leaving your faction's teritory&e."));
                }
            });
        });
    }
}
