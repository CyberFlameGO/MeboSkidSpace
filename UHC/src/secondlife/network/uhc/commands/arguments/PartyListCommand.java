package secondlife.network.uhc.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.managers.PartyManager;
import secondlife.network.uhc.party.Party;
import secondlife.network.uhc.player.UHCData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.List;

public class PartyListCommand extends BaseCommand {

	public PartyListCommand(UHC plugin) {
		super(plugin);

		this.command = "partylist";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			if(!PartyManager.isEnabled()) {
				player.sendMessage(Color.translate("&cParties are currently disabled."));
				return;
			}
			
			Party party = PartyManager.getByPlayer(player);

			if(party == null) {
				player.sendMessage(Color.translate("&7You are not in a party."));
				return;
			}

			UHCData leaderData = UHCData.getByName(party.getOwner().getName());
			String leaderName = (leaderData.isAlive() ? "&a" : "&c") + party.getOwner().getName();

			List<String> users = new ArrayList<>(party.getPlayers());
			users.remove(party.getOwner().getName());

			StringBuilder builder = new StringBuilder();

			users.forEach(user -> {
				if(builder.length() > 0) {
					builder.append("&f, ");
				}

				OfflinePlayer member = Bukkit.getOfflinePlayer(user);
				UHCData memberData = UHCData.getByName(member.getName());

				builder.append((memberData.isAlive() ? "&a" : "&c")).append(member.getName());
			});

			player.sendMessage(Msg.BIG_LINE);
			player.sendMessage(Color.translate("&dParty Information:"));
			player.sendMessage(Color.translate("&eLeader: " + leaderName));
			player.sendMessage(Color.translate("&eMembers (&d" + party.getPlayers().size() + "&e): " + builder.toString()));
			player.sendMessage(Msg.BIG_LINE);
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(player, args[0])) return;

			Party party = PartyManager.getByPlayer(target);

			if(party == null) {
				player.sendMessage(Color.translate("&c&l" + args[0] + " &cisn't in a party."));
				return;
			}

			UHCData leaderData = UHCData.getByName(party.getOwner().getName());
			String leaderName = (leaderData.isAlive() ? "&a" : "&c") + party.getOwner().getName();

			List<String> users = new ArrayList<>(party.getPlayers());
			users.remove(party.getOwner().getName());

			StringBuilder builder = new StringBuilder();

			users.forEach(user -> {
				if(builder.length() > 0) {
					builder.append("&f, ");
				}

				OfflinePlayer member = Bukkit.getOfflinePlayer(user);
				UHCData memberData = UHCData.getByName(member.getName());

				builder.append((memberData.isAlive() ? "&a" : "&c")).append(member.getName());
			});

			player.sendMessage(Msg.BIG_LINE);
			player.sendMessage(Color.translate("&dParty Information:"));
			player.sendMessage(Color.translate("&eLeader: " + leaderName));
			player.sendMessage(Color.translate("&eMembers (&d" + party.getPlayers().size() + "&e): " + builder.toString()));
			player.sendMessage(Msg.BIG_LINE);
		}

	}

}
