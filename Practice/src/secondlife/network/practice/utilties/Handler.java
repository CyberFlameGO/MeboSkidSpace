package secondlife.network.practice.utilties;

import secondlife.network.practice.Practice;

public class Handler {
	
	protected Practice plugin;
		
	public Handler(Practice plugin) {
		this.plugin = plugin;
	}
	
	public static void disable(Practice plugin) {
		
	}

	public Practice getInstance() {
		return plugin;
	}
}
