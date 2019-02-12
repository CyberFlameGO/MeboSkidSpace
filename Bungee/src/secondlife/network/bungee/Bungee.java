package secondlife.network.bungee;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import secondlife.network.bungee.antibot.BotBoth;
import secondlife.network.bungee.commands.*;
import secondlife.network.bungee.handlers.*;
import secondlife.network.bungee.utils.Handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class Bungee extends Plugin {

	@Getter public static Bungee instance;
	
	public static String incomingBungeeBroadcastChannel = "BungeeBroadcast";
	public static String incomingAnnounceChannel = "Announce";
	public static String incomingFilterChannel = "Filter";
	public static String incomingCommandChannel = "Command";
	public static String incomingBanChannel = "AutoBan";
	public static String incomingAlertsChannel = "Alerts";
	public static String incomingAuthChannel = "Auth";
	public static String incomingPermissionsChannel = "Permissions";
	public static String outgoingPremiumChannel = "Premium";

	public static Configuration configuration;

	@Override
	public void onEnable() {
		instance = this;

		loadConfig();

		BotBoth.load(this.getDataFolder());

		setupBoth();
		clearBots();

		new AnnounceHandler(this);
		new AutoMessageHandler(this);
		new MaintenanceHandler(this);
		new AntiBotHandler(this);
		new MotdHandler(this);
		new PlayerHandler(this);
		new PluginMessageHandler(this);
		new ReportHandler(this);
		new RequestHandler(this);
		new SilentHandler(this);
		new StaffChatHandler(this);
		
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ABNatureCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ABVisualCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new GListCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new HubCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new JoinCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new MaintenanceCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new MotdCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReloadCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReportCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new RequestCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new SilentCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new StaffChatCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new StaffListCommand());
		
		ProxyServer.getInstance().registerChannel(incomingBungeeBroadcastChannel);
		ProxyServer.getInstance().registerChannel(incomingAnnounceChannel);
		ProxyServer.getInstance().registerChannel(incomingFilterChannel);
		ProxyServer.getInstance().registerChannel(incomingCommandChannel);
		ProxyServer.getInstance().registerChannel(incomingBanChannel);
		ProxyServer.getInstance().registerChannel(incomingAlertsChannel);
		ProxyServer.getInstance().registerChannel(incomingAuthChannel);
		ProxyServer.getInstance().registerChannel(incomingPermissionsChannel);
		ProxyServer.getInstance().registerChannel(outgoingPremiumChannel);
	}
	
	@Override
	public void onDisable() {
		Handler.clear();
	}

	private void loadConfig() {
		if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();

		File file = new File(this.getDataFolder(), "config.yml");

		if(!file.exists()) {
			try(InputStream in = this.getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath(), new CopyOption[0]);
			} catch(IOException e) {
				e.printStackTrace();
			}
		} try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(this.getDataFolder(), "config.yml"));
		} catch(IOException e) {
			throw new RuntimeException("Unable to save configuration", e);
		}
	}

	public void reloadConfig() {
		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
		} catch(IOException e) {
			throw new RuntimeException("Unable to load configuration", e);
		}
	}


	public void clearBots() {
		this.getProxy().getScheduler().schedule(this, new Runnable() {
			public void run() {
				BotBoth.attacks.clear();
				BotBoth.pings.clear();
			}
		}, 1L, 1L, TimeUnit.MINUTES);
	}

	public void setupBoth() {
		this.getProxy().getScheduler().schedule(this, new Runnable() {
			public void run() {
				BotBoth.joins = 0;
			}
		}, 0L, 1L, TimeUnit.SECONDS);

		this.getProxy().getScheduler().runAsync(this, new Runnable() {
			public void run() {
				try {
					Thread.sleep(AntiBotHandler.startup_time * 1000);
				} catch (InterruptedException e) {}

				AntiBotHandler.startup_multiplier = 1;
			}
		});
	}
}
