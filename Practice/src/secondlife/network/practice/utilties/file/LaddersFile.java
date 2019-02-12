package secondlife.network.practice.utilties.file;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import secondlife.network.practice.Practice;
import secondlife.network.practice.utilties.Handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class LaddersFile extends Handler {

    public static File file;
    public static YamlConfiguration configuration;

    public LaddersFile(Practice plugin) {
        super(plugin);

        file = new File(plugin.getDataFolder(), "ladders.yml");

        if(!file.exists()) {
            plugin.saveResource("ladders.yml", false);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void load() {
        file = new File(Practice.getInstance().getDataFolder(), "ladders.yml");

        if(!file.exists()) {
            Practice.getInstance().saveResource("ladders.yml", false);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            configuration.save(file);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public static double getDouble(String path) {
        if(configuration.contains(path)) {
            return configuration.getDouble(path);
        }

        return 0.0;
    }

    public static int getInt(String path) {
        if (configuration.contains(path)) {
            return configuration.getInt(path);
        }
        return 0;
    }

    public static boolean getBoolean(String path) {
        return configuration.contains(path) && configuration.getBoolean(path);
    }

    public static String getString(String path) {
        if(configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', configuration.getString(path));
        }

        return "String at path: " + path + " not found!";
    }

    public static List<String> getStringList(String path) {
        if(configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<String>();

            for(String string : configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }

            return strings;
        }

        return Arrays.asList("String List at path: " + path + " not found!");
    }
}
