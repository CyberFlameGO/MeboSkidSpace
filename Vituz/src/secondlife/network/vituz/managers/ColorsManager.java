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

public class ColorsManager extends Manager {

    private String[] colors = {
            "Purple", "Blue", "Light Gray",
            "Gray", "Pink", "Green",
            "Light Blue", "Orange",
            "Red", "Dark Red", "Yellow",
            "Dark Green", "Reset Color"
    };

    public ColorsManager(Vituz plugin) {
        super(plugin);
    }

    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 18, BLUE + "" + BOLD + "Choose a color.");

        Stream.of(colors).forEach(colorName -> {
            ChatColor color = null;

            if(colorName.equalsIgnoreCase("Purple")) {
                color = DARK_PURPLE;
            } else if(colorName.equalsIgnoreCase("Blue")) {
                color = BLUE;
            } else if(colorName.equalsIgnoreCase("Light Gray")) {
                color = GRAY;
            } else if(colorName.equalsIgnoreCase("Gray")) {
                color = DARK_GRAY;
            } else if(colorName.equalsIgnoreCase("Pink")) {
                color = LIGHT_PURPLE;
            } else if(colorName.equalsIgnoreCase("Green")) {
                color = GREEN;
            } else if(colorName.equalsIgnoreCase("Light Blue")) {
                color = AQUA;
            } else if(colorName.equalsIgnoreCase("Orange")) {
                color = GOLD;
            } else if(colorName.equalsIgnoreCase("Red")) {
                color = RED;
            } else if(colorName.equalsIgnoreCase("Dark Red")) {
                color = DARK_RED;
            } else if(colorName.equalsIgnoreCase("Yellow")) {
                color = YELLOW;
            } else if(colorName.equalsIgnoreCase("Dark Green")) {
                color = DARK_GREEN;
            } else if(colorName.equalsIgnoreCase("Reset Color")) {
                color = WHITE;
            }

            inventory.addItem(new ItemBuilder(Material.WOOL)
                    .durability(WoolUtil.convertChatColorToWoolData(color)).name(color + colorName)
                    .lore(Arrays.asList(
                            "&7&m------------------------------",
                            color + "Click to set your nick name color.",
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

    public ChatColor getColor(String colorName) {
        ChatColor color = null;

        if(colorName.equalsIgnoreCase("Purple")) {
            color = DARK_PURPLE;
        } else if(colorName.equalsIgnoreCase("Blue")) {
            color = BLUE;
        } else if(colorName.equalsIgnoreCase("Light Gray")) {
            color = GRAY;
        } else if(colorName.equalsIgnoreCase("Gray")) {
            color = DARK_GRAY;
        } else if(colorName.equalsIgnoreCase("Pink")) {
            color = LIGHT_PURPLE;
        } else if(colorName.equalsIgnoreCase("Green")) {
            color = GREEN;
        } else if(colorName.equalsIgnoreCase("Light Blue")) {
            color = AQUA;
        } else if(colorName.equalsIgnoreCase("Orange")) {
            color = GOLD;
        } else if(colorName.equalsIgnoreCase("Red")) {
            color = RED;
        } else if(colorName.equalsIgnoreCase("Dark Red")) {
            color = DARK_RED;
        } else if(colorName.equalsIgnoreCase("Yellow")) {
            color = YELLOW;
        } else if(colorName.equalsIgnoreCase("Dark Green")) {
            color = DARK_GREEN;
        } else if(colorName.equalsIgnoreCase("Reset Color")) {
            color = WHITE;
        }

        return color;
    }
}
