package secondlife.network.meetupgame.listeners;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import secondlife.network.meetupgame.data.MeetupData;

public class DeathMessagesListener implements Listener {

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
        if(entity != null) {
            input = input.replaceFirst("(?i)" + getEntityName(entity), ChatColor.RED + getPlayerDisplayName(entity) + ChatColor.YELLOW);
        }

        if(killer != null && (entity == null || !killer.equals(entity))) {
            input = input.replaceFirst("(?i)" + getEntityName(killer), ChatColor.RED + getKillerDisplayName(killer) + ChatColor.YELLOW);
        }

        return input;
    }

    public static String getEntityName(Entity entity) {        
        return entity instanceof Player ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
    }

    public static String getPlayerDisplayName(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            MeetupData uhcData = MeetupData.getByName(player.getName());
            
            return player.getName() + ChatColor.GRAY + '[' + ChatColor.RESET + uhcData.getGameKills() + ChatColor.GRAY + ']';
        } else {
            return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
        }
    }
    
    public static String getKillerDisplayName(Entity entity) {
        if(entity instanceof Player) {
            Player player = (Player) entity;

            MeetupData uhcData = MeetupData.getByName(player.getName());
            int kills = (uhcData.getGameKills() + 1);

            return player.getName() + ChatColor.GRAY + '[' + ChatColor.RESET + kills + ChatColor.GRAY + ']';
        } else {
            return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
        }
    }
}
