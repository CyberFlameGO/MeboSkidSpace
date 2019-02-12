package secondlife.network.vituz.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.arguments.*;
import secondlife.network.vituz.commands.arguments.console.BungeeCommand;
import secondlife.network.vituz.commands.arguments.message.*;
import secondlife.network.vituz.commands.arguments.staff.*;
import secondlife.network.vituz.commands.arguments.staff.gamemode.AdventureCommand;
import secondlife.network.vituz.commands.arguments.staff.gamemode.CreativeCommand;
import secondlife.network.vituz.commands.arguments.staff.gamemode.GamemodeCommand;
import secondlife.network.vituz.commands.arguments.staff.gamemode.SurvivalCommand;
import secondlife.network.vituz.commands.arguments.staff.inventory.*;
import secondlife.network.vituz.commands.arguments.staff.teleport.*;
import secondlife.network.vituz.punishments.commands.*;
import secondlife.network.vituz.ranks.commands.GrantCommand;
import secondlife.network.vituz.ranks.commands.GrantsCommand;
import secondlife.network.vituz.ranks.commands.RankCommand;
import secondlife.network.vituz.ranks.commands.profile.AddPermissionCommand;
import secondlife.network.vituz.ranks.commands.profile.ListPermissionCommand;
import secondlife.network.vituz.ranks.commands.profile.RemovePermissionCommand;
import secondlife.network.vituz.utilties.Manager;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends Manager implements CommandExecutor {
	
	private List<BaseCommand> commands;

	public CommandHandler(Vituz plugin) {
		super(plugin);

		this.commands = new ArrayList<BaseCommand>();

		this.commands.add(new CrateCommand(plugin));

		this.commands.add(new AltViewCommand(plugin));
		this.commands.add(new BanCommand(plugin));
		this.commands.add(new BlacklistCommand(plugin));
		this.commands.add(new CheckCommand(plugin));
		this.commands.add(new ClearPunishmentsCommand(plugin));
		this.commands.add(new IPBanCommand(plugin));
		this.commands.add(new IPCommand(plugin));
		this.commands.add(new KickCommand(plugin));
		this.commands.add(new MuteCommand(plugin));
		this.commands.add(new RequestBanCommand(plugin));
		this.commands.add(new UnbanCommand(plugin));
		this.commands.add(new UnblacklistCommand(plugin));
		this.commands.add(new UnipbanCommand(plugin));
		this.commands.add(new UnmuteCommand(plugin));
		
		this.commands.add(new GrantCommand(plugin));
		this.commands.add(new GrantsCommand(plugin));
		this.commands.add(new RankCommand(plugin));

		this.commands.add(new AddPermissionCommand(plugin));
		this.commands.add(new ListPermissionCommand(plugin));
		this.commands.add(new RemovePermissionCommand(plugin));
		
		this.commands.add(new BungeeCommand(plugin));
		
		this.commands.add(new BroadcastCommand(plugin));
		this.commands.add(new IgnoreCommand(plugin));
		this.commands.add(new MessageCommand(plugin));
		this.commands.add(new ReplyCommand(plugin));
		this.commands.add(new SocialspyCommand(plugin));
		this.commands.add(new SoundsCommand(plugin));
		this.commands.add(new ChallengeCommand(plugin));
		this.commands.add(new ToggleChatCommand(plugin));
		this.commands.add(new TogglePMCommand(plugin));
		
		this.commands.add(new AdventureCommand(plugin));
		this.commands.add(new CreativeCommand(plugin));
		this.commands.add(new GamemodeCommand(plugin));
		this.commands.add(new SurvivalCommand(plugin));

		this.commands.add(new CrateCommand(plugin));
		
		this.commands.add(new ClearCommand(plugin));
		this.commands.add(new CraftCommand(plugin));
		this.commands.add(new EnchantCommand(plugin));
		this.commands.add(new EnderchestCommand(plugin));
		this.commands.add(new GiveCommand(plugin));
		this.commands.add(new GiveYourSelfCommand(plugin));
		this.commands.add(new InvseeCommand(plugin));
		this.commands.add(new MoreCommand(plugin));
		this.commands.add(new RenameCommand(plugin));
		this.commands.add(new RepairCommand(plugin));
		
		this.commands.add(new BackCommand(plugin));
		this.commands.add(new TeleportallCommand(plugin));
		this.commands.add(new TeleportCommand(plugin));
		this.commands.add(new TeleporthereCommand(plugin));
		this.commands.add(new TeleportpositionCommand(plugin));
		this.commands.add(new TopCommand(plugin));
		this.commands.add(new WarpCommand(plugin));
		this.commands.add(new WorldCommand(plugin));

		this.commands.add(new ChatControlCommand(plugin));
		this.commands.add(new DayCommand(plugin));
		this.commands.add(new ExpCommand(plugin));
		this.commands.add(new FeedCommand(plugin));
		this.commands.add(new FlyCommand(plugin));
		this.commands.add(new FreezeCommand(plugin));
		this.commands.add(new GodCommand(plugin));
		this.commands.add(new HealCommand(plugin));
		this.commands.add(new KickallCommand(plugin));
		this.commands.add(new KillallCommand(plugin));
		this.commands.add(new KillCommand(plugin));
		this.commands.add(new LagCommand(plugin));
		this.commands.add(new LoreCommand(plugin));
		this.commands.add(new NightCommand(plugin));
		this.commands.add(new NotesCommand(plugin));
		this.commands.add(new PlayerdistanceCommand(plugin));
		this.commands.add(new ProtocolCommand(plugin));
		this.commands.add(new PlaytimeCommand(plugin));
		this.commands.add(new SettingsCommand(plugin));
		this.commands.add(new RebootCommand(plugin));
		this.commands.add(new SpawnerCommand(plugin));
		this.commands.add(new SpeedCommand(plugin));
		this.commands.add(new StaffJoinCommand(plugin));
		this.commands.add(new StatusCommand(plugin));
		this.commands.add(new SunCommand(plugin));
		this.commands.add(new TasksCommand(plugin));
		this.commands.add(new ViewdistanceCommand(plugin));

		this.commands.add(new ChallengeCommand(plugin));
		this.commands.add(new ColorCommand(plugin));
		this.commands.add(new DiscordCommand(plugin));
		this.commands.add(new DonateCommand(plugin));
		this.commands.add(new FacebookCommand(plugin));
		this.commands.add(new ForumCommand(plugin));
		this.commands.add(new ListCommand(plugin));
		this.commands.add(new PingCommand(plugin));
		this.commands.add(new PrefixCommand(plugin));
		this.commands.add(new RulesCommand(plugin));
		this.commands.add(new SeenCommand(plugin));
		this.commands.add(new TeamSpeakCommand(plugin));
		
		for(BaseCommand command : this.commands) {
			plugin.getCommand(command.getCommand()).setExecutor(this);
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
