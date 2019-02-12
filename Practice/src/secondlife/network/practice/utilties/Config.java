package secondlife.network.practice.utilties;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import secondlife.network.practice.Practice;

import java.io.File;
import java.io.IOException;

public class Config {

	public String fileName;
	public File configFile;
	private FileConfiguration config;

	public Config(String fileName) {
		this.fileName = fileName;
		this.configFile = new File(Practice.getInstance().getDataFolder(), fileName);

		if (!this.configFile.exists()) {
			this.configFile.getParentFile().mkdirs();

			if (Practice.getInstance().getResource(fileName) == null) {
				try {
					this.configFile.createNewFile();
				} catch (IOException e) {
					Practice.getInstance().getLogger().severe("Failed to create new file " + fileName);
				}
			} else {
				Practice.getInstance().saveResource(fileName, false);
			}
		}

		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	public Config(File file, String fileName) {
		this.configFile = new File(file, fileName);

		if (!this.configFile.exists()) {
			this.configFile.getParentFile().mkdirs();

			if (Practice.getInstance().getResource(fileName) == null) {
				try {
					this.configFile.createNewFile();
				} catch (IOException e) {
					Practice.getInstance().getLogger().severe("Failed to create new file " + fileName);
				}
			} else {
				Practice.getInstance().saveResource(fileName, false);
			}
		}

		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public void save() {
		try {
			this.getConfig().save(this.configFile);
		} catch (IOException e) {
			Bukkit.getLogger().severe("Could not save config file " + this.configFile.toString());
			e.printStackTrace();
		}
	}
}
