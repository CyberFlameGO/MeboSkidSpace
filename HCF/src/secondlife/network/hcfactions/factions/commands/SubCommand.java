package secondlife.network.hcfactions.factions.commands;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import secondlife.network.hcfactions.HCF;

@Getter
public abstract class SubCommand {
	
	private HCF instance;
	
	public String[] aliases;
	public String permission;
	public boolean forPlayerUseOnly;

	public SubCommand(HCF plugin) {
		this.instance = plugin;
		
		this.aliases = new String[0];
		this.permission = "";
		this.forPlayerUseOnly = false;
	}

	public abstract void execute(CommandSender sender, String[] args);
}
