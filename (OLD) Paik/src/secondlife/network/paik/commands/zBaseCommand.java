package secondlife.network.paik.commands;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import secondlife.network.paik.Paik;

public abstract class zBaseCommand {

	@Getter public Paik instance;
	public boolean forPlayerUseOnly;

	public String command;
	public String permission;

	public zBaseCommand(Paik plugin) {
		this.instance = plugin;

		this.command = "";
		this.permission = "";

		this.forPlayerUseOnly = false;
	}

	public abstract void execute(CommandSender sender, String[] args);
}
