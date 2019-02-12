
package secondlife.network.hcfactions.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.StringUtils;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class HCFData {

	@Getter public static Map<String, HCFData> dataMap = new HashMap<>();

	private String name;
	private String realName;

	private int kills;
	private int deaths;
	private int balance;
	private int killStreak;

	private long gapple;

    private boolean claimMap;
    private boolean lightning;
    private boolean reclaimed;
    private boolean combatLogger;

    private boolean event = false;
    private boolean spectating = false;

    private boolean loaded;

    public HCFData(String name) {
    	this.name = name;
    	this.realName = name;

    	this.kills = 0;
    	this.deaths = 0;
    	this.balance = 0;
    	this.killStreak = 0;

    	this.gapple = 0;

    	this.claimMap = false;
    	this.lightning = true;
    	this.reclaimed = false;
    	this.combatLogger = false;

    	this.loaded = false;

    	dataMap.put(this.name, this);
	}

	public void save() {
    	if(!this.loaded) return;

		Document document = new Document();

		document.put("name", this.name.toLowerCase());
		document.put("realName", this.name);

		document.put("kills", this.kills);
		document.put("deaths", this.deaths);
		document.put("balance", this.balance);
		document.put("kill_streak", this.killStreak);
		document.put("gapple", this.gapple);
		document.put("claim_map", this.claimMap);
		document.put("lightning", this.lightning);
		document.put("reclaim", this.reclaimed);
		document.put("combat_logger", this.combatLogger);

		Vituz.getInstance().getDatabaseManager().getHcfProfiles().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));
	}

	public void load() {
		Document document = (Document) Vituz.getInstance().getDatabaseManager().getHcfProfiles().find(Filters.eq("name", this.name.toLowerCase())).first();

		if(document != null) {

			if(document.containsKey("recentName")) {
				this.name = document.getString("recentName");
			}

			if(document.containsKey("kills")) {
				this.kills = document.getInteger("kills");
			}

			if(document.containsKey("deaths")) {
				this.deaths = document.getInteger("deaths");
			}
			if(document.containsKey("balance")) {
				this.balance = document.getInteger("balance");
			}

			if(document.containsKey("kill_streak")) {
				this.killStreak = document.getInteger("kill_streak");
			}

			if(document.containsKey("gapple")) {
				this.gapple = document.getLong("gapple");
			}

			boolean claim_map = document.getBoolean("claim_map");
			boolean lightning = document.getBoolean("lightning");
			boolean reclaim = document.getBoolean("reclaim");
			boolean combat_logger = document.getBoolean("combat_logger");


			this.claimMap = claim_map;
			this.lightning = lightning;
			this.reclaimed = reclaim;
			this.combatLogger = combat_logger;
		}

		this.loaded = true;
	}


	public static void giveLobbyItems(Player player) {
		player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).name("&cLeave Event").build());
		player.updateInventory();
	}

	public static void sendToSpawnAndReset(Player player) {
		HCFUtils.clearPlayer(player);
		String strin = UtilitiesFile.configuration.getString("World-Spawn.world-spawn");
		Location spawn = StringUtils.destringifyLocation(strin);
		HCFData.getByName(player.getName()).setEvent(false);
		HCFData.getByName(player.getName()).setSpectating(false);
		player.teleport(spawn);
	}

	public static HCFData getByName(String name) {
		HCFData data = dataMap.get(name);

		return data == null ? new HCFData(name) : data;
	}
}
