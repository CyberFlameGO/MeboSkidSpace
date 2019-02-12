package secondlife.network.vituz.ranks.commands.profile;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.ranks.redis.RankPublisher;
import secondlife.network.vituz.ranks.redis.RankSubscriberAction;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

/**
 * Created by Marko on 04.04.2018.
 */
public class AddPermissionCommand extends BaseCommand {

    public AddPermissionCommand(Vituz plugin) {
        super(plugin);

        this.command = "addpermission";
        this.permission = Permission.OP_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Color.translate("&cUsage: /addpermission <player> <permission>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        RankData profile;

        if(target == null) {
            profile = RankData.getByName(args[0]);
        } else {
            profile = RankData.getByName(target.getName());
        }

        if (!profile.isLoaded()) {
            profile.load();
        }

        String permission = args[1].toLowerCase();

        if(profile.getPermissions().contains(permission)) {
            sender.sendMessage(Color.translate("&cPlayer named '" + args[0] + "' already has permission node '" + permission + "'."));
            return;
        }

        profile.getPermissions().add(permission);
        profile.setupAtatchment();

        if(target == null) {
            JsonObject object = new JsonObject();
            object.addProperty("action", RankSubscriberAction.ADD_PLAYER_PERMISSION.name());

            JsonObject payload = new JsonObject();

            payload.addProperty("player", sender.getName());
            payload.addProperty("name", args[0]);
            payload.addProperty("permission", permission);

            object.add("payload", payload);

            RankPublisher.write(object.toString());
        }

        profile.save();
        sender.sendMessage(Color.translate("&aPermission '" + permission + "' successfully given to player named '" + args[0] + "'."));
    }
}
