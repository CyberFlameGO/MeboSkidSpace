package secondlife.network.vituz.commands.arguments.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;

public class ToggleChatCommand extends BaseCommand {

    public ToggleChatCommand(Vituz plugin) {
        super(plugin);

        this.command = "togglechat";
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        PlayerData data = PlayerData.getByName(player.getName());

        data.setToggleChat(!data.isToggleChat());

        player.sendMessage(Color.translate("&eYou have " + (data.isToggleChat() ? "&aEnabled" : "&cDisabled") +
                " &echat messages."));
    }
}