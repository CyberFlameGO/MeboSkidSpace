package secondlife.network.practice.runnable;

import secondlife.network.practice.Practice;
import java.util.Iterator;
import org.bukkit.entity.Item;

public class ItemDespawnRunnable implements Runnable {

	private final Practice plugin = Practice.getInstance();

	@Override
	public void run() {
		/*Iterator<Item> it = this.plugin.getFfaManager().getItemTracker().keySet().iterator();
		while (it.hasNext()) {
			Item item = it.next();
			long l = this.plugin.getFfaManager().getItemTracker().get(item);
			if (l + 15000 < System.currentTimeMillis()) {
				item.remove();
				it.remove();
			}
		}*/
	}

}
