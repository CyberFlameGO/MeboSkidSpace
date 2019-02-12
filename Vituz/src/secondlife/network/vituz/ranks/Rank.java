package secondlife.network.vituz.ranks;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter 
@Setter
public class Rank {
	
	@Getter
	private static List<Rank> ranks = new ArrayList<>();

	private UUID uuid;
	private List<UUID> inheritance;
	private List<String> permissions;
	private RankData data;

	public Rank(UUID uuid, List<UUID> inheritance, List<String> permissions, RankData data) {
		this.uuid = uuid;
		this.inheritance = inheritance;
		this.permissions = permissions;
		this.data = data;
		
		ranks.add(this);
	}

	public static Rank getDefaultRank() {
		for(Rank rank : Rank.ranks) {
			if(rank.getData().isDefaultRank()) return rank;
		}

		Rank defaultRank = new Rank(UUID.randomUUID(), new ArrayList<>(), new ArrayList<>(), new RankData("Default", "", "", true));

		ranks.add(defaultRank);

		return defaultRank;
	}

	public static Rank getByName(String name) {
		for(Rank rank : Rank.ranks) {
			if(rank.getData().getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) return rank;
		}
		
		return null;
	}

	public static Rank getByUuid(UUID uuid) {
		for(Rank rank : Rank.ranks) {
			if(rank.getUuid().equals(uuid)) return rank;
		}

		return null;
	}
}
