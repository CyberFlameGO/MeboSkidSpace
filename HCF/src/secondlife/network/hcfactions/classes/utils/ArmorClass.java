package secondlife.network.hcfactions.classes.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class ArmorClass {

	public static long default_max_duration = TimeUnit.MINUTES.toMillis(8L);

	public Set<PotionEffect> passiveEffects = new HashSet<>();
	public String name;
	public int warmupDelay;

	public ArmorClass(String name, int warmupDelay) {
		this.name = name;
		this.warmupDelay = warmupDelay;
	}

	public boolean onEquip(Player player) {
		for(PotionEffect effect : passiveEffects) {
			player.addPotionEffect(effect, true);
		}

		player.sendMessage(Color.translate("&eClass &d" + name + " &7" + Msg.KRUZIC + " &aEnabled&e!"));
		return true;
	}

	public void onUnequip(Player player) {
		for(PotionEffect effect : passiveEffects) {
			for(PotionEffect active : player.getActivePotionEffects()) {
				if(active.getDuration() > default_max_duration && active.getType().equals(effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
					if(player.isOnline()) {
						player.removePotionEffect(effect.getType());
						break;
					}
				}
			}
		}

		player.sendMessage(Color.translate("&eClass &d" + name + " &7" + Msg.KRUZIC + " &cDisabled&e!"));
	}

	public abstract boolean isApplicableFor(Player player);
}
