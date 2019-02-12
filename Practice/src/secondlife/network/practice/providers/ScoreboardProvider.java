package secondlife.network.practice.providers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.EventState;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.oitc.OITCPlayer;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.events.parkour.ParkourPlayer;
import secondlife.network.practice.events.sumo.SumoEvent;
import secondlife.network.practice.events.sumo.SumoPlayer;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.queue.QueueEntry;
import secondlife.network.practice.queue.QueueType;
import secondlife.network.practice.tournament.Tournament;
import secondlife.network.practice.utilties.MathUtil;
import secondlife.network.practice.utilties.TimeUtil;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.providers.ScoreProvider;
import secondlife.network.vituz.providers.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.providers.scoreboard.TitleGetter;
import secondlife.network.vituz.utilties.Color;

import java.util.*;

/**
 * Created by Marko on 15.05.2018.
 */
public class ScoreboardProvider implements ScoreProvider {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration sc = new ScoreboardConfiguration();

        sc.setTitleGetter(new TitleGetter("§5§lSecondLife"));
        sc.setScoreGetter(new ScoreboardProvider());

        return sc;
    }

    @Override
    public String[] getScores(Player player) {
        List<String> board = new ArrayList<>();

        PracticeData playerData = PracticeData.getByName(player.getName());

        if (playerData == null) {
            return null;
        }

        switch (playerData.getPlayerState()) {
            case LOADING:
            case EDITING:
            case FFA:
            case SPAWN:
            case EVENT:
            case SPECTATING:
                return this.getLobbyBoard(player, false);
            case QUEUE:
                return this.getLobbyBoard(player, true);
            case FIGHTING:
                return this.getGameBoard(player);
        }

        return board.toArray(new String[] {});
    }

    public String[] getLobbyBoard(Player player, boolean queuing) {
        List<String> board = new ArrayList<>();

        PracticeData playerData = PracticeData.getByName(player.getName());

        if (playerData == null) {
            return null;
        }

        add(board, "&2&7&m----------------------");

        Party party = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());
        Tournament tournament = Practice.getInstance().getTournamentManager().getTournament(player.getUniqueId());
        PracticeEvent event = Practice.getInstance().getEventManager().getEventPlaying(player);

        if (Practice.getInstance().getEventManager().getSpectators().containsKey(player.getUniqueId())) {
            event = Practice.getInstance().getEventManager().getSpectators().get(player.getUniqueId());
        }

        if (event == null) {
            add(board, "&fOnline: &d" + Practice.getInstance().getServer().getOnlinePlayers().size());
            add(board, "&fPlaying: &d" + Practice.getInstance().getMatchManager().getFighters());

            if (System.currentTimeMillis() < Practice.getInstance().getEventManager().getCooldown()) {
                add(board, "&fCooldown: &d" + TimeUtil.convertToFormat(Practice.getInstance().getEventManager().getCooldown()));
            }

            if (tournament != null) {
                add(board, " ");
                add(board, "&fTournament:");
                add(board, "&d" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName());
                add(board, "&fRound: &d" + tournament.getCurrentRound());
                add(board, "&fPlayers: &d" + tournament.getPlayers().size() + "/" + tournament.getSize());
            } else if (party != null) {
                add(board, " ");
                add(board, "&fParty:");
                add(board, "&fLeader: &d" + Bukkit.getPlayer(party.getLeader()).getName());
                add(board, "&fMembers: &d" + party.getMembers().size());
            }

            add(board, "&f ");

            if(queuing) {
                QueueEntry queueEntry = party == null ? Practice.getInstance().getQueueManager().getQueueEntry(player.getUniqueId()) : Practice.getInstance().getQueueManager().getQueueEntry(party.getLeader());

                add(board, "&fQueue:");
                add(board, "&d" + queueEntry.getQueueType().getName() + " " + queueEntry.getKitName());

                if (queueEntry.getQueueType() != QueueType.UNRANKED) {
                    long queueTime = System.currentTimeMillis() -
                            (party == null ? Practice.getInstance().getQueueManager().getPlayerQueueTime(player
                                    .getUniqueId())
                                    : Practice.getInstance().getQueueManager().getPlayerQueueTime(party.getLeader()));

                    int eloRange = playerData.getEloRange();

                    int seconds = Math.round(queueTime / 1000L);
                    if (seconds > 5) {
                        if (eloRange != -1) {
                            eloRange += seconds * 50;
                            if (eloRange >= 3000) {
                                eloRange = 3000;
                            }
                        }
                    }

                    int elo = 1000;

                    if (queueEntry.getQueueType() == QueueType.RANKED) {
                        elo = playerData.getElo(queueEntry.getKitName());
                    }

                    String eloRangeString = "[" + Math.max(elo - eloRange / 2, 0) + " -> " + Math.max(elo + eloRange / 2, 0) + "]";

                    add(board, "&fELO range:");
                    add(board, "&d" + eloRangeString);
                }
            } else {
                int maxMatches = PracticeData.getPremiumMatches(player.getName());
                if (maxMatches == 1337) {
                    add(board, "&fPremium Matches:");
                    add(board, "&dUnlimited");
                } else {
                    add(board, "&fPremium Matches: " + "&d" + playerData.getPremiumMatches());
                }
            }
        } else {
            add(board, "&fEvent &d" + "(" + event.getName() + ")");

            if (event instanceof SumoEvent) {
                SumoEvent sumoEvent = (SumoEvent) event;

                int playingSumo = sumoEvent.getByState(SumoPlayer.SumoState.WAITING).size() + sumoEvent.getByState(SumoPlayer.SumoState.FIGHTING).size() + sumoEvent.getByState(SumoPlayer.SumoState.PREPARING).size();
                add(board, " &fPlayers: &d" + playingSumo + "/" + event.getLimit());


                int countdown = sumoEvent.getCountdownTask().getTimeUntilStart();

                if (countdown > 0 && countdown <= 60) {
                    add(board, " &fStarting: &d" + countdown + "s");
                }

                if (sumoEvent.getPlayer(player) != null) {
                    SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);
                    add(board, " &fState: &d" + org.apache.commons.lang.StringUtils.capitalize(sumoPlayer.getState().name().toLowerCase()));
                }

                if (sumoEvent != null && sumoEvent.getFighting().size() > 0) {
                    StringJoiner nameJoiner = new StringJoiner(" &fvs &d");
                    StringJoiner pingJoiner = new StringJoiner(" &fvs &d ");

                    for (String fighterName : sumoEvent.getFighting()) {
                        nameJoiner.add(fighterName);

                        Player fighter = Bukkit.getPlayer(fighterName);

                        if (fighter != null) {
                            pingJoiner.add("&d" + VituzAPI.getPing(fighter) + " ms");
                        }
                    }

                    add(board, "&3&7&m----------------------");
                    add(board, "&d" + nameJoiner.toString());
                    add(board, pingJoiner.toString());
                }
            } else if (event instanceof ParkourEvent) {
                ParkourEvent parkourEvent = (ParkourEvent) event;

                int playingParkour = parkourEvent.getByState(ParkourPlayer.ParkourState.WAITING).size() + parkourEvent.getByState(ParkourPlayer.ParkourState.INGAME).size();
                add(board, " &fPlayers: &d" + playingParkour + "/" + event.getLimit());


                int countdown = parkourEvent.getCountdownTask().getTimeUntilStart();

                if (countdown > 0 && countdown <= 60) {
                    add(board, " &fStarting: &d" + countdown + "s");
                }

                if (parkourEvent.getPlayer(player) != null) {
                    ParkourPlayer parkourPlayer = parkourEvent.getPlayer(player);

                    if (parkourPlayer.getLastCheckpoint() != null && parkourPlayer.getCheckpointId() > 0) {
                        add(board, " &fCheckpoint: &d#" + parkourPlayer.getCheckpointId());
                    }
                }
            } else if (event instanceof OITCEvent) {
                OITCEvent oitcEvent = (OITCEvent) event;

                int playingOITC = oitcEvent.getPlayers().size();
                add(board, " &fPlayers: &d" + playingOITC + "/" + event.getLimit());


                int countdown = oitcEvent.getCountdownTask().getTimeUntilStart();

                if (countdown > 0 && countdown <= 60) {
                    add(board, " &fStarting: &d" + countdown + "s");
                }

                if (oitcEvent.getPlayer(player) != null) {
                    OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);

                    if (oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() == OITCPlayer.OITCState.RESPAWNING) {

                        add(board, " &fKills: &d" + oitcPlayer.getScore());
                        add(board, " &fLives: &d" + oitcPlayer.getLives());
                    }
                }


                List<OITCPlayer> sortedList = oitcEvent.sortedScores();
                if (sortedList.size() >= 2 && event.getState() == EventState.STARTED) {
                    add(board, "&3&7&m----------------------");
                    add(board, "&5&lTop Kills");

                    Player first = Bukkit.getPlayer(sortedList.get(0).getUuid());
                    Player second = Bukkit.getPlayer(sortedList.get(1).getUuid());

                    if (first != null) {
                        add(board, "&f1. &7" + first.getName() + ": &d" + sortedList.get(0).getScore());
                    }

                    if (second != null) {
                        add(board, "&f2. &7" + second.getName() + ": &d" + sortedList.get(1).getScore());
                    }

                    if (sortedList.size() >= 3) {
                        Player third = Bukkit.getPlayer(sortedList.get(2).getUuid());

                        if (third != null) {
                            add(board, "&f3. &7" + third.getName() + ": &d" + sortedList.get(2).getScore());
                        }
                    }
                }
            }

        }

        add(board, " ");
        add(board, "&dsecondlife.network");
        add(board, "&4&7&m----------------------");

        return board.toArray(new String[] {});
    }

    public String[] getGameBoard(Player player) {
        List<String> board = new ArrayList<>();

        PracticeData playerData = PracticeData.getByName(player.getName());

        if (playerData == null) {
            return null;
        }

        add(board, "&2&7&m----------------------");

        Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());

        add(board, "&fLadder: &d" + match.getKit().getName());

        Player opponentPlayer;
        if(!match.isParty() && !match.isFFA()) {
            opponentPlayer = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()
                    ? Bukkit.getPlayer(match.getTeams().get(1).getPlayers().get(0))
                    : Bukkit.getPlayer(match.getTeams().get(0).getPlayers().get(0));

            if(opponentPlayer == null) {
               return getLobbyBoard(player, false);
            }

            add(board, "&fOpponent: &d" + opponentPlayer.getName());

            add(board, "");
            add(board, "&fYour Ping: &d" + VituzAPI.getPing(player) + " ms");
            add(board, "&fTheir Ping: &d" + VituzAPI.getPing(opponentPlayer) + " ms");
        } else if (match.isParty() && !match.isFFA()) {
            MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) : (playerData.getTeamID() == 0 ? match.getTeams().get(1) : match.getTeams().get(0));
            MatchTeam playerTeam = match.getTeams().get(playerData.getTeamID());

            if(opposingTeam.getPlayers().size() == 2 && playerTeam.getPlayers().size() == 2) {
                Player teammate = Practice.getInstance().getServer().getPlayer(playerTeam.getPlayers().get(0) == player.getUniqueId() ? playerTeam.getPlayers().get(1) : playerTeam.getPlayers().get(0));

                add(board, " ");
                add(board, "&fTeammates:");

                if(teammate != null) {
                    if (playerTeam.getAlivePlayers().contains(teammate.getUniqueId())) {
                        add(board, "&f " + teammate.getName() + "&d (" + MathUtil.roundToHalves(teammate.getHealth() / 2.0D) + " ❤)");

                        boolean potionMatch = false;
                        boolean soupMatch = false;

                        for(ItemStack item : match.getKit().getContents()) {
                            if(item == null) continue;

                            if(item.getType() == Material.MUSHROOM_SOUP) {
                                soupMatch = true;
                                break;
                            } else if(item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
                                potionMatch = true;
                                break;
                            }
                        }

                        if (potionMatch) {
                            int potCount = (int) Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();

                            add(board, " &d" + potCount + " pots");
                        } else if (soupMatch) {
                            int soupCount = (int) Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull).map(ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();

                            add(board, " &d" + soupCount + " soups");
                        }
                    } else {
                        add(board, "&f " + teammate.getName() + " &4(✘)");
                    }
                }

                if (opposingTeam.getAlivePlayers().size() > 0) {
                    add(board, "&fOpponents: ");

                    for(UUID opponent : opposingTeam.getAlivePlayers()) {
                        add(board, "&d " + Practice.getInstance().getServer().getPlayer(opponent).getName());
                    }

                    add(board, "");
                    add(board, "&fYour Ping: &d" + VituzAPI.getPing(player) + " ms");
                }
            } else {
                add(board, "&fTeammates: &d" + playerTeam.getAlivePlayers().size());
                add(board, "&fOpponents: &d" + opposingTeam.getAlivePlayers().size());

                add(board, "");
                add(board, "&fYour Ping: &d" + VituzAPI.getPing(player) + " ms");
            }
        } else if(match.isFFA()) {
            add(board, "&fOpponents: &d" + (match.getTeams().get(0).getAlivePlayers().size() - 1));

            add(board, "");
            add(board, "&fYour Ping: &d" + VituzAPI.getPing(player) + " ms");
        }

        add(board, " ");
        add(board, "&dsecondlife.network");
        add(board, "&4&7&m----------------------");

        return board.toArray(new String[] {});
    }
    
    private void add(List list, String text) {
        list.add(Color.translate(text));
    }
}
