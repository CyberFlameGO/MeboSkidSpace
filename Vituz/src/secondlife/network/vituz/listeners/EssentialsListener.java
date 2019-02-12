package secondlife.network.vituz.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerShutdownEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.events.PlayerMessageEvent;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.providers.scoreboard.VituzScoreboard;
import secondlife.network.vituz.providers.tab.TabLayout;
import secondlife.network.vituz.providers.tab.VituzTab;
import secondlife.network.vituz.utilties.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EssentialsListener implements Listener {

	private Vituz plugin = Vituz.getInstance();

	public static ArrayList<UUID> commandCooldown = new ArrayList<>();

	private String[] message = {
			"&7&m-------------------------------------",
			"  &dWelcome&f %PLAYER% &dto the &5&lSecondLife Network &f" + VituzAPI.getServerName() + "&d!",
			"",
			" &5&l" + Msg.KRUZIC + " &dForum: &fforum.secondlife.network",
			" &5&l" + Msg.KRUZIC + " &dStore: &fstore.secondlife.network",
		    " &5&l" + Msg.KRUZIC + " &dTeamspeak: &fts.secondlife.network",
			" &5&l" + Msg.KRUZIC + " &dFacebook: &fwww.facebook.com/SecondLifeNetwork",
			" &5&l" + Msg.KRUZIC + " &dDiscord: &fdiscord.gg/tN8Tugp",
			" &5&l" + Msg.KRUZIC + " &dRules: &fgoo.gl/S8wjRc",
			"&7&m-------------------------------------"
	};

	private Material[] materials = {
			Material.GOLDEN_APPLE, Material.COOKED_BEEF, Material.RAW_BEEF,
			Material.COOKED_CHICKEN, Material.RAW_CHICKEN, Material.BAKED_POTATO,
			Material.GOLDEN_CARROT, Material.PORK, Material.GRILLED_PORK,
			Material.PUMPKIN_PIE, Material.POTION, Material.DIAMOND_SWORD,
			Material.GOLD_SWORD, Material.IRON_SWORD, Material.STONE_SWORD,
			Material.WOOD_SWORD
	};

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
		PlayerData playerData = PlayerData.getByName(player.getName());

		if(!playerData.getPrefix().equals("")) {
			player.setDisplayName(Color.translate("&7[" + playerData.getPrefix() + "&7]" + VituzAPI.getPrefix(player) + playerData.getColor() + player.getName() + VituzAPI.getSuffix(player)));
		} else {
			player.setDisplayName(Color.translate(VituzAPI.getPrefix(player) + playerData.getColor() + player.getName() + VituzAPI.getSuffix(player)));
		}

        if(!player.hasPermission(Permission.STAFF_PERMISSION) && plugin.getChatControlManager().isFiltered(player, PlayerData.getByName(player.getName()), event.getMessage())) {
            event.setCancelled(true);
            return;
        }

        Iterator<Player> iterator = event.getRecipients().iterator();

        while(iterator.hasNext()) {
            Player target = iterator.next();
            PlayerData data = PlayerData.getByName(target.getName());

            if(data.getIgnoring().contains(player.getName())) {
                iterator.remove();
            } else {
                if(data.isToggleChat() || player.hasPermission(Permission.STAFF_PERMISSION)) continue;

                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerPreMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player player = event.getRecipient();

        PlayerData sData = PlayerData.getByName(sender.getName());
        PlayerData pData = PlayerData.getByName(player.getName());

        if(sender.hasPermission(Permission.STAFF_PERMISSION)) {
            if(!sData.isToggleMsg()) {
                event.setCancelled(true);

                sender.sendMessage(Color.translate("&cYou have private messages toggled."));
                return;
            }
        } else {
            if(!pData.isToggleMsg() || pData.getIgnoring().contains(sender.getName())) {
                event.setCancelled(true);

                sender.sendMessage(Color.translate(player.getDisplayName() + " &chas private messaging toggled."));
                return;
            }
        }

        if(pData.isSounds()) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1.0f, 1.0f);
        }
    }

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();

        Tasks.runLater(() ->
            VituzTab.addPlayer(player)
        , 1L);
        VituzScoreboard.create(player);

        if(VituzNametag.isInitiated()) {
            player.setMetadata("sl-LoggedIn", new FixedMetadataValue(Vituz.getInstance(), true));
            VituzNametag.initiatePlayer(player);
            VituzNametag.reloadPlayer(player);
            VituzNametag.reloadOthersFor(player);
        }

		Stream.of(message).forEach(message ->
			player.sendMessage(Color.translate(message.replace("%PLAYER%", player.getName())))
		);

		PlayerData data = PlayerData.getByName(player.getName());
		String worldTime = data.getWorldTime();

		switch(worldTime) {
			case "DAY": player.setPlayerTime(0L, false);
			case "NIGHT": player.setPlayerTime(14000L, false);
			case "DEFAULT": player.resetPlayerTime();
			default: player.resetPlayerTime();
		}

		IntStream.range(0, data.getNotes().size()).forEach(i -> {
			Msg.sendMessage("&7&m------------------------------------------", Permission.STAFF_PERMISSION);
			Msg.sendMessage("&eNotes of &d" + player.getName() + "&e.", Permission.STAFF_PERMISSION);
			Msg.sendMessage("&7" + (i + 1) + ") &d" + data.getNotes().get(i), Permission.STAFF_PERMISSION);
			Msg.sendMessage("&7&m------------------------------------------", Permission.STAFF_PERMISSION);
		});

		Tasks.runLater(() -> {
			if(player.hasPermission(Permission.STAFF_PERMISSION)) {
				ServerUtils.sendPermissionToBungee(player, Permission.STAFF_PERMISSION);
			}

			if(player.hasPermission(Permission.OP_PERMISSION)) {
				ServerUtils.sendPermissionToBungee(player, Permission.OP_PERMISSION);
			}
		}, 20);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Player player = event.getPlayer();

		plugin.getEssentialsManager().getLastReplied().remove(player.getUniqueId());

        VituzTab.removePlayer(player);
        TabLayout.remove(player);
        VituzScoreboard.remove(player);
        player.removeMetadata("sl-LoggedIn", Vituz.getInstance());
        VituzNametag.getTeamMap().remove(player.getName());
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			PlayerData.getByName(event.getPlayer().getName()).setBackLocation(StringUtils.stringifyLocation(event.getTo()));
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		PlayerData.getByName(event.getEntity().getName()).setBackLocation(StringUtils.stringifyLocation(event.getEntity().getLocation()));
	}

	@EventHandler
    public void onServerShutdown(ServerShutdownEvent event) {
	    if(plugin.getDatabaseManager().isDevMode()) return;

		ServerUtils.bungeeBroadcast("&7[&4&lServer Manager&7] &f" + VituzAPI.getServerName() + " &cjust went &4&lOffline&c.", Permission.STAFF_PERMISSION);

        if(VituzAPI.getServerName().equalsIgnoreCase("Hub")) return;

        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerUtils.sendToServer(player, "Hub");
            player.sendMessage(Color.translate("&eYou have been sent to hub due to server restart."));
        });
    }

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();

		if(item == null || item.getType() == Material.AIR) return;

		Block block = event.getClickedBlock();

		if(block.getType() == Material.FENCE || block.getType() == Material.NETHER_FENCE || block.getType() == Material.CAULDRON) {
			Stream.of(materials).forEach(material -> {
				if(item.getType() == material) event.setCancelled(true);
			});
		}
	}

	@EventHandler
	public void onPlayerCommandPreproccess(PlayerCommandPreprocessEvent event) {
		if(Stream.of("/worldedit:/calc", "/worldedit:/eval", "/worldedit:/solve", "//calc", "//eval", "//solve").
                noneMatch(event.getMessage().toLowerCase()::startsWith)) return;

		event.setCancelled(true);
        event.getPlayer().sendMessage(Color.translate("&cAre you trying to get banned?"));
        Msg.sendMessage("&4&l" + event.getPlayer().getName() + " &ctried to crash the server &7(&d" + event.getMessage() +"&7)", Permission.STAFF_PERMISSION);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
	    if(event.isCancelled()) return;

		Player player = event.getPlayer();

		if(!player.hasPermission(Permission.OP_PERMISSION)) return;

        String[] lines = event.getLines();

        IntStream.range(0, lines.length).forEach(i ->
            event.setLine(i, Color.translate(lines[i]))
        );
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if(!event.toWeatherState()) return;
		
		event.setCancelled(true);
		event.getWorld().setWeatherDuration(0);
		event.getWorld().setThundering(false);
	}

	@EventHandler
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if(!(event.getEntity() instanceof FishHook)) return;

		event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(1.025));
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getEntity().getType() == EntityType.GUARDIAN || event.getEntity().getType() == EntityType.RABBIT) {
			event.setCancelled(true);
		}
	}

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled() || event.getMessage().startsWith("//")) return;

        String[] args = event.getMessage().split(" ");
        if (args[0].contains(":")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Color.translate("&cYou can't do this!"));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();

        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        if(event.getClickedInventory().getTitle().contains(Color.translate("&eInvsee of "))) {
            event.setCancelled(true);

            if(item.getItemMeta().getDisplayName().contains(Color.translate("Close"))) {
                event.getWhoClicked().closeInventory();
            }
        }
    }
}
