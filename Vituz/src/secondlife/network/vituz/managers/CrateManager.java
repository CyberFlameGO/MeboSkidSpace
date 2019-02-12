package secondlife.network.vituz.managers;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.CrateData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Manager;

import java.io.File;
import java.util.List;
import java.util.Random;

public class CrateManager extends Manager {

    public CrateManager(Vituz plugin) {
        super(plugin);
    }

    public void setupBroadcast(Player player, CrateData crate, List<ItemStack> finalLoot, List<String> finalLootName) {
        if(crate.getName().equalsIgnoreCase("Legend") || crate.getName().equalsIgnoreCase("KOTH") || crate.getName().equalsIgnoreCase("Conquest")) {
            ItemStack loot = crate.getItems().get(new Random().nextInt(crate.getItems().size()));

            finalLoot.add(loot);

            if(loot.hasItemMeta()) {
                finalLootName.add(loot.getItemMeta().getDisplayName() + Color.translate(" &5x ") + loot.getAmount());
            }

            finalLootName.add(Color.translate("&d" + WordUtils.capitalize(loot.getType().toString().toLowerCase(), null).replace("_", " ") + " &5x" + loot.getAmount()));
        }
    }

    public void broadcast(Player player, CrateData crate, List<String> finalLootName) {
        if(crate.getName().equalsIgnoreCase("Legend") || crate.getName().equalsIgnoreCase("KOTH") || crate.getName().equalsIgnoreCase("Conquest")) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.sendMessage(Color.translate("&7[&5&lCrates&7] &d" + player.getName() + " &dhas obtained loot from &d" + crate.getName() + " "));
                players.sendMessage(Color.translate("&5&lLoot: "));
                players.sendMessage(Color.translate("" + StringUtils.join(finalLootName, "&7, &d")));
            });
        }
    }

    public int getBonus(Player player) {
        int toReturn = 0;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String perm = info.getPermission();
            if (perm.startsWith("crate.bonus.")) {
                int temp = 0;
                try {
                    temp = Integer.parseInt(perm.replace("crate.bonus.", "").replace(" ", ""));
                } catch (NumberFormatException ignored) {
                }

                if (toReturn > 0 && temp < toReturn) {
                    continue;
                }

                toReturn = temp;
            }
        }

        return toReturn;
    }

    public void addProtection(int x, int y, int z) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(chestProtectFile(x, y, z));

        try {
            config.save(chestProtectFile(x, y, z));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private File chestProtectFile(int x, int y, int z) {
        return new File("plugins/Vituz/crates", String.valueOf(x + "_" + y + "_" + z + ".yml"));
    }

    public boolean doesExists(int x, int y, int z) {
        if(!existChestProtect(x, y, z)) {
            boolean bool = false;

            if(existChestProtect(x, y, z + 1)) {
                bool = true;
            }

            return bool;
        }

        return true;
    }

    private boolean existChestProtect(int x, int y, int z) {
        return chestProtectFile(x, y, z).exists();
    }
}
