package secondlife.network.bungee.utils;

import lombok.Getter;
import secondlife.network.bungee.Bungee;
import secondlife.network.bungee.handlers.AnnounceHandler;
import secondlife.network.bungee.handlers.ReportHandler;
import secondlife.network.bungee.handlers.RequestHandler;

public class Handler {

	@Getter private Bungee plugin;
    
    public Handler(Bungee plugin) {
        this.plugin = plugin;
    }
    
    public static void clear() {
    	AnnounceHandler.disable();
    	RequestHandler.disable();
    	ReportHandler.disable();
    }
}
