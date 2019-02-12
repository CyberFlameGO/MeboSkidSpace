package secondlife.network.meetuplobby.party.command;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.meetuplobby.party.Party;
import secondlife.network.meetuplobby.queue.QueueAction;
import secondlife.network.meetuplobby.utilities.OfflinePlayer;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Marko on 16.06.2018.
 */
public class PartyCommand extends Command {

    private MeetupLobby plugin = MeetupLobby.getInstance();

    public PartyCommand() {
        super("party");

        setAliases(Arrays.asList("team", "p", "f", "faction"));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Party party = plugin.getPartyManager().getByUuid(player.getUniqueId());

        if(args.length < 1) {
            sendUsage(player);
        } else {
            switch(args[0]) {
                case "create": {
                    if(party != null) {
                        player.sendMessage(Color.translate("&cYou already have a party."));
                        return false;
                    }

                    if(plugin.getQueueManager().isInQueue(player)) {
                        player.sendMessage(Color.translate("&cYou can't create a party while in the queue."));
                        return false;
                    }

                    party = new Party();
                    party.setLeader(player.getUniqueId());
                    party.getPlayers().add(new OfflinePlayer(player.getName(), player.getUniqueId()));

                    plugin.getPartyManager().getParties().put(player.getUniqueId(), party);

                    player.sendMessage(Color.translate("&eYou have created a new party."));
                    break;
                }

                case "show":
                case "info": {
                    if(party == null) {
                        player.sendMessage(Color.translate("&cYou aren't in a party."));
                        return false;
                    }

                    Player leader = Bukkit.getPlayer(party.getLeader());

                    StringBuilder players = new StringBuilder();

                    for(OfflinePlayer offlinePlayer : party.getPlayers()) {
                        players.append(offlinePlayer.getUsername());
                        players.append(", ");
                    }

                    players.setLength(players.length() - 2);

                    player.sendMessage(Color.translate("&7&m---------------------------------------"));
                    player.sendMessage(Color.translate("&eParty of &d" + leader.getName()));
                    player.sendMessage(Color.translate("&ePlayers: &d" + players));
                    player.sendMessage(Color.translate("&7&m---------------------------------------"));
                }

                case "inv":
                case "invite": {
                    if(args.length < 2) {
                        sendUsage(player);
                        return false;
                    }

                    if(!party.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage(Color.translate("&cYou must be the leader to invite players."));
                        return false;
                    }

                    if(plugin.getQueueManager().isInQueue(player)) {
                        player.sendMessage(Color.translate("&cYou can't invite players to your party while in the queue."));
                        return false;
                    }

                    Player other = Bukkit.getPlayer(args[1]);

                    if(Msg.checkOffline(sender, args[1])) return false;

                    if(party.hasPlayer(other.getUniqueId())) {
                        player.sendMessage(Color.translate("&cThat player is already in your party."));
                        return false;
                    }

                    if(party.getInvited().contains(other.getUniqueId())) {
                        player.sendMessage(Color.translate("&cThat player has already been invited to your party."));
                        return false;
                    }

                    party.getInvited().add(other.getUniqueId());

                    player.sendMessage(Color.translate("&eSuccessfully sent invite to &d" + other.getName() + "&e."));

                    ActionMessage actionMessage = new ActionMessage();
                    actionMessage.addText("&eYou have been invited to join &d" + player.getName() + "'s &eparty. Type &a/party join " + player.getName() + " &eor ");
                    actionMessage.addText("&a&lclick here").setClickEvent(ActionMessage.ClickableType.RunCommand, "/party join " + player.getName()).addHoverText(Color.translate("&aClick this to join!"));
                    actionMessage.addText(" &eto join.");
                    actionMessage.sendToPlayer(other);
                }

                case "accept":
                case "join": {
                    if(args.length < 2) {
                        sendUsage(player);
                        return false;
                    }

                    if(party == null) {
                        player.sendMessage(Color.translate("&cYou aren't in a party."));
                        return false;
                    }

                    if(plugin.getQueueManager().isInQueue(player)) {
                        player.sendMessage(Color.translate("&cYou can't join that party while in a queue."));
                        return false;
                    }

                    Player other = Bukkit.getPlayer(args[1]);

                    if(Msg.checkOffline(sender, args[1])) return false;

                    Party otherParty = plugin.getPartyManager().getByUuid(other.getUniqueId());

                    if(otherParty == null) {
                        player.sendMessage(Color.translate("&cThat party doesn't exist."));
                        return false;
                    }

                    if(!otherParty.getInvited().contains(player.getUniqueId())) {
                        player.sendMessage(Color.translate("&cYou have not been invited to that party."));
                        return false;
                    }

                    if(otherParty.getPlayers().size() < 2) {
                        if(plugin.getQueueManager().isInQueue(other)) {
                            player.sendMessage(Color.translate("&cYou can't join that party while they are in the queue."));
                            return false;
                        }

                        otherParty.getInvited().remove(player.getUniqueId());
                        otherParty.getPlayers().add(new OfflinePlayer(player.getName(), player.getUniqueId()));
                    } else {
                        player.sendMessage(Color.translate("&cThat party is full."));
                    }
                }

                case "leave": {
                    if(party == null) {
                        player.sendMessage(Color.translate("&cYou aren't in a party."));
                        return false;
                    }

                    if(party.getLeader().equals(player.getUniqueId())) {
                        plugin.getPartyManager().getParties().remove(player.getUniqueId());
                    }

                    Iterator<OfflinePlayer> iterator = party.getPlayers().iterator();

                    while(iterator.hasNext()) {
                        OfflinePlayer offlinePlayer = iterator.next();

                        if(offlinePlayer.getUuid().equals(player.getUniqueId())) {
                            iterator.remove();
                            break;
                        }
                    }

                    if(plugin.getQueueManager().isInQueue(player)) {
                        JsonObject object = new JsonObject();
                        object.addProperty("action", QueueAction.REMOVE_PARTY.name());

                        JsonObject payload = new JsonObject();
                        payload.add("party", party.toJson());

                        object.add("payload", payload);

                        plugin.getPublisher().write(object.toString());
                    }

                    for(Player partyPlayer : plugin.getPartyManager().getPlayersFromParty(party)) {
                        partyPlayer.sendMessage(Color.translate( "&d" + player.getName() + " &ehas left the party."));
                    }

                    player.sendMessage(Color.translate("&eYou have left the party."));
                }
            }
        }

        return false;
    }

    public void sendUsage(Player sender) {
        sender.sendMessage(Color.translate("&7&m---------------------------------------"));
        sender.sendMessage(Color.translate("&9&lParty Help"));
        sender.sendMessage(Color.translate("&7&m---------------------------------------"));
        sender.sendMessage(Color.translate("&9General Commands:"));
        sender.sendMessage(Color.translate("&e/party create &7- Create a new party"));
        sender.sendMessage(Color.translate("&e/party info &7- See your party information"));
        sender.sendMessage(Color.translate("&e/party invite <player> &7- Invite players to your party"));
        sender.sendMessage(Color.translate("&e/party join <party> &7- Join parties"));
        sender.sendMessage(Color.translate("&e/party leave &7- Leave parties"));
        sender.sendMessage(Color.translate("&7&m---------------------------------------"));
    }
}
