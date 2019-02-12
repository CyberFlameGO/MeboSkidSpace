package secondlife.network.hcfactions.factions.handlers;

import com.google.common.base.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.utils.events.*;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.RegenStatus;
import secondlife.network.hcfactions.game.events.eotw.EOTWHandler;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.timers.SpawnTagHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class FactionHandler extends Handler implements Listener {
	
    private static String LAND_CHANGED_META_KEY = "landChangedMessage";
    private static long LAND_CHANGE_MSG_THRESHOLD = 225L;
	
	public FactionHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

    @EventHandler(ignoreCancelled = true, priority=EventPriority.MONITOR)
    public void onFactionRenameMonitor(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        
        if(!(faction instanceof KothFaction)) return;

        KothFaction kothFaction = (KothFaction) faction;

        if(kothFaction != null && kothFaction.getCaptureZone() != null) {
            kothFaction.getCaptureZone().setName(event.getNewName());
        }
    }

    @EventHandler(ignoreCancelled = true, priority=EventPriority.MONITOR)
    public void onFactionCreate(FactionCreateEvent event) {
        Faction faction = event.getFaction();
        Player player = (Player) event.getSender();
        
        if(!(faction instanceof PlayerFaction)) return;

        PlayerFaction playerFaction = (PlayerFaction)faction;

        Msg.sendMessage("&eFaction &9" + playerFaction.getName() + " &ehas been &acreated &eby &d" + player.getDisplayName());
    }

    @EventHandler(ignoreCancelled = true, priority=EventPriority.MONITOR)
    private void onPlayerClaimEnter(FactionPlayerClaimEnterEvent event) {
        Faction toFaction = event.getToFaction();

        Player player = event.getPlayer();

        if(toFaction.isSafezone()) {
            if(!SpawnTagHandler.isActive(player)) {
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setFireTicks(0);
                player.setSaturation(4.0F);
            } else {
                player.teleport(event.getFrom());
            }
        }

        if(getLastLandChangedMeta(player) <= 0L) {
            Faction fromFaction = event.getFromFaction();

            player.sendMessage(Color.translate("&eNow leaving: " + fromFaction.getDisplayName(player) + " &e(" + (fromFaction.isDeathban() ? "&cDeathban" : "&aNon-Deathban") + "&e)"));
            player.sendMessage(Color.translate("&eNow entering: " + toFaction.getDisplayName(player) + " &e(" + (toFaction.isDeathban() ? "&cDeathban" : "&aNon-Deathban") + "&e)"));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreFactionJoin(FactionPlayerJoinEvent event) {
        Faction faction = event.getFaction();
        Optional<Player> optionalPlayer = event.getPlayer();
        
        if(faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            PlayerFaction playerFaction = (PlayerFaction) faction;

            if(!HCFConfiguration.kitMap && !EOTWHandler.isEOTW() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
                event.setCancelled(true);
                player.sendMessage(Color.translate("&cYou can't join factions that are not regenerating DTR!"));
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(FactionPlayerLeaveEvent event) {
        if(event.isForce() || event.isKick()) return;

        Faction faction = event.getFaction();
        
        if(!(faction instanceof PlayerFaction)) return;
		Optional<Player> optional = event.getPlayer();

		if(!optional.isPresent()) return;
		
		Player player = optional.get();
		
		if(RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation()) != faction) return;
		
		event.setCancelled(true);
		player.sendMessage(Color.translate("&cYou can leave this faction when you leave their claim."));
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
        
        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
        } else {
			playerFaction.printDetails(player);

            playerFaction.broadcast(Color.translate("&a&lMember Online&7: &d" + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + "&7!"), player.getUniqueId());
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
        
        if(playerFaction != null) {
            playerFaction.broadcast("&c&lMember Offline&7: &d" + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + "&7!");
        }
    }

    private long getLastLandChangedMeta(Player player) {
        MetadataValue value = player.getMetadata("landChangedMessage").iterator().hasNext() ? player.getMetadata("landChangedMessage").iterator().next() : null;

        long millis = System.currentTimeMillis();
        long remaining = value == null ? 0 : value.asLong() - millis;

        if(remaining <= 0) {
            player.setMetadata("landChangedMessage", new FixedMetadataValue(HCF.getInstance(), (millis + 225)));
        }

        return remaining;
    }
}
