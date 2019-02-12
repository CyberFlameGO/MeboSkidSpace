package secondlife.network.uhc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.deathlookup.data.DeathData;
import secondlife.network.uhc.deathlookup.data.ProfileFight;
import secondlife.network.uhc.deathlookup.data.ProfileFightEnvironment;
import secondlife.network.uhc.deathlookup.data.killer.type.ProfileFightEnvironmentKiller;
import secondlife.network.uhc.deathlookup.data.killer.type.ProfileFightPlayerKiller;
import secondlife.network.uhc.managers.PracticeManager;
import secondlife.network.uhc.utilties.BaseListener;
import secondlife.network.vituz.utilties.PlayerUtils;

public class ProfileFightListener extends BaseListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!PlayerUtils.isMongoConnected()) return;

        DeathData data = new DeathData(event.getName());

        if(!data.isLoaded()) {
            data.load();
        }

        if(!data.isLoaded()) {
            PlayerUtils.kick(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DeathData.getByName(event.getPlayer().getName()).save();
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if(plugin.getPracticeManager().getUsers().contains(player.getUniqueId())) {
            return;
        }

        DeathData deathData = DeathData.getByName(player.getName());
        EntityDamageEvent damageEvent = player.getLastDamageCause();

        if (player.getKiller() != null) {
            ProfileFight fight = new ProfileFight(player, new ProfileFightPlayerKiller(player.getKiller()));
            deathData.getFights().add(fight);

            DeathData.getByName(player.getKiller().getName()).getFights().add(fight);

            new BukkitRunnable() {
                @Override
                public void run() {
                    deathData.save();
                }
            }.runTaskAsynchronously(UHC.getInstance());
            return;
        }

        if (damageEvent == null) {
            deathData.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    deathData.save();
                }
            }.runTaskAsynchronously(UHC.getInstance());
            return;
        }

        DamageCause cause = damageEvent.getCause();

        if (cause == DamageCause.PROJECTILE || cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.POISON || cause == DamageCause.MAGIC || cause == DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        try {
            deathData.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(cause.name().toUpperCase()))));
            new BukkitRunnable() {
                @Override
                public void run() {
                    deathData.save();
                }
            }.runTaskAsynchronously(UHC.getInstance());
        } catch (Exception ignored) {
            deathData.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    deathData.save();
                }
            }.runTaskAsynchronously(UHC.getInstance());
        }
    }
}
