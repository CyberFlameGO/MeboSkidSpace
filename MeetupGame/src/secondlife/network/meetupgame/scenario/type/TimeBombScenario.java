package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetupgame.UHC;
import secondlife.network.meetupgame.handlers.UHCHandler;
import secondlife.network.meetupgame.handlers.data.UHCPlayer;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.ScenarioHandler;
import secondlife.network.meetupgame.utilties.UHCUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.holograms.Hologram;
import secondlife.network.vituz.utilties.holograms.HologramAPI;

/**
 * Created by Marko on 11.06.2018.
 */
public class TimeBombScenario extends Scenario implements Listener {

	public static boolean clearDrops = false;

	public TimeBombScenario() {
		super("Time Bomb", Material.TNT, "When player dies,", "their loot will drop into a chest!", "After 30s, the chest will explode!");
	}
	
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(UHCHandler.started) {
            Player victim = event.getEntity();
                        
            if(clearDrops) {
            	event.getDrops().clear();
            } else {
                if(ScenarioHandler.getScenario("BareBones").isEnabled()) {
                	event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 2));
                	event.getDrops().add(new ItemStack(Material.DIAMOND));
                    event.getDrops().add(new ItemStack(Material.ARROW, 32));
                    event.getDrops().add(new ItemStack(Material.STRING, 2));
                }
                
                this.spawnHead(victim);
            }
        }
    }
	
	public static void onDeath(Player victim, UHCPlayer uhcplayer, Location where, World world) {
		if(ScenarioHandler.getScenario("Time Bomb").isEnabled()) {
			clearDrops = true;
			
			where.getBlock().setType(Material.CHEST);
			Chest chest = (Chest) where.getBlock().getState();
			
			where.add(1, 0, 0).getBlock().setType(Material.CHEST);
			where.add(0, 1, 0).getBlock().setType(Material.AIR);
			where.add(1, 1, 0).getBlock().setType(Material.AIR);
			
			chest.getInventory().addItem(UHCUtils.getGoldenHead());
			chest.getInventory().addItem(uhcplayer.getArmor());
			
			for(ItemStack itemStack : uhcplayer.getItems()) {
				if(itemStack == null || itemStack.getType() == Material.AIR) {
					continue;
				}
				
				chest.getInventory().addItem(itemStack);
			}
			
			Hologram hologram = HologramAPI.createHologram(chest.getLocation().clone().add(0.5, 1, 0), Color.translate("&a31s"));
			
			hologram.spawn();
			
			new BukkitRunnable() {
				private int time = 31;

				public void run() {
					time--;

					if(time == 0) {
						hologram.despawn();
						
						this.cancel();
						return;
					} else if(time == 1) {
						hologram.setText(Color.translate("&4" + time + "s"));
					} else if(time == 2) {
						hologram.setText(Color.translate("&c" + time + "s"));
					} else if(time == 3) {
						hologram.setText(Color.translate("&6" + time + "s"));
					} else if(time <= 15) {
						hologram.setText(Color.translate("&e" + time + "s"));
					} else {
						hologram.setText(Color.translate("&a" + time + "s"));
					}
				}
			}.runTaskTimer(UHC.getInstance(), 0L, 20L);
			
			String name = victim.getName();
			
			new BukkitRunnable() {
				public void run() {
					
                    if((where.getBlockX() < 101 && where.getBlockZ() < 101) 
                        	|| (where.getBlockX() < -101 && where.getBlockZ() < -101) 
                        	|| (where.getBlockX() < 101 && where.getBlockZ() < -101)
                        	|| (where.getBlockX() < -101 && where.getBlockZ() < 101)) {
						for(int x = where.getBlockX() - 3; x < where.getBlockX() + 3; x++) {
							for(int y = where.getBlockY() - 3; y < where.getBlockY() + 3; y++) {
								for(int z = where.getBlockZ() - 3; z < where.getBlockZ() + 3; z++) {
									Location location = new Location(world, x, y, z);
									if(location.getBlock().getType() == Material.CHEST) {
										location.getBlock().setType(Material.AIR);
									}
								}
							}
						}
						
						Msg.sendMessage(Color.translate("&8[&6&lTimeBomb&8] &f" + name + "'s &ecorpse has exploded!"));
						
						where.getWorld().createExplosion(where.getBlockX() + 0.5D, where.getBlockY() + 1, where.getBlockZ() + 0.5D, 10.0F, false, false);
						where.getWorld().strikeLightning(where);
					} else {
						where.getBlock().setType(Material.AIR);
						
						Msg.sendMessage(Color.translate("&8[&6&lTimeBomb&8] &f" + name + "'s &ecorpse has exploded!"));
						
						where.getWorld().createExplosion(where.getBlockX() + 0.5D, where.getBlockY() + 1, where.getBlockZ() + 0.5D, 10.0F, false, true);
						where.getWorld().strikeLightning(where);
					}
				}
			}.runTaskLater(UHC.getInstance(), 600L);
		}
	}
	
	private void spawnHead(Player player) {
        player.getLocation().getBlock().setType(Material.FENCE);
        player.getWorld().getBlockAt(player.getLocation().add(0.0D, 1.0D, 0.0D)).setType(Material.SKULL);
        
        Skull skull = (Skull) player.getLocation().add(0.0D, 1.0D, 0.0D).getBlock().getState();
        
        skull.setOwner(player.getName());
        skull.update();
        
        Block block = player.getLocation().add(0.0D, 1.0D, 0.0D).getBlock();
        block.setData((byte) 1);
    }

}
