package secondlife.network.hcfactions.handlers;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.Handler;

public class DeathMessagesHandler extends Handler implements Listener {
		
	public DeathMessagesHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        
        if(message == null || message.isEmpty()) return;
        
		event.setDeathMessage(getDeathMessage(message, event.getEntity(), getKiller(event)));
	}

    private CraftEntity getKiller(PlayerDeathEvent event) {
        EntityLiving lastAttacker = ((CraftPlayer) event.getEntity()).getHandle().getLastDamager();
        
        return lastAttacker == null ? null : lastAttacker.getBukkitEntity();
    }
    
    private String getDeathMessage(String input, Entity entity, Entity killer) {
		input = input.replaceFirst("\\[", ChatColor.GRAY + "[" + ChatColor.GRAY);
		input = replaceLast(input, "]", ChatColor.GRAY + "]" + ChatColor.GRAY);
    	
        if(entity != null) {
            input = input.replaceFirst("(?i)" + getEntityName(entity), ChatColor.RED + getPlayerDisplayName(entity) + ChatColor.YELLOW);
        }

        if(killer != null && (entity == null || !killer.equals(entity))) {
            input = input.replaceFirst("(?i)" + getEntityName(killer), ChatColor.RED + getKillerDisplayName(killer) + ChatColor.YELLOW);
        }

        return input;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
    }

    public static String getEntityName(Entity entity) {        
        return entity instanceof Player ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
    }

    public static String getPlayerDisplayName(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            HCFData data = HCFData.getByName(player.getName());
            
            return player.getName() + ChatColor.GRAY + '[' + ChatColor.RESET + data.getKills() + ChatColor.GRAY + ']';
        } else {
            return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
        }
    }
    
    public static String getKillerDisplayName(Entity entity) {
        if(entity instanceof Player) {
            Player player = (Player) entity;

            HCFData data = HCFData.getByName(player.getName());
                        
            return player.getName() + ChatColor.GRAY + '[' + ChatColor.RESET + data.getKills() + ChatColor.GRAY + ']';
        } else {
            return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
        }
    }
}
