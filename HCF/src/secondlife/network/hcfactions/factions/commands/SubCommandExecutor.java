package secondlife.network.hcfactions.factions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.captain.*;
import secondlife.network.hcfactions.factions.commands.leader.FactionCoLeaderCommand;
import secondlife.network.hcfactions.factions.commands.leader.FactionDemoteCommand;
import secondlife.network.hcfactions.factions.commands.leader.FactionDisbandCommand;
import secondlife.network.hcfactions.factions.commands.leader.FactionLeaderCommand;
import secondlife.network.hcfactions.factions.commands.member.*;
import secondlife.network.hcfactions.factions.commands.staff.*;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubCommandExecutor extends Handler implements CommandExecutor {
	
	private List<SubCommand> factionCmds;

	public SubCommandExecutor(HCF plugin) {
		super(plugin);
		
		this.factionCmds = new ArrayList<SubCommand>();

		// Captain
		this.factionCmds.add(new FactionAllyCommand(plugin));
		this.factionCmds.add(new FactionAnnouncementCommand(plugin));
		this.factionCmds.add(new FactionClaimCommand(plugin));
		this.factionCmds.add(new FactionClaimsCommand(plugin));
		this.factionCmds.add(new FactionInviteCommand(plugin));
		this.factionCmds.add(new FactionInvitesCommand(plugin));
		this.factionCmds.add(new FactionKickCommand(plugin));
		this.factionCmds.add(new FactionPromoteCommand(plugin));
		this.factionCmds.add(new FactionSetHomeCommand(plugin));
		this.factionCmds.add(new FactionTagCommand(plugin));
		this.factionCmds.add(new FactionUnallyCommand(plugin));
		this.factionCmds.add(new FactionUninviteCommand(plugin));
		this.factionCmds.add(new FactionWithdrawCommand(plugin));
		
		// Leader
		this.factionCmds.add(new FactionCoLeaderCommand(plugin));
		this.factionCmds.add(new FactionDemoteCommand(plugin));
		this.factionCmds.add(new FactionDisbandCommand(plugin));
		this.factionCmds.add(new FactionLeaderCommand(plugin));
		
		// Member
		this.factionCmds.add(new FactionAcceptCommand(plugin));
		this.factionCmds.add(new FactionChatCommand(plugin));
		this.factionCmds.add(new FactionCreateCommand(plugin));
		this.factionCmds.add(new FactionDepositCommand(plugin));
		this.factionCmds.add(new FactionHomeCommand(plugin));
		this.factionCmds.add(new FactionLeaveCommand(plugin));
		this.factionCmds.add(new FactionListCommand(plugin));
		this.factionCmds.add(new FactionMapCommand(plugin));
		this.factionCmds.add(new FactionPointsCommand(plugin));
		this.factionCmds.add(new FactionShowCommand(plugin));
		this.factionCmds.add(new FactionStuckCommand(plugin));
		this.factionCmds.add(new FactionUnclaimCommand(plugin));
		
		// Staff
		this.factionCmds.add(new FactionClaimforCommand(plugin));
		this.factionCmds.add(new FactionClearclaimsCommand(plugin));
		this.factionCmds.add(new FactionCreateSystemCommand(plugin));
		this.factionCmds.add(new FactionForceDemoteCommand(plugin));
		this.factionCmds.add(new FactionForceJoinCommand(plugin));
		this.factionCmds.add(new FactionForceKickCommand(plugin));
		this.factionCmds.add(new FactionForceLeaderCommand(plugin));
		this.factionCmds.add(new FactionForcePromoteCommand(plugin));
		this.factionCmds.add(new FactionRemoveCommand(plugin));
		this.factionCmds.add(new FactionSetBalanceCommand(plugin));
		this.factionCmds.add(new FactionSetDTRCommand(plugin));
		this.factionCmds.add(new FactionSetDTRRegenCommand(plugin));
		this.factionCmds.add(new FactionTpHereCommand(plugin));
		
		this.getInstance().getCommand("faction").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("faction")) {
			if(args.length == 0) {
		    	sender.sendMessage(Color.translate("&7&m---------------------------------------"));
		    	sender.sendMessage(Color.translate("&9&lFaction Help"));
		    	sender.sendMessage(Color.translate("&7&m---------------------------------------"));
		    	sender.sendMessage(Color.translate("&9General Commands:"));
		    	sender.sendMessage(Color.translate("&e/f create <factionName> &7- Create a new faction"));
		    	sender.sendMessage(Color.translate("&e/f accept <factionName> &7- Accept a pending invitation"));
		    	sender.sendMessage(Color.translate("&e/f leave &7- Leave your current faction"));
		    	sender.sendMessage(Color.translate("&e/f home &7- Teleport to your faction home"));
				sender.sendMessage(Color.translate("&e/f top &7- Shows you the list of 10 factions"));
		    	sender.sendMessage(Color.translate("&e/f stuck &7- Teleport out of enemy territory"));
		    	sender.sendMessage(Color.translate("&e/f deposit <amount&7|&eall> &7- Deposit money into your faction balance"));
		    	sender.sendMessage("");
		    	sender.sendMessage(Color.translate("&9Information Commands:"));
		    	sender.sendMessage(Color.translate("&e/f who <player&7|&efactionName] &7- Display faction information"));
		    	sender.sendMessage(Color.translate("&e/f uninvite <player> &7- Revoke an invitation"));
		    	sender.sendMessage(Color.translate("&e/f invites &7- List all open invitations"));
		    	sender.sendMessage(Color.translate("&e/f kick <player> &7- Kick a player from your faction"));
		    	sender.sendMessage(Color.translate("&e/f claim &7- Start a claim for your faction"));
		    	sender.sendMessage(Color.translate("&e/f sethome &7- Set your faction's home at your current location"));
		    	sender.sendMessage(Color.translate("&e/f withdraw <amoun> &7- Withdraw money from your faction's balance"));
		    	sender.sendMessage(Color.translate("&e/f announcement [message here] &7- Set your faction's announcement"));
		    	sender.sendMessage("");
		    	sender.sendMessage(Color.translate("&9Leader Commands:"));
		    	sender.sendMessage(Color.translate("&e/f coleader <player> &7- Add co-leader"));
		    	sender.sendMessage(Color.translate("&e/f promote <player> &7- Add or remove a captain"));
		    	sender.sendMessage(Color.translate("&e/f unclaim &7- Unclaim land"));
		    	sender.sendMessage(Color.translate("&e/f rename <newName> &7- Rename your faction"));
		    	sender.sendMessage(Color.translate("&e/f disband &7- Disband your faction"));
		    	if(sender.hasPermission(Permission.OP_PERMISSION)) {
		        	sender.sendMessage("");
		    		sender.sendMessage(Color.translate("&9Staff Commands:"));
		    		sender.sendMessage(Color.translate("&e/f forcedemote <player> &7- Force demote player"));
		    		sender.sendMessage(Color.translate("&e/f givebal [player&7|&efactionName] [balance] &7- Gives balance to a player's faction"));
		    		sender.sendMessage(Color.translate("&e/f forcejoin [player&7|&efactionName] &7- Force joins player factions"));
		    		sender.sendMessage(Color.translate("&e/f forcekick <player> &7- Force kick player from faction"));
		    		sender.sendMessage(Color.translate("&e/f forcepromote <player> &7- Force promote player from faction"));
		    		sender.sendMessage(Color.translate("&e/f setdtr [player&7|&efactionName] &7- Set dtr to player faction"));
		    		sender.sendMessage(Color.translate("&e/f setdtrregen [player&7|&efactionName] &7- Set dtrregen to player faction"));
					sender.sendMessage(Color.translate("&e/f tphere [factionName] &7- Teleports factions to your self"));
					sender.sendMessage(Color.translate("&e/f createsystem <factionName> &7- Create a new system faction"));
				}
		    	sender.sendMessage(Color.translate("&7&m---------------------------------------"));
				return true;
			} else {
				SubCommand subCommand = null;
				
				for(SubCommand subCommand2 : this.factionCmds) {
					String[] aliases;
					
					for(int length = (aliases = subCommand2.aliases).length, i = 0; i < length; ++i) {
						if(aliases[i].equalsIgnoreCase(args[0])) {
							subCommand = subCommand2;
							break;
						}
					}
				}
				
				if(subCommand == null) {
					sender.sendMessage(Color.translate("&cCommand '" + args[0] + "' not found!"));
					return true;
				}

				if(sender instanceof ConsoleCommandSender && subCommand.forPlayerUseOnly) {
					sender.sendMessage(Msg.NO_CONSOLE);
					return true;
				}
				
				if(!sender.hasPermission(subCommand.permission) && !subCommand.permission.equals("")) {
					sender.sendMessage(Msg.NO_PERMISSION);
					return true;
				}
				
				args = Arrays.copyOfRange(args, 0, args.length);
				
				subCommand.execute(sender, args);
			}
		}
		
		return true;
	}
}