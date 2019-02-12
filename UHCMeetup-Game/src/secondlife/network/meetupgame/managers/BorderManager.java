package secondlife.network.meetupgame.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.border.Border;
import secondlife.network.meetupgame.border.InvisibleFix;
import secondlife.network.meetupgame.tasks.BorderTask;
import secondlife.network.meetupgame.tasks.BorderTimeTask;
import secondlife.network.meetupgame.utilties.Manager;
import secondlife.network.vituz.utilties.Color;

import java.util.Arrays;

public class BorderManager extends Manager {
	
	public static Player player;
	
	public BorderManager(MeetupGame plugin) {
		super(plugin);

		handleStart();

		new Border(Bukkit.getWorld("world"), 150);
	}
	
	private void handleStart() {
		new BukkitRunnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					checkBorder(all);
				}
			}
		}.runTaskTimer(plugin, 20L, 20L);
	}
		
	private void checkBorder(Player player) {
        int size = GameManager.getGameData().getBorder();
        World world = player.getWorld();
        
        if(world.getName().equalsIgnoreCase("world")) {
        	if (player.getLocation().getBlockX() > size) {
                player.teleport(new Location(world, size - 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                    player.sendMessage(Color.translate("&eYou have reached the border."));
                }
            }
            if (player.getLocation().getBlockZ() > size) {
                player.teleport(new Location(world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), size - 2));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                    player.sendMessage(Color.translate("&eYou have reached the border."));
                }
            }
            if (player.getLocation().getBlockX() < -size) {
                player.teleport(new Location(world, -size + 2, player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                    player.sendMessage(Color.translate("&eYou have reached the border."));
                }
            }
            if (player.getLocation().getBlockZ() < -size) {
                player.teleport(new Location(world, player.getLocation().getBlockX(), player.getLocation().getBlockY(), -size + 2));
                if (player.getLocation().getBlockY() < world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
                    player.teleport(new Location(world, player.getLocation().getBlockX(), world.getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) + 2, player.getLocation().getBlockZ()));
                    player.sendMessage(Color.translate("&eYou have reached the border."));
                }
            }
        }
    }
	
	private void shrinkBorder(int size, BukkitRunnable runnable) {
        new BorderTask().runTaskTimerAsynchronously(plugin, 200L, 200L);
        runnable.cancel();

		GameManager.getGameData().setBorder(size);
        
        World w = Bukkit.getWorld("world");
        
        new Border(w, size);

		for(Player player : w.getPlayers()) {
			if(player.getLocation().getBlockX() > size) {
				player.setNoDamageTicks(59);
				player.setFallDistance(0.0f);

				player.teleport(new Location(w, size - 4, (w.getHighestBlockYAt(size - 4, player.getLocation().getBlockZ()) + 0.5), player.getLocation().getBlockZ()));
				player.setFallDistance(0.0f);

				player.getLocation().add(0, 2, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 3, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 4, 0).getBlock().setType(Material.AIR);

				player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));
			}

			if(player.getLocation().getBlockZ() > size) {
				player.setNoDamageTicks(59);
				player.setFallDistance(0.0f);

				player.teleport(new Location(w, player.getLocation().getBlockX(), (w.getHighestBlockYAt(player.getLocation().getBlockX(), size - 4) + 0.5), size - 4));
				player.setFallDistance(0.0f);

				player.getLocation().add(0, 2, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 3, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 4, 0).getBlock().setType(Material.AIR);

				player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));
			}

			if(player.getLocation().getBlockX() < -size) {
				player.setNoDamageTicks(59);
				player.setFallDistance(0.0f);

				player.teleport(new Location(w, -size + 4, (w.getHighestBlockYAt(-size + 4, player.getLocation().getBlockZ()) + 0.5), player.getLocation().getBlockZ()));
				player.setFallDistance(0.0f);

				player.getLocation().add(0, 2, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 3, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 4, 0).getBlock().setType(Material.AIR);

				player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));

			}

			if(player.getLocation().getBlockZ() < -size) {
				player.setNoDamageTicks(59);
				player.setFallDistance(0.0f);

				player.teleport(new Location(w, player.getLocation().getBlockX(), (w.getHighestBlockYAt(player.getLocation().getBlockX(), -size + 4) + 0.5), -size + 4));
				player.setFallDistance(0.0f);

				player.getLocation().add(0, 2, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 3, 0).getBlock().setType(Material.AIR);
				player.getLocation().add(0, 4, 0).getBlock().setType(Material.AIR);

				player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));
			}
		}
    }
	
	public void handleStartSeconds() {
        new BukkitRunnable() {
            int i = 11;
            
            World w = Bukkit.getWorld("world");
            int border = GameManager.getGameData().getBorder();

            @Override
            public void run() {
                i--;
                if (i >= 1) {
                	if(Arrays.asList(10, 5, 4, 3, 2, 1).contains(i)) {
						String seconds = i > 1 ? "seconds" : "second";

						Bukkit.broadcastMessage(Color.translate("&eBorder will shrink in &d" + i + " &e" + seconds + "."));
					}
                } else if(i == 0) {
					if(border == 150) {
						new Border(w, 100);

						InvisibleFix.fixPlayer(player);

						shrinkBorder(100, this);
						GameManager.getGameData().setCanBorderTime(true);
						BorderTimeTask.seconds = 60;

						Bukkit.broadcastMessage(Color.translate("&eBorder has shrunk to &d100&e."));
					} else if(border == 100) {
						new Border(w, 50);

						InvisibleFix.fixPlayer(player);

						shrinkBorder(50, this);
						GameManager.getGameData().setCanBorderTime(true);
						BorderTimeTask.seconds = 60;

						Bukkit.broadcastMessage(Color.translate("&eBorder has shrunk to &d50&e."));
					} else if(border == 50) {
						new Border(w, 25);

						InvisibleFix.fixPlayer(player);

						shrinkBorder(25, this);
						GameManager.getGameData().setCanBorderTime(true);
						BorderTimeTask.seconds = 60;

						Bukkit.broadcastMessage(Color.translate("&eBorder has shrunk to &d25&e."));
					} else if(border == 25) {
						new Border(w, 10);

						InvisibleFix.fixPlayer(player);

						shrinkBorder(10, this);
						GameManager.getGameData().setCanBorderTime(false);
						BorderTimeTask.seconds = 0;

						Bukkit.broadcastMessage(Color.translate("&eBorder has shrunk to &d10&e."));
					}
				}
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}