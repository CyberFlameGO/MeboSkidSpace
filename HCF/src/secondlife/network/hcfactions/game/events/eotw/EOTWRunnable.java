package secondlife.network.hcfactions.game.events.eotw;

import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.game.events.faction.KothFaction;

import java.util.concurrent.TimeUnit;

public class EOTWRunnable extends BukkitRunnable {
	
	public static long eotw_wait_millis = TimeUnit.SECONDS.toMillis(15L);
    public static int eotw_wait_seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(eotw_wait_millis));
    public static long eotw_cappable_wait = TimeUnit.SECONDS.toMillis(30L);
    
	public static boolean hasInformedStarted;
	public static boolean hasInformedCapable;
	public static long startStamp;
	public static KothFaction kothFaction;

	public EOTWRunnable() {
		startStamp = (System.currentTimeMillis() + eotw_wait_millis);
	}

	@Override
	public void run() {
		/*long elapsedMillis = getElapsedMilliseconds();
		int elapsedSeconds = (int) Math.round(elapsedMillis / 1000.0D);
		
		if((!hasInformedStarted) && (elapsedSeconds >= 0)) {
			Faction eotwFaction = RegisterHandler.getInstancee().getFactionManager().getFaction("EOTW");
			
			if(eotwFaction == null) {
				eotwFaction = new KothFaction();
			} else if(!(eotwFaction instanceof KothFaction)) {
				RegisterHandler.getInstancee().getFactionManager().removeFaction(eotwFaction, Bukkit.getConsoleSender());
				
				eotwFaction = new KothFaction();
			}

			kothFaction = ((KothFaction) eotwFaction);
			
			for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
				if(!(faction instanceof PlayerFaction)) return;
				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "f setdtr all -999");
			}

			Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "All factions have been set raidable!");
			
			Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "All pvp protected users have been removed!");

			hasInformedStarted = true;
			
			Bukkit.broadcastMessage(Color.translate("&8[&4&lEOTW&8] &4&lEOTW &chas begun!"));
		} else if((!hasInformedCapable) && (elapsedMillis >= eotw_cappable_wait)) {
			if(kothFaction != null) {
				GameHandler.getGameHandler().tryContesting(kothFaction, Bukkit.getConsoleSender());
			}
			
			hasInformedCapable = true;
		}
		if((elapsedMillis < 0L) && (elapsedMillis >= -eotw_wait_millis)) {
			if(elapsedSeconds < 0) {
				return;
			}
			
			Bukkit.broadcastMessage(Color.translate("&8[&4&lEOTW&8] &4&lEOTW &cis starting in &4&l" + StringUtils.getRemaining(Math.abs(elapsedSeconds) * 1000L, true, false) + "!"));
			
            for(Player players : Bukkit.getOnlinePlayers()) {
				players.playSound(players.getLocation(), Sound.NOTE_BASS_DRUM, 1f, 1f);
			}
		}*/
	}
	
	public static long getStartingTime() {
		long difference = System.currentTimeMillis() - startStamp;
		
		return difference > 0L ? 0L : Math.abs(difference);
	}

	public static long getStartingTime(long now) {
		long difference = now - startStamp;
		
		return difference > 0L ? 0L : Math.abs(difference);
	}

	public static long getCappableTime() {
		return eotw_cappable_wait - getElapsedMilliseconds();
	}

	public static long getElapsedMilliseconds() {
		return System.currentTimeMillis() - startStamp;
	}
}