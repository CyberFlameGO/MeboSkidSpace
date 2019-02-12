package secondlife.network.vituz.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.PlayerUtils;

public class DataPunishListener implements Listener {

    private Vituz plugin = Vituz.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!PlayerUtils.isMongoConnected(event)) return;

        String name = event.getName();
        PunishData profile = PunishData.getByName(name);

        if (!profile.isLoaded()) {
            profile.load();
        }

        if(!profile.isLoaded()) {
            PlayerUtils.kick(event);
            return;
        }

        Punishment ban = profile.getBannedPunishment();

        profile.setAddress(event.getAddress().getHostAddress());

        if (!profile.getAddresses().contains(profile.getAddress())) {
            profile.getAddresses().add(profile.getAddress());
        }

        if (!plugin.getConfig().getString("SERVERNAME").equals("Hub")) {
            if (ban != null) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(ban.getType().getMessage());
                return;
            }
        }

        if (profile.isBlacklisted()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(PunishmentType.BLACKLIST.getMessage());
            return;
        }

        if (profile.isIPBanned()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(PunishmentType.IPBAN.getMessage());
            return;
        }

        for (String alts : profile.getAlts()) {
            if (!alts.equals(event.getName())) {
                PunishData alt = PunishData.getByName(alts);

                if (alt == null || !alt.isBlacklisted()) {
                    continue;
                }

                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(Color.translate("&cYour account has been blacklisted from the " + plugin.getEssentialsManager().getServerName() + " Network.\n" + "&cThis punishment is in relation to " + ((alt.getName() == null) ? "another account" : alt.getName()) + ".\n" + "&cThis punishment cannot be appealed."));
            }
        }

        for (String alts : profile.getAlts()) {
            if (!alts.equals(event.getName())) {
                PunishData alt = PunishData.getByName(alts);

                if (alt == null || !alt.isIPBanned()) {
                    continue;
                }

                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(Color.translate("&cYour account has been IP banned from the " + plugin.getEssentialsManager().getServerName() + " Network.\n" + "&cThis punishment is in relation to " + ((alt.getName() == null) ? "another account" : alt.getName()) + ".\n" + "&cThis punishment cannot be appealed."));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PunishData.getByName(event.getPlayer().getName()).save();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishData profile = PunishData.getByName(player.getName());

        if(!profile.isLoaded()) {
            profile.load();
        }

        Punishment punishment = profile.getMutedPunishment();

        if (punishment != null) {
            event.setCancelled(true);

            player.sendMessage(PunishmentType.MUTE.getMessage().replace("%DURATION%", punishment.getTimeLeft()));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack stack = event.getCurrentItem();

        if (stack != null && stack.getType() != Material.AIR) {
            String title = event.getInventory().getTitle();
            String displayName = stack.getItemMeta().getDisplayName();

            if ((title.contains("Mutes") || title.contains("Bans") || title.contains("Blacklists")) && player.hasPermission(Permission.STAFF_PLUS_PERMISSION)) {
                event.setCancelled(true);

                PunishmentType type = title.contains("Mutes") ? PunishmentType.MUTE : (title.contains("Bans") ? PunishmentType.BAN : PunishmentType.BLACKLIST);

                int page = Integer.parseInt(title.substring(title.lastIndexOf("/") - 1, title.lastIndexOf("/")));
                int total = Integer.parseInt(title.substring(title.lastIndexOf("/") + 1, title.lastIndexOf("/") + 2));

                String playerName = ChatColor.stripColor(event.getInventory().getItem(4).getItemMeta().getLore().get(0).substring(event.getInventory().getItem(4).getItemMeta().getLore().get(0).indexOf(" "), event.getInventory().getItem(4).getItemMeta().getLore().get(0).length())).trim();

                PunishData profile = PunishData.getByName(playerName);

                if (profile == null) {
                    return;
                }

                if (!profile.isLoaded()) {
                    profile.load();
                }

                if (displayName.contains("Next Page")) {
                    if (page + 1 > total) {
                        player.sendMessage(Color.translate("&cThere are no more pages."));
                        return;
                    }

                    player.openInventory(profile.getPunishmentsInventory(type, page + 1));
                    return;
                } else if (displayName.contains("Previous Page")) {
                    if (page == 1) {
                        player.sendMessage(Color.translate("&cYou're on the first page."));
                        return;
                    }

                    player.openInventory(profile.getPunishmentsInventory(type, page - 1));
                }
            }

            if (title.endsWith("Punishments") && player.hasPermission(Permission.STAFF_PLUS_PERMISSION)) {
                event.setCancelled(true);

                String name = ChatColor.stripColor(title.substring(0, title.indexOf("'")));
                PunishData profile = PunishData.getByName(name);

                if (profile == null) {
                    return;
                }

                if (!profile.isLoaded()) {
                    profile.load();
                }

                player.openInventory(profile.getPunishmentsInventory(displayName.contains("Mute") ? PunishmentType.MUTE : (displayName.contains("Ban") ? PunishmentType.BAN : PunishmentType.BLACKLIST), 1));
            }
        }
    }
}
