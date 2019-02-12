package secondlife.network.victions.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.victions.Victions;
import secondlife.network.victions.managers.PotionLimitManager;
import secondlife.network.vituz.utilties.Color;

public class PotionLimitListener implements Listener {

	private Victions plugin = Victions.getInstance();

	@EventHandler
    public void onBrew(BrewEvent event) {
		BrewerInventory brewer = event.getContents();
		
		ItemStack ingredient = brewer.getIngredient().clone();
        ItemStack[] potions = new ItemStack[3];
        
        for(int i = 0; i < 3; ++i) {
        	if(event.getContents().getItem(i) != null) {
        		potions[i] = brewer.getItem(i).clone();
        	}
        }

        new BukkitRunnable() {
            public void run() {
                for(int i = 0; i < 3; ++i) {
                    if(brewer.getItem(i) != null && brewer.getItem(i).getType() == Material.POTION) {
						for(PotionEffect potionEffect : Potion.fromItemStack(brewer.getItem(i)).getEffects()) {
							for(PotionLimitManager.PotionLimit potionLimit : plugin.getPotionLimitManager().getPotionLimits()) {
								int maxLevel = potionLimit.getLevel();
								int level = potionEffect.getAmplifier() + 1;

								Potion potion = Potion.fromItemStack(brewer.getItem(i));

								if(maxLevel == 0 || level > maxLevel) {
									brewer.setIngredient(ingredient);

									for(int item = 0; item < 3; ++item) {
										brewer.setItem(item, potions[item]);
									}

									return;
								}

								if(potion.hasExtendedDuration() && !potionLimit.isExtended()) {
									brewer.setIngredient(ingredient);

									for(int item = 0; item < 3; ++item) {
										brewer.setItem(item, potions[item]);
									}

									return;
								}
							}
						}
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }
	
	@EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
        if(!item.getType().equals(Material.POTION)) return;
        if(item.getType().equals(Material.POTION) && item.getDurability() == 0) return;

        Potion.fromItemStack(item).getEffects().forEach(potionEffect -> {
        	plugin.getPotionLimitManager().getPotionLimits().forEach(potionLimit -> {
        		if(potionLimit.getType().equals(potionEffect.getType())) {
					int maxLevel = potionLimit.getLevel();
					int level = potionEffect.getAmplifier() + 1;

					Potion potion = Potion.fromItemStack(item);

					if(maxLevel == 0 || level > maxLevel) {
						event.setCancelled(true);
						player.setItemInHand(new ItemStack(Material.AIR));
						player.sendMessage(Color.translate("&cThis Potion Effect is disabled."));
						return;
					}

					if(potion.hasExtendedDuration() && !potionLimit.isExtended()) {
						event.setCancelled(true);
						player.setItemInHand(new ItemStack(Material.AIR));
						player.sendMessage(Color.translate("&cThis Potion Effect is disabled."));
						return;
					}
				}
			});
		});
    }
	
	@EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion thrownPotion = event.getPotion();

		thrownPotion.getEffects().forEach(potionEffect -> {
			plugin.getPotionLimitManager().getPotionLimits().forEach(potionLimit -> {
				if(potionLimit.getType().equals(potionEffect.getType())) {
					if(thrownPotion.getShooter() instanceof Player) {
						Player shooter = (Player) thrownPotion.getShooter();

						int maxLevel = potionLimit.getLevel();
						int level = potionEffect.getAmplifier() + 1;

						Potion potion = Potion.fromItemStack(thrownPotion.getItem());

						if(maxLevel == 0 || level > maxLevel) {
							event.setCancelled(true);
							shooter.sendMessage(Color.translate("&cThis Potion Effect is disabled."));
							return;
						}
						if(potion.hasExtendedDuration() && !potionLimit.isExtended()) {
							event.setCancelled(true);
							shooter.sendMessage(Color.translate("&cThis Potion Effect is disabled."));
							return;
						}
					} else {
						int maxLevel = potionLimit.getLevel();
						int level = potionEffect.getAmplifier();

						Potion potion = Potion.fromItemStack(thrownPotion.getItem());

						if(maxLevel == 0 || level > maxLevel) {
							event.setCancelled(true);
							return;
						}

						if(potion.hasExtendedDuration() && !potionLimit.isExtended()) {
							event.setCancelled(true);
							return;
						}
					}
				}
			});
		});
    }
}
