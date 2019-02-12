package secondlife.network.paik.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.paik.Paik;
import secondlife.network.paik.checks.movement.Speed;
import secondlife.network.paik.checks.movement.fly.FlyA;
import secondlife.network.paik.checks.movement.fly.FlyB;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;
import secondlife.network.paik.handlers.events.PlayerMoveByBlockEvent;
import secondlife.network.paik.utils.*;
import secondlife.network.paik.utils.file.ConfigFile;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class CheatHandler extends Handler implements Listener {

    public static HashMap<UUID, Long> ignore;
    public static HashMap<UUID, Long> ignoreJump;
    public static File logsDirecotry;
    public static DecimalFormat dc;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
    public static Date date = new Date();

    // TEST KURAC 123

    public CheatHandler(Paik plugin) {
        super(plugin);

        ignore = new HashMap<UUID, Long>();
        ignoreJump = new HashMap<UUID, Long>();
        logsDirecotry = new File(this.getInstance().getDataFolder(), "logs");
        dc = new DecimalFormat("##.##");

        refresh();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCheat(PlayerCheatEvent event) {
        if (!ConfigFile.configuration.getBoolean("enabled")) return;

        if (ConfigFile.configuration.getBoolean("test-mode")) {
            Message.sendMessage(Color.translate("&c" + event.getPlayer().getName() + "&4 " + event.getCheck()));
            event.setCancelled(true);
        }

        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (AlertsHandler.isActive(player, event.getCheck())) return;

        AlertsHandler.applyCooldown(player, event.getCheck());

        if ( event.getCheck().equalsIgnoreCase("PingSpoof")
                || event.getCheck().equalsIgnoreCase("AutoPotion")
                || event.getCheck().equalsIgnoreCase("DoubleClick")
                || event.getCheck().toLowerCase().startsWith("autoblock")
                || event.getCheck().toLowerCase().startsWith("timer")
                || event.getCheck().toLowerCase().startsWith("refill")
                || event.getCheck().toLowerCase().startsWith("inventory")) {
            handleAlertNoVL(player, event.getCheck() + " (Experimental)", event.getLocation(), event.getPing(), event.getTps());
            return;
        }

        try {
            log(player, "", event.getCheck(), event.getLocation(), event.getPing(), dc.format(event.getTps()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (event.getCheck().equalsIgnoreCase("VClip")
                || event.getCheck().equalsIgnoreCase("Phase")
                || event.getCheck().equalsIgnoreCase("CrackedClient")
                || event.getCheck().equalsIgnoreCase("ImpossiblePitch")
                || event.getCheck().toLowerCase().startsWith("autoclicker")) {
            handleAlertNoVL(player, event.getCheck(), event.getLocation(), event.getPing(), event.getTps());
            return;
        }

        PlayerStats stats = PlayerStatsHandler.getStats(player);

        stats.setVl(stats.getVl() + 1);

        handleAlert(player, event.getCheck(), event.getLocation(), stats.getVl(), event.getPing(), event.getTps());

        if (!ConfigFile.configuration.getBoolean("autobans") || player.hasPermission("secondlife.staff")) return;

        if (event.getCheck().equalsIgnoreCase("Killaura (Invalid Swing)")) {
            handleBan(player);
            return;
        }

        if (stats.getVl() > 4) {
            try {
                log(player, event.getCheck(), "WAS AUTOBANNED FOR", LocationUtils.getLocation(player), player.getPing(), new DecimalFormat("##.##").format(Bukkit.spigot().getTPS()[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (player.getName().equalsIgnoreCase("Zidovcina")) return;

            handleBan(player);
        }
    }

    public static void handleBan(Player player) {
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        if (stats.isBanned()) return;

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("BanChannel");
            out.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(Paik.getInstance(), "AutoBan", b.toByteArray());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "banip " + player.getName() + "[Paik] Unfair Advantage -s");

        stats.setBanned(true);
    }

    public static void handleAlert(Player player, String check, String location, int vl, int ping, double tps) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("AlertsChannel");
            out.writeUTF(player.getName());
            out.writeUTF(check);
            out.writeUTF(location);
            out.writeInt(vl);
            out.writeInt(ping);
            out.writeDouble(tps);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(Paik.getInstance(), "Alerts", b.toByteArray());
    }

    public static void handleAlertNoVL(Player player, String check, String location, int ping, double tps) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("AlertsNoVLChannel");
            out.writeUTF(player.getName());
            out.writeUTF(check);
            out.writeUTF(location);
            out.writeInt(ping);
            out.writeDouble(tps);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(Paik.getInstance(), "Alerts", b.toByteArray());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        PlayerStats stats = PlayerStatsHandler.getStats(player);

        if (stats == null) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        double horizontal = Math.sqrt(Math.pow(to.getX() - from.getX(), 2.0D) + Math.pow(to.getZ() - from.getZ(), 2.0D));

        stats.setDelta(horizontal);

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

        Bukkit.getPluginManager().callEvent(new PlayerMoveByBlockEvent(player, to, from));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (AlertsHandler.delays.containsKey(player.getUniqueId())) {
            AlertsHandler.delays.remove(player.getUniqueId());
        }

        if (FlyA.onGround.containsKey(player.getUniqueId())) {
            FlyA.onGround.remove(player.getUniqueId());
        }

        if (FlyA.upTicks.containsKey(player.getUniqueId())) {
            FlyA.upTicks.remove(player.getUniqueId());
        }

        if (ignoreJump.containsKey(player.getUniqueId())) {
            ignoreJump.remove(player.getUniqueId());
        }

        if (ignore.containsKey(player.getUniqueId())) {
            ignore.remove(player.getUniqueId());
        }

        // lOGGING

        PlayerStats stats = PlayerStatsHandler.getStats(player);

        if(stats != null && !stats.getLogs().isEmpty()) {
            File log = new File(logsDirecotry, player.getName() + ".txt");

            if(!log.exists()) {
                try {
                    log.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileWriter fw = new FileWriter(log, false);
                BufferedWriter bw = new BufferedWriter(fw);

                for(String string : stats.getLogs()) {
                    bw.write(string);
                    bw.newLine();
                }

                bw.close();
                fw.close();
                stats.getLogs().clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerStats stats = PlayerStatsHandler.getStats(event.getPlayer());

        stats.setLastBlockBreak(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.clearList();

        Player player = event.getPlayer();

        PlayerStats stats = PlayerStatsHandler.getStats(player);

        stats.setJoined(System.currentTimeMillis());

        this.ignore(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.clearList();
        this.ignore(event.getPlayer());
    }

    @EventHandler
    public void onPlayerWorldChanged(PlayerChangedWorldEvent event) {
        this.clearList();

        Player player = event.getPlayer();

        this.ignore(player);

        player.setSneaking(false);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.clearList();

        Player player = event.getPlayer();

        this.ignore(player);

        PlayerStats stats = PlayerStatsHandler.getStats(player);

        stats.setLastBlockPlace(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        this.clearList();

        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        this.ignore(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        this.clearList();

        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        this.ignore(player);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        this.clearList();
        this.ignore(event.getPlayer());
    }

    @EventHandler
    public void onPlayerStatisticIncreamentEvent(PlayerStatisticIncrementEvent event) {
        this.clearList();

        if (event.getStatistic() != Statistic.JUMP) return;

        Player player = event.getPlayer();

        Speed.removeJumpOne(player);
        ignoreJump.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
    }

    public void clearList() {
        if (ServerUtils.isServerLagging()) {
            if (!ignore.isEmpty()) {
                ignore.clear();
            }
            if (!ignoreJump.isEmpty()) {
                ignoreJump.clear();
            }
            return;
        }
    }

    public void ignore(Player player) {
        Speed.removeOne(player);
        FlyA.removeOne(player);
        FlyB.removeOne(player);
        ignore.put(player.getUniqueId(), System.currentTimeMillis() + 3000);
    }

    public static void log(Player player, String message, String check, String location, int ping, String tps) throws IOException {
        PlayerStats stats = PlayerStatsHandler.getStats(player);

        if(stats == null) return;

        String line = sdf.format(date) + " " + player.getName() + " " + check.toUpperCase() + " " + message + " LOCATION: " + location + " PING: " + ping + " TPS: " + tps;

        stats.getLogs().add(line);
    }

    public static void logPayload(Player player, String message) throws IOException {
        File log = new File(Paik.getInstance().getDataFolder(), "payload.txt");

        if (!log.exists()) {
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
        try {
            bw.write(sdf.format(date) + " " + player.getName() + ": " + message);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bw.flush();
            bw.close();
        }
    }

    public static void clear() {
        AlertsHandler.delays.clear();

        CommandHandler.commands.clear();

        FlyA.onGround.clear();
        FlyA.upTicks.clear();
        ignoreJump.clear();
        ignore.clear();
    }

    public static void clearNormal() {
        AlertsHandler.delays.clear();

        FlyA.onGround.clear();
        FlyA.upTicks.clear();
    }

    public static void refresh() {
        new BukkitRunnable() {
            public void run() {
                clearNormal();
            }
        }.runTaskTimerAsynchronously(Paik.getInstance(), 3600L, 3600L);
    }
}
