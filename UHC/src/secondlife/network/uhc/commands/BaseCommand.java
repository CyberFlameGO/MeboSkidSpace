package secondlife.network.uhc.commands;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import secondlife.network.uhc.UHC;

@Getter
public abstract class BaseCommand {

	protected UHC instance;
	protected UHC plugin = UHC.getInstance();

	public boolean forPlayerUseOnly;
	public String command;
	public String permission;

	public BaseCommand(UHC plugin) {
		this.instance = plugin;

		this.command = "";
		this.permission = "";

		this.forPlayerUseOnly = false;
	}

	public abstract void execute(CommandSender sender, String[] args);
}
