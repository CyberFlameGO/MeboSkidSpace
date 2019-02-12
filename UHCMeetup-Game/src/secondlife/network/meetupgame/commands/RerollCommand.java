package secondlife.network.meetupgame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.GameData;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.states.GameState;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 23.07.2018.
 */
public class RerollCommand {

    private static MeetupGame plugin = MeetupGame.getInstance();

    @Command(names = {"reroll"})
    public static void handleLeave(Player player) {
        GameData gameData = GameManager.getGameData();

        if(!gameData.getGameState().equals(GameState.STARTING)) {
            player.sendMessage(Color.translate("&cYou can't use this command now."));
            return;
        }

        MeetupData data = MeetupData.getByName(player.getName());

        if(data.getRerolls() <= 0) {
            player.sendMessage(Color.translate("&cYou don't have any rerolls!"));
            return;
        }

        data.setRerolls(data.getRerolls() - 1);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        plugin.getKitsManager().handleGiveKit(player);
    }

    @Command(names = {"reroll add"})
    public static void handleLeave(CommandSender sender, @Parameter(name = "name") String name, @Parameter(name = "amount") int amount) {
        Player target = Bukkit.getPlayer(name);
        MeetupData data;

        if(target == null) {
            data = MeetupData.getByName(name);
        } else {
            data = MeetupData.getByName(target.getName());
        }

        if(amount >= 1000) {
            sender.sendMessage(Color.translate("&cYo bro you broke the limits!"));
            return;
        }

        data.setRerolls(data.getRerolls() + amount);
        sender.sendMessage(Color.translate("&eYou gave &d" + amount + " &ererolls to &d" + name + " &eand now he has &d" + data.getRerolls() + " &ererolls."));
    }
}
