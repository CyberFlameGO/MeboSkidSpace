package secondlife.network.meetupgame.scenario.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Tasks;

import java.util.List;

public class TimeBombScenario extends Scenario implements Listener {

	public TimeBombScenario() {
		super("Time Bomb", Material.TNT, "When player dies,", "their loot will drop into a chest.", "After 30s, the chest will explode.");
	}

	public static void handleDeath(List<ItemStack> drops, Entity entity) {
		if(!(entity instanceof Player)) return;

		Player victim = (Player) entity;

		Location where = victim.getLocation();

		drops.clear();

		where.getBlock().setType(Material.CHEST);
		Chest chest = (Chest) where.getBlock().getState();

		where.add(1, 0, 0).getBlock().setType(Material.CHEST);
		where.add(0, 1, 0).getBlock().setType(Material.AIR);
		where.add(1, 1, 0).getBlock().setType(Material.AIR);

		chest.getInventory().addItem(MeetupUtils.getGoldenHead());

		for(ItemStack itemStack : victim.getInventory().getArmorContents()) {
			if(itemStack == null || itemStack.getType() == Material.AIR) {
				continue;
			}

			chest.getInventory().addItem(itemStack);
		}

		for(ItemStack itemStack : victim.getInventory().getContents()) {
			if(itemStack == null || itemStack.getType() == Material.AIR) {
				continue;
			}

			chest.getInventory().addItem(itemStack);
		}

		/*Hologram hologram = HologramAPI.createHologram(chest.getLocation().clone().add(0.5, 1, 0), Color.translate("&a31s"));

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
		}.runTaskTimer(MeetupGame.getInstance(), 0L, 20L);*/

		String name = victim.getName();

		Tasks.runLater(() -> {
			if((where.getBlockX() < 101 && where.getBlockZ() < 101)
					|| (where.getBlockX() < -101 && where.getBlockZ() < -101)
					|| (where.getBlockX() < 101 && where.getBlockZ() < -101)
					|| (where.getBlockX() < -101 && where.getBlockZ() < 101)) {
				for(int x = where.getBlockX() - 3; x < where.getBlockX() + 3; x++) {
					for(int y = where.getBlockY() - 3; y < where.getBlockY() + 3; y++) {
						for(int z = where.getBlockZ() - 3; z < where.getBlockZ() + 3; z++) {
							Location location = new Location(Bukkit.getWorld("world"), x, y, z);
							if(location.getBlock().getType() == Material.CHEST) {
								location.getBlock().setType(Material.AIR);
							}
						}
					}
				}

				Msg.sendMessage("&d" + name + "'s &ecorpse has exploded.");

				where.getWorld().createExplosion(where.getBlockX() + 0.5D, where.getBlockY() + 1, where.getBlockZ() + 0.5D, 10.0F, false, false);
				where.getWorld().strikeLightning(where);
			} else {
				where.getBlock().setType(Material.AIR);

				Msg.sendMessage("&d" + name + "'s &ecorpse has exploded.");

				where.getWorld().createExplosion(where.getBlockX() + 0.5D, where.getBlockY() + 1, where.getBlockZ() + 0.5D, 10.0F, false, true);
				where.getWorld().strikeLightning(where);
			}
		}, 600L);
	}
}
