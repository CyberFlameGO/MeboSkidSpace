package secondlife.network.hcfactions.timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.providers.nametags.VituzNametag;

import java.util.ArrayList;
import java.util.UUID;

public class ArcherHandler extends Handler implements Listener {
	
	public static ArrayList<UUID> tag = new ArrayList<>();

	public ArcherHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, this.getInstance());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		
		if(!tag.contains(player.getUniqueId())) return;
				
		event.setDamage(event.getDamage() * (1.0 + 25 / 100.0));
	}
	
	public static class TaggedTask extends BukkitRunnable {
		private Player victim;

		public TaggedTask(Player victim) {
			this.victim = victim;
			
			tag.add(victim.getUniqueId());
			
			this.runTaskLater(HCF.getInstance(), 10 * 20L);
		}

		public void run() {
			tag.remove(this.victim.getUniqueId());
			
			VituzNametag.reloadPlayer(this.victim);
			VituzNametag.reloadOthersFor(this.victim);
		}
	}
}