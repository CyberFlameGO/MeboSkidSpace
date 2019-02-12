package secondlife.network.vituz;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.vituz.commands.CommandHandler;
import secondlife.network.vituz.data.CrateData;
import secondlife.network.vituz.listeners.CustomMovementListener;
import secondlife.network.vituz.managers.*;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.providers.tab.VituzTab;
import secondlife.network.vituz.tasks.ChallengeTask;
import secondlife.network.vituz.tasks.FreezeTask;
import secondlife.network.vituz.utilties.*;
import secondlife.network.vituz.utilties.command.VituzCommandHandler;
import secondlife.network.vituz.utilties.cuboid.Cuboid;
import secondlife.network.vituz.utilties.cuboid.NamedCuboid;
import secondlife.network.vituz.utilties.item.ItemDB;
import secondlife.network.vituz.utilties.item.SimpleItemDB;
import secondlife.network.vituz.visualise.CustomPacketHandler;

@Getter
public class Vituz extends JavaPlugin {
	
	@Getter private static Vituz instance;

	private ConfigFile config;
	private ConfigFile ranks;
	private ConfigFile utilities;

	private ItemDB itemDB;

	private AutoRestartManager autoRestartManager;
	private ChallengesManager challengesManager;
	private ChatControlManager chatControlManager;
	private ColorsManager colorsManager;
	private CrateManager crateManager;
	private DatabaseManager databaseManager;
	private EssentialsManager essentialsManager;
	private FreezeManager freezeManager;
	private HorseManager horseManager;
	private PrefixesManager prefixesManager;
	private RankManager rankManager;
	private ServerColorsManager serverColorsManager;

	@Override
	public void onEnable() {
		instance = this;

		this.config = new ConfigFile(this, "config.yml");
		this.ranks = new ConfigFile(this, "ranks.yml");
		this.utilities = new ConfigFile(this, "utilities.yml");

		ConfigurationSerialization.registerClass(Cuboid.class);
		ConfigurationSerialization.registerClass(NamedCuboid.class);

		ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementListener());

		new CommandHandler(this);

		VituzCommandHandler.hook();

		this.itemDB = new SimpleItemDB(this);

		this.autoRestartManager = new AutoRestartManager(this);
		this.challengesManager = new ChallengesManager(this);
		this.chatControlManager = new ChatControlManager(this);
		this.colorsManager = new ColorsManager(this);
		this.crateManager = new CrateManager(this);
		this.databaseManager = new DatabaseManager(this);
		this.essentialsManager = new EssentialsManager(this);
		this.freezeManager = new FreezeManager(this);
		this.horseManager = new HorseManager(this);
		this.prefixesManager = new PrefixesManager(this);
		this.rankManager = new RankManager(this);
		this.serverColorsManager = new ServerColorsManager(this);

		this.getDatabaseManager().setupRedis();
		this.getDatabaseManager().loadStatus();

		ServerUtils.getClassesInPackage(this, "secondlife.network.vituz.listeners").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
			try {
				Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		ServerUtils.hook();
		VituzScoreboard.hook();
		VituzTab.hook();

		new ChallengeTask();
		new FreezeTask();

		VituzNametag.hook();

		if((VituzAPI.getServerName().contains("UHCMeetup-") && !VituzAPI.getServerName().equals("UHCMeetup-Lobby"))
				|| VituzAPI.getServerName().equals("UHC")
				|| VituzAPI.getServerName().equals("KitMap")) {
			ClubSpigot.INSTANCE.addPacketHandler(new CustomPacketHandler());
		}

		if(this.getDatabaseManager().isCrates()) {
			CrateData.load();
		}

		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "timings off");
		if(VituzAPI.getServerName().contains("UHCMeetup-") && !VituzAPI.getServerName().equals("UHCMeetup-Lobby")) {
			VituzAPI.setMaxPlayers(60);
		} else {
			VituzAPI.setMaxPlayers(1000);
		}
		PasteUtils.setDeveloperKey("8f3898360d2fe368a723d12839fa8374");

		if(!this.getDatabaseManager().isDevMode()) {
			Tasks.runLater(() ->
				ServerUtils.bungeeBroadcast("&7[&2&lServer Manager&7] &f" + VituzAPI.getServerName() + " &ais now &2&lOnline&a.", Permission.STAFF_PERMISSION)
			, 60L);
		}
	}

	@Override
	public void onDisable() {
		CrateData.getCrates().forEach(CrateData::save);

		this.getEssentialsManager().clear();

		this.getRankManager().save();

		this.getDatabaseManager().getClient().close();
		this.getDatabaseManager().getPunishSubscriber().getJedisPubSub().unsubscribe();
		this.getDatabaseManager().getRankSubscriber().getJedisPubSub().unsubscribe();
		this.getDatabaseManager().getPool().destroy();
	}
}