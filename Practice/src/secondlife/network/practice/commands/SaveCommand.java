package secondlife.network.practice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import secondlife.network.practice.Practice;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class SaveCommand extends Command {

    private final Practice plugin = Practice.getInstance();

    public SaveCommand() {
        super("save");

        setUsage(CC.RED + "Usage: /save");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if(!PlayerUtil.testPermission(commandSender, Permission.OP_PERMISSION)) return true;

        Practice.getInstance().getArenaManager().saveArenas();
        Practice.getInstance().getKitManager().saveKits();
        Practice.getInstance().getSpawnManager().saveConfig();

        commandSender.sendMessage(Color.translate("&dSaved data!"));
        return true;
    }

}
