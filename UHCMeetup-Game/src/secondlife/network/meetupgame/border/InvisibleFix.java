package secondlife.network.meetupgame.border;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.MeetupData;

import java.util.ArrayList;
import java.util.List;

public class InvisibleFix {
	
	public static void fixPlayer(Player player) {
		new BukkitRunnable() {
			public void run() {
				World world = player.getWorld();
				WorldServer worldServer = ((CraftWorld) world).getHandle();

				EntityTracker tracker = worldServer.tracker;
				EntityTrackerEntry entry = tracker.trackedEntities.get(player.getEntityId());
				
				List<EntityHuman> players = new ArrayList<>();
				
				int distance = 64 * 64;

				for(Player all : Bukkit.getOnlinePlayers()) {
					MeetupData uhcp = MeetupData.getByName(all.getName());
					if(all.getWorld() == player.getWorld() && all.getLocation().distanceSquared(player.getLocation()) <= distance && uhcp.isAlive()) {
						players.add((EntityHuman) all);
					}
				}
				
				entry.trackedPlayers.removeAll(players);
				entry.track(players);
			}
		}.runTaskLater(MeetupGame.getInstance(), 20L);
	}
}
