package secondlife.network.practice.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.*;

public class PartyCommand extends Command {
	private final static String NOT_LEADER = CC.RED + "You aren't the leader of the party!";
	private final static String[] HELP_MESSAGE = new String[] {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			CC.PRIMARY + "Party Commands:",
			CC.SECONDARY + "(*) /party help " + ChatColor.GRAY + "- Displays the help menu",
			CC.SECONDARY + "(*) /party create " + ChatColor.GRAY + "- Creates a party instance",
			CC.SECONDARY + "(*) /party leave " + ChatColor.GRAY + "- Leave your current party",
			CC.SECONDARY + "(*) /party info " + ChatColor.GRAY + "- Displays your party information",
			CC.SECONDARY + "(*) /party join (player) " + ChatColor.GRAY + "- Join a party (invited or unlocked)",
			"",
			CC.PRIMARY + "Leader Commands:",
			CC.SECONDARY + "(*) /party open " + ChatColor.GRAY + "- Open your party for others to join",
			CC.SECONDARY + "(*) /party limit (amount) " + ChatColor.GRAY + "- Set a limit to your party",
			CC.SECONDARY + "(*) /party invite (player) " + ChatColor.GRAY + "- Invites a player to your party",
			CC.SECONDARY + "(*) /party kick (player) " + ChatColor.GRAY + "- Kicks a player from your party",
			"",
			CC.SECONDARY + "(*) Use @message or !message for party chat!",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
	};

	private final Practice plugin = Practice.getInstance();

	public PartyCommand() {
		super("party");

		setDescription("Manager player parties.");
		setUsage(CC.RED + "Usage: /party <subcommand> [player]");
		setAliases(Collections.singletonList("p"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;

		Player player = (Player) sender;
		PracticeData playerData = PracticeData.getByName(player.getName());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		String subCommand = args.length < 1 ? "help" : args[0];

		switch (subCommand.toLowerCase()) {
			case "create":
				if(party != null) {
					player.sendMessage(CC.RED + "You are already in a party.");
				} else if(playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					this.plugin.getPartyManager().createParty(player);
				}
				break;
			case "leave":
				if(party == null) {
					player.sendMessage(CC.RED + "You aren't in a party.");
				} else if(playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					this.plugin.getPartyManager().leaveParty(player);
				}
				break;
			case "inv":
			case "invite":
				if(party == null) {
					player.sendMessage(CC.RED + "You aren't in a party.");
				} else if(!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
					player.sendMessage(CC.RED + "You aren't the party leader.");
				} else if(this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
					player.sendMessage(CC.RED + "You are in a tournament.");
				} else if(args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party invite <player>.");
				} else if(party.isOpen()) {
					player.sendMessage(CC.PRIMARY + "The party is open, so anyone can join.");
				} else if(party.getMembers().size() >= party.getLimit()) {
					player.sendMessage(CC.RED + "The party has reached its member limit.");
				} else {
					if(party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}

					Player target = this.plugin.getServer().getPlayer(args[1]);

					if(Msg.checkOffline(sender, args[1])) return true;

					PracticeData targetData = PracticeData.getByName(player.getName());

					if(target.getUniqueId() == player.getUniqueId()) {
						player.sendMessage(CC.RED + "You can't invite yourself.");
					} else if(this.plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
						player.sendMessage(CC.RED + "That player is already in a party.");
					} else if(targetData.getPlayerState() != PlayerState.SPAWN) {
						player.sendMessage(CC.RED + "That player isn't in spawn.");
					} else if(this.plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
						player.sendMessage(CC.RED + "You already sent a party request to that player. Please wait until it expires.");
					} else {
						this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());

						ActionMessage actionMessage = new ActionMessage();

						actionMessage.addText(CC.SECONDARY + sender.getName() + CC.PRIMARY + " has sent you a party invite. ");
						actionMessage.addText(CC.GREEN + "Click to accept").addHoverText(Color.translate("&eClick to join party!")).setClickEvent(ActionMessage.ClickableType.RunCommand, "/party accept " + sender.getName());

						actionMessage.sendToPlayer(target);

						party.broadcast(CC.SECONDARY + target.getName() + CC.PRIMARY + " was invited to the party.");
					}
				}
				break;
			case "accept":
				if(party != null) {
					player.sendMessage(CC.RED + "You are already in a party.");
				} else if(args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party accept <player>.");
				} else if(playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					Player target = this.plugin.getServer().getPlayer(args[1]);

					if(Msg.checkOffline(sender, args[1])) return true;

					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if(targetParty == null) {
						player.sendMessage(CC.RED + "That player does not have a party.");
					} else if(targetParty.getMembers().size() >= targetParty.getLimit()) {
						player.sendMessage(CC.RED + "That party is full.");
					} else if(!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
						player.sendMessage(CC.RED + "You don't have an invite from that player.");
					} else {
						this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
					}
				}
				break;
			case "join":
				if(party != null) {
					player.sendMessage(CC.RED + "You are already in a party.");
				} else if(args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party join <player>.");
				} else if(playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					Player target = this.plugin.getServer().getPlayer(args[1]);

					if(Msg.checkOffline(sender, args[1])) return true;

					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if (targetParty == null || !targetParty.isOpen() || targetParty.getMembers().size() >= targetParty.getLimit()) {
						player.sendMessage(CC.RED + "You can't join this party.");
					} else {
						this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
					}
				}
				break;
			case "kick":
				if(party == null) {
					player.sendMessage(CC.RED + "You aren't in a party.");
				} else if(args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party kick <player>.");
				} else {
					if(party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}

					Player target = this.plugin.getServer().getPlayer(args[1]);

					if(Msg.checkOffline(sender, args[1])) return true;

					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if(targetParty == null || targetParty.getLeader() != party.getLeader()) {
						player.sendMessage(CC.RED + "That player isn't in your party.");
					} else {
						this.plugin.getPartyManager().leaveParty(target);
					}
				}
				break;
			case "limit":
				if(party == null) {
					player.sendMessage(CC.RED + "You aren't in a party.");
				} else if(args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party kick <player>.");
				} else {
					if(party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}

					try {
						int limit = Integer.parseInt(args[1]);

						if(limit < 2 || limit > 50) {
							player.sendMessage(CC.RED + "That isn't a valid limit.");
						} else {
							party.setLimit(limit);
							player.sendMessage(CC.PRIMARY + "Your party's limit is now " + CC.SECONDARY
									+ limit + CC.PRIMARY + " members.");
						}
					} catch (NumberFormatException e) {
						player.sendMessage(CC.RED + "That isn't a valid limit.");
					}
				}
				break;
			case "open":
			case "close":
				if(party == null) {
					player.sendMessage(CC.RED + "You aren't in a party.");
				} else {
					if(party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}

					party.setOpen(!party.isOpen());

					party.broadcast(CC.PRIMARY + "Your party is now " + CC.SECONDARY + (party.isOpen() ? "open" : "closed") + CC.PRIMARY + ".");
				}
				break;
			case "info":
				if (party == null) {
					player.sendMessage(ChatColor.RED + "You aren't in a party.");
				} else {

					List<UUID> members = new ArrayList<>(party.getMembers());
					members.remove(party.getLeader());

					StringBuilder builder = new StringBuilder(ChatColor.YELLOW + "Members (" + CC.SECONDARY + party.getMembers().size() + CC.PRIMARY + "): ");

					String[] information = new String[] {
							ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
							ChatColor.LIGHT_PURPLE + "Party Information:",
							ChatColor.YELLOW + "Leader: " + ChatColor.LIGHT_PURPLE + this.plugin.getServer().getPlayer(party.getLeader()).getName(),
							ChatColor.YELLOW + builder.toString(),
							ChatColor.YELLOW + "Party State: " + ChatColor.LIGHT_PURPLE + (party.isOpen() ? "Open" : "Locked"),
							ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
					};

					player.sendMessage(information);
				}
				break;
			default:
				player.sendMessage(PartyCommand.HELP_MESSAGE);
				break;
		}
		return true;
	}
}
