package secondlife.network.vituz.managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.inventory.VituzMenu;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsManager extends VituzMenu {

    public SettingsManager(Player player) {
        super(player, "Options", 45);
    }

    @Override
    public void updateInventory(Player player) {
        PlayerData data = PlayerData.getByName(player.getName());

        this.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name(" ").build());

        this.getInventory().setItem(10, addItem(Material.NOTE_BLOCK, "Toggle Sounds", data.isSounds(), "Do you want to hear", "messaging sounds?"));
        this.getInventory().setItem(12, addItem(Material.PAPER, "Toggle Messages", data.isToggleMsg(), "Do you want to see", "messages from other", "players?"));
        this.getInventory().setItem(14, addItem(Material.SLIME_BALL, "Toggle Time", data.getWorldTime(), "Toggle between day and night."));
        this.getInventory().setItem(16, addItem(Material.SIGN, "Public Chat", data.isToggleChat(), "Do you want to see", "public chat messages?"));

        this.getInventory().setItem(28, addItem(Material.ENCHANTED_BOOK, "Tab List Info", data.isTab(), "Do you want to see", "extra info on your", "tab list?"));
        this.getInventory().setItem(30, new ItemBuilder(Material.LEASH).name("&5Server Color").lore(Color.translate(Arrays.asList("", "&dClick this to open", "&dinventory and edit", "&dserver colors."))).build());

        if(player.hasPermission(Permission.PREFIX_PERMISSION)) {
            this.getInventory().setItem(32, new ItemBuilder(Material.BOOK).name("&5Prefixes").lore(Color.translate(Arrays.asList("", "&dClick this to open", "&dprefix inventory."))).build());
        } else {
            this.getInventory().setItem(32, new ItemBuilder(Material.REDSTONE_BLOCK).name("&c&lPrefixes").lore(Color.translate(Arrays.asList("", "&cYou don't have any prefixes."))).build());
        }

        if(player.hasPermission(Permission.COLOR_PERMISSION)) {
            this.getInventory().setItem(34, new ItemBuilder(Material.INK_SACK).durability(10).name("&5Colors").lore(Color.translate(Arrays.asList("", "&dClick this to open", "&dcolors inventory."))).build());
        } else {
            this.getInventory().setItem(34, new ItemBuilder(Material.REDSTONE_BLOCK).name("&c&lColors").lore(Color.translate(Arrays.asList("", "&cYou don't have permission for this."))).build());
        }
    }

    @Override
    public void onClickItem(Player player, ItemStack itemStack, boolean isRightClicked) {
        if(itemStack != null) {
            PlayerData data = PlayerData.getByName(player.getName());

            if(data == null) return;

            if(itemStack.getType() == Material.NOTE_BLOCK) {
                player.performCommand("sounds");
            } else if(itemStack.getType() == Material.PAPER) {
                player.performCommand("tpm");
            } else if(itemStack.getType() == Material.SLIME_BALL) {
                if(data.getWorldTime().equalsIgnoreCase("DAY")) {
                    data.setWorldTime("NIGHT");
                    player.setPlayerTime(14000L, false);
                    player.sendMessage(Color.translate("&eYou have set your time to &dNight&e."));
                } else if(data.getWorldTime().equalsIgnoreCase("NIGHT")) {
                    data.setWorldTime("DEFAULT");
                    player.setPlayerTime(0L, false);
                    player.sendMessage(Color.translate("&eYou have set your time to &dDefault&e."));
                } else if(data.getWorldTime().equalsIgnoreCase("DEFAULT")) {
                    data.setWorldTime("DAY");
                    player.resetPlayerTime();
                    player.sendMessage(Color.translate("&eYou have set your time to &dDay&e."));
                }
            } else if(itemStack.getType() == Material.SIGN) {
                player.performCommand("tc");
            } else if(itemStack.getType() == Material.ENCHANTED_BOOK) {
                if(data.isTab()) {
                    data.setTab(false);

                    player.sendMessage(Color.translate("&eYou have set your tab list to &dVanilla&e."));
                } else {
                    data.setTab(true);

                    player.sendMessage(Color.translate("&eYou have set your tab list to &dDetailed&e."));
                }
            } else if(itemStack.getType() == Material.LEASH) {
                player.openInventory(Vituz.getInstance().getServerColorsManager().getMainInventory());
            } else if(itemStack.getType() == Material.BOOK) {
                player.performCommand("prefix");
            } else if(itemStack.getType() == Material.INK_SACK) {
                player.performCommand("color");
            }
        }
    }

    @Override
    public void onClose() {}

    public ItemStack addItem(Material material, String name, boolean value, String... info) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(Color.translate("&5" + name));

        List<String> lore = new ArrayList<>();

        lore.add("");

        for(String text : info) {
            lore.add(Color.translate("&d" + text));
        }

        lore.add("");

        if(value) {
            lore.add(Color.translate("  &5" + Msg.KRUZIC + " &aYes"));
            lore.add(Color.translate("   &cNo"));
        } else {
            lore.add(Color.translate("   &cYes"));
            lore.add(Color.translate("  &5" + Msg.KRUZIC + " &aNo"));
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack addItem(Material material, String name, String time, String... info) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(Color.translate("&5" + name));

        ArrayList<String> lore = new ArrayList<>();

        lore.add("");

        for(String text : info) {
            lore.add(Color.translate("&d" + text));
        }

        lore.add("");

        if(time.equalsIgnoreCase("DAY")) {
            lore.add(Color.translate("  &5" + Msg.KRUZIC + " &aDay"));
            lore.add(Color.translate("   &cNight"));
            lore.add(Color.translate("   &cReset Time"));
        } else if(time.equalsIgnoreCase("NIGHT")) {
            lore.add(Color.translate("   &cDay"));
            lore.add(Color.translate("  &5" + Msg.KRUZIC + " &aNight"));
            lore.add(Color.translate("   &cReset Time"));
        } else if(time.equalsIgnoreCase("DEFAULT")) {
            lore.add(Color.translate("   &cDay"));
            lore.add(Color.translate("   &cNight"));
            lore.add(Color.translate("  &5" + Msg.KRUZIC + " &aReset Time"));
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}