package secondlife.network.vituz.ranks.grant;

import java.util.ArrayList;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.ranks.RankData;

@Getter
@Setter
public class Grant {

	private String issuer;
	private UUID rankId;
	private String reason;
	private long dateAdded;
	private long duration;
	private boolean active;

	public Grant(String issuer, Rank rank, long dateAdded, long duration, String reason, boolean active) {
		this.issuer = issuer;
		this.rankId = rank.getUuid();
		this.dateAdded = dateAdded;
		this.duration = duration;
		this.reason = reason;
		this.active = active;
	}

	public Rank getRank() {
		Rank toReturn = Rank.getByUuid(this.rankId);
		
		if(toReturn == null) {
			this.active = false;
			
			toReturn = new Rank(UUID.randomUUID(), new ArrayList<UUID>(), new ArrayList<String>(), new RankData("N/A"));
			
			Rank.getRanks().remove(toReturn);
			
			return toReturn;
		}
		
		return toReturn;
	}

	public boolean isExpired() {
		return !this.active || System.currentTimeMillis() >= this.dateAdded + this.duration;
	}
}
