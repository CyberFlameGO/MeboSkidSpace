package secondlife.network.hcfactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.arguments.*;
import secondlife.network.hcfactions.staff.commands.StaffModeCommand;
import secondlife.network.hcfactions.staff.commands.VanishCommand;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends Handler implements CommandExecutor {
	
	private List<BaseCommand> commands;

	public CommandHandler(HCF plugin) {
		super(plugin);

		this.commands = new ArrayList<>();
		
		this.commands.add(new BalanceCommand(plugin));
		this.commands.add(new ChestCommand(plugin));
		this.commands.add(new CoordsCommand(plugin));
		if(!HCFConfiguration.kitMap) this.commands.add(new EOTWCommand(plugin));
		this.commands.add(new EventCommand(plugin));
		this.commands.add(new FirstJoinItemsCommand(plugin));
		this.commands.add(new FocusCommand(plugin));
		this.commands.add(new HelpCommand(plugin));
		this.commands.add(new LogoutCommand(plugin));
		if(!HCFConfiguration.kitMap) this.commands.add(new MapKitCommand(plugin));
		this.commands.add(new PayCommand(plugin));
		this.commands.add(new ReclaimCommand(plugin));
		if(!HCFConfiguration.kitMap) this.commands.add(new RegenCommand(plugin));
		this.commands.add(new SaveDataCommand(plugin));
		this.commands.add(new SetCommand(plugin));
		this.commands.add(new SpawnCommand(plugin));
		this.commands.add(new StaffModeCommand(plugin));
		this.commands.add(new StatsCommand(plugin));
		this.commands.add(new TellCoordsCommand(plugin));
		this.commands.add(new ToggleLightningCommand(plugin));
		this.commands.add(new VanishCommand(plugin));
		this.commands.add(new zSecondLifeCommand(plugin));
		
		for(BaseCommand command : this.commands) {
			this.getInstance().getCommand(command.getCommand()).setExecutor(this);
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for(BaseCommand baseCommand : this.commands) {
			if(command.getName().equalsIgnoreCase(baseCommand.getCommand())) {
				if(((sender instanceof ConsoleCommandSender)) && (baseCommand.isForPlayerUseOnly())) {
					sender.sendMessage(Msg.NO_CONSOLE);
					return true;
				}
				
				if((!sender.hasPermission(baseCommand.getPermission())) && (!baseCommand.getPermission().equals(""))) {
					sender.sendMessage(Msg.NO_PERMISSION);
					return true;
				}
				
				baseCommand.execute(sender, args);
				return true;
			}
		}
		
		return true;
	}
}
