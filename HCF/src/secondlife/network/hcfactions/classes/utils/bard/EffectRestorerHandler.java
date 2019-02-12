package secondlife.network.hcfactions.classes.utils.bard;

import club.minemen.spigot.event.potion.PotionEffectExpireEvent;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.classes.Bard;
import secondlife.network.hcfactions.classes.utils.events.ArmorClassUnequipEvent;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Tasks;

import java.util.Collection;
import java.util.UUID;

public class EffectRestorerHandler extends Handler implements Listener {

	private static Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

	public EffectRestorerHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

	@EventHandler
	public void onArmorClassUnequip(ArmorClassUnequipEvent event) {
		restores.rowKeySet().remove(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPotionEffectExpire(PotionEffectExpireEvent event) {
		LivingEntity entity = event.getEntity();
		
		if(!(entity instanceof Player)) return;
		
		Player player = (Player) entity;

		PotionEffect previous = (PotionEffect) restores.remove(player.getUniqueId(), event.getEffect().getType());
		
		if(previous == null) return;
		
		event.setCancelled(true);
		
		new BukkitRunnable() {
			public void run() {
				player.addPotionEffect(previous, true);
			}
		}.runTask(this.getInstance());
	}

	public static void setRestoreEffect(Player player, PotionEffect effect) {
		boolean shouldCancel = true;
				
		Collection<PotionEffect> activeList = player.getActivePotionEffects();
		for(PotionEffect active : activeList) {
			if(!active.getType().equals(effect.getType())) continue;

			if(effect.getAmplifier() < active.getAmplifier()) {
				return;
			} else if(effect.getAmplifier() == active.getAmplifier()) {
				if(effect.getDuration() < active.getDuration()) return;
			}

			restores.put(player.getUniqueId(), active.getType(), active);
			shouldCancel = false;
			break;
		}

		Tasks.run(() -> {
			player.addPotionEffect(effect, true);
		});
		if(shouldCancel && effect.getDuration() > Bard.held_reapply_ticks && effect.getDuration() < Bard.default_max_duration) {
			restores.remove(player.getUniqueId(), effect.getType());
		}

	}
}
