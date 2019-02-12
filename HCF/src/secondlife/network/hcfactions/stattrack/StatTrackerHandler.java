package secondlife.network.hcfactions.stattrack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;

public class StatTrackerHandler extends Handler implements Listener {

    public StatTrackerHandler(HCF plugin) {
        super(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer != null) {
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    new StatTracker(itemStack, StatTrackerType.ARMOR).add(killer.getDisplayName(), player.getDisplayName());
                }
            }

            if (killer.getItemInHand() != null) {
                ItemStack itemStack = killer.getItemInHand();
                if (itemStack.getType().name().contains("SWORD")) {
                    new StatTracker(itemStack, StatTrackerType.WEAPON).add(killer.getDisplayName(), player.getDisplayName());
                }
            }
        }
    }

}
