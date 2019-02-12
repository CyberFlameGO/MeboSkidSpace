package secondlife.network.practice.managers;

import secondlife.network.practice.Practice;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.TtlHashMap;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PartyManager {

	private final Practice plugin = Practice.getInstance();

	private Map<UUID, List<UUID>> partyInvites = new TtlHashMap<>(TimeUnit.SECONDS, 15);
	private Map<UUID, Party> parties = new HashMap<>();
	private Map<UUID, UUID> partyLeaders = new HashMap<>();

	public boolean isLeader(UUID uuid) {
		return this.parties.containsKey(uuid);
	}

	public void removePartyInvites(UUID uuid) {
		this.partyInvites.remove(uuid);
	}

	public boolean hasPartyInvite(UUID player, UUID other) {
		return this.partyInvites.get(player) != null && this.partyInvites.get(player).contains(other);
	}

	public void createPartyInvite(UUID requester, UUID requested) {
		this.partyInvites.computeIfAbsent(requested, k -> new ArrayList<>()).add(requester);
	}

	public boolean isInParty(UUID player, Party party) {
		Party targetParty = this.getParty(player);
		return targetParty != null && targetParty.getLeader() == party.getLeader();
	}

	public Party getParty(UUID player) {
		if (this.parties.containsKey(player)) {
			return this.parties.get(player);
		}
		if (this.partyLeaders.containsKey(player)) {
			UUID leader = this.partyLeaders.get(player);
			return this.parties.get(leader);
		}
		return null;
	}

	public void createParty(Player player) {
		Party party = new Party(player.getUniqueId());

		if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
			player.sendMessage(CC.RED + "You need to leave the tournament and make a party with the amount of teammates the tournament" +
					"has.");
			return;
		}

		this.parties.put(player.getUniqueId(), party);
		this.plugin.getInventoryManager().addParty(player);
		PracticeData.sendToSpawnAndReset(player);

		player.sendMessage(CC.PRIMARY + "You have created a party.");
	}

	private void disbandParty(Party party, boolean tournament) {
		this.plugin.getInventoryManager().removeParty(party);
		this.parties.remove(party.getLeader());

		party.broadcast(CC.PRIMARY + "Your party was disbanded"
				+ (tournament ? " due to the tournament, because one of your party members left." : "."));

		party.members().forEach(member -> {
			PracticeData memberData = PracticeData.getByName(member.getName());

			if (this.partyLeaders.get(member.getUniqueId()) != null) {
				this.partyLeaders.remove(member.getUniqueId());
			}
			if (memberData.getPlayerState() == PlayerState.SPAWN) {
				PracticeData.sendToSpawnAndReset(member);
			}
		});
	}

	public void leaveParty(Player player) {
		Party party = this.getParty(player.getUniqueId());

		if (party == null) {
			return;
		}

		PracticeData playerData = PracticeData.getByName(player.getName());

		if (this.parties.containsKey(player.getUniqueId())) {
			this.disbandParty(party, false);
		} else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
			this.disbandParty(party, true);
		} else {
			party.broadcast(CC.SECONDARY + player.getName() + CC.PRIMARY + " left the party.");
			party.removeMember(player.getUniqueId());

			this.partyLeaders.remove(player.getUniqueId());

			this.plugin.getInventoryManager().updateParty(party);
		}

		switch (playerData.getPlayerState()) {
			case FIGHTING:
				this.plugin.getMatchManager().removeFighter(player, playerData, false);
				break;
			case SPECTATING:
				this.plugin.getMatchManager().removeSpectator(player);
				break;
		}

		PracticeData.sendToSpawnAndReset(player);
	}

	public void joinParty(UUID leader, Player player) {
		Party party = this.getParty(leader);

		if (this.plugin.getTournamentManager().getTournament(leader) != null) {
			player.sendMessage(CC.RED + "This player is in a tournament.");
			return;
		}

		this.partyLeaders.put(player.getUniqueId(), leader);
		party.addMember(player.getUniqueId());
		this.plugin.getInventoryManager().updateParty(party);

		PracticeData.sendToSpawnAndReset(player);

		party.broadcast(CC.SECONDARY + player.getName() + CC.PRIMARY + " joined the party.");
	}

}
