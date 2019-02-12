package secondlife.network.victions.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import secondlife.network.victions.Victions;
import secondlife.network.victions.kit.Kit;
import secondlife.network.victions.utilities.CustomLocation;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class FactionsData {

	@Getter
	public static Map<String, FactionsData> factionDatas = new HashMap<>();

	private String name;
	private String realName;

	private int kills = 0;
	private int deaths = 0;
	private int balance = 0;

	private boolean jellyLegs = false;
	private boolean nightVision = false;
	private boolean factionFly = false; // Ne treba save
	private boolean loaded;
	private boolean needToTeleport = false;

	private Map<String, Long> kitCooldown = new HashMap<>();
	private Map<UUID, Long> homeCooldown = new HashMap<>();
	private Map<UUID, Long> logoutCooldown = new HashMap<>();
	private Map<UUID, Long> pearlCooldown = new HashMap<>();
	private Map<String, CustomLocation> homes = new HashMap<>();

	public FactionsData(String name) {
		this.name = name;

		factionDatas.put(name, this);
	}

	public void save() {
		if(!loaded) return;

		Document document = new Document();

		JsonArray homeElements = new JsonArray();

		homes.entrySet().forEach(home -> {
			JsonObject homeDocument = new JsonObject();

			homeDocument.addProperty("home_name", home.getKey());
			homeDocument.addProperty("home_location", CustomLocation.locationToString(home.getValue()));

			homeElements.add(homeDocument);
		});

		document.put("name", name.toLowerCase());
		document.put("realName", name);
		document.put("kills", kills);
		document.put("deaths", deaths);
		document.put("balance", balance);
		document.put("jelly_legs", jellyLegs);
		document.put("night_vision", nightVision);
		document.put("homes", homeElements);

		Vituz.getInstance().getDatabaseManager().getFactionsProfiles().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));

		loaded = false;
	}

	public void load() {
		Document document = (Document) Vituz.getInstance().getDatabaseManager().getFactionsProfiles().find(Filters.eq("name", this.name.toLowerCase())).first();

		if(document != null) {
			new JsonParser().parse(document.getString("homes")).getAsJsonArray().forEach(element -> {
				JsonObject homeDocument = element.getAsJsonObject();

				if(homeDocument.has("home_name")) {
					setHome(homeDocument.get("home_name").getAsString(),
							CustomLocation.stringToLocation(homeDocument.get("home_location").getAsString()));
				}
			});

			this.realName = document.getString("realName");
			this.kills = document.getInteger("kills");
			this.deaths = document.getInteger("deaths");
			this.balance = document.getInteger("balance");
			this.jellyLegs = document.getBoolean("jelly_legs");
			this.nightVision = document.getBoolean("night_vision");
		}

		loaded = true;
	}

	public boolean isPearlActive(Player player) {
		return pearlCooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < pearlCooldown.get(player.getUniqueId());
	}

	public void cancelPearl(Player player) {
		if(pearlCooldown.containsKey(player.getUniqueId())) {
			pearlCooldown.remove(player.getUniqueId());
		}
	}

	public void applyPearlCooldown(Player player) {
		pearlCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (15 * 1000));
	}

	public long getPearlMillisecondsLeft(Player player) {
		if(pearlCooldown.containsKey(player.getUniqueId())) {
			return Math.max(pearlCooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}

	public boolean isLogoutActive(Player player) {
		return logoutCooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < logoutCooldown.get(player.getUniqueId());
	}

	public void cancelLogout(Player player) {
		if(logoutCooldown.containsKey(player.getUniqueId())) {
			logoutCooldown.remove(player.getUniqueId());
		}
	}

	public void applyLogoutCooldown(Player player) {
		logoutCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (30 * 1000));

		Bukkit.getScheduler().runTaskLater(Victions.getInstance(), () -> {
			if(isLogoutActive(player)) {
				player.setMetadata("LogoutCommand", new FixedMetadataValue(Victions.getInstance(), true));
				player.kickPlayer(Color.translate("&cYou have been safely logged out from the server!"));
				cancelLogout(player);
			}
		}, 30 * 20L);
	}

	public long getLoogutMillisecondsLeft(Player player) {
		if(logoutCooldown.containsKey(player.getUniqueId())) {
			return Math.max(logoutCooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}

	private void setHome(String home, CustomLocation location) {
		homes.put(home, location);
	}

	public boolean isHomeActive(Player player) {
		return homeCooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < homeCooldown.get(player.getUniqueId());
	}

	public void cancelHome(Player player) {
		if(homeCooldown.containsKey(player.getUniqueId())) {
			homeCooldown.remove(player.getUniqueId());
		}
	}

	public void applyHomeCooldown(Player player, Location location) {
		int cooldown = 5;

		if(player.hasPermission(Permission.OP_PERMISSION)) {
			cooldown = 0;
		} else if(player.hasPermission(Permission.XENON_PERMISSION) || player.hasPermission(Permission.PARTNER_PERMISSION)) {
			cooldown = 3;
		}

		homeCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (cooldown * 1000));

		Bukkit.getScheduler().runTaskLater(Victions.getInstance(), () -> {
			if(isHomeActive(player)) {
				player.teleport(location);
				player.sendMessage(Color.translate("&eYou have been teleported to home."));
				cancelHome(player);
			}
		}, cooldown * 20L);
	}

	public long getHomeMillisecondsLeft(Player player) {
		if(homeCooldown.containsKey(player.getUniqueId())) {
			return Math.max(homeCooldown.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}

	public boolean isKitActive(Player player, Kit kit) {
		if(kit.getDelay() == 0
				|| player.hasPermission(Permission.OP_PERMISSION)
				|| !kitCooldown.containsKey(kit.getName())) {

			return false;
		}

		long value = kitCooldown.get(kit.getName());

		return value == -1L || System.currentTimeMillis() < value;
	}

	public void applyKitCooldown(Kit kit) {
		kitCooldown.put(kit.getName(), (kit.getDelay() == -1) ? -1L : (System.currentTimeMillis() + kit.getDelay() * 1000));
	}

	public long getKitMillisecondsLeft(Kit kit) {
		if(kitCooldown.containsKey(kit.getName())) {
			return Math.max(kitCooldown.get(kit.getName()) - System.currentTimeMillis(), 0L);
		}

		return 0L;
	}

	public int getHomeLimit() {
		switch (VituzAPI.getRankName(realName)) {
			case "Xenon":
			case "Partner": {
				return 10;
			}

			case "Krypton": {
				return 7;
			}

			case "Titanium":
			case "Media": {
				return 6;
			}

			case "Nitrogen": {
				return 5;
			}

			case "Hydrogen": {
				return 3;
			}
		}

		return 1;
	}

	public static FactionsData getByName(String name) {
		FactionsData data = factionDatas.get(name);

		return data == null ? new FactionsData(name) : factionDatas.get(name);
	}
}