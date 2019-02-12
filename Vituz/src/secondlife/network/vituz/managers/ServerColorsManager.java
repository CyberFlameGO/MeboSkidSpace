package secondlife.network.vituz.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.item.ItemBuilder;
import secondlife.network.vituz.utilties.Manager;
import secondlife.network.vituz.utilties.WoolUtil;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.*;

public class ServerColorsManager extends Manager {

    private String[] colors = {
            "Dark Blue", "Dark Green", "Dark Aqua",
            "Dark Red", "Dark Purple", "Gold",
            "Gray", "Dark Gray", "Blue",
            "Green", "Aqua", "Red",
            "Light Purple", "Yellow"
    };

    public ServerColorsManager(Vituz plugin) {
        super(plugin);
    }

    public Inventory getColorInventory(String name) {
        Inventory inventory = Bukkit.createInventory(null, 18, BLUE + "" + BOLD + "Set as " + name + " color");

        Stream.of(colors).forEach(colors -> {
            ChatColor color = null;

            if(colors.contains("Dark Blue")) {
                color = DARK_BLUE;
            } else if(colors.contains("Dark Green")) {
                color = DARK_GREEN;
            } else if(colors.contains("Dark Aqua")) {
                color = DARK_AQUA;
            } else if(colors.contains("Dark Red")) {
                color = DARK_RED;
            } else if(colors.contains("Dark Purple")) {
                color = DARK_PURPLE;
            } else if(colors.contains("Gold")) {
                color = GOLD;
            } else if(colors.contains("Gray")) {
                color = GRAY;
            } else if(colors.contains("Dark Gray")) {
                color = DARK_GRAY;
            } else if(colors.contains("Blue")) {
                color = BLUE;
            } else if(colors.contains("Green")) {
                color = GREEN;
            } else if(colors.contains("Aqua")) {
                color = AQUA;
            } else if(colors.contains("Red")) {
                color = RED;
            } else if(colors.contains("Light Purple")) {
                color = LIGHT_PURPLE;
            } else if(colors.contains("Yellow")) {
                color = YELLOW;
            }

            inventory.addItem(new ItemBuilder(Material.WOOL)
                    .durability(WoolUtil.convertChatColorToWoolData(color)).name(color + colors)
                    .lore(Arrays.asList(
                            "&7&m------------------------------",
                            color + "Click to set this color as your " + name + " color.",
                            "&7&m------------------------------"))
                    .build());
        });

        IntStream.range(0, inventory.getSize()).forEach(i -> {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&5&lstore.secondlife.network").durability(7).build());
            }
        });

        return inventory;
    }

    public Inventory getMainInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, BLUE + BOLD.toString() + "Choose..");

        inventory.setItem(3, new ItemBuilder(Material.DIAMOND_SWORD).name("&5&lMain Color").build());
        inventory.setItem(5, new ItemBuilder(Material.GOLD_SWORD).name("&5&lSecond Color").build());

        return inventory;
    }

    public ChatColor getColorName(String colorName) {
        ChatColor color = null;

        if(colorName.contains("Dark Blue")) {
            color = DARK_BLUE;
        } else if(colorName.contains("Dark Green")) {
            color = DARK_GREEN;
        } else if(colorName.contains("Dark Aqua")) {
            color = DARK_AQUA;
        } else if(colorName.contains("Dark Red")) {
            color = DARK_RED;
        } else if(colorName.contains("Dark Purple")) {
            color = DARK_PURPLE;
        } else if(colorName.contains("Gold")) {
            color = GOLD;
        } else if(colorName.contains("Gray")) {
            color = GRAY;
        } else if(colorName.contains("Dark Gray")) {
            color = DARK_GRAY;
        } else if(colorName.contains("Blue")) {
            color = BLUE;
        } else if(colorName.contains("Green")) {
            color = GREEN;
        } else if(colorName.contains("Aqua")) {
            color = AQUA;
        } else if(colorName.contains("Red")) {
            color = RED;
        } else if(colorName.contains("Light Purple")) {
            color = LIGHT_PURPLE;
        } else if(colorName.contains("Yellow")) {
            color = YELLOW;
        }

        return color;
    }

    public String getColorString(String colorName) {
        String color = null;

        if(colorName.contains("Dark Blue")) {
            color = "§1";
        } else if(colorName.contains("Dark Green")) {
            color = "§2";
        } else if(colorName.contains("Dark Aqua")) {
            color = "§9";
        } else if(colorName.contains("Dark Red")) {
            color = "§4";
        } else if(colorName.contains("Dark Purple")) {
            color = "§5";
        } else if(colorName.contains("Gold")) {
            color = "§6";
        } else if(colorName.contains("Gray")) {
            color = "§7";
        } else if(colorName.contains("Dark Gray")) {
            color = "§8";
        } else if(colorName.contains("Blue")) {
            color = "§1";
        } else if(colorName.contains("Green")) {
            color = "§a";
        } else if(colorName.contains("Aqua")) {
            color = "§b";
        } else if(colorName.contains("Red")) {
            color = "§c";
        } else if(colorName.contains("Light Purple")) {
            color = "§d";
        } else if(colorName.contains("Yellow")) {
            color = "§e";
        }

        return color;
    }
}
