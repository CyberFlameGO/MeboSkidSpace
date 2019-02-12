package secondlife.network.uhc;

import club.minemen.spigot.ClubSpigot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.uhc.border.worldborder.Config;
import secondlife.network.uhc.border.worldborder.DynMapFeatures;
import secondlife.network.uhc.border.worldborder.commands.WBCommand;
import secondlife.network.uhc.commands.CommandHandler;
import secondlife.network.uhc.listeners.CustomMovementHandler;
import secondlife.network.uhc.managers.*;
import secondlife.network.uhc.providers.NametagsProvider;
import secondlife.network.uhc.providers.ScoreboardProvider;
import secondlife.network.uhc.providers.TabProvider;
import secondlife.network.uhc.scenario.ScenarioListeners;
import secondlife.network.uhc.utilties.BaseListener;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.uhc.utilties.WorldCreator;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.providers.tab.VituzTab;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.ConfigFile;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class UHC extends JavaPlugin {

	@Getter
	private static UHC instance;

	private WBCommand wbCommand;

	private BorderManager borderManager;
	private CombatLoggerManager combatLoggerManager;
	private GameManager gameManager;
	private GlassManager glassManager;
	private InventoryManager inventoryManager;
	private MobStackManager mobStackManager;
	private OptionManager optionManager;
	private PartyManager partyManager;
	private PlayerManager playerManager;
	private PracticeManager practiceManager;
	private RequestManager requestManager;
	private ScenarioManager scenarioManager;
	private SpectatorManager spectatorManager;
	private VanishManager vanishManager;

	private ConfigFile mainConfig, utiltiesConfig;

	private String[] ranks = {
	  "Owner", "PlatformAdmin", "SeniorAdmin",
      "Admin", "SeniorMod", "Mod+",
      "Mod", "TrialMod"
    };

	@Override
	public void onEnable() {
		instance = this;

		registerConfigs();

		wbCommand = new WBCommand();

		Config.load(this, false);

		DynMapFeatures.setup();

		setupWorld();
		registerItems();
		registerManagers();
		registerCommands();
		registerListeners();

		ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

		VituzTab.setLayoutProvider(new TabProvider());
		VituzNametag.registerProvider(new NametagsProvider());
		VituzScoreboard.setConfiguration(ScoreboardProvider.create());

        FindIterable<Document> fetched = (FindIterable<Document>) Vituz.getInstance().getDatabaseManager().getRanksProfiles().find();
        Iterator<Document> iterator = fetched.iterator();

        if(iterator.hasNext()) {
            Document current = iterator.next();

            if(current.containsKey("grants")) {
                JsonArray rankArray = new JsonParser().parse(current.getString("grants")).getAsJsonArray();

                rankArray.forEach(rankElement -> {
                    JsonObject rankObject = rankElement.getAsJsonObject();

                    if (!rankObject.has("rankName")) {
                        return;
                    }

                    String rankName = rankObject.get("rankName").getAsString();

                    Stream.of(ranks).forEach(rank -> {
                        if(rankName.contains(rank)) {
                            String name = current.getString("realName") != null ? current.getString("realName") : current.getString("name");

                            Bukkit.getOfflinePlayer(name).setWhitelisted(true);
                            Msg.logConsole("&5&l" + name + " &dhas been added to whitelist!");                        }
                    });
                });
            }
        }
	}
	
	@Override
	public void onDisable() {
		DynMapFeatures.removeAllBorders();
		Config.StopBorderTimer();
		Config.StoreFillTask();
		Config.StopFillTask();

		combatLoggerManager.handleOnDisable();
		gameManager.handleOnDisable();
		practiceManager.handleOnDisable();
		spectatorManager.handleOnDisable();
		vanishManager.handleOnDisable();
		mobStackManager.handleOnDisable();
	}

	private void registerConfigs() {
		mainConfig = new ConfigFile(this, "config.yml");
		utiltiesConfig = new ConfigFile(this, "utilities.yml");
	}

	private void setupWorld() {
		Bukkit.getScheduler().runTaskLater(this, () -> {
			new WorldCreator(true, gameManager.isWorld() ? true : false);

			World uhc = Bukkit.getWorld("uhc_world");
			uhc.setGameRuleValue("doDaylightCycle", "false");
			uhc.setTime(0);
            uhc.setGameRuleValue("doFireTick", "false");
            uhc.setGameRuleValue("naturalRegeneration", "false");

			World uhcNether = Bukkit.getWorld("world_nether");
			uhcNether.setGameRuleValue("doDaylightCycle", "false");
			uhcNether.setTime(0);
			uhcNether.setGameRuleValue("naturalRegeneration", "false");
		}, 60L);
	}

	private void registerCommands() {
		getCommand("wborder").setExecutor(wbCommand);
	}

	private void registerManagers() {
		new CommandHandler(this);

		borderManager = new BorderManager(this);
		combatLoggerManager = new CombatLoggerManager(this);
		gameManager = new GameManager(this);
		glassManager = new GlassManager(this);
		inventoryManager = new InventoryManager(this);
		mobStackManager = new MobStackManager(this);
		optionManager = new OptionManager(this);
		partyManager = new PartyManager(this);
		playerManager = new PlayerManager(this);
		practiceManager = new PracticeManager(this);
		requestManager = new RequestManager(this);
		scenarioManager = new ScenarioManager(this);
		spectatorManager = new SpectatorManager(this);
		vanishManager = new VanishManager(this);
	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new ScenarioListeners(), this);

		UHCUtils.getClassesInPackage(this, "secondlife.network.uhc.listeners").stream().filter(BaseListener.class::isAssignableFrom).forEach(clazz -> {
			try {
				Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	private void registerItems() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Color.translate("&5&lGolden Head"));

		List<String> lore = new ArrayList<>();
		lore.add(Color.translate("&5Some say consuming the head of a"));
		lore.add(Color.translate("&5fallen foe strengthens the blood"));

		meta.setLore(lore);
		stack.setItemMeta(meta);

		ShapedRecipe shapedRecipe = new ShapedRecipe(stack);

		shapedRecipe.shape("EEE", "ERE", "EEE");
		shapedRecipe.setIngredient('E', Material.GOLD_INGOT).setIngredient('R', Material.SKULL_ITEM, 3);

		Bukkit.addRecipe(shapedRecipe);
		ShapelessRecipe glmelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1));

		glmelon.addIngredient(Material.GOLD_BLOCK);
		glmelon.addIngredient(Material.MELON);

		Bukkit.addRecipe(glmelon);
	}
}