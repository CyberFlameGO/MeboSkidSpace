package secondlife.network.paik;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.paik.checks.combat.AutoClicker;
import secondlife.network.paik.checks.combat.Killaura;
import secondlife.network.paik.checks.movement.Speed;
import secondlife.network.paik.checks.movement.Timer;
import secondlife.network.paik.checks.movement.fly.FlyA;
import secondlife.network.paik.handlers.*;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.handlers.fixes.BookExploitHandler;
import secondlife.network.paik.handlers.fixes.FenceGlitchHandler;
import secondlife.network.paik.utils.DirectoryUtils;
import secondlife.network.paik.utils.PasteUtils;
import secondlife.network.paik.utils.file.ConfigFile;

public class Paik extends JavaPlugin {

	@Getter private static Paik instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.registerOther();
		this.registerHandlers();
		this.registerChecks();
	}
	
	@Override
	public void onDisable() {
		CheatHandler.clear();
	}
	
	private void registerOther() {
		
		new ConfigFile(this);
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "AutoBan");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Alerts");
		
		DirectoryUtils.registerDirectory();

		PasteUtils.setDeveloperKey("8f3898360d2fe368a723d12839fa8374");
	}

	private void registerChecks() {
		new AutoClicker(this);
		new Killaura(this);
		new FlyA(this);
		new Speed(this);
		new Timer(this);
	}

	private void registerHandlers() {
		new PlayerStatsHandler(this);
		
		new BookExploitHandler(this);
		new FenceGlitchHandler(this);
		
		new AlertsHandler(this);
		new CheatHandler(this);
		new CommandHandler(this);
		new PlayerHandler(this);

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketHandler(this));
	}
}
