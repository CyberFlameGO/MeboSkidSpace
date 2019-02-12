package secondlife.network.vituz.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Manager;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.ServerUtils;

@Getter
public class ChatControlManager extends Manager {

	private String[] blockedNormal = {
			"kurac", "picka", "dildo",
			"debil", "jebem", "jebo",
			"rekt", "noob", "nigger",
			"niger", "dick", "pussy",
			"vape.gg", "bitch", "kurva",
			"peder", "kreten", "idiot",
			"demon.gg", "drek.io", "pornhub",
			"brazzers", "xnxx", "xvideos",
			"redtube", "picke", "supak",
			"kys", "killyourself"
	};

	private String[] blockedHard = {
			"cetnik", "ustasa", "siptar"
	};

	private String[] blockedJasko = {
			"postar", "posta"
	};

	private String msg = Color.translate("&7[&4⚠&7] &eYour message contains blocked word(s).");

	@Setter
	private boolean muted = false;

	@Setter
	private int delay = 5;
	
	public ChatControlManager(Vituz plugin) {
		super(plugin);
	}

	public boolean isFiltered(Player player, PlayerData data, String message) {
		boolean cancel = false;
		String fixedMessage = message.toLowerCase().replaceAll("[^a-z0-9 ]", "").replace("@", "a").replace("3", "e").replace("0", "o").replace("4", "a").replace("1", "i").replace("5", "s");

		for(String hard : blockedHard) {
			if(!cancel && fixedMessage.contains(hard.toLowerCase())) {
				player.sendMessage(msg);
				ServerUtils.filterToBungee(player, VituzAPI.getServerName(), fixedMessage);
				VituzAPI.dispatchCommandOnMainThread("mute " + player.getName() + " 6h Bad Sportsmanship (Hard)");
				Msg.logConsole("[⚠] Muted " + player.getName() + " for bad sportsmanship (Hard) (6h)");
				cancel = true;
			}
		}

		for(String normal : blockedNormal) {
			if(!cancel && fixedMessage.contains(normal.toLowerCase()) && !fixedMessage.contains("pickaxe")) {
				player.sendMessage(msg);
				ServerUtils.filterToBungee(player, VituzAPI.getServerName(), fixedMessage);
				data.setFilter(data.getFilter() + 1);
				check(player, data);
				cancel = true;
			}
		}

		for(String jasko : blockedJasko) {
			if(!cancel && fixedMessage.contains(jasko.toLowerCase())) {
				player.sendMessage(Color.translate("&eHej ti, zasto prozivas dzaksa?!"));
				cancel = true;
			}
		}

		if(!cancel && (fixedMessage.equals("L") || fixedMessage.equals("LL") || fixedMessage.equals("LLL"))) {
			player.sendMessage(msg);
			ServerUtils.filterToBungee(player, VituzAPI.getServerName(), fixedMessage);
			data.setFilter(data.getFilter() + 1);
			check(player, data);
			cancel = true;
		}

		if(isFilterActive(player)) {
			applyFilterCooldown(player);
			data.setSpam(data.getSpam() + 1);
			check(player, data);
		} else {
			data.setSpam(0);
			applyFilterCooldown(player);
		}

		return cancel;
	}

	private void check(Player player, PlayerData data) {
		if(data.getSpam() == 2 || data.getSpam() == 3) {
			player.sendMessage(Color.translate("&cConsider sending messages slower. You might get muted for spamming!"));
		}

		if(data.getSpam() == 4) {
			data.setSpam(0);
			VituzAPI.dispatchCommandOnMainThread("mute " + player.getName() + " 2h Spamming");
			player.sendMessage(Color.translate("&cYou have been muted for spamming!"));
			Msg.logConsole("[⚠] Muted " + player.getName() + " for spamming (2h)");
		}

		if(data.getFilter() == 2) {
			data.setFilter(0);
			VituzAPI.dispatchCommandOnMainThread("mute " + player.getName() + " 2h Bad Sportsmanship (Normal)");
			player.sendMessage(Color.translate("&cYou have been muted for bad sportsmanship!"));
			Msg.logConsole("[⚠] Muted " + player.getName() + " for sportsmanship (Normal) (2h)");
		}
	}

	public void applyCooldown(Player player) {
		PlayerData.getByName(player.getName()).setChatDelay(System.currentTimeMillis() + (delay * 1000));
	}

	public boolean isActive(Player player) {
		return System.currentTimeMillis() < PlayerData.getByName(player.getName()).getChatDelay();
	}

	public long getMillisecondsLeft(Player player) {
		return Math.max(PlayerData.getByName(player.getName()).getChatDelay() - System.currentTimeMillis(), 0L);
	}

	private void applyFilterCooldown(Player player) {
		PlayerData.getByName(player.getName()).setSilentSpam(System.currentTimeMillis() + 1750);
	}

	private boolean isFilterActive(Player player) {
		return System.currentTimeMillis() < PlayerData.getByName(player.getName()).getSilentSpam();
	}
}
