package secondlife.network.vituz.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class InventoryListener implements Listener {

    private Vituz plugin = Vituz.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack stack = event.getCurrentItem();

        if (stack == null || stack.getType() == Material.AIR || !stack.hasItemMeta() || event.getClickedInventory() == null) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if(inventory.getTitle().equals(plugin.getChallengesManager().getInventory(player).getTitle())) {
            event.setCancelled(true);

            if (stack.getItemMeta().getDisplayName().contains("1")) {
                player.openInventory(plugin.getChallengesManager().getChallengeWeek1(player));
                return;
            }

            player.sendMessage(Color.translate("&eYou can currently view only week 1 challenges."));
        }

        if(inventory.getTitle().contains("Week 1")) {
            event.setCancelled(true);
        }

        if(inventory.getTitle().equals(plugin.getColorsManager().getInventory().getTitle()) && stack.getItemMeta().hasDisplayName()) {
            event.setCancelled(true);

            if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                event.setCancelled(true);
                return;
            }

            String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            PlayerData data = PlayerData.getByName(player.getName());

            if(player.hasPermission(Permission.STAFF_PERMISSION)) {
                data.setColor(plugin.getColorsManager().getColor(name) + "§o");

                player.sendMessage(Color.translate("&eYour tag has been set to " + plugin.getColorsManager().getColor(name) + name + "&e."));
            } else {
                data.setColor(plugin.getColorsManager().getColor(name) + "");

                player.sendMessage(Color.translate("&eYour tag has been set to " + plugin.getColorsManager().getColor(name) + name + "&e."));
            }

            player.closeInventory();
        }

        if (inventory.getTitle().equals(plugin.getPrefixesManager().getInventory().getTitle())) {
            event.setCancelled(true);

            if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                event.setCancelled(true);
                return;
            }

            if (stack.getItemMeta().getDisplayName().contains("Common")) {
                if (!player.hasPermission(Permission.PREFIX_PERMISSION + ".common")) {
                    event.setCancelled(true);

                    player.closeInventory();
                    player.sendMessage(Color.translate("&eIf you want to use this prefix you must purchase it at &dstore.secondlife.network&e."));
                    return;
                }
            }

            if (stack.getItemMeta().getDisplayName().contains("Epic")) {
                if (!player.hasPermission(Permission.PREFIX_PERMISSION + ".epic")) {
                    event.setCancelled(true);

                    player.closeInventory();
                    player.sendMessage(Color.translate("&eIf you want to use this prefix you must purchase it at &dstore.secondlife.network&e."));
                    return;
                }
            }

            if (stack.getItemMeta().getDisplayName().contains("Special")) {
                if (!player.hasPermission(Permission.PREFIX_PERMISSION + ".special")) {
                    event.setCancelled(true);

                    player.closeInventory();
                    player.sendMessage(Color.translate("&eIf you want to use this prefix you must purchase it at &dstore.secondlife.network&e."));
                    return;
                }
            }

            if (stack.getItemMeta().getDisplayName().contains("OP")) {
                if (!player.hasPermission(Permission.PREFIX_PERMISSION + ".op")) {
                    event.setCancelled(true);

                    player.closeInventory();
                    player.sendMessage(Color.translate("&eIf you want to use this prefix you must purchase it at &dstore.secondlife.network&e."));
                    return;
                }
            }

            if (stack.getItemMeta().getDisplayName().contains("Reset")) {
                if (!player.hasPermission(Permission.PREFIX_PERMISSION)) {
                    event.setCancelled(true);

                    player.closeInventory();
                    player.sendMessage(Color.translate("&eIf you want to use this prefix you must purchase it at &dstore.secondlife.network&e."));
                    return;
                }

                PlayerData data = PlayerData.getByName(player.getName());

                data.setPrefix("");

                player.sendMessage(Color.translate("&eYour prefix has been set to &dNone&e."));

                player.closeInventory();
                return;
            }

            String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());

            name = name.replace(" Common", "&a");
            name = name.replace(" Epic", "&b");
            name = name.replace(" Special", "&5");
            name = name.replace(" OP", "&e");

            PlayerData data = PlayerData.getByName(player.getName());

            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.MIDDLE) {
                if(!player.hasPermission(Permission.XENON_PERMISSION)) {
                    player.sendMessage(Msg.NO_PERMISSION);
                    player.closeInventory();
                    return;
                }

                String fixedName = ChatColor.stripColor(stack.getItemMeta().getDisplayName());

                fixedName = fixedName.replace(" Common", "");
                fixedName = fixedName.replace(" Epic", "");
                fixedName = fixedName.replace(" Special", "");
                fixedName = fixedName.replace(" OP", "");

                if (!data.getPrefix().contains(fixedName)) {
                    player.closeInventory();
                    player.sendMessage(Color.translate("&eBefore you start editing your prefix you &dMUST SET THAT PREFIX&e."));
                    return;
                }

                player.openInventory(plugin.getPrefixesManager().getColorInventory(fixedName));
                return;
            }


            data.setPrefix(plugin.getPrefixesManager().getColor(name) + "§l" + name);
            player.sendMessage(Color.translate("&eYour prefix has been set to " + plugin.getPrefixesManager().getColor(name) + "&l" + name + "&e."));

            player.closeInventory();
        }

        if(inventory.getTitle().contains(" Prefix")) {
            event.setCancelled(true);

            if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                event.setCancelled(true);
                return;
            }

            String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            PlayerData data = PlayerData.getByName(player.getName());

            String title = ChatColor.stripColor(inventory.getTitle());
            title = title.replace(" Prefix", "");

            data.setPrefix(plugin.getPrefixesManager().getColorName(name) + "§l" + title);
            player.sendMessage(Color.translate("&eYour prefix color been set to " + plugin.getPrefixesManager().getColorName(name) + "&l" + plugin.getPrefixesManager().getColorString(name) + "&e."));
            player.closeInventory();
        }

        if (inventory.getTitle().contains("Set as ")) {
            event.setCancelled(true);

            if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                event.setCancelled(true);
                return;
            }

            PlayerData data = PlayerData.getByName(player.getName());
            String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());

            if (inventory.getTitle().contains("Set as Main color")) {
                if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                    event.setCancelled(true);
                    return;
                }

                data.setMainColor(plugin.getServerColorsManager().getColorString(name));

                player.sendMessage(Color.translate("&eYou have changed your main color to " + plugin.getServerColorsManager().getColorName(name) + "&l" + name + "&e."));
                player.closeInventory();
            } else if (inventory.getTitle().contains("Set as Second color")) {
                if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                    event.setCancelled(true);
                    return;
                }

                data.setSecondColor(plugin.getServerColorsManager().getColorString(name));

                player.sendMessage(Color.translate("&eYou have changed your second color to " + plugin.getServerColorsManager().getColorName(name) + "&l" + name + "&e."));
                player.closeInventory();
            }
        }

        if (inventory.getTitle().equals(plugin.getServerColorsManager().getMainInventory().getTitle())) {
            event.setCancelled(true);

            if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                event.setCancelled(true);
                return;
            }

            if(stack.getItemMeta().getDisplayName().contains("Main Color")) {
                if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                    event.setCancelled(true);
                    return;
                }

                player.openInventory(plugin.getServerColorsManager().getColorInventory("Main"));
            } else if (stack.getItemMeta().getDisplayName().contains("Second Color")) {
                if (stack.getItemMeta().getDisplayName().contains("store.secondlife.network")) {
                    event.setCancelled(true);
                    return;
                }

                player.openInventory(plugin.getServerColorsManager().getColorInventory("Second"));
            }
        }
    }
}
