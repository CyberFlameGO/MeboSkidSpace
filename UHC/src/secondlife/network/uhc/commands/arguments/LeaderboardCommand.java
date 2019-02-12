package secondlife.network.uhc.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.managers.PlayerManager;

public class LeaderboardCommand extends BaseCommand {

    public LeaderboardCommand(UHC plugin) {
        super(plugin);

        this.command = "leaderboard";
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PlayerManager.getLeaderboard((Player) sender);
    }
}
