package secondlife.network.practice.arena;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.practice.utilties.CustomLocation;

@Getter
@Setter
public class StandaloneArena {

	private CustomLocation a;
	private CustomLocation b;

	private CustomLocation min;
	private CustomLocation max;

	private CustomLocation aBed;
	private CustomLocation bBed;

	public StandaloneArena(CustomLocation a, CustomLocation b, CustomLocation min, CustomLocation max) {
		this.a = a;
		this.b = b;
		this.min = min;
		this.max = max;
	}

	public StandaloneArena(CustomLocation a, CustomLocation b, CustomLocation min, CustomLocation max, CustomLocation aBed, CustomLocation bBed) {
		this.a = a;
		this.b = b;
		this.min = min;
		this.max = max;
		this.aBed = aBed;
		this.bBed = bBed;
	}
}
