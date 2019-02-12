package secondlife.network.hcfactions.timers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

public class GappleHandler extends Handler implements Listener {

    public GappleHandler(HCF plugin) {
        super(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
    	Player player = event.getPlayer();
    	ItemStack stack = event.getItem();
        
        if(stack.getType().equals(Material.GOLDEN_APPLE) && stack.getDurability() == 1) {
            if(isActive(player)) {
                event.setCancelled(true);
                
                player.sendMessage(Color.translate("&cYou still have a &6&lGapple &ccooldown for another &l" + StringUtils.getRemaining(getMillisecondsLeft(player), true, false) + "&c!"));
            } else {
                applyCooldown(player);
            }
        }
    }

    public static void applyCooldown(Player player) {
        HCFData.getByName(player.getName()).setGapple(System.currentTimeMillis() + 7200 * 1000);

        player.sendMessage(Color.translate("&2&l▇▇&0&l▇&0&l▇&2&l▇▇▇▇"));
        player.sendMessage(Color.translate("&2&l▇▇▇&0&l▇&2&l▇▇▇▇"));
        player.sendMessage(Color.translate("&2&l▇▇&6&l▇▇▇▇&2&l▇▇ &6Super Golden Apple:"));
        player.sendMessage(Color.translate("&2&l▇&6&l▇▇&d&l▇&6&l▇▇▇&2&l▇ &2Consumed"));
        player.sendMessage(Color.translate("&2&l▇&6&l▇&d&l▇&6&l▇▇▇▇&2&l▇ &6Cooldown: &d2 hours"));
        player.sendMessage(Color.translate("&2&l▇&6&l▇▇▇▇▇▇&2&l▇"));
        player.sendMessage(Color.translate("&2&l▇&6&l▇▇▇▇▇▇&2&l▇"));
        player.sendMessage(Color.translate("&2&l▇▇&6&l▇▇▇▇&2&l▇▇"));
    }


    public static boolean isActive(Player player) {
        if(HCFData.getByName(player.getName()) == null) return false;

        return System.currentTimeMillis() < HCFData.getByName(player.getName()).getGapple();
    }

    public static long getMillisecondsLeft(Player player) {
        return Math.max(HCFData.getByName(player.getName()).getGapple() - System.currentTimeMillis(), 0L);
    }
}
