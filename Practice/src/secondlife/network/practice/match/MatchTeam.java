package secondlife.network.practice.match;

import lombok.Getter;
import secondlife.network.practice.team.KillableTeam;

import java.util.List;
import java.util.UUID;

@Getter
public class MatchTeam extends KillableTeam {

	private final int teamID;

	public MatchTeam(UUID leader, List<UUID> players, int teamID) {
		super(leader, players);
		this.teamID = teamID;
	}
}
