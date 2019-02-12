package secondlife.network.paik.utils;

import lombok.Getter;
import secondlife.network.paik.Paik;

public class Handler {
	
	@Getter private Paik instance;
	
	public Handler(Paik instance) {
        this.instance = instance;
    }
}
