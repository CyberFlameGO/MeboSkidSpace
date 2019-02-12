package secondlife.network.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.config.Option;
import secondlife.network.uhc.scenario.Scenario;
import secondlife.network.uhc.state.GameState;
import secondlife.network.uhc.utilties.Manager;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.uhc.utilties.items.Items;
import secondlife.network.vituz.utilties.Color;

import java.util.Comparator;

public class InventoryManager extends Manager {

	public static Inventory
			uhcSettings, uhcPlayerSettings, options,
			scatter, uhcPractice, toggleScenarios,
			maxPlayers, rates, team,
			scenarioInfo;
	
	public InventoryManager(UHC plugin) {
		super(plugin);

		scenarioInfo = Bukkit.createInventory(null, 45, Color.translate("&eCurrent Scenarios"));
		uhcSettings = Bukkit.createInventory(null, 27, Color.translate("&eUHC Settings"));
		uhcPlayerSettings = Bukkit.createInventory(null, 27, Color.translate("&eUHC Settings"));
		options = Bukkit.createInventory(null, 27, Color.translate("&eOptions Inventory"));
		scatter = Bukkit.createInventory(null, 9, Color.translate("&eScatter Inventory"));
		uhcPractice = Bukkit.createInventory(null, 9, Color.translate("&ePractice"));
		toggleScenarios = Bukkit.createInventory(null, 45, Color.translate("&eToggle Scenarios"));
		maxPlayers = Bukkit.createInventory(null, 45, Color.translate("&eMax Players"));
		rates = Bukkit.createInventory(null, 36, Color.translate("&eRates Inventory"));
		team = Bukkit.createInventory(null, 27, Color.translate("&eParties"));
		
		runInventories();
	}

	public static void setupSettings() {
		uhcSettings.clear();

		uhcSettings.setItem(9, Items.getParties());
		uhcSettings.setItem(17, Items.getUHCPractice());
		uhcSettings.setItem(4, ScenarioManager.getScenariosItem());
		uhcSettings.setItem(6, Items.getMaxPlayers());
		uhcSettings.setItem(2, Items.getToggleScenarios());
		uhcSettings.setItem(20, Items.getRates());
		uhcSettings.setItem(22, Items.getOptions());
		uhcSettings.setItem(24, Items.getStartInventory());
	}

	public static void setupPlayerSettings() {
		uhcPlayerSettings.clear();

		uhcPlayerSettings.setItem(9, Items.getIsPartiesEnabled());
		uhcPlayerSettings.setItem(17, Items.getIsUHCPracticeEnabled());
		uhcPlayerSettings.setItem(4, ScenarioManager.getScenariosItem());
		uhcPlayerSettings.setItem(6, Items.getCurrentMaxPlayers());
		uhcPlayerSettings.setItem(2, Items.getAppleRate());
		uhcPlayerSettings.setItem(20, Items.getShears());
		uhcPlayerSettings.setItem(22, Items.getCurrentScenarios());
		uhcPlayerSettings.setItem(24, Items.getOptions());
	}

	public static void setupScatter() {
		scatter.clear();

		scatter.setItem(2, UHCUtils.getYes());
		scatter.setItem(6, UHCUtils.getNo());
	}

	public static void setupMaxPlayers() {
		maxPlayers.clear();

		maxPlayers.setItem(0, Items.getBack());
		maxPlayers.setItem(19, Items.getMaxOnlineMinus50());
		maxPlayers.setItem(20, Items.getMaxOnlineMinus10());
		maxPlayers.setItem(21, Items.getMaxOnlineMinus5());
		maxPlayers.setItem(22, Items.getCurrentMaxPlayers());
		maxPlayers.setItem(23, Items.getMaxOnlinePlus5());
		maxPlayers.setItem(24, Items.getMaxOnlinePlus10());
		maxPlayers.setItem(25, Items.getMaxOnlinePlus50());
	}

	public static void setupRates() {
		rates.clear();

		rates.setItem(0, Items.getBack());
		rates.setItem(10, Items.getShearsRateMinus2());
		rates.setItem(11, Items.getShearsRateMinus1());
		rates.setItem(13, Items.getShears());
		rates.setItem(15, Items.getShearsRatePlus1());
		rates.setItem(16, Items.getShearsRatePlus2());
		rates.setItem(19, Items.getAppleRateMinus2());
		rates.setItem(20, Items.getAppleRateMinus1());
		rates.setItem(22, Items.getAppleRate());
		rates.setItem(24, Items.getAppleRatePlus1());
		rates.setItem(25, Items.getAppleRatePlus2());
	}

	public static void setupTeam() {
		team.clear();

		team.setItem(0, Items.getBack());
		team.setItem(10, Items.getPartiesEnable());
		team.setItem(11, Items.getTeamAdd1());
		team.setItem(13, Items.getIsPartiesEnabled());
		team.setItem(15, Items.getTeamRemove1());
		team.setItem(16, Items.getPartiesDisable());
	}

	public static void setupUHCPractice() {
		uhcPractice.setItem(2, Items.getEnablePractice());
		uhcPractice.setItem(6, Items.getDisablePractice());
	}

	public static void runInventories() {
		setupUHCPractice();
		setupScatter();

		new BukkitRunnable() {
			public void run() {
				setToggleScenario();
				setupPlayerSettings();

				if(!GameManager.getGameState().equals(GameState.PLAYING)) {
					setupSettings();
					setupMaxPlayers();
					setupRates();
					setupTeam();
					setOptions();
				}
			}
		}.runTaskTimerAsynchronously(UHC.getInstance(), 20L, 20L);
	}
	
	public static void setOptions() {
		options.clear();

		OptionManager.getOptions().stream()
				.sorted(Comparator.comparing(Option::isBoolean))
				.forEach(option -> options.addItem(OptionManager.getConfigItem(option)));
	}
	
	public static void setToggleScenario() {
		toggleScenarios.clear();

		ScenarioManager.getScenarios().stream()
				.sorted(Comparator.comparing(Scenario::getName))
				.forEach(scenario -> toggleScenarios.addItem(ScenarioManager.getScenarioItem(scenario)));
        
		toggleScenarios.setItem(44, Items.getDisableAllScenarios());
	}
	
	public static Inventory setScenarioInfo(Player player) {
		scenarioInfo.clear();

		ScenarioManager.getScenarios().stream()
				.sorted(Comparator.comparing(Scenario::getName))
				.forEach(scenario -> {
					if(scenario.isEnabled()) {
						scenarioInfo.addItem(ScenarioManager.getScenarioDescription(scenario));
					}
				});
		
		player.openInventory(scenarioInfo);
		return scenarioInfo;
	}
}
