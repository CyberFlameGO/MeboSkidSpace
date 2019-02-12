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

/**
 * Created by Marko on 27.04.2018.
 */
public class PrefixesManager extends Manager {

    private String[] common = new String[] {
            "✓ Common", "✖ Common", "⚙ Common",
            "☺ Common", "☮ Common", "⚒ Common",
    };

    private String[] epic = new String[] {
            "✬ Epic", "♞ Epic", "❤ Epic",
            "❦ Epic", "♚ Epic", "♽ Epic",
    };

    private String[] special = new String[] {
            "♻ Special", "♛ Special", "☀ Special",
            "⍣ Special", "⍟ Special", "♫ Special" +
            "",
    };

    private String[] op = new String[] {
            "✠ OP", "❖ OP", "☪ OP",
            "☯ OP", "☬ OP", "☠ OP",
    };


    private String[] colors = new String[] {
            "Dark Blue", "Dark Green", "Dark Aqua",
            "Dark Red", "Dark Purple", "Gold",
            "Gray", "Dark Gray", "Blue",
            "Green", "Aqua", "Red",
            "Light Purple", "Yellow",
    };

    public PrefixesManager(Vituz plugin) {
        super(plugin);
    }

    public Inventory getColorInventory(String name) {
        Inventory inventory = Bukkit.createInventory(null, 18, BLUE + "" + BOLD + name + " Prefix");

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
                            color + "Click to set this color to your prefix.",
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

    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, BLUE + "" + BOLD + "Choose a prefix.");

        Stream.of(common).forEach(colors -> {
            ChatColor color = null;

            if(colors.contains("Common")) {
                color = GREEN;
            }

            inventory.addItem(new ItemBuilder(Material.WOOL)
                    .durability(WoolUtil.convertChatColorToWoolData(color)).name(color + colors)
                    .lore(Arrays.asList(
                            "&7&m------------------------------",
                            color + "Click to set this prefix.",
                            "",
                            color + "For Xenon users:",
                            color + "You can edit color of this prefix",
                            color + "by clicking MIDDLE or RIGHT CLICK",
                            "&7&m------------------------------"))
                    .build());
        });

        Stream.of(epic).forEach(colors -> {
            ChatColor color = null;

            if(colors.contains("Epic")) {
                color = AQUA;
            }

            inventory.addItem(new ItemBuilder(Material.WOOL)
                    .durability(WoolUtil.convertChatColorToWoolData(color)).name(color + colors)
                    .lore(Arrays.asList(
                            "&7&m------------------------------",
                            color + "Click to set this prefix.",
                            "",
                            color + "For Xenon users:",
                            color + "You can edit color of this prefix",
                            color + "by clicking MIDDLE or RIGHT CLICK",
                            "&7&m------------------------------"))
                    .build());
        });

        Stream.of(special).forEach(colors -> {
            ChatColor color = null;

            if(colors.contains("Special")) {
                color = DARK_PURPLE;
            }

            inventory.addItem(new ItemBuilder(Material.WOOL)
                    .durability(WoolUtil.convertChatColorToWoolData(color)).name(color + colors)
                    .lore(Arrays.asList(
                            "&7&m------------------------------",
                            color + "Click to set this prefix.",
                            "",
                            color + "For Xenon users:",
                            color + "You can edit color of this prefix",
                            color + "by clicking MIDDLE or RIGHT CLICK",
                            "&7&m------------------------------"))
                    .build());
        });

        Stream.of(op).forEach(colors -> {
            ChatColor color = null;

            if(colors.contains("OP")) {
                color = GOLD;
            }

            inventory.addItem(new ItemBuilder(Material.WOOL)
                    .durability(WoolUtil.convertChatColorToWoolData(color)).name(color + colors)
                    .lore(Arrays.asList(
                            "&7&m------------------------------",
                            color + "Click to set this prefix.",
                            "",
                            color + "For Xenon users:",
                            color + "You can edit color of this prefix",
                            color + "by clicking MIDDLE or RIGHT CLICK",
                            "&7&m------------------------------"))
                    .build());
        });

        inventory.setItem(26, new ItemBuilder(Material.LEASH).name("&d&lReset Prefix").lore(Arrays.asList(
                "&7&m------------------------------",
                "&dClick to reset prefix.",
                "&7&m------------------------------"))
                .build());

        IntStream.range(0, inventory.getSize()).forEach(i -> {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).name("&5&lstore.secondlife.network").durability(7).build());
            }
        });

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
            color = "Dark Blue";
        } else if(colorName.contains("Dark Green")) {
            color = "Dark Green";
        } else if(colorName.contains("Dark Aqua")) {
            color = "Dark Aqua";
        } else if(colorName.contains("Dark Red")) {
            color = "Dark Red";
        } else if(colorName.contains("Dark Purple")) {
            color = "Dark Purple";
        } else if(colorName.contains("Gold")) {
            color = "Gold";
        } else if(colorName.contains("Gray")) {
            color = "Gray";
        } else if(colorName.contains("Dark Gray")) {
            color = "Dark Gray";
        } else if(colorName.contains("Blue")) {
            color = "Blue";
        } else if(colorName.contains("Green")) {
            color = "Green";
        } else if(colorName.contains("Aqua")) {
            color = "Aqua";
        } else if(colorName.contains("Red")) {
            color = "Red";
        } else if(colorName.contains("Light Purple")) {
            color = "Light Purple";
        } else if(colorName.contains("Yellow")) {
            color = "Yellow";
        }

        return color;
    }

    public ChatColor getColor(String colorName) {
        ChatColor color = null;

        if(colorName.contains("&a")) {
            color = GREEN;
        } else if(colorName.contains("&b")) {
            color = AQUA;
        } else if(colorName.contains("&5")) {
            color = DARK_PURPLE;
        } else if(colorName.contains("&e")) {
            color = GOLD;
        } else if(colorName.contains("&4")) {
            color = DARK_RED;
        }

        return color;
    }
}
