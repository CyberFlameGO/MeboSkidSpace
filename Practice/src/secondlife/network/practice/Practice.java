package secondlife.network.practice;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.practice.commands.*;
import secondlife.network.practice.commands.duel.AcceptCommand;
import secondlife.network.practice.commands.duel.DuelCommand;
import secondlife.network.practice.commands.duel.SpectateCommand;
import secondlife.network.practice.commands.event.*;
import secondlife.network.practice.commands.management.ArenaCommand;
import secondlife.network.practice.commands.management.KitCommand;
import secondlife.network.practice.commands.management.RankedCommand;
import secondlife.network.practice.commands.management.SpawnsCommand;
import secondlife.network.practice.handlers.*;
import secondlife.network.practice.leaderboard.Leaderboards;
import secondlife.network.practice.managers.*;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.providers.ScoreboardProvider;
import secondlife.network.practice.providers.TabProvider;
import secondlife.network.practice.runnable.ExpBarRunnable;
import secondlife.network.practice.runnable.NametagRunnable;
import secondlife.network.practice.utilties.file.ArenaFile;
import secondlife.network.practice.utilties.file.ConfigFile;
import secondlife.network.practice.utilties.file.LaddersFile;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboardHandler;
import secondlife.network.vituz.providers.tab.VituzTabHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
public class Practice extends JavaPlugin {

	// TODO NametagProvider

	@Getter private static Practice instance;

	public static final int DEFAULT_HIT_DELAY = 20;

	private InventoryManager inventoryManager;
	private EditorManager editorManager;
	private ArenaManager arenaManager;
	private MatchManager matchManager;
	private PartyManager partyManager;
	private QueueManager queueManager;
	private EventManager eventManager;
	private ItemManager itemManager;
	private KitManager kitManager;
	private SpawnManager spawnManager;
	private TournamentManager tournamentManager;
	private ChunkManager chunkManager;
	private Leaderboards leaderboards;

	@Override
	public void onEnable() {
		instance = this;

		new ConfigFile(this);
		new ArenaFile(this);
		new LaddersFile(this);

		ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

		this.registerCommands();
		this.registerListeners();
		this.registerManagers();

		this.removeCrafting(Material.SNOW_BLOCK);

		this.leaderboards = new Leaderboards();

		//this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(), 20L * 60L * 5L, 20L * 60L * 5L);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.leaderboards, 0L, 20L * 60 * 5);
		new NametagRunnable();

		VituzTabHandler.setLayoutProvider(new TabProvider());
		VituzScoreboardHandler.setConfiguration(ScoreboardProvider.create());
	}

	@Override
	public void onDisable() {
		for(PracticeData playerData : PracticeData.getPlayerDatas().values()) {
			playerData.save();
		}

		this.arenaManager.saveArenas();
		this.kitManager.saveKits();
		this.spawnManager.saveConfig();
	}

	private void registerListeners() {
		new EnderpearlHandler(this);
		new PearlFixHandler(this);
		new ChatHandler(this);

		Arrays.asList(
				new EntityHandler(),
				new PlayerHandler(),
				new MatchHandler(),
				new WorldHandler(),
				new InventoryHandler()
		).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
	}

	private void registerManagers() {
		this.spawnManager = new SpawnManager();
		this.arenaManager = new ArenaManager();
		this.chunkManager = new ChunkManager();
		this.editorManager = new EditorManager();
		this.itemManager = new ItemManager();
		this.kitManager = new KitManager();
		this.matchManager = new MatchManager();
		this.partyManager = new PartyManager();
		this.queueManager = new QueueManager();
		this.inventoryManager = new InventoryManager();
		this.eventManager = new EventManager();
		this.tournamentManager = new TournamentManager();
	}

	private void registerCommands() {
		Arrays.asList(
				new JoinEventCommand(),
				new LeaveEventCommand(),
				new StatusEventCommand(),
				new HostCommand(),
				new EventManagerCommand(),
				new SpectateEventCommand(),

				new ToggleDuelCommand(),
				new PremiumMatchesCommand(),
				new ToggleSpectatorsCommand(),
				new ResetStatsCommand(),
				new AcceptCommand(),
				new RankedCommand(),
				new ArenaCommand(),
				new PartyCommand(),
				new DuelCommand(),
				new SpectateCommand(),
				new SaveCommand(),
				new KitCommand(),
				new InventoryCommand(),
				new SpawnCommand(),
				new StatsCommand(),
				new SpawnsCommand(),
				new TournamentCommand()
		).forEach(command -> registerCommand(command, command.getName()));
	}

	public void registerCommand(Command command, String name) {
		Map<String, Command> commands = new HashMap<>();
		commands.put(name, command);
		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Practice", entry.getValue());
		}
	}

	private void removeCrafting(Material material) {
		Iterator<Recipe> iterator = getServer().recipeIterator();

		while(iterator.hasNext()) {
			Recipe recipe = iterator.next();
			if(recipe != null && recipe.getResult().getType() == material) {
				iterator.remove();
			}
		}
	}
}
