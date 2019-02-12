package secondlife.network.paik.handlers;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.paik.Paik;
import secondlife.network.paik.PaikAPI;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.CustomLocation;
import secondlife.network.paik.utilties.Handler;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.paik.utilties.events.player.PlayerBanEvent;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class PlayerHandler extends Handler implements Listener {

    public PlayerHandler(Paik plugin) {
        super(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.plugin.getPlayerDataManager().addPlayerData(event.getPlayer());

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            PlayerConnection playerConnection = ((CraftPlayer)event.getPlayer()).getHandle().playerConnection;
            PacketPlayOutCustomPayload packetPlayOutCustomPayload = new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer("CB-Client".getBytes())));
            PacketPlayOutCustomPayload packetPlayOutCustomPayload2 = new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer("CC".getBytes())));

            playerConnection.sendPacket(packetPlayOutCustomPayload);
            playerConnection.sendPacket(packetPlayOutCustomPayload2);
        }, 10L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(this.plugin.getAlertsManager().hasAlertsToggled(event.getPlayer())) {
            this.plugin.getAlertsManager().toggleAlerts(event.getPlayer());
        }

        this.plugin.getPlayerDataManager().removePlayerData(event.getPlayer());
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if(playerData != null) {
            playerData.setSendingVape(true);
        }
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if(playerData != null) {
            playerData.setInventoryOpen(false);
        }
    }
    
    @EventHandler
    public void onPlayerAlert(PlayerAlertEvent event) {
        if(!this.plugin.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        if(player == null) return;

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if(playerData == null) return;

        if(Vituz.getInstance().getDatabaseManager().isDevMode()) {
            ActionMessage alertMessage = new ActionMessage();
            alertMessage.addText("&8(&bPaik&8) &c" + player.getName() + event.getAlert()).addHoverText(Color.translate("&bClick to teleport!")).setClickEvent(ActionMessage.ClickableType.RunCommand, "/tp " + player.getName());

            for(Player online : Bukkit.getOnlinePlayers()) {
                alertMessage.sendToPlayer(online);
            }
        }
    }
    
    @EventHandler
    public void onPlayerBan(PlayerBanEvent event) {
        if(!this.plugin.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        if(player == null) return;

        try {
            LogsHandler.log(player, event.getReason(), "WAS AUTOBANNED FOR", CustomLocation.getLocation(player), PaikAPI.getPing(player), new DecimalFormat("##.##").format(Bukkit.spigot().getTPS()[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {
            public void run() {
                if(Bukkit.isPrimaryThread()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "banip " + player.getName() + " [Paik] Unfair Advantage -s");
                }
            }
        }.runTask(this.getInstance());

        handleBan(player);

        /*if(DatabaseHandler.devMode) {
            Msg.sendMessage("&b&lPaik has banned &f" + player.getName() + " &bfor &f" + event.getReason());
        } else {
            handleBan(player);
        }*/
    }

    public static void handleBan(Player player) {
        PunishData data = PunishData.getByName(player.getName());

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("BanChannel");
            out.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(Paik.getInstance(), "AutoBan", b.toByteArray());
    }

    public static void handleAlert(Player player, String check, String location, int ping, double tps) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("AlertsChannel");
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
}
