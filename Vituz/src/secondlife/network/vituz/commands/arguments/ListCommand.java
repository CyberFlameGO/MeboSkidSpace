package secondlife.network.vituz.commands.arguments;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

/**
 * Created by Marko on 04.05.2018.
 */
public class ListCommand extends BaseCommand {

    public ListCommand(Vituz plugin) {
        super(plugin);

        this.command = "list";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            new BukkitRunnable() {
                public void run() {
                    StringBuilder rankBuilder = new StringBuilder();
                    StringBuilder nameBuilder = new StringBuilder();

                    ImmutableList.copyOf(Rank.getRanks()).reverse().forEach(rank -> {
                        if(rankBuilder.length() > 0) {
                            rankBuilder.append("&f, ");
                        }

                        rankBuilder.append(rank.getData().getColorPrefix()).append(rank.getData().getName());

                        Bukkit.getOnlinePlayers().forEach(players -> {
                            if (VituzAPI.getRank(players).equals(rank)) {
                                if (nameBuilder.length() > 0) {
                                    nameBuilder.append("&f, ");
                                }

                                if(sender instanceof Player) {
                                    Player player = (Player) sender;

                                    if(!players.hasPermission(Permission.STAFF_PERMISSION) && player.canSee(players)) {
                                        nameBuilder.append(VituzAPI.getFullNamePrefix(players));
                                    }
                                } else {
                                    nameBuilder.append(VituzAPI.getFullNamePrefix(players));
                                }
                            }
                        });
                    });
                    sender.sendMessage(Color.translate(rankBuilder.toString()));
                    sender.sendMessage(Color.translate("&f(" +  Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ") [" + nameBuilder.toString() + "&f]"));


                    /*for (Rank rank : ImmutableList.copyOf(Rank.getRanks()).reverse()) {
                        if(rankBuilder.length() > 0) {
                            rankBuilder.append("&f, ");
                        }

                        rankBuilder.append(rank.getData().getColorPrefix() + rank.getData().getName());

                        for(Player players : Bukkit.getOnlinePlayers()) {
                            if (VituzAPI.getRank(players).equals(rank)) {
                                if (nameBuilder.length() > 0) {
                                    nameBuilder.append("&f, ");
                                }

                                if(player.canSee(players)) {
                                    nameBuilder.append(VituzAPI.getFullNamePrefix(players));
                                }
                            }
                        }
                    }*/
                }
            }.runTaskAsynchronously(this.getPlugin());
        }
    }
}
