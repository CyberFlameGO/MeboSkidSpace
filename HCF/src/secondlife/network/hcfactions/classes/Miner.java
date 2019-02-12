package secondlife.network.hcfactions.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.classes.utils.ArmorClass;

public class Miner extends ArmorClass implements Listener {
    
    public Miner() {
		super("Miner", !HCFConfiguration.kitMap ? 3 : 1);
		
        passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    public void onUnequip(Player player) {
        super.onUnequip(player);
    }

    @Override
    public boolean isApplicableFor(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        
        if(helmet == null || helmet.getType() != Material.IRON_HELMET) return false;
        
        ItemStack chestplate = player.getInventory().getChestplate();
        if(chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE) return false;
        
        ItemStack leggings = player.getInventory().getLeggings();
        if(leggings == null || leggings.getType() != Material.IRON_LEGGINGS) return false;
        
        ItemStack boots = player.getInventory().getBoots();
        return boots != null && boots.getType() == Material.IRON_BOOTS;
    }
}
