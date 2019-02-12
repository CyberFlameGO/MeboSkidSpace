package secondlife.network.practice.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.tournament.Tournament;
import secondlife.network.practice.tournament.TournamentState;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.practice.utilties.TeamUtil;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Permission;

import java.util.UUID;

public class TournamentCommand extends Command {

	private final static String[] HELP_MESSAGE = new String[] {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			CC.PRIMARY + "Tournament Commands:",
			CC.SECONDARY + "(*) /tournament join <id>" + ChatColor.GRAY + "- Joins a tournament",
			CC.SECONDARY + "(*) /tournament status <id>" + ChatColor.GRAY + "- Gives you a status",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
	};

	private final Practice plugin = Practice.getInstance();

	public TournamentCommand() {
		super("tournament");

		setUsage(CC.RED + "Usage: /tournament [args]");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if(args.length == 0) {
			commandSender.sendMessage(TournamentCommand.HELP_MESSAGE);
			return true;
		}

		switch (args[0].toLowerCase()) {
			case "create":
				if(!PlayerUtil.testPermission(commandSender, Permission.OP_PERMISSION)) return true;

				if(args.length == 5) {
					try {
						int id = Integer.parseInt(args[1]);
						int teamSize = Integer.parseInt(args[3]);
						int size = Integer.parseInt(args[4]);
						String kitName = args[2];

						if(size % teamSize != 0) {
							commandSender.sendMessage(CC.RED + "This tournament size and team size would not work together.");
							return true;
						}

						if(this.plugin.getTournamentManager().getTournament(id) != null) {
							commandSender.sendMessage(CC.RED + "This tournament already exists.");
							return true;
						}

						Kit kit = this.plugin.getKitManager().getKit(kitName);

						if(kit == null) {
							commandSender.sendMessage(CC.RED + "This kit does not exist!");
							return true;
						}

						this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);
					} catch(NumberFormatException e) {
						commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
					}
				} else {
					commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
				}
				break;
			case "remove":
				if(!PlayerUtil.testPermission(commandSender, Permission.OP_PERMISSION)) return true;

				if(args.length == 2) {
					int id = Integer.parseInt(args[1]);
					Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

					if(tournament != null) {
						this.plugin.getTournamentManager().removeTournament(id);
						commandSender.sendMessage(CC.PRIMARY + "Successfully removed tournament " + CC.SECONDARY + id + CC.PRIMARY + ".");
					} else {
						commandSender.sendMessage(CC.RED + "This tournament does not exist.");
					}
				} else {
					commandSender.sendMessage(CC.RED + "Usage: /tournament remove <id>");
				}
				break;
			case "announce":
				if(!PlayerUtil.testPermission(commandSender, Permission.OP_PERMISSION)) return true;

				if(args.length == 2) {
					int id = Integer.parseInt(args[1]);
					Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

					if(tournament != null) {
						ActionMessage actionMessage = new ActionMessage();

						actionMessage.addText(CC.SECONDARY + commandSender.getName() + CC.PRIMARY + " is hosting a "
								+ CC.SECONDARY + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName() + CC.PRIMARY
								+ " tournament! ");
						actionMessage.addText(CC.GREEN + "[Join]").addHoverText(CC.GREEN + "Click to join!").setClickEvent(ActionMessage.ClickableType.RunCommand,"/tournament join " + id);

						this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
								this.plugin.getServer().getOnlinePlayers().forEach(actionMessage::sendToPlayer));
					}
				} else {
					commandSender.sendMessage(CC.RED + "Usage: /tournament announce <id>");
				}
				break;
			case "join":
				if(!(commandSender instanceof Player)) return true;

				Player player = (Player) commandSender;
				PracticeData playerData = PracticeData.getByName(player.getName());

				if(playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do that in this state.");
					return true;
				}

				if(this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
					player.sendMessage(CC.RED + "You are already in a tournament!");
					return true;
				}

				if(args.length == 2) {
					try {
						int id = Integer.parseInt(args[1]);
						Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

						if(tournament != null) {
							if(tournament.getTeamSize() > 1) {
								Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

								if (party != null && party.getMembers().size() != tournament.getTeamSize()) {
									player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
									return true;
								}
							}

							if(tournament.getSize() > tournament.getPlayers().size()) {
								if((tournament.getTournamentState() == TournamentState.WAITING
										|| tournament.getTournamentState() == TournamentState.STARTING)
										&& tournament.getCurrentRound() == 1) {
									this.plugin.getTournamentManager().joinTournament(id, player);
								} else {
									player.sendMessage(CC.RED + "This tournament has already started!");
								}
							} else {
								player.sendMessage(CC.RED + "This tournament is already full!");
							}
						} else {
							player.sendMessage(CC.RED + "This tournament doesn't exist!");
						}
					} catch (NumberFormatException e) {
						player.sendMessage(CC.RED + "This isn't a number!");
					}
				} else {
					player.sendMessage(CC.RED + "Usage: /tournament join <id>");
				}
				break;
			case "status":
				if (args.length == 2) {
					try {
						int id = Integer.parseInt(args[1]);
						Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

						if(tournament != null) {
							StringBuilder builder = new StringBuilder();
							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
							builder.append(CC.SECONDARY).append("Tournament ").append(tournament.getId()).append(CC.PRIMARY).append("'s matches:");
							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");

							for(UUID matchUUID : tournament.getMatches()) {
								Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);

								MatchTeam teamA = match.getTeams().get(0);
								MatchTeam teamB = match.getTeams().get(1);

								String teamANames = TeamUtil.getNames(teamA);
								String teamBNames = TeamUtil.getNames(teamB);

								builder.append(teamANames).append(" vs. ").append(teamBNames).append("\n");
							}

							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
							builder.append(CC.PRIMARY).append("Round: ").append(CC.SECONDARY).append(tournament.getCurrentRound()).append("\n");
							builder.append(CC.PRIMARY).append("Players: ").append(CC.SECONDARY).append(tournament.getPlayers().size()).append("\n");
							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
							commandSender.sendMessage(builder.toString());
						} else {
							commandSender.sendMessage(CC.RED + "This tournament does not exist!");
						}
					} catch (NumberFormatException e) {
						commandSender.sendMessage(CC.RED + "This isn't a number!");
					}
				}
				break;
			default:
				commandSender.sendMessage(TournamentCommand.HELP_MESSAGE);
				break;
		}
		return false;
	}
}
