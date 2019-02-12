package secondlife.network.victions.listeners;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import secondlife.network.victions.Victions;
import secondlife.network.victions.VictionsAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;

/**
 * Created by Marko on 28.07.2018.
 */
public class SpawnTagListener implements Listener {

    private Victions plugin = Victions.getInstance();

    private String[] commands = {
            "f home", "fhome", "f stuck",
            "fstuck", "logout"
    };

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            if(event.getDamager() instanceof Player) {
                Player victim = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();

                Faction victimFaction = FPlayers.getInstance().getByPlayer(victim).getFaction();
                Faction damagerFaction = FPlayers.getInstance().getByPlayer(damager).getFaction();

                if(victimFaction.isPeaceful() || damagerFaction.isPeaceful()) return;
                if(victimFaction.equals(damagerFaction) && !victimFaction.isWilderness()) return;
                if(!VictionsAPI.isPvPEnabled(victim) || !VictionsAPI.isPvPEnabled(damager)) return;
                if(PlayerData.getByName(victim.getName()).isFrozen() || PlayerData.getByName(damager.getName()).isFrozen()) return;
                //if(FactionsManager.getInstance().getVanishHandler().isVanished(victim) || FactionsManager.getInstance().getVanishHandler().isVanished(damager)) return;
                if(victimFaction.getRelationTo(damagerFaction) == Relation.ALLY) return;

                plugin.getPlayerManager().applyTagger(damager, victim);
                plugin.getPlayerManager().applyOther(damager, victim);

            } else if(event.getDamager() instanceof Projectile) {

                Projectile projectile = (Projectile) event.getDamager();

                if(projectile.getShooter() instanceof Player) {

                    Player shooter = (Player) projectile.getShooter();

                    if(shooter != event.getEntity()) {
                        Player player = (Player) event.getEntity();

                        Faction playerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
                        Faction shooterFaction = FPlayers.getInstance().getByPlayer(shooter).getFaction();

                        if(playerFaction.isPeaceful() || shooterFaction.isPeaceful()) return;
                        if(playerFaction.equals(shooterFaction) && !playerFaction.isWilderness()) return;
                        if(!VictionsAPI.isPvPEnabled(player) || !VictionsAPI.isPvPEnabled(shooter)) return;
                        if(PlayerData.getByName(player.getName()).isFrozen() || PlayerData.getByName(shooter.getName()).isFrozen()) return;
                        //if(FactionsManager.getInstance().getVanishHandler().isVanished(player) || FactionsManager.getInstance().getVanishHandler().isVanished(shooter)) return;
                        if(playerFaction.getRelationTo(shooterFaction) == Relation.ALLY) return;

                        plugin.getPlayerManager().applyTagger(shooter, player);
                        plugin.getPlayerManager().applyOther(shooter, player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if(plugin.getPlayerManager().isSpawnTagActive(player)) {
            player.setAllowFlight(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if(plugin.getPlayerManager().isSpawnTagActive(player)) {
            boolean sendMessage = false;

            for(String command : commands) {

                if(event.getMessage().toLowerCase().startsWith("/" + command.toLowerCase())) {
                    event.setCancelled(true);

                    sendMessage = true;

                }

            }

            if(sendMessage) {
                player.sendMessage(Color.translate("&eYou can't use this command while you are spawn tagged."));
            }

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(plugin.getPlayerManager().getTagged().containsKey(player.getUniqueId())) {
            plugin.getPlayerManager().getTagged().remove(player.getUniqueId());
        }
    }
}
