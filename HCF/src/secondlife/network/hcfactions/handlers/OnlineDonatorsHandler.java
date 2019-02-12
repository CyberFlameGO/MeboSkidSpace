package secondlife.network.hcfactions.handlers;

import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;

/**
 * Created by Marko on 04.04.2018.
 */
public class OnlineDonatorsHandler extends Handler implements Listener {

    public OnlineDonatorsHandler(HCF plugin) {
        super(plugin);

        setupDonators();
    }

    public void setupDonators() {
        ArrayList<String> donators = new ArrayList<>();

        new BukkitRunnable() {
            public void run() {
                donators.clear();

                Bukkit.getOnlinePlayers().forEach(player -> {
                    if(player.hasPermission(Permission.DONOR_PERMISSION) && !player.hasPermission(Permission.STAFF_PERMISSION)) {
                        donators.add(Color.translate(VituzAPI.getPrefix(player).replace("&l", "")) + player.getName());
                    }
                });
                String donatorsMessage = Color.translate("&5Online &lDonators &7" + Msg.DOUBLE_ARROW_RIGHT + " &7" + Joiner.on("&7, &d").join(donators));
                String buyRankMessage = Color.translate("&5You can become this rank at &dstore.secondlife.network&5!");

                if(!donators.isEmpty()) {
                    Msg.sendMessage(donatorsMessage);
                    Msg.sendMessage(buyRankMessage);
                }
            }
        }.runTaskTimerAsynchronously(this.getInstance(), 6020L, 6000L);
    }
}
