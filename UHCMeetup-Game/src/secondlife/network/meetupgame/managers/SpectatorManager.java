package secondlife.network.meetupgame.managers;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.data.MeetupData;
import secondlife.network.meetupgame.data.SpectatorData;
import secondlife.network.meetupgame.states.PlayerState;
import secondlife.network.meetupgame.utilties.Manager;
import secondlife.network.meetupgame.utilties.MeetupUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Tasks;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.*;

/**
 * Created by Marko on 08.07.2018.
 */

@Getter
public class SpectatorManager extends Manager {

    private Map<UUID, SpectatorData> spectators = new HashMap<>();
    private Inventory aliveInventory;

    public SpectatorManager(MeetupGame plugin) {
        super(plugin);
    }

    public void handleOnDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            handleDisable(player);
        }

        spectators.clear();
    }

    public void handleEnable(Player player) {
        SpectatorData data = new SpectatorData();

        data.setContents(player.getInventory().getContents());
        data.setArmor(player.getInventory().getArmorContents());
        data.setGameMode(player.getGameMode());

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.setGameMode(GameMode.CREATIVE);

        if(Bukkit.getWorld("world") != null) {
            player.teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));
        }

        plugin.getVanishManager().handleVanish(player);
        MeetupData.getByName(player.getName()).setPlayerState(PlayerState.SPECTATING);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        MeetupUtils.loadSpectatorInventory(player);

        spectators.put(player.getUniqueId(), data);

        Tasks.runLater(() -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            MeetupUtils.loadSpectatorInventory(player);
        }, 40L);
    }

    public void handleDisable(Player player) {
        if(spectators.containsKey(player.getUniqueId())) {
            SpectatorData data = spectators.get(player.getUniqueId());

            player.getInventory().setContents(data.getContents());
            player.getInventory().setArmorContents(data.getArmor());
            player.setGameMode(data.getGameMode());

            if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

            spectators.remove(player.getUniqueId());
        }
    }

    public void handleRandomTeleport(Player player) {
        List<Player> online = new ArrayList<>();

        for(Player players : Bukkit.getOnlinePlayers()) {
            MeetupData p = MeetupData.getByName(players.getName());

            if(players != player && p.getPlayerState().equals(PlayerState.PLAYING)) {
                online.add(players);
            }
        }

        if(online.size() != 0) {
            Player target = online.get(new Random().nextInt(online.size()));
            player.teleport(target);
            player.sendMessage(Color.translate("&eYou have been randomly teleported to &d" + target.getName() + "&e."));
        }
    }

    public Inventory getAliveInventory() {
        aliveInventory = Bukkit.createInventory(null, 54, "Alive Players:");

        aliveInventory.clear();

        for(MeetupData uhcPlayers : MeetupData.getMeetupDatas().values()) {
            if(uhcPlayers.isAlive()) {
                OfflinePlayer targetOffline = Bukkit.getOfflinePlayer(uhcPlayers.getName());

                if(targetOffline != null && targetOffline.isOnline()) {
                    Player target = Bukkit.getPlayer(uhcPlayers.getName());

                    aliveInventory.addItem(new ItemBuilder(Material.SKULL_ITEM).name("&7" + target.getName()).lore("&eLeft click to teleport to the &d" + target.getName()).build());
                }
            }
        }

        return aliveInventory;
    }

    /*public Inventory getAliveInventory(int page) {
        int total = (int) Math.ceil(plugin.getGameManager().getAlivePlayers() / 9.0);

        if(total == 0) {
            total = 1;
        }

        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.RED + "Alive Players - " + page + "/" + total);

        inventory.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name("&cPrevious Page").build());
        inventory.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name("&cNext Page").build());
        inventory.setItem(4, new ItemBuilder(Material.PAPER).name("&cPage " + page + "/" + ((total == 0) ? 1 : total)).build());

        List<MeetupData> toPut = new ArrayList<>();

        MeetupData.getMeetupDatas()
                .values().stream().filter(MeetupData::isAlive)
                .sorted(Comparator.comparing(MeetupData::getName))
                .forEach(toPut::add);

        for(int i = page * 9 + 9; i < page * 9 + 18; i++) {
            if(toPut.size() > i) {
                continue;
            }

            MeetupData data = toPut.get(i);

            ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM);
            builder.name("&7" + data.getRealName());
            builder.lore("&eClick to teleport to the &d" + data.getRealName());

            inventory.setItem(9 + toPut.indexOf(data) % 9, builder.build());
        }

//        toPut.forEach(data -> {
//            if(toPut.indexOf(data) >= page * 9 - 9 && toPut.indexOf(data) < page * 9) {
//
//            }
//        });

        /*List<UHCData> toLoop = new ArrayList<>(PlayerManager.getAlivePlayers());
        Collections.reverse(toLoop);

        for(UHCData data : toLoop) {
            if (data.isAlive() && toLoop.indexOf(data) >= page * 9 - 9 && toLoop.indexOf(data) < page * 9) {
                ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM);
                builder.name("&7" + data.getRealName());
                builder.lore("&eClick to teleport to the &d" + data.getRealName());

                inventory.setItem(9 + toLoop.indexOf(data) % 9, builder.build());
            }
        }

        return inventory;
    }*/

    public Inventory getInventory(int page) {
        int alive = plugin.getGameManager().getAlivePlayers();

        int total = (int) Math.ceil(alive / 9.0);
        if(total == 0) {
            total = 1;
        }

        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.RED + "Alive Players - " + page + "/" + total);

        inventory.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name("&cPrevious Page").build());
        inventory.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name("&cNext Page").build());
        inventory.setItem(4, new ItemBuilder(Material.PAPER).name("&cPage " + page + "/" + total).build());

        List<MeetupData> toLoop = new ArrayList<>(MeetupData.getMeetupDatas().values());
        Collections.reverse(toLoop);
        toLoop.removeIf(MeetupData::isNotAlive);

        toLoop.forEach(data -> {
            if(toLoop.indexOf(data) >= page * 9 - 9 && toLoop.indexOf(data) < page * 9) {
                String name = data.getRealName() != null ? data.getRealName() : data.getName();

                ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM);
                builder.name("&7" + name);
                builder.lore("&eClick to teleport to the &d" + data.getRealName());

                inventory.setItem(9 + toLoop.indexOf(data) % 9, builder.build());
            }
        });


        return inventory;
    }

    public Inventory createInventory(Player player, Player target) {
        Inventory inv = Bukkit.createInventory(null, 54, "Inventory preview");

        ItemStack[] contents = target.getInventory().getContents();
        ItemStack[] armor = target.getInventory().getArmorContents();

        inv.setContents(contents);

        inv.setItem(45, armor[0]);
        inv.setItem(46, armor[1]);
        inv.setItem(47, armor[2]);
        inv.setItem(48, armor[3]);

        inv.setItem(36, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(37, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(38, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(39, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(40, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(41, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(42, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(43, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(44, this.createGlass(ChatColor.RED + "Inventory Preview"));
        inv.setItem(49, this.createGlass(ChatColor.RED + "Inventory Preview"));

        inv.setItem(50, this.createItem(Material.SPECKLED_MELON, ChatColor.RED + "Health", (int)((Damageable)target).getHealth()));
        inv.setItem(51, this.createItem(Material.GRILLED_PORK, ChatColor.RED + "Hunger", target.getFoodLevel()));
        inv.setItem(52, this.createSkull(target, ChatColor.GREEN + target.getName()));
        inv.setItem(53, this.createWool(ChatColor.RED + "Close Preview", 14));

        return inv;
    }

    public ItemStack createItem(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);

        return item;
    }

    public ItemStack createGlass(String name) {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);

        return item;
    }

    public ItemStack createWool(String name, int value) {
        ItemStack item = new ItemStack(Material.WOOL, 1, (short) value);
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(name);
        item.setItemMeta(itemmeta);
        return item;
    }

    public ItemStack createSkull(Player player, String name) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
        skullmeta.setDisplayName(name);
        skullmeta.setOwner(player.getName());
        item.setItemMeta(skullmeta);

        return item;
    }
}
