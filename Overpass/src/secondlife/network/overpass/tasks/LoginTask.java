package secondlife.network.overpass.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.overpass.Overpass;
import secondlife.network.overpass.data.OverpassData;
import secondlife.network.vituz.utilties.Color;

/**
 * Created by Marko on 10.05.2018.
 */
public class LoginTask extends BukkitRunnable {

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player));
    }

    private void sendMessage(Player player) {
        OverpassData overpassData = OverpassData.getByName(player.getName());

        if(overpassData.isNeedLogin() && overpassData.isFullyRegistered()) {
            player.sendMessage(Color.translate("&ePlease use &d/login <password> &eto login."));
        } else if(!overpassData.isRegister()) {
            player.sendMessage(Color.translate("&ePlease use &d/register <password> <password> <email> &eto register."));
        } else if(overpassData.isNeedToEnterCode() && !overpassData.isFullyRegistered()) {
            player.sendMessage(Color.translate("&ePlease use &d/code <code> &eto complete your registration."));
            player.sendMessage(Color.translate("&eYou can find your code at your email."));
            player.sendMessage(Color.translate("&eMake sure to check your spam folder and other folders in your email!"));
        }
    }
}
