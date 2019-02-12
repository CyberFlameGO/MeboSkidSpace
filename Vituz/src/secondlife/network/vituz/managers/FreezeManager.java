package secondlife.network.vituz.managers;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Manager;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

@Getter
public class FreezeManager extends Manager {

    private boolean frozen = false;

    public FreezeManager(Vituz plugin) {
        super(plugin);
    }

    public void handleFreezeServer(CommandSender sender) {
        frozen = !frozen;
        Msg.sendMessage(Color.translate("&c&lServer has been " + (frozen ? "&4&lfrozen" : "&a&lunfrozen") + " &c&lby &4&l" + sender.getName() + "&c&l."));
    }

    public void handleFreeze(Player player, Player target) {
        PlayerData data = PlayerData.getByName(target.getName());

        if(data.isFrozen()) {
            data.setFrozen(false);
            Msg.sendMessage(Color.translate("&2&l" + target.getName() + " &ahas been un-frozen by " + player.getDisplayName() + "&a."), Permission.STAFF_PERMISSION);
            return;
        }

        data.setFrozen(true);
        Msg.sendMessage(Color.translate("&4&l" + target.getName() + " &chas been frozen by " + player.getDisplayName() + "&c."), Permission.STAFF_PERMISSION);
    }

    public void setFrozen(Player target) {
        PlayerData data = PlayerData.getByName(target.getName());
        data.setFrozen(true);
        Msg.sendMessage(Color.translate("&4&l" + target.getName() + " &chas been frozen&c."), Permission.STAFF_PERMISSION);
    }

    public void handleMove(Player player, Location from, Location to) {
        if(!PlayerData.getByName(player.getName()).isFrozen()) return;

        if(from.getX() != to.getX() || from.getZ() != to.getZ()) {
            player.teleport(from);
        }
    }
}
