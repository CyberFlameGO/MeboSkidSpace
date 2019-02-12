package secondlife.network.vituz.managers;

import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.item.ItemBuilder;
import secondlife.network.vituz.utilties.Manager;

import java.util.*;

/**
 * Created by Marko on 19.07.2018.
 */

@Getter
public class EssentialsManager extends Manager {

    private String serverName = "SecondLife";
    private String appealAt = "bit.ly/2yDuWkp";
    private HashMap<UUID, UUID> lastReplied = new HashMap<>();
    private ArrayList<String> warps = new ArrayList<>();

    public EssentialsManager(Vituz plugin) {
        super(plugin);
    }

    public void clear() {
        lastReplied.clear();
        warps.clear();
    }

    public void getInventory(Player opener, Player player) {
        Inventory toReturn = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Invsee of " + player.getName());

        toReturn.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Close").build());
        toReturn.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Close").build());
        toReturn.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Inventory Contents").lore(Collections.singletonList(ChatColor.YELLOW + "Player: " + ChatColor.RED + player.getName())).build());

        List<ItemStack> contents = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        List<ItemStack> armor = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));

        for (int i = 0; i < contents.size(); i++) {
            if (i <= 8) {
                ItemStack item = contents.get(i);
                if (item != null) {
                    toReturn.setItem(i + 9, item);
                }
            }
        }

        for (int i = 0; i < contents.size(); i++) {
            if (i > 8) {
                ItemStack item = contents.get(i);
                if (item != null) {
                    int position = i;

                    if (position <= 17) {
                        position += 27;
                    } else if (position < 27) {
                        position += 9;
                    } else {
                        position -= 18;
                    }

                    while (toReturn.getItem(position) != null) {
                        position++;
                        if (position == toReturn.getSize()) break;
                    }

                    if (position != toReturn.getSize()) {
                        toReturn.setItem(position, item);
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            toReturn.setItem(49 + i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14).name(" ").build());
        }

        for (int i = 0; i < armor.size(); i++) {
            ItemStack item = armor.get(i);
            if (item != null && item.getType() != Material.AIR) {
                toReturn.setItem(45 + i, item);
            } else {
                toReturn.setItem(45 + i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14).name(" ").build());
            }
        }

        List<String> lore = new ArrayList<>();
        if (!player.getActivePotionEffects().isEmpty()) {
            lore.add("&7&m------------------------------");
            for (PotionEffect effect : player.getActivePotionEffects()) {
                String name = WordUtils.capitalize(effect.getType().getName().replace("_", " ").toLowerCase());
                lore.add("&e" + name + " " + effect.getAmplifier() + "&c for &e" + DurationFormatUtils.formatDuration(effect.getDuration(), "mm:ss") + "m");
            }
            lore.add("&7&m------------------------------");
        }

        ItemStack effects = new ItemBuilder(Material.POTION).name(ChatColor.RED + (player.getActivePotionEffects().isEmpty() ? "No Potion Effects" : player.getActivePotionEffects().size() + " Effect" + (player.getActivePotionEffects().size() == 1 ? "" : "s"))).lore(lore).build();

        toReturn.setItem(52, new ItemBuilder(Material.PUMPKIN_PIE).name(ChatColor.RED + "Food Level of " + (player.getFoodLevel() / 2)).build());
        toReturn.setItem(53, effects);

        opener.openInventory(toReturn);
    }
}
