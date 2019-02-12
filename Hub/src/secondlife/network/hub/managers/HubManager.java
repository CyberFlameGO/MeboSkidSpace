package secondlife.network.hub.managers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hub.Hub;
import secondlife.network.hub.utilties.Manager;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.*;

/**
 * Created by Marko on 22.07.2018.
 */

@Getter
public class HubManager extends Manager {

    private Map<UUID, Long> cooldowns = new HashMap<>();
    private List<UUID> hidingPlayers = new ArrayList<>();

    public HubManager(Hub plugin) {
        super(plugin);
    }

    private void handleSetOp(Player player) {
        ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).name("&5&lOperator").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.MAROON).build();
        ItemStack leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).name("&5&lOperator").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.MAROON).build();
        ItemStack chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&5&lOperator").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.MAROON).build();

        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        player.updateInventory();
    }

    private void handleSetStaff(Player player) {
        ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).name("&5&lStaff").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.AQUA).build();
        ItemStack leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).name("&5&lStaff").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.AQUA).build();
        ItemStack chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&5&lStaff").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.AQUA).build();

        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        player.updateInventory();
    }

    private void handleSetDonator(Player player) {
        ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).name("&5&lDonator").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.GREEN).build();
        ItemStack leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).name("&5&lDonator").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.GREEN).build();
        ItemStack chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&5&lDonator").lore(Arrays.asList("&dYou current rank is " + VituzAPI.getRankName(player.getName()) + "&d!")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.GREEN).build();

        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        player.updateInventory();
    }

    private void handleSetBanned(Player player) {
        ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).name("&5&lYou are banned").lore(Arrays.asList("&dNoob")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.BLACK).build();
        ItemStack leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).name("&5&lYou are banned").lore(Arrays.asList("&dNoob")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.BLACK).build();
        ItemStack chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&5&lYou are banned").lore(Arrays.asList("&dNoob")).enchantment(Enchantment.DURABILITY, 3).color(org.bukkit.Color.BLACK).build();

        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setHelmet(new ItemBuilder(Material.BEDROCK).build());

        player.updateInventory();
    }

    public void handleSetArmor(Player player) {
        if(VituzAPI.isBanned(player)) {
            handleSetBanned(player);
        } else {
            if(player.hasPermission(Permission.DONOR_PERMISSION) && !player.hasPermission(Permission.STAFF_PERMISSION)) {
                handleSetDonator(player);
            } else if(player.hasPermission(Permission.STAFF_PERMISSION) && !player.isOp()) {
                handleSetStaff(player);
            } else if(player.isOp()) {
                handleSetOp(player);
            }
        }
    }
}
