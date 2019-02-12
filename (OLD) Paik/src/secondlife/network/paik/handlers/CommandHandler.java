package secondlife.network.paik.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import secondlife.network.paik.Paik;
import secondlife.network.paik.commands.KillauraCommand;
import secondlife.network.paik.commands.LogsCommand;
import secondlife.network.paik.commands.OCMCCommand;
import secondlife.network.paik.commands.PaikBanCommand;
import secondlife.network.paik.commands.PaikCommand;
import secondlife.network.paik.commands.PingCommand;
import secondlife.network.paik.commands.zBaseCommand;
import secondlife.network.paik.utils.Handler;
import secondlife.network.paik.utils.Message;

public class CommandHandler extends Handler implements CommandExecutor {
	
	public static List<zBaseCommand> commands;

	public CommandHandler(Paik plugin) {
		super(plugin);
		
		commands = new ArrayList<zBaseCommand>();

		commands.add(new KillauraCommand(plugin));
		commands.add(new LogsCommand(plugin));
		commands.add(new OCMCCommand(plugin));
		commands.add(new PaikBanCommand(plugin));
		commands.add(new PaikCommand(plugin));
		commands.add(new PingCommand(plugin));
		
		for (zBaseCommand command : commands) {
			this.getInstance().getCommand(command.command).setExecutor(this);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for (zBaseCommand command : commands) {
			if (cmd.getName().equalsIgnoreCase(command.command)) {
				if (((sender instanceof ConsoleCommandSender)) && (command.forPlayerUseOnly)) {
					sender.sendMessage(Message.COMMANDS_FOR_PLAYER_USE_ONLY.toString());
					return true;
				}
				if ((!sender.hasPermission(command.permission)) && (!command.permission.equals(""))) {
					sender.sendMessage(Message.COMMANDS_NO_PERMISSION_MESSAGE.toString());
					return true;
				}
				command.execute(sender, args);
				return true;
			}
		}
		return true;
	}
}
