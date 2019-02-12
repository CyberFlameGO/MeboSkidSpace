package secondlife.network.practice.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.block.Block;
import secondlife.network.practice.utilties.CustomLocation;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Arena {

	private final String name;

	private List<StandaloneArena> standaloneArenas;
	private List<StandaloneArena> availableArenas;

	private CustomLocation a;
	private CustomLocation b;

	private CustomLocation min;
	private CustomLocation max;

	private boolean enabled;

	private boolean bedWars;
	private Block aBed;
	private Block bBed;

	public StandaloneArena getAvailableArena() {
		StandaloneArena arena = this.availableArenas.get(0);

		this.availableArenas.remove(0);

		return arena;
	}

	public void addStandaloneArena(StandaloneArena arena) {
		this.standaloneArenas.add(arena);
	}

	public void addAvailableArena(StandaloneArena arena) {
		this.availableArenas.add(arena);
	}
}
