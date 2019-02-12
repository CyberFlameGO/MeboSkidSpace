package secondlife.network.hcfactions;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import secondlife.network.hcfactions.classes.utils.ArmorClassHandler;
import secondlife.network.hcfactions.classes.utils.bard.EffectRestorerHandler;
import secondlife.network.hcfactions.commands.CommandHandler;
import secondlife.network.hcfactions.commands.arguments.event.*;
import secondlife.network.hcfactions.economy.EconomySignHandler;
import secondlife.network.hcfactions.elevators.SignElevatorHandler;
import secondlife.network.hcfactions.events.EventManager;
import secondlife.network.hcfactions.events.SpawnManager;
import secondlife.network.hcfactions.factions.claim.ClaimWandHandler;
import secondlife.network.hcfactions.factions.commands.SubCommandExecutor;
import secondlife.network.hcfactions.factions.handlers.ProtectionHandler;
import secondlife.network.hcfactions.factions.handlers.SignSubclaimHandler;
import secondlife.network.hcfactions.handlers.*;
import secondlife.network.hcfactions.staff.handlers.StaffModeHandler;
import secondlife.network.hcfactions.staff.handlers.VanishHandler;
import secondlife.network.hcfactions.stattrack.StatTrackerHandler;
import secondlife.network.hcfactions.timers.*;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.hcfactions.utilties.file.ConfigFile;
import secondlife.network.hcfactions.utilties.file.LimitersFile;
import secondlife.network.hcfactions.utilties.file.SchedulesFile;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.hcfactions.utilties.redis.UUIDUtils;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.providers.tab.VituzTab;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class HCF extends JavaPlugin {
	
	@Getter private static HCF instance;

	public static JedisPool redis;

	private secondlife.network.vituz.utilties.ConfigFile factions;
	private EventManager eventManager;
	private SpawnManager spawnManager;

    @Override
	public void onEnable() {
		instance = this;

		RegisterHandler.hook();

		factions = new secondlife.network.vituz.utilties.ConfigFile(this, "factions.yml");

		new ConfigFile(this);
		new LimitersFile(this);
		new UtilitiesFile(this);
		new SchedulesFile(this);

		connectToRedis();
		registerHandlers();
		registerCommands();

		UUIDUtils.hook();

		eventManager = new EventManager();
		spawnManager = new SpawnManager();

		VituzTab.setLayoutProvider(new TabLayoutHandler());
		VituzNametag.registerProvider(new NametagsHandler("", 16));
		VituzScoreboard.setConfiguration(ScoreboardLayoutHandler.create());

		new BukkitRunnable() {
			public void run() {
				long l = System.currentTimeMillis();

				Bukkit.broadcastMessage(Color.translate("&a&lSaving " + RegisterHandler.getInstancee().getFactionManager().getFactions().size() + " factions..."));

				RegisterHandler.getInstancee().getFactionManager().saveFactionData();

				Bukkit.broadcastMessage(Color.translate("&a&lSaving took " + (System.currentTimeMillis() - l) + " ms!"));
			}
		}.runTaskTimerAsynchronously(this, 12000L, 12000L);
	}

	@Override
	public void onDisable() {
		RegisterHandler.getInstancee().getFactionManager().saveFactionData();
		spawnManager.saveConfig();

		Handler.disable();
	}

	private void registerHandlers() {
		new HCFDataHandler(this);
		new RegisterHandler(this);
		new EffectRestorerHandler(this);
		new HCFConfiguration(this);
		new CommandHandler(this);
		new SubCommandExecutor(this);
		new GlassHandler(this);

		new ArmorClassHandler(this);
		new ClaimWandHandler(this);
		new ProtectionHandler(this);
		new SignSubclaimHandler(this);

		ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

		new ArcherHandler(this);
		new AppleHandler(this);
		new EnderpearlHandler(this);
		new GappleHandler(this);
		new LogoutHandler(this);
		new ClassWarmupHandler(this);
		new HomeHandler(this);
		new SpawnTagHandler(this);
		new GameHandler(this);
		new StuckHandler(this);

		new OnlineDonatorsHandler(this);
		new PearlFixHandler(this);
		new SignElevatorHandler(this);
		new BorderHandler(this);
		new ChatHandler(this);
		new CombatLoggerHandler(this);
		new DeathMessagesHandler(this);
		new DynamicPlayerHandler(this);
		if(HCFConfiguration.kitMap) new ThrowableCobwebHandler(this);
		new EventSignHandler(this);
		new FurnaceSpeedHandler(this);
		new StatTrackerHandler(this);
		if(HCFConfiguration.kitMap) new KillStreakHandler(this);
		if(HCFConfiguration.kitMap) new KitMapHandler(this);
		if(HCFConfiguration.kitMap) new KitSignHandler(this);
		new MapKitHandler(this);
		new ShopHandler(this);
		new EconomySignHandler(this);
		new StaffModeHandler(this);
		new VanishHandler(this);
	}

	public void connectToRedis() {
        try {
			if(Vituz.getInstance().getConfig().getBoolean("DATABASE.AUTHENTICATION.ENABLED")) {
				redis = new JedisPool(new JedisPoolConfig(), Vituz.getInstance().getDatabaseManager().getDedihost(), 6379, 20_000, Vituz.getInstance().getConfig().getString("DATABASE.AUTHENTICATION.PASSWORD"), 0, null);
			} else {
				redis = new JedisPool(new JedisPoolConfig(), Vituz.getInstance().getDatabaseManager().getDedihost(), 6379, 20_000);
			}
        } catch(Exception e) {
            redis = null;

            e.printStackTrace();

            Msg.logConsole("&4&l*** &c&lCouldn't connect to a Redis instance at " + Vituz.getInstance().getDatabaseManager().getDedihost() + "&c!");
        }
    }

	private void registerCommands() {
		Arrays.asList(
				new JoinEventCommand(),
				new LeaveEventCommand(),
				new HostCommand(),
				new EventManagerCommand(),
				new SpawnsCommand()
		).forEach(command -> registerCommand(command, command.getName()));
	}

	public void registerCommand(Command command, String name) {
		Map<String, Command> commands = new HashMap<>();
		commands.put(name, command);
		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "SecondLife", entry.getValue());
		}
	}
}