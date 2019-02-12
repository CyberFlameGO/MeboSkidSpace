package secondlife.network.hcfactions.timers;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.games.EventFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.factions.utils.events.capzone.CaptureZoneEnterEvent;
import secondlife.network.hcfactions.factions.utils.events.capzone.CaptureZoneLeaveEvent;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.handlers.EventSignHandler;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class GameHandler extends Handler implements Listener {

	public static long RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
    public static long startStamp;
    public static long lastContestedEventMillis; 
    public static EventFaction eventFaction;
    @Getter public static GameHandler gameHandler;

    private List<KothFaction> activeKoths = new ArrayList<>();

    public static EventFaction getEventFaction() {
        return eventFaction;
    }

	public GameHandler(HCF plugin) {
		super(plugin);

        gameHandler = this;
		
		new BukkitRunnable() {
			public void run() {
				if(eventFaction != null) {
					eventFaction.getGameType().getEventType().tick(GameHandler.this, eventFaction);
					return;
				}
			}
		}.runTaskTimerAsynchronously(plugin, 20L, 20L);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}	

    public static void stopCooldown() {
        if(eventFaction != null) {
            for(CaptureZone captureZone : eventFaction.getCaptureZones()) {
                captureZone.setCappingPlayer(null);
            }

            eventFaction.setDeathban(true);
            eventFaction.getGameType().getEventType().stopTiming();
            eventFaction = null;
            startStamp = -1L;
        }

    }

    public static long getRemaining() {
        if (eventFaction == null) {
            return 0L;
        } else if (eventFaction instanceof KothFaction) {
            return ((KothFaction) eventFaction).getCaptureZone().getRemainingCaptureMillis();
        } else {
            return getRemaining();
        }
    }

    public void handleWinner(Player winner) {
        if(eventFaction == null) return;

        PlayerFaction faction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(winner);

        if(eventFaction.getGameType().getDisplayName().equalsIgnoreCase("koth")) {
            Msg.sendMessage("&8&m---------------------------------");
            Msg.sendMessage("&8\u2588&e\u2588\u2588\u2588\u2588\u2588\u2588\u2588&8\u2588");
            Msg.sendMessage("&e\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            Msg.sendMessage("&e\u2588&6\u2588&e\u2588&6\u2588&e\u2588&6\u2588&e\u2588&6\u2588&e\u2588");
            Msg.sendMessage("&e\u2588&6\u2588\u2588\u2588\u2588\u2588\u2588\u2588&e\u2588             &a&l" + eventFaction.getName());
            Msg.sendMessage("&e\u2588&6\u2588&b\u2588&6\u2588&b\u2588&6\u2588&b\u2588&6\u2588&e\u2588 &7was captured by &6" + winner.getName());
            Msg.sendMessage("&e\u2588&6\u2588\u2588\u2588\u2588\u2588\u2588\u2588&e\u2588 &7after &a" + DurationFormatUtils.formatDurationWords(getUptime(), true, true));
            Msg.sendMessage("&e\u2588\u2588\u2588&7\u2588\u2588\u2588&e\u2588\u2588\u2588");
            Msg.sendMessage("&e\u2588\u2588\u2588\u2588&7\u2588&e\u2588\u2588\u2588\u2588");
            Msg.sendMessage("&8\u2588&e\u2588\u2588\u2588&7\u2588&e\u2588\u2588\u2588&8\u2588");
            Msg.sendMessage("&8&m---------------------------------");

        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key KoTH 1 " + winner.getName());

            if(HCFConfiguration.kitMap) {
        		HCFData data = HCFData.getByName(winner.getName());
        		
        		data.setBalance(data.getBalance() + 3000);        		
        	}
        } else if(eventFaction.getGameType().getDisplayName().equalsIgnoreCase("conquest")) {
            Msg.sendMessage("&8&m---------------------------------");
            Msg.sendMessage("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            Msg.sendMessage("&7\u2588\u2588\u2588&a\u2588&7\u2588\u2588\u2588");
            Msg.sendMessage("&7\u2588\u2588&a\u2588&7\u2588&a\u2588&7\u2588\u2588 &2[Conquest]");
            Msg.sendMessage("&7\u2588\u2588&a\u2588&7\u2588&a\u2588&7\u2588\u2588 &econtrolled by");
            Msg.sendMessage("&7\u2588&a\u2588\u2588\u2588\u2588\u2588&7\u2588 &2" + winner.getName());
            Msg.sendMessage("&7\u2588&a\u2588&7\u2588\u2588\u2588&a\u2588&7\u2588");
            Msg.sendMessage("&7\u2588&a\u2588&7\u2588\u2588\u2588&a\u2588&7\u2588");
            Msg.sendMessage("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            Msg.sendMessage("&8&m---------------------------------");

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key Conquest 10 " + winner.getName());
        } else if(eventFaction.getGameType().getDisplayName().equalsIgnoreCase("citadel")) {
            Msg.sendMessage("&8&m---------------------------------");
            Msg.sendMessage("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            Msg.sendMessage("&7\u2588\u2588&5\u2588\u2588\u2588\u2588&7\u2588");
            Msg.sendMessage("&7\u2588&5\u2588&7\u2588\u2588\u2588\u2588\u2588");
            Msg.sendMessage("&7\u2588&5\u2588&7\u2588\u2588\u2588\u2588\u2588 &6[Citadel]");
            Msg.sendMessage("&7\u2588&5\u2588&7\u2588\u2588\u2588\u2588\u2588 &econtrolled by");
            Msg.sendMessage("&7\u2588&5\u2588&7\u2588\u2588\u2588\u2588\u2588 &6" + winner.getName());
            Msg.sendMessage("&7\u2588\u2588&5\u2588\u2588\u2588\u2588&7\u2588");
            Msg.sendMessage("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            Msg.sendMessage("&8&m---------------------------------");
        }

        if(eventFaction instanceof KothFaction) {
        	if(faction == null) return;

            faction.setKothCaptures(faction.getKothCaptures() + 1);

            faction.setPoints(faction.getPoints() + 10);
            faction.broadcast("&eYour faction has gotten &d10 points&e because &d" + winner.getName() + " &e captured &d" + eventFaction.getName());
        }
        
        World world = winner.getWorld();
        
		if(winner.getInventory().firstEmpty() == -1) { 
			world.dropItemNaturally(winner.getLocation(), EventSignHandler.getEventSign(eventFaction.getName(), winner.getName()));
		} else {
			winner.getInventory().addItem(new ItemStack(EventSignHandler.getEventSign(eventFaction.getName(), winner.getName())));
		}

		stopCooldown();
    }

    public boolean tryContesting(EventFaction eventFaction, CommandSender sender) {
        if(eventFaction instanceof KothFaction) {
            KothFaction kothFaction = (KothFaction) eventFaction;
           
            if(kothFaction.getCaptureZone() == null) {
                sender.sendMessage(Color.translate("&cFailed to schedule &l" + eventFaction.getName() + " &cas it's capture zone is not set!"));
                return false;
            }
        }

        long millis = System.currentTimeMillis();

        lastContestedEventMillis = millis;
        startStamp = millis;
        GameHandler.eventFaction = eventFaction;

        eventFaction.getGameType().getEventType().onContest(eventFaction, this);

        Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
       
        for(CaptureZone captureZone : captureZones) {
            if(captureZone.isActive()) {
                Player player = Iterables.getFirst(captureZone.getCuboid().getPlayers(), null);
               
                if(player != null && eventFaction.getGameType().getEventType().onControlTake(player, captureZone)) {
                    captureZone.setCappingPlayer(player);
                }
            }
        }

        eventFaction.setDeathban(false); 
        return true;
    }

    public long getUptime() {
        return System.currentTimeMillis() - startStamp;
    }

    private void handleDisconnect(Player player) {
        if(eventFaction == null) return;
        
        Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
        for(CaptureZone captureZone : captureZones) {
            if(Objects.equal(captureZone.getCappingPlayer(), player)) {
                captureZone.setCappingPlayer(null);
                eventFaction.getGameType().getEventType().onControlLoss(player, captureZone, eventFaction);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.handleDisconnect(event.getEntity());
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
        if(eventFaction == null) return;

        CaptureZone captureZone = event.getCaptureZone();
        
        if(!eventFaction.getCaptureZones().contains(captureZone)) return;

        Player player = event.getPlayer();
        
        if(captureZone.getCappingPlayer() == null && eventFaction.getGameType().getEventType().onControlTake(player, captureZone)) {
            captureZone.setCappingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
        if(Objects.equal(event.getFaction(), eventFaction)) {
            Player player = event.getPlayer();
            CaptureZone captureZone = event.getCaptureZone();
            
            if(Objects.equal(player, captureZone.getCappingPlayer())) {
                captureZone.setCappingPlayer(null);
                eventFaction.getGameType().getEventType().onControlLoss(player, captureZone, eventFaction);

                for(Player target : captureZone.getCuboid().getPlayers()) {
                    if(target != null && !target.equals(player) && eventFaction.getGameType().getEventType().onControlTake(target, captureZone)) {
                        captureZone.setCappingPlayer(target);
                        break;
                    }
                }
            }
        }
    }
}