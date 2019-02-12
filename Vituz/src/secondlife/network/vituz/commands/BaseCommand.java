package secondlife.network.vituz.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import secondlife.network.vituz.Vituz;

@Getter
public abstract class BaseCommand {

	protected Vituz plugin;

	public boolean forPlayerUseOnly;
	public String command;
	public String permission;

	public BaseCommand(Vituz plugin) {
		this.plugin = plugin;

		this.command = "";
		this.permission = "";

		this.forPlayerUseOnly = false;
	}

	public abstract void execute(CommandSender sender, String[] args);
}
