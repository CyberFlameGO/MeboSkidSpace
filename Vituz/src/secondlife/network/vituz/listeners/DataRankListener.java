package secondlife.network.vituz.listeners;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.ranks.grant.Grant;
import secondlife.network.vituz.ranks.grant.procedure.GrantProcedure;
import secondlife.network.vituz.ranks.grant.procedure.GrantProcedureStage;
import secondlife.network.vituz.ranks.redis.RankPublisher;
import secondlife.network.vituz.ranks.redis.RankSubscriberAction;
import secondlife.network.vituz.utilties.*;

public class DataRankListener implements Listener {

    private Vituz plugin = Vituz.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerUtils.checkMongo(event);

        RankData profile = RankData.getByName(event.getPlayer().getName());

        if(!profile.isLoaded()) {
            profile.load();
        }

        profile.setupAtatchment();

        if(!profile.isLoaded()) {
            PlayerUtils.kick(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RankData data = RankData.getByName(player.getName());

        if(data.getAttachment() != null && !data.getPermissions().isEmpty()) {
            player.removeAttachment(data.getAttachment());
        }

        data.save();
    }

    @EventHandler
    public void onDisableGrant(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack stack = event.getCurrentItem();
       
		if(stack != null && stack.getType() != Material.AIR) {
            String title = event.getInventory().getTitle();
            String displayName = stack.getItemMeta().getDisplayName();
          
            if(title.contains(ChatColor.RED + "Grants") && player.hasPermission(Permission.OP_PERMISSION)) {
                event.setCancelled(true);
              
                int page = Integer.parseInt(title.substring(title.lastIndexOf("/") - 1, title.lastIndexOf("/")));
                int total = Integer.parseInt(title.substring(title.lastIndexOf("/") + 1, title.lastIndexOf("/") + 2));
              
                String playerName = ChatColor.stripColor(event.getInventory().getItem(4).getItemMeta().getLore().get(0).substring(event.getInventory().getItem(4).getItemMeta().getLore().get(0).indexOf(" "), event.getInventory().getItem(4).getItemMeta().getLore().get(0).length())).trim();
                RankData data = RankData.getByName(playerName);

                if (!data.isLoaded()) {
                    data.load();
                }

                if(event.getRawSlot() == 9 && data != null && stack.getDurability() == 5) {
                    Grant activeGrant = data.getActiveGrant();
                    activeGrant.setActive(false);
                    data.save();
                    
                    Player players = Bukkit.getPlayer(data.getName());

                    Tasks.run(() -> {
                        data.load();

                        if(players == null) {
                            JsonObject object = new JsonObject();
                            object.addProperty("action", RankSubscriberAction.DELETE_GRANT.name());

                            JsonObject payload = new JsonObject();
                            payload.addProperty("name", data.getName());
                            object.add("payload", payload);

                            RankPublisher.write(object.toString());

                            data.save();
                        } else {
                            Rank rank = Rank.getDefaultRank();

                            if(rank != null) {
                                players.sendMessage(Color.translate("&eYour rank has been set to " + rank.getData().getColorPrefix() + rank.getData().getName() + "&e."));
                                data.setupAtatchment();
                            }
                        }
                    });
                    
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Grant successfully disabled.");
                    return;
                }
                
                if(displayName.contains("Next Page")) {
                    if(page + 1 > total) {
                        player.sendMessage(ChatColor.RED + "There are no more pages.");
                        return;
                    }
                    
                    player.openInventory(plugin.getRankManager().getGrantsInventory(data, playerName, page + 1));
                } else if(displayName.contains("Previous Page")) {
                    if(page == 1) {
                        player.sendMessage(ChatColor.RED + "You're on the first page.");
                        return;
                    }
                    player.openInventory(plugin.getRankManager().getGrantsInventory(data, playerName, page - 1));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        ItemStack stack = event.getCurrentItem();
        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if(procedure != null && stack != null && stack.getType() != Material.AIR) {
            event.setCancelled(true);

            if(GrantProcedure.getData().getStage() == GrantProcedureStage.RANK && inventory.getTitle().equals(GrantProcedure.getInventory().getTitle()) && stack.getItemMeta().hasDisplayName()) {
                Rank rank = Rank.getByName(ChatColor.stripColor(stack.getItemMeta().getDisplayName()));

                if(rank != null) {
                    GrantProcedure.getData().setRank(rank);
                    GrantProcedure.getData().setStage(GrantProcedureStage.DURATION);

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.GREEN + "Please enter a duration in chat (use 'perm' or 'permanent' for permanent ranks).");
                    player.sendMessage(" ");
                }

                player.closeInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if(procedure != null && (GrantProcedure.getData().getStage() == GrantProcedureStage.DURATION || GrantProcedure.getData().getStage() == GrantProcedureStage.REASON || GrantProcedure.getData().getStage() == GrantProcedureStage.CONFIRMATION)) {
            event.setCancelled(true);

            try {
                if(event.getMessage().equalsIgnoreCase("cancel")) {
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.RED + "Cancelling grant.");
                    player.sendMessage(" ");
                    GrantProcedure.getProcedures().remove(procedure);
                    return;
                }

                if(GrantProcedure.getData().getStage() == GrantProcedureStage.DURATION) {
                    if(event.getMessage().equalsIgnoreCase("permanent") || event.getMessage().equalsIgnoreCase("perm")) {
                        GrantProcedure.getData().setDuration(2147483647L);
                    } else {
                        GrantProcedure.getData().setDuration(System.currentTimeMillis() - DateUtil.parseDateDiff(event.getMessage(), false));
                    }

                    GrantProcedure.getData().setStage(GrantProcedureStage.REASON);

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "Duration successfully recorded.");
                    player.sendMessage(ChatColor.YELLOW + "Please enter a reason in chat.");
                    player.sendMessage(" ");
                } else {
                    GrantProcedure.getData().setReason(event.getMessage());
                    GrantProcedure.getData().setStage(GrantProcedureStage.CONFIRMATION);
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "Reason successfully recorded.");

                    ActionMessage actionMessage = new ActionMessage();

                    actionMessage.addText("&eWould you like to proceed with this grant?: ");
                    actionMessage.addText("&a&lYES ").setClickEvent(ActionMessage.ClickableType.RunCommand, "/grant confirm").addHoverText(Color.translate("&aConfirm this grant"));
                    actionMessage.addText("&c&lNO").setClickEvent(ActionMessage.ClickableType.RunCommand, "/grant cancel").addHoverText(Color.translate("&cCancel this grant"));
                    actionMessage.sendToPlayer(player);
                }
            } catch(Exception e) {
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "Please enter a valid duration or type 'cancel' to cancel.");
                player.sendMessage(" ");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        GrantProcedure procedure = GrantProcedure.getByPlayer(player);

        if(procedure != null) {
            GrantProcedureStage stage = GrantProcedure.getData().getStage();

            if(stage == GrantProcedureStage.RANK && inventory.getTitle().equals(GrantProcedure.getInventory().getTitle())) {
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "Grant procedure cancelled.");
                player.sendMessage(" ");

                GrantProcedure.getProcedures().remove(procedure);
            }
        }
    }
}
