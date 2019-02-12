package secondlife.network.practice.commands;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.NumberUtils;

import java.util.Arrays;
import java.util.UUID;

public class PremiumMatchesCommand extends Command {

    private final Practice plugin = Practice.getInstance();

    public PremiumMatchesCommand() {
        super("premiummatches");

        setAliases(Arrays.asList("premiumm", "pmm"));
        setUsage(CC.RED + "Usage: /premiummatches [player] [amount]");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Msg.NO_PERMISSION);
            return false;
        }

        if(args.length < 1) {
            sender.sendMessage(usageMessage);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        PracticeData profile;

        if(target == null) {
            profile = PracticeData.getByName(args[0]);
        } else {
            profile = PracticeData.getByName(target.getName());
        }

        if(!profile.isLoaded()) {
            profile.load();
        }

        if(!NumberUtils.isInteger(args[1])) return false;

        int amount = Integer.parseInt(args[1]);

        if(amount > 1000) return false;

        if(args.length == 3 && args[2].equalsIgnoreCase("RANK") && !profile.isMatches()) {
            profile.setMatches(true);
        }

        profile.setPremiumMatchesExtra(profile.getPremiumMatchesExtra() + amount);

        sender.sendMessage(CC.PRIMARY + "You gave " + CC.SECONDARY + target.getName() + CC.SECONDARY + amount + CC.PRIMARY + " premium matches!");
        return true;
    }

}
