package secondlife.network.uhc.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.state.GameState;
import secondlife.network.uhc.utilties.Manager;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 07.07.2018.
 */

@Getter
@Setter
public class GameManager extends Manager {

    @Getter
    @Setter
    private static GameState gameState = GameState.LOBBY;

    private List<UUID> saveUsers = new ArrayList<>();
    private List<UUID> invTask = new ArrayList<>();
    private List<UUID> oreAlerts = new ArrayList<>();

    private String winner = "";
    private String nextuhc = "Unknown";

    private int shearsRate = 1;
    private int appleRate = 2;
    private int initial;

    private boolean pvp = false;
    private boolean mapGenerating = false;
    private boolean worldUsed = false;
    private boolean generated = false;
    private boolean restarted = false;
    private boolean world = false;
    private boolean borderTime = false;
    private boolean borderShrink = true;
    private boolean stats = true;
    private boolean noScenarios = true;

    public GameManager(UHC plugin) {
        super(plugin);

        Date now = new Date();
        int hours = now.getHours();

        // UHC-1 12 16 20 24

        // UHC-2 14 18 22

        if(VituzAPI.getServerName().equals("UHC-1")) {
            if(hours >= 21) {
                nextuhc = "00:00";
            } else if(hours >= 17) {
                nextuhc = "20:00";
            } else if(hours >= 13) {
                nextuhc = "16:00";
            } else {
                nextuhc = "12:00";
            }
        } else if(VituzAPI.getServerName().equals("UHC-2")) {
            if(hours >= 19) {
                nextuhc = "22:00";
            } else if(hours >= 15) {
                nextuhc = "18:00";
            } else {
                nextuhc = "14:00";
            }
        }
    }

    public void handleOnDisable() {
        saveUsers.clear();
        invTask.clear();
        oreAlerts.clear();
    }

    public void handleOreAlerts(Player player) {
        if(oreAlerts.contains(player.getUniqueId())) {
            oreAlerts.remove(player.getUniqueId());

            player.sendMessage(Color.translate("&eYou have &cdisabled&e xray alerts."));
        } else {
            oreAlerts.add(player.getUniqueId());

            player.sendMessage(Color.translate("&eYou have &aenabled&e xray alerts."));
        }
    }

    public void handleRemoveSaveUser(Player player) {
        if(saveUsers.contains(player.getUniqueId())) {
            saveUsers.remove(player.getUniqueId());
        }
    }

    public void setPvP(boolean bol) {
        if(Bukkit.getWorld("uhc_world") != null) {
            pvp = bol;

            Bukkit.getWorld("uhc_world").setPVP(bol);

            if(Bukkit.getWorld("uhc_nether") != null) {
                Bukkit.getWorld("uhc_nether").setPVP(bol);
            }

            if(bol) {
                Msg.sendMessage("&ePvP is now &aEnabled&e.");

                if(Vituz.getInstance().getChatControlManager().isMuted()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chat mute");
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chat delay 10");
            } else {
                Msg.sendMessage("&ePvP is now &cDisabled&e.");
            }
        }
    }

    public class InventoryTask extends BukkitRunnable {

        public InventoryTask() {
            runTaskTimerAsynchronously(UHC.getInstance(), 1L, 1L);
        }

        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(invTask.contains(player.getUniqueId()) && !player.getOpenInventory().getTitle().equals(Color.translate("&eScatter Inventory"))) {
                    player.openInventory(InventoryManager.scatter);
                }
            });
        }
    }
}
