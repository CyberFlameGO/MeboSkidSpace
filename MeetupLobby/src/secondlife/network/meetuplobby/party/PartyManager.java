package secondlife.network.meetuplobby.party;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.meetuplobby.utilities.OfflinePlayer;

import java.util.*;

public class PartyManager {

    @Getter private Map<UUID, Party> parties;

    public PartyManager() {
        this.parties = new HashMap<>();
    }

    public Party getByUuid(UUID uuid) {
        if (parties.containsKey(uuid)) {
            return parties.get(uuid);
        }
        else {
            for (Party party : this.parties.values()) {
                for (OfflinePlayer offlinePlayer : party.getPlayers()) {
                    if (offlinePlayer.getUuid().equals(uuid)) {
                        return party;
                    }
                }
            }
        }

        return null;
    }

    public List<Player> getPlayersFromParty(Party party) {
        List<Player> players = new ArrayList<>();

        for (OfflinePlayer offlinePlayer : party.getPlayers()) {
            Player player = Bukkit.getPlayer(offlinePlayer.getUuid());

            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }

        return players;
    }

}
