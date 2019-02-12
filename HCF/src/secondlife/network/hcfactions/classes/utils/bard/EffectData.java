package secondlife.network.hcfactions.classes.utils.bard;

import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectData {

	public PotionEffect clickable;
	public PotionEffect heldable;
	
	public int energyCost;

	public EffectData(int energyCost, PotionEffect clickable, PotionEffect heldable) {
		this.energyCost = energyCost;
		this.clickable = clickable;
		this.heldable = heldable;
	}
}
