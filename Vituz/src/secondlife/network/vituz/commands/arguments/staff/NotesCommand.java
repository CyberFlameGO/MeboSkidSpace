package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;

/**
 * Created by Marko on 09.05.2018.
 */
public class NotesCommand extends BaseCommand {

    public NotesCommand(Vituz plugin) {
        super(plugin);

        this.command = "notes";
        this.permission = Permission.STAFF_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sendUsage(sender);
        } else {
            if(args[0].equalsIgnoreCase("check")) {
                Player target = Bukkit.getPlayer(args[1]);

                PlayerData data;

                if(target == null) {
                    data = PlayerData.getByName(args[1]);
                } else {
                    data = PlayerData.getByName(target.getName());
                }

                if (!data.isLoaded()) {
                    data.load();
                }

                sender.sendMessage(Color.translate("&eDisplaying notes of &d" + data.getName() + "&e."));

                new BukkitRunnable() {
                    public void run() {
                        if(data.getNotes().isEmpty()) {
                            sender.sendMessage(Color.translate("&cThat player don't have any notes set yet."));
                        } else {
                            for(int i = 0; i < data.getNotes().size(); i++) {
                                if(!data.getNotes().get(i).equalsIgnoreCase("none")) {
                                    sender.sendMessage(Color.translate("&7" + (i + 1) + ") &d" + data.getNotes().get(i)));
                                } else {
                                    sender.sendMessage(Color.translate("&cThat player don't have any notes set yet."));
                                }
                            }
                        }
                    }
                }.runTaskAsynchronously(this.getPlugin());
            } else if(args[0].equalsIgnoreCase("add")) {
                Player target = Bukkit.getPlayer(args[1]);

                PlayerData data;

                if(target == null) {
                    data = PlayerData.getByName(args[1]);
                } else {
                    data = PlayerData.getByName(target.getName());
                }

                if (!data.isLoaded()) {
                    data.load();
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }

                String note = sb.toString().trim();

                sender.sendMessage(Color.translate("&eYou have added &7(&d" + note.toString() + "&7) &enote to &d" + data.getName() + "&e."));

                data.getNotes().remove("none");
                data.getNotes().add(note);
                data.save();
            } else if(args[0].equalsIgnoreCase("remove")) {
                Player target = Bukkit.getPlayer(args[1]);

                PlayerData data;

                if(target == null) {
                    data = PlayerData.getByName(args[1]);
                } else {
                    data = PlayerData.getByName(target.getName());
                }

                if (!data.isLoaded()) {
                    data.load();
                }

                if(!NumberUtils.isInteger(args[2])) {
                    sender.sendMessage(Color.translate("&cInvalid number."));
                    return;
                }

                int number = Integer.valueOf(args[2]);
                int absNumber = Math.abs(number);

                if(number == 0) {
                    sender.sendMessage(Color.translate("&cInvalid number."));
                } else {
                    if(data.getNotes().size() < absNumber) {
                        sender.sendMessage(Color.translate("&cThat note doesn't exists."));
                    } else {
                        data.getNotes().remove(absNumber - 1);
                        data.save();

                        sender.sendMessage(Color.translate("&eYou have removed note of &d" + data.getName() + " &7(&d" + absNumber + "&7)&e."));
                    }
                }
            }
        }
    }

    public void sendUsage(CommandSender sender) {
        sender.sendMessage(Color.translate("&cUsage: /notes <check|add|remove> <player>"));
    }
}
