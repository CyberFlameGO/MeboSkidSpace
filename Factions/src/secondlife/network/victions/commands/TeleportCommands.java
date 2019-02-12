package secondlife.network.victions.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.victions.Victions;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Tasks;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 28.07.2018.
 */
public class TeleportCommands {

    private static Victions plugin = Victions.getInstance();

    @Command(names = {"tpa"})
    public static void handleTpa(Player player, @Parameter(name = "name") Player target) {
        if(plugin.getTeleportManager().getTpaUsers().containsKey(player.getUniqueId())
                || plugin.getTeleportManager().getTpaUsers().containsKey(target.getUniqueId())
                || plugin.getTeleportManager().getTpaUsers().containsValue(player.getUniqueId())
                || plugin.getTeleportManager().getTpaUsers().containsValue(target.getUniqueId())

                || plugin.getTeleportManager().getTpaHereUsers().containsKey(player.getUniqueId())
                || plugin.getTeleportManager().getTpaHereUsers().containsKey(target.getUniqueId())
                || plugin.getTeleportManager().getTpaHereUsers().containsValue(player.getUniqueId())
                || plugin.getTeleportManager().getTpaHereUsers().containsValue(target.getUniqueId())) {

            player.sendMessage(Color.translate("&cYou or " + target.getName() + " has already a pending request."));
            return;
        }

        plugin.getTeleportManager().getTpaUsers().put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(Color.translate("&eYou asked to teleport to the &d" + target.getName() + "&e."));
        target.sendMessage(Color.translate("&d" + player.getName() + " &ehas asked to teleport to you."));

        ActionMessage actionMessage = new ActionMessage();
        actionMessage.addText("&aClick here to agree")
                .setClickEvent(ActionMessage.ClickableType.RunCommand, "/tpaccept");
        actionMessage.addText("&cClick here to disagree")
                .setClickEvent(ActionMessage.ClickableType.RunCommand, "/tpdeny");

        actionMessage.sendToPlayer(target);

        Tasks.runLater(() -> {
            if(plugin.getTeleportManager().getTpaUsers().containsKey(target.getUniqueId())) {
                plugin.getTeleportManager().getTpaUsers().remove(target.getUniqueId());

                player.sendMessage(Color.translate("&d" + target.getName() + " &edidn't answer your teleport request."));
                target.sendMessage(Color.translate("&eYou didn't answer &d" + player.getName() + " &eteleport request."));
                player.sendMessage(Color.translate("&eYou asked to teleport to &d" + target.getName() + "&e."));
            }
        }, 35 * 20L);
    }

    @Command(names = {"tpaccept"})
    public static void handleTpaccept(Player player) {
        Player target;
        
        if(plugin.getTeleportManager().getTpaUsers().containsKey(player.getUniqueId())) {
            target = Bukkit.getPlayer(plugin.getTeleportManager().getTpaUsers().get(player.getUniqueId()));

            if(target != null) {
                target.sendMessage(Color.translate("&d" + player.getName() + " &ehas accepted your teleport request."));
                player.sendMessage(Color.translate("&eYou have accepted &d" + target.getName() + "'s &eteleport request."));

                player.sendMessage(Color.translate("&eYou will be teleported in &d3 seconds&e."));
                FactionsData.getByName(player.getName()).setNeedToTeleport(true);

                Tasks.runLater(() -> {
                    if(target.isOnline() && player.isOnline() && FactionsData.getByName(player.getName()).isNeedToTeleport()) {
                        target.teleport(player);
                        plugin.getTeleportManager().getTpaUsers().remove(player.getUniqueId());
                        FactionsData.getByName(player.getName()).setNeedToTeleport(false);
                    }
                }, 60L);
            } else {
                player.sendMessage(ChatColor.RED + "No player with the name '" + target.getName() + "' found.");
                plugin.getTeleportManager().getTpaUsers().remove(player.getUniqueId());
            }
        } else if (plugin.getTeleportManager().getTpaHereUsers().containsKey(player.getUniqueId())) {
            target = Bukkit.getPlayer(plugin.getTeleportManager().getTpaHereUsers().get(player.getUniqueId()));

            if(target != null) {
                target.sendMessage(Color.translate("&d" + player.getName() + " &ehas accepted your teleport request."));
                player.sendMessage(Color.translate("&eYou have accepted &d" + target.getName() + "'s &eteleport request."));

                player.sendMessage(Color.translate("&eYou will be teleported in &d3 seconds&e."));
                FactionsData.getByName(player.getName()).setNeedToTeleport(true);

                Tasks.runLater(() -> {
                    if(target.isOnline() && player.isOnline() && FactionsData.getByName(player.getName()).isNeedToTeleport()) {
                        player.teleport(target);
                        plugin.getTeleportManager().getTpaHereUsers().remove(player.getUniqueId());
                        FactionsData.getByName(player.getName()).setNeedToTeleport(false);
                    }
                }, 60L);
            } else {
                player.sendMessage(ChatColor.RED + "No player with the name '" + target.getName() + "' found.");
                plugin.getTeleportManager().getTpaHereUsers().remove(player.getUniqueId());
            }
        } else {
            player.sendMessage(Color.translate("&eYou don't have any pending teleport requests."));
        }
    }

    @Command(names = {"tpahere"})
    public static void handleTpaHere(Player player, @Parameter(name = "name") Player target) {
        if(plugin.getTeleportManager().getTpaUsers().containsKey(player.getUniqueId())
                || plugin.getTeleportManager().getTpaUsers().containsKey(target.getUniqueId())
                || plugin.getTeleportManager().getTpaUsers().containsValue(player.getUniqueId())
                || plugin.getTeleportManager().getTpaUsers().containsValue(target.getUniqueId())

                || plugin.getTeleportManager().getTpaHereUsers().containsKey(player.getUniqueId())
                || plugin.getTeleportManager().getTpaHereUsers().containsKey(target.getUniqueId())
                || plugin.getTeleportManager().getTpaHereUsers().containsValue(player.getUniqueId())
                || plugin.getTeleportManager().getTpaHereUsers().containsValue(target.getUniqueId())) {

            player.sendMessage(Color.translate("&cYou or " + target.getName() + " has already a pending request."));
            return;
        }

        plugin.getTeleportManager().getTpaHereUsers().put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(Color.translate("&eYou asked to teleport here &d" + target.getName() + "&e."));
        target.sendMessage(Color.translate("&d" + player.getName() + " &ehas asked to teleport to you."));

        ActionMessage actionMessage = new ActionMessage();
        actionMessage.addText("&aClick here to agree")
                .setClickEvent(ActionMessage.ClickableType.RunCommand, "/tpaccept");
        actionMessage.addText("&cClick here to disagree")
                .setClickEvent(ActionMessage.ClickableType.RunCommand, "/tpdeny");

        actionMessage.sendToPlayer(target);

        Tasks.runLater(() -> {
            if(plugin.getTeleportManager().getTpaHereUsers().containsKey(target.getUniqueId())) {
                plugin.getTeleportManager().getTpaHereUsers().remove(target.getUniqueId());

                player.sendMessage(Color.translate("&d" + target.getName() + " &edidn't answer your teleport request."));
                target.sendMessage(Color.translate("&eYou didn't answer &d" + player.getName() + " &eteleport request."));
                player.sendMessage(Color.translate("&eYou asked to teleport to &d" + target.getName() + "&e."));
            }
        }, 35 * 20L);
    }

    @Command(names = {"tpdeny", "tpadeny", "denytpa"})
    public static void handleDeny(Player player) {
        Player target;

        if(plugin.getTeleportManager().getTpaUsers().containsKey(player.getUniqueId())) {
            target = Bukkit.getPlayer(plugin.getTeleportManager().getTpaUsers().get(player.getUniqueId()));
            plugin.getTeleportManager().getTpaUsers().remove(player.getUniqueId());
        } else if(plugin.getTeleportManager().getTpaHereUsers().containsKey(player.getUniqueId())) {
            target = Bukkit.getPlayer(plugin.getTeleportManager().getTpaHereUsers().get(player.getUniqueId()));
            plugin.getTeleportManager().getTpaHereUsers().remove(player.getUniqueId());
        } else {
            player.sendMessage(Color.translate("&eYou don't have any pending teleport requests."));
            return;
        }

        target.sendMessage(Color.translate("&d" + player.getName() + " &ehas denied your teleport request."));
        player.sendMessage(Color.translate("&eYou denied " + target.getName() + "'s &eteleport request."));
    }
}
