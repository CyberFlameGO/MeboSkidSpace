package secondlife.network.practice.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.practice.Practice;
import secondlife.network.practice.handlers.EnderpearlHandler;
import secondlife.network.practice.kit.PlayerKit;
import secondlife.network.practice.utilties.Config;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.handlers.DatabaseHandler;
import secondlife.network.vituz.utilties.ItemBuilder;
import secondlife.network.vituz.utilties.Permission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PracticeData {

	@Getter public static Map<String, PracticeData> playerDatas = new HashMap<>();

	public static final int DEFAULT_ELO = 1000;

	private final Map<String, Map<Integer, PlayerKit>> playerKits = new HashMap<>();
	private final Map<String, Integer> rankedLosses = new HashMap<>();
	private final Map<String, Integer> rankedWins = new HashMap<>();
	private final Map<String, Integer> rankedElo = new HashMap<>();
	private final Map<String, Integer> partyElo = new HashMap<>();

	private String name;
	private String realName;
	//private UUID uniqueId;

	private PlayerState playerState = PlayerState.LOADING;

	private UUID currentMatchID;
	private UUID duelSelecting;
	private boolean acceptingDuels = true;
	private boolean allowingSpectators = true;
	private boolean scoreboardEnabled = true;
	private int cheatFreeMatches;
	private int eloRange = 250;
	private int pingRange = 50;
	private int teamID = -1;
	private int rematchID = -1;
	private int missedPots;
	private int longestCombo;
	private int combo;
	private int id = 0;
	private int hits;

	private Block bed;
	private int bedwarsRespawn;

	private int oitcEventKills;
	private int oitcEventDeaths;

	private int oitcEventWins;
	private int oitcEventLosses;

	private int sumoEventWins;
	private int sumoEventLosses;

	private int parkourEventWins;
	private int parkourEventLosses;

	private int redroverEventWins;
	private int redroverEventLosses;

	private int rankedPlayed;

	private int premiumMatchesPlayed;
	private int premiumMatchesExtra;
	private int premiumLosses;
	private int premiumWins;
	private int premiumElo = PracticeData.DEFAULT_ELO;

	private boolean matches = false;

	private boolean loaded;

	public PracticeData(String name) {
		this.name = name;

		this.setPlayerState(PlayerState.SPAWN);

		/*Player player = Bukkit.getPlayer(name);

		if(player != null) {
			this.uniqueId = player.getUniqueId();
		}*/

		playerDatas.put(this.name, this);
	}

	public void save() {
		if(!loaded) return;

		Document document = new Document();

		JsonArray eloDocument = new JsonArray();

		for(Map.Entry<String, Integer> entry : this.rankedElo.entrySet()) {
			JsonObject kitDocument = new JsonObject();

			kitDocument.addProperty("kit_name", entry.getKey());
			kitDocument.addProperty("kit_elo", entry.getValue());

			eloDocument.add(kitDocument);
		}

		for(Map.Entry<String, Integer> entry : this.rankedWins.entrySet()) {
			JsonObject practiceDocument = new JsonObject();

			practiceDocument.addProperty("kit_wins", entry.getKey());
			practiceDocument.addProperty("kit_wins_int", entry.getValue());

			eloDocument.add(practiceDocument);
		}

		for(Map.Entry<String, Integer> entry : this.rankedLosses.entrySet()) {
			JsonObject practiceDocument = new JsonObject();

			practiceDocument.addProperty("kit_losses", entry.getKey());
			practiceDocument.addProperty("kit_losses_int", entry.getValue());

			eloDocument.add(practiceDocument);
		}

		for(Map.Entry<String, Integer> entry : this.partyElo.entrySet()) {
			JsonObject practiceDocument = new JsonObject();

			practiceDocument.addProperty("kit_party_name", entry.getKey());
			practiceDocument.addProperty("party_elo", entry.getValue());

			eloDocument.add(practiceDocument);
		}

		document.put("name", this.name.toLowerCase());
		document.put("realName", name);

		document.put("accepting_duels", this.acceptingDuels);
		document.put("allowing_spectators", this.allowingSpectators);
		document.put("ping_range", this.pingRange);
		document.put("elo_range", this.eloRange);
		document.put("premium_matches_played", this.premiumMatchesPlayed);
		document.put("premium_matches_extra", this.premiumMatchesExtra);
		document.put("premium_losses", this.premiumLosses);
		document.put("premium_wins", this.premiumWins);
		document.put("premium_elo", this.premiumElo);
		document.put("id", this.id);
		document.put("ranked_played", this.rankedPlayed);

		document.put("oitcEventWins", this.oitcEventWins);
		document.put("oitcEventLosses", this.oitcEventLosses);
		document.put("sumoEventWins", this.sumoEventWins);
		document.put("sumoEventLosses", this.sumoEventLosses);
		document.put("parkourEventWins", this.parkourEventWins);
		document.put("parkourEventLosses", this.parkourEventLosses);
		document.put("givenPM", this.matches);

		document.put("player_elo", eloDocument.toString());

		Config config = new Config("/players/" + this.name.toLowerCase());

		Practice.getInstance().getKitManager().getKits().forEach(kit -> {
			Map<Integer, PlayerKit> playerKits = getPlayerKits(kit.getName());

			if(playerKits != null) {
				playerKits.forEach((key, value) -> {
					config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", value.getDisplayName());
					config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
				});
			}
		});

		config.save();

		DatabaseHandler.practiceProfiles.replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));

		playerDatas.remove(this.name);
		loaded = false;
	}

	public void load() {
		Document document = (Document) DatabaseHandler.practiceProfiles.find(Filters.eq("name", this.name.toLowerCase())).first();

		if(document != null) {
			for(JsonElement element : new JsonParser().parse(document.getString("player_elo")).getAsJsonArray()) {
				JsonObject practiceDocument = element.getAsJsonObject();

				if(practiceDocument.has("kit_name")) {
					setElo(practiceDocument.get("kit_name").getAsString(), practiceDocument.get("kit_elo").getAsInt());
				}

				if(practiceDocument.has("kit_wins")) {
					setWins(practiceDocument.get("kit_wins").getAsString(), practiceDocument.get("kit_wins_int").getAsInt());
				}

				if(practiceDocument.has("kit_losses")) {
					setLosses(practiceDocument.get("kit_losses").getAsString(), practiceDocument.get("kit_losses_int").getAsInt());
				}

				if(practiceDocument.has("kit_party_name")) {
					setPartyElo(practiceDocument.get("kit_party_name").getAsString(), practiceDocument.get("party_elo").getAsInt());
				}
			}

			/*Player player = Bukkit.getPlayer(name);

			if(player != null) {
				this.uniqueId = player.getUniqueId();
			}*/

			this.realName = document.getString("realName");

			if(document.containsKey("ping_range")) {
				this.pingRange = document.getInteger("ping_range");
			}

			if(document.containsKey("elo_range")) {
				this.eloRange = document.getInteger("elo_range");
			}

			if(document.containsKey("premium_elo")) {
				this.premiumElo = document.getInteger("premium_elo");
			}

			if(document.containsKey("premium_wins")) {
				this.premiumWins = document.getInteger("premium_wins");
			}

			if(document.containsKey("premium_losses")) {
				this.premiumLosses = document.getInteger("premium_losses");
			}

			if(document.containsKey("premium_matches_extra")) {
				this.premiumMatchesExtra = document.getInteger("premium_matches_extra");
			}

			if(document.containsKey("premium_matches_played")) {
				this.premiumMatchesPlayed = document.getInteger("premium_matches_played");
			}

			if(document.containsKey("id")) {
				this.id = document.getInteger("id");
			}

			if(document.containsKey("ranked_played")) {
				this.rankedPlayed = document.getInteger("ranked_played");
			}

			if(document.containsKey("oitcEventWins")) {
				this.oitcEventWins = document.getInteger("oitcEventWins");
			}

			if(document.containsKey("oitcEventLosses")) {
				this.oitcEventLosses = document.getInteger("oitcEventLosses");
			}

			if(document.containsKey("sumoEventWins")) {
				this.sumoEventWins = document.getInteger("sumoEventWins");
			}

			if(document.containsKey("sumoEventLosses")) {
				this.sumoEventLosses = document.getInteger("sumoEventLosses");
			}

			if(document.containsKey("parkourEventWins")) {
				this.parkourEventWins = document.getInteger("parkourEventWins");
			}

			if(document.containsKey("parkourEventLosses")) {
				this.parkourEventLosses = document.getInteger("parkourEventLosses");
			}

			boolean allowingSpectators = document.getBoolean("allowing_spectators");
			boolean acceptingDuels = document.getBoolean("accepting_duels");
			boolean nigger = document.getBoolean("givenPM");

			this.allowingSpectators = allowingSpectators;
			this.acceptingDuels = acceptingDuels;
			this.matches = nigger;

			Config config = new Config("/players/" + this.name.toLowerCase());
			ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");

			if(playerKitsSection != null) {
				Practice.getInstance().getKitManager().getKits().forEach(kit -> {
					ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit.getName());

					if(kitSection != null) {
						kitSection.getKeys(false).forEach(kitKey -> {
							Integer kitIndex = Integer.parseInt(kitKey);
							String displayName = kitSection.getString(kitKey + ".displayName");

							ItemStack[] contents = ((List<ItemStack>) kitSection.get(kitKey + ".contents")).toArray(new ItemStack[0]);

							PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, contents, displayName);

							addPlayerKit(kitIndex, playerKit);
						});
					}
				});
			}
		}

		loaded = true;
	}

	public int getPremiumMatches() {
		return Math.max(getPremiumMatches(this.name) + this.premiumMatchesExtra - this.premiumMatchesPlayed, 0);
	}

	public int getWins(String kitName) {
		return this.rankedWins.computeIfAbsent(kitName, k -> 0);
	}

	public void setWins(String kitName, int wins) {
		this.rankedWins.put(kitName, wins);
	}

	public int getLosses(String kitName) {
		return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
	}

	public void setLosses(String kitName, int losses) {
		this.rankedLosses.put(kitName, losses);
	}

	public int getElo(String kitName) {
		return this.rankedElo.computeIfAbsent(kitName, k -> PracticeData.DEFAULT_ELO);
	}

	public void setElo(String kitName, int elo) {
		this.rankedElo.put(kitName, elo);
	}

	public int getPartyElo(String kitName) {
		return this.partyElo.computeIfAbsent(kitName, k -> PracticeData.DEFAULT_ELO);
	}

	public void setPartyElo(String kitName, int elo) {
		this.partyElo.put(kitName, elo);
	}

	public void addPlayerKit(int index, PlayerKit playerKit) {
		this.getPlayerKits(playerKit.getName()).put(index, playerKit);
	}

	public Map<Integer, PlayerKit> getPlayerKits(String kitName) {
		return this.playerKits.computeIfAbsent(kitName, k -> new HashMap<>());
	}

	public static PracticeData getByKurac(UUID uuid) {
		String name = Bukkit.getPlayer(uuid).getName();

		PracticeData data = playerDatas.get(name);

		return data == null ? new PracticeData(name) : data;
	}

	public static PracticeData getByName(String name) {
		PracticeData data = playerDatas.get(name);

		return data == null ? new PracticeData(name) : data;
	}

	public static void giveLobbyItems(Player player) {
		boolean inParty = Practice.getInstance().getPartyManager().getParty(player.getUniqueId()) != null;
		boolean inTournament = Practice.getInstance().getTournamentManager().getTournament(player.getUniqueId()) != null;
		boolean inEvent = Practice.getInstance().getEventManager().getEventPlaying(player) != null;
		boolean isRematching = Practice.getInstance().getMatchManager().isRematching(player.getUniqueId());
		ItemStack[] items = Practice.getInstance().getItemManager().getSpawnItems();

		if (inTournament) {
			items = Practice.getInstance().getItemManager().getTournamentItems();
		}
		else if (inEvent) {
			items = Practice.getInstance().getItemManager().getEventItems();
		}
		else if (inParty) {
			items = Practice.getInstance().getItemManager().getPartyItems();
		}

		player.getInventory().setContents(items);

		if (isRematching && !inParty && !inTournament && !inEvent) {
			player.getInventory()
					.setItem(3,
							new ItemBuilder(Material.BLAZE_POWDER).name("&eRequest Rematch").build());
			player.getInventory()
					.setItem(5,
							new ItemBuilder(Material.PAPER).name("&eView Opponent's Inventory").build());
		}

		player.updateInventory();
	}

	public static void sendToSpawnAndReset(Player player) {
		reset(player);

		if(!getByName(player.getName()).getPlayerState().equals(PlayerState.SPAWN)) {
			getByName(player.getName()).setPlayerState(PlayerState.SPAWN);
			player.teleport(Practice.getInstance().getSpawnManager().getSpawnLocation().toBukkitLocation());
		}
	}

	public static void reset(Player player) {
		PlayerUtil.clearPlayer(player);
		EnderpearlHandler.stopCooldown(player);

		giveLobbyItems(player);

		if (!player.isOnline()) {
			return;
		}

		Bukkit.getOnlinePlayers().forEach(p -> {
			player.hidePlayer(p);
			p.hidePlayer(player);
		});
	}


	public static int getPremiumMatches(String name) {
		Player player = Bukkit.getPlayer(name);

		if(player.hasPermission(Permission.OP_PERMISSION)) {
			return 1337;
		} else if(player.hasPermission(Permission.MEDIA_PERMISSION) || player.hasPermission(Permission.PARTNER_PERMISSION) || player.hasPermission(Permission.XENON_PERMISSION)) {
			return 200;
		} else if(player.hasPermission(Permission.KRYPTON_PERMISSION)) {
			return 125;
		} else if(player.hasPermission(Permission.TITANIUM_PERMISSION)) {
			return 100;
		} else if(player.hasPermission(Permission.NITROGEN_PERMISSION)) {
			return 75;
		} else if(player.hasPermission(Permission.HYDROGEN_PERMISSION)) {
			return 50;
		}

		return 0;
	}
}
