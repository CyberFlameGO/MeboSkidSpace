package secondlife.network.bungee.handlers;

import net.md_5.bungee.api.plugin.Listener;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.utils.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SilentHandler extends Handler implements Listener {

	public static List<UUID> silent = new ArrayList<>();
	
	public SilentHandler(Bungee plugin) {
		super(plugin);
	}
}
