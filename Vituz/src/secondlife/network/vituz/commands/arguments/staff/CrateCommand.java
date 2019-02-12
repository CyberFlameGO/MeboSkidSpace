package secondlife.network.vituz.commands.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.CrateData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.Arrays;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class CrateCommand extends BaseCommand {

    public CrateCommand(Vituz plugin) {
        super(plugin);

        this.command = "crate";
        this.permission = Permission.STAFF_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Crates Usage:");
            sender.sendMessage(ChatColor.RED + "/crate list");
            sender.sendMessage(ChatColor.RED + "/crate create <name>");
            sender.sendMessage(ChatColor.RED + "/crate delete <name>");
            sender.sendMessage(ChatColor.RED + "/crate items <name>");
            sender.sendMessage(ChatColor.RED + "/crate setchest");
            sender.sendMessage(ChatColor.RED + "/crate key <name> <amount> <player>");
        } else {
            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GREEN + "Listing all registered crates:");

                for (CrateData crate : CrateData.getCrates()) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + crate.getName());
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                String name = args[1];
                CrateData crate = CrateData.getByName(name);

                if (crate != null) {
                    sender.sendMessage(ChatColor.RED + "A crate named '" + crate.getName() + "' already exists.");
                    return;
                }

                crate = new CrateData(name);
                sender.sendMessage(ChatColor.RED + "Crate named '" + crate.getName() + "' successfully created.");
            } else if (args[0].equalsIgnoreCase("delete")) {
                String name = args[1];
                CrateData crate = CrateData.getByName(name);

                if (crate == null) {
                    sender.sendMessage(ChatColor.RED + "A crate named '" + name + "' does not exist.");
                    return;
                }

                Vituz.getInstance().getDatabaseManager().getCrateData().deleteOne(eq("name", crate.getName()));
                CrateData.getCrates().remove(crate);
                sender.sendMessage(ChatColor.RED + "Crate named '" + name + "' successfully deleted.");
            } else if (args[0].equalsIgnoreCase("items")) {
                if (!(sender instanceof Player)) return;

                Player player = (Player) sender;

                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Usage: /crate items <name>");
                    return;
                }

                String name = args[1];
                CrateData crate = CrateData.getByName(name);

                if (crate == null) {
                    player.sendMessage(ChatColor.RED + "A crate named '" + name + "' does not exist.");
                    return;
                }

                Inventory inventory = Bukkit.createInventory(player, 9 * 6, ChatColor.RED + "Items - 1/1");

                inventory.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED.toString()).build());
                inventory.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED.toString()).build());
                inventory.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Page 1/1").lore(Arrays.asList(ChatColor.YELLOW + "SOTW: " + ChatColor.RED + crate.getName())).build());

                for (int i = 0; i < crate.getItems().size(); i++) {
                    inventory.setItem(9 + i, crate.getItems().get(i));
                }

                player.openInventory(inventory);
            } else if (args[0].equalsIgnoreCase("key")) {
                if (args.length <= 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: /crate key <name> <amount> <player>");
                    return;
                }

                CrateData crate = CrateData.getByName(args[1]);
                if (crate == null) {
                    sender.sendMessage(ChatColor.RED + "A crate named '" + args[0] + "' does not exist.");
                    return;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (Exception exception) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount");
                    return;
                }

                if (amount < 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount");
                    return;
                }

                Player toGive;
                if (args.length == 3) {
                    if (sender instanceof Player) {
                        toGive = (Player) sender;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /crate key <name> <amount> <player>");
                        return;
                    }
                } else {
                    toGive = Bukkit.getPlayer(args[3]);
                }

                if (toGive == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid player.");
                    return;
                }


                sender.sendMessage(ChatColor.GOLD + "You have successfully given " + ChatColor.YELLOW + amount + ChatColor.GOLD + " crate key" + (amount == 1 ? "" : "s") + " to " + ChatColor.YELLOW + toGive.getName() + ChatColor.GOLD + ".");

                if(toGive.getInventory().firstEmpty() == -1) {
                    toGive.getWorld().dropItemNaturally(toGive.getLocation(), crate.getKey(amount));
                } else {
                    toGive.getInventory().addItem(crate.getKey(amount));
                }
            } else if(args[0].equalsIgnoreCase("setchest")) {
                if (!(sender instanceof Player)) return;

                Player player = (Player) sender;

                Block block = player.getTargetBlock((Set<Material>) null, 5);

                if(block == null) {
                    player.sendMessage(Color.translate("&cYou must be looking at enderchest."));
                    return;
                }

                if(block.getType() != Material.ENDER_CHEST) {
                    player.sendMessage(Color.translate("&cYou must be looking at enderchest."));
                    return;
                }

                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();

                Vituz.getInstance().getCrateManager().addProtection(x, y, z);

                player.sendMessage(Color.translate("&aYou have saved crate location."));
            }
        }
    }
}
