package secondlife.network.vituz.ranks.commands.profile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.UUID;

/**
 * Created by Marko on 04.04.2018.
 */
public class ListPermissionCommand extends BaseCommand {

    public ListPermissionCommand(Vituz plugin) {
        super(plugin);

        this.command = "listpermissions";
        this.permission = Permission.OP_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Color.translate("&cUsage: /listpermissions <player>"));
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

        sender.sendMessage(ChatColor.GREEN + "Listing permissions of " + ChatColor.translateAlternateColorCodes('&', new StringBuilder().append(profile.getActiveGrant().getRank().getData().getPrefix()).append(args[0]).append(profile.getActiveGrant().getRank().getData().getSuffix()).toString()) + ChatColor.GREEN + ":");

        if(!profile.getPermissions().isEmpty()) {
            sender.sendMessage(ChatColor.GREEN + "Base permissions:");

            for(String permission : profile.getPermissions()) {
                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + permission);
            }
        }

        Rank rank = profile.getActiveGrant().getRank();
        int count;

        if(rank != null) {
            count = 0;
            for(String permission : rank.getPermissions()) {
                if(count == 0) {
                    sender.sendMessage(ChatColor.GREEN + "Permissions inherited from " + rank.getData().getColorPrefix() + rank.getData().getName() + ChatColor.GREEN + ":");
                }

                count++;
                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + permission);

                if((count > 0) && (count == rank.getPermissions().size())) {
                    count = 0;

                    for(UUID otherId : rank.getInheritance()) {
                        if(count == 0) {
                            sender.sendMessage(ChatColor.GREEN + "Also inherits permissions from the following ranks:");
                        }

                        count++;

                        Rank other = Rank.getByUuid(otherId);

                        if(other != null) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " * " + other.getData().getColorPrefix() + other.getData().getName());
                        }
                    }
                }
            }
        }
    }
}
