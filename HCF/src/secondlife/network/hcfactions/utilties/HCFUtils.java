package secondlife.network.hcfactions.utilties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.time.FastDateFormat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import secondlife.network.hcfactions.staff.OptionType;
import secondlife.network.hcfactions.staff.handlers.StaffModeHandler;
import secondlife.network.hcfactions.staff.handlers.VanishHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class HCFUtils {
	
	public static String BIG_LINE = Color.translate("&7&m-------------------------------");
	public static String NO_FACTION = Color.translate("&cYou aren't in a faction!");
	public static String ALREADY_IN_FACTION = Color.translate("&cYou are already in a faction!");
    public static String INVALID_ROLE = Color.translate("&cYou aren't allowed to do that with this role!");
	public static String FACTION_NOT_FOUND = Color.translate("&cThat faction doesn't exists!");
	
	public static FastDateFormat KOTH_FORMAT = FastDateFormat.getInstance("m:ss", TimeZone.getTimeZone("Europe/Zagreb"), Locale.ENGLISH);
    public static ImmutableMap<ChatColor, DyeColor> CHAT_DYE_COLOUR_MAP = ImmutableMap.<ChatColor, DyeColor> builder().put(ChatColor.AQUA, DyeColor.LIGHT_BLUE).put(ChatColor.BLACK, DyeColor.BLACK).put(ChatColor.BLUE, DyeColor.LIGHT_BLUE).put(ChatColor.DARK_AQUA, DyeColor.CYAN).put(ChatColor.DARK_BLUE, DyeColor.BLUE).put(ChatColor.DARK_GRAY, DyeColor.GRAY).put(ChatColor.DARK_GREEN, DyeColor.GREEN).put(ChatColor.DARK_PURPLE, DyeColor.PURPLE).put(ChatColor.DARK_RED, DyeColor.RED).put(ChatColor.GOLD, DyeColor.ORANGE).put(ChatColor.GRAY, DyeColor.SILVER).put(ChatColor.GREEN, DyeColor.LIME).put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA).put(ChatColor.RED, DyeColor.RED).put(ChatColor.WHITE, DyeColor.WHITE).put(ChatColor.YELLOW, DyeColor.YELLOW).build();
    public static ImmutableSet<PotionEffectType> DEBUFF_TYPES = ImmutableSet.<PotionEffectType> builder().add(PotionEffectType.BLINDNESS).add(PotionEffectType.CONFUSION).add(PotionEffectType.HARM).add(PotionEffectType.HUNGER).add(PotionEffectType.POISON).add(PotionEffectType.SATURATION).add(PotionEffectType.SLOW).add(PotionEffectType.SLOW_DIGGING).add(PotionEffectType.WEAKNESS).add(PotionEffectType.WITHER).build();;

    public static void clearPlayer(Player player) {
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
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

    public static Location getHighestLocation(Location origin, Location def) {
        Location cloned = origin.clone();
        World world = cloned.getWorld();
        
        int x = cloned.getBlockX();
        int y = world.getMaxHeight();
        int z = cloned.getBlockZ();
        
        while(y > origin.getBlockY()) {
            Block block = world.getBlockAt(x, --y, z);
            
            if(!block.isEmpty()) {
                Location next = block.getLocation();
                
                next.setPitch(origin.getPitch());
                next.setYaw(origin.getYaw());
                return next;
            }
        }
        
        return def;
    }
    
    public static Player getFinalAttacker(EntityDamageEvent ede, boolean ignoreSelf) {
        Player attacker = null;
                
        if(ede instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ede;
            Entity damager = event.getDamager();
            
            if(event.getDamager() instanceof Player) {
                attacker = (Player) damager;
            } else if(event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                ProjectileSource shooter = projectile.getShooter();
                
                if(shooter instanceof Player) {
                    attacker = (Player) shooter;
                }
            }
            
            if(attacker != null && ignoreSelf && event.getEntity().equals(attacker)) {
                attacker = null;
            }
        }
        
        return attacker;
    }
    
    public static boolean isDebuff(PotionEffectType type) {
        return DEBUFF_TYPES.contains((Object) type);
    }

    public static boolean isDebuff(PotionEffect potionEffect) {
        return isDebuff(potionEffect.getType());
    }

    public static boolean isDebuff(ThrownPotion thrownPotion) {
        for (PotionEffect effect : thrownPotion.getEffects()) {
            if (isDebuff(effect)) {
                return true;
            }
        }
        return false;
    }
    
    public static DyeColor toDyeColor(ChatColor colour) {
        return (DyeColor) CHAT_DYE_COLOUR_MAP.get((Object) colour);
    }

	public static void disableStaffMode(Player player) {
		if(StaffModeHandler.isInStaffMode(player)) {
			StaffModeHandler.disableStaffMode(player);
		}
		
		if(VanishHandler.isVanished(player)) {
			VanishHandler.unvanishPlayer(player);
		}
	}
	
	public static ThreadLocal<DecimalFormat> seconds = new ThreadLocal() {
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0");
		}
	};
	
	public static String getBardFormat(long millis, boolean trailingZero) {
		return (trailingZero ? StringUtils.remaining_seconds_trailing : StringUtils.remaining_seconds).get().format(millis * 0.001);
	}
	
	public static String getBardFormat(long millis, boolean trailingZero, boolean showMillis) {
		return (showMillis ? (trailingZero ? StringUtils.remaining_seconds_trailing : StringUtils.remaining_seconds) : seconds).get().format(millis * 0.001);
	}
	
	public static String getVanishOptionsList(Player player) {
		StringBuilder builder = new StringBuilder();
		for(OptionType optionType : OptionType.values()) {
			if(optionType.getPlayers().contains(player.getUniqueId())) {
				builder.append(ChatColor.GREEN + optionType.getName()).append(", ");
			} else {
				builder.append(ChatColor.RED + optionType.getName()).append(ChatColor.GRAY + ", ");
			}
		}
		if(builder.length() != 0) {
			builder.delete(builder.length() - 2, builder.length());
		}
		
		return builder.toString();
	}
}
