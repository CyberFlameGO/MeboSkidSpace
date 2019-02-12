package secondlife.network.victions.managers;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import secondlife.network.victions.Victions;
import secondlife.network.victions.kit.Kit;
import secondlife.network.victions.utilities.Manager;

import java.io.IOException;
import java.util.*;

public class KitManager extends Manager {

	private Victions plugin = Victions.getInstance();
	private Map<String, Kit> kits = new HashMap<>();

	@Getter
	private List<String> rankedKits = new ArrayList<>();
	
	public KitManager(Victions plugin) {
		super(plugin);

		this.loadKits();

		this.kits.entrySet().stream()
				.filter(kit -> kit.getValue().isEnabled())
				.forEach(kit -> this.rankedKits.add(kit.getKey()));
	}

	public void loadKits() {
		FileConfiguration fileConfig = plugin.getKitsConfig();
		ConfigurationSection kitSection = fileConfig.getConfigurationSection("kits");

		if (kitSection == null) {
			return;
		}

		kitSection.getKeys(false).forEach(name -> {
			ItemStack[] contents = ((List<ItemStack>) kitSection.get(name + ".contents")).toArray(new ItemStack[0]);
			ItemStack[] armor = ((List<ItemStack>) kitSection.get(name + ".armor")).toArray(new ItemStack[0]);

			ItemStack icon = (ItemStack) kitSection.get(name + ".icon");

			boolean enabled = kitSection.getBoolean(name + ".enabled");
			int delay = kitSection.getInt(name + ".delay");

			Kit kit = new Kit(name, contents, armor, icon, enabled, delay);
			this.kits.put(name, kit);
		});
	}

	public void saveKits() {
		FileConfiguration fileConfig = plugin.getKitsConfig();

		fileConfig.set("kits", null);

		this.kits.forEach((kitName, kit) -> {
			if (kit.getIcon() != null && kit.getContents() != null && kit.getArmor() != null) {
				fileConfig.set("kits." + kitName + ".contents", kit.getContents());
				fileConfig.set("kits." + kitName + ".armor", kit.getArmor());
				fileConfig.set("kits." + kitName + ".icon", kit.getIcon());
				fileConfig.set("kits." + kitName + ".enabled", kit.isEnabled());
				fileConfig.set("kits." + kitName + ".delay", kit.getDelay());
			}
		});

		try {
			fileConfig.save(plugin.getKitsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteKit(String name) {
		this.kits.remove(name);
	}

	public void createKit(String name) {
		this.kits.put(name, new Kit(name));
	}

	public Collection<Kit> getKits() {
		return this.kits.values();
	}

	public Kit getKit(String name) {
		return this.kits.get(name);
	}

}
