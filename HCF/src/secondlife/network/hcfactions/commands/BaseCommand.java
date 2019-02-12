package secondlife.network.hcfactions.commands;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;

@Getter
public abstract class BaseCommand {

	public HCF instance;
	public Handler handler;
	
	public boolean forPlayerUseOnly;
	public String command;
	public String permission;

	public BaseCommand(HCF plugin) {
		this.instance = plugin;

		this.command = "";
		this.permission = "";

		this.forPlayerUseOnly = false;
	}

	public abstract void execute(CommandSender sender, String[] args);
}
