package secondlife.network.meetupgame.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

/**
 * Created by Marko on 12.06.2018.
 */
public class GameListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player entity = (Player) event.getEntity();

        if(!(event.getDamager() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getDamager();

        if(!(arrow.getShooter() instanceof Player)) return;

        Player shooter = (Player) arrow.getShooter();

        if(entity.getName().equals(shooter.getName())) return;

        double health = Math.ceil(entity.getHealth() - event.getFinalDamage()) / 2.0D;

        if(health > 0.0D) {
            shooter.sendMessage(Color.translate("&d" + entity.getName() + " &eis now at &d" + health + Msg.HEART + "&e."));
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if(item == null) return;
        if(item.getType() != Material.GOLDEN_APPLE) return;
        if(item.getItemMeta() == null) return;
        if(!item.getItemMeta().hasDisplayName()) return;
        if(!item.getItemMeta().getDisplayName().contains("Golden Head")) return;

        Player player = event.getPlayer();

        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
    }
}
