package secondlife.network.vituz.commands.arguments.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;

public class SoundsCommand extends BaseCommand {

    public SoundsCommand(Vituz plugin) {
        super(plugin);

        this.command = "sounds";
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        PlayerData data = PlayerData.getByName(player.getName());

        data.setSounds(!data.isSounds());

        player.sendMessage(Color.translate("&eYou have " + (data.isSounds() ? "&aEnabled" : "&cDisabled") +
                " &esounds."));
    }
}