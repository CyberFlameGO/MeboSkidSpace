package secondlife.network.practice.utilties;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.practice.Practice;
import secondlife.network.vituz.utilties.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public final class PlayerUtil {

	private PlayerUtil() {
	}

	public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack itemStack1 = player.getInventory().getContents()[i];
			if (itemStack1 == null || itemStack1.getType() == type || itemStack1.getType() == Material.AIR) {
				player.getInventory().setItem(i, itemStack);
				break;
			}
		}
	}

	public static boolean testPermission(CommandSender sender, String permission) {
		if(sender.hasPermission(permission)) {
			return true;
		} else {
			return false;
		}
	}

	public static void respawnPlayer(PlayerDeathEvent event){
		new BukkitRunnable() {
			public void run() {
				try {
					Object nmsPlayer = event.getEntity().getClass().getMethod("getHandle").invoke(event.getEntity());
					Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

					Class< ? > EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

					Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
					minecraftServer.setAccessible(true);
					Object mcserver = minecraftServer.get(con);

					Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
					Method moveToWorld = playerlist.getClass().getMethod("moveToWorld" , EntityPlayer , int.class , boolean.class);
					moveToWorld.invoke(playerlist , nmsPlayer , 0 , false);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.runTaskLater(Practice.getInstance(), 2L);
	}

	public static void clearPlayer(Player player) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(12.8F);
		player.setMaximumNoDamageTicks(Practice.DEFAULT_HIT_DELAY);
		player.setFireTicks(0);
		player.setFallDistance(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setWalkSpeed(0.2F);
		player.getInventory().setHeldItemSlot(0);
		player.setAllowFlight(false);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.closeInventory();
		player.setGameMode(GameMode.SURVIVAL);
		player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
		player.updateInventory();
	}

	public static void sendFirework(FireworkEffect effect, Location location) {
		Firework f = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fm = f.getFireworkMeta();
		fm.addEffect(effect);
		f.setFireworkMeta(fm);

		try {
			Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
			Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
			Object firework = craftFireworkClass.cast(f);
			Method handle = firework.getClass().getMethod("getHandle");
			Object entityFirework = handle.invoke(firework);
			Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
			Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
			ticksFlown.setAccessible(true);
			ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
			ticksFlown.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendMessage(String message, Player... players) {
		for (Player player : players) {
			player.sendMessage(Color.translate(message));
		}
	}

	public static void sendMessage(String message, Set<Player> players) {
		for (Player player : players) {
			player.sendMessage(message);
		}
	}

	private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = prefix + version + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}
}
