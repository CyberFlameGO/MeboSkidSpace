package secondlife.network.paik.handlers;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import secondlife.network.paik.Paik;
import secondlife.network.paik.client.EnumClientType;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.Handler;
import secondlife.network.paik.utilties.events.ModListRetrieveEvent;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

import java.util.Map;
import java.util.StringJoiner;

public class ModListHandler extends Handler implements Listener {
    
    private static Map<String, String> BLACKLISTED_MODS = ImmutableMap.of("MouseTweaks",
            "Mouse Tweaks",
            "Particle Mod",
            "Particle Mod",
            "npcmod",
            "NPC Mod");

    public ModListHandler(Paik plugin) {
        super(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onModListRetrieve(ModListRetrieveEvent event) {
        Player player = event.getPlayer();

        if(player == null) return;

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if(playerData == null) return;

        Map<String, String> mods = event.getMods();
        EnumClientType type;

        if(mods.containsKey("gc")) {
            type = EnumClientType.HACKED_CLIENT_F;
        } else if("1.0".equals(mods.get("OpenComputers"))) {
            type = EnumClientType.HACKED_CLIENT_G;
        } else if(mods.containsKey("ethylene")) {
            type = EnumClientType.HACKED_CLIENT_H;
        } else if("1.7.6.git".equals(mods.get("Schematica"))) {
            type = EnumClientType.HACKED_CLIENT_I;
        } else if(mods.containsKey("Aimbot")) {
            type = EnumClientType.HACKED_CLIENT_J;
        } else if("1.0 ".equals(mods.get("timechanger"))) {
            type = EnumClientType.HACKED_CLIENT_E2;
        } else if("1.0".equals(mods.get("TcpNoDelayMod-2.0"))) {
            type = EnumClientType.HACKED_CLIENT_K;
        } else if(mods.containsKey("mergeclient")) {
            type = EnumClientType.HACKED_CLIENT_L;
        } else if(mods.containsKey("wigger")) {
            type = EnumClientType.HACKED_CLIENT_L2;
        } else {
            type = EnumClientType.FORGE;
            StringJoiner blacklisted = new StringJoiner(", ");
            boolean kick = false;

            for(String modId : ModListHandler.BLACKLISTED_MODS.keySet()) {
                if(mods.containsKey(modId)) {
                    blacklisted.add(ModListHandler.BLACKLISTED_MODS.get(modId));
                    kick = true;
                }
            }

            if(kick) {
                player.kickPlayer(ChatColor.RED + "Please contact an administrator.");
            }
        }

        playerData.setClient(type);
        playerData.setForgeMods(mods);

        if(type.isHacked()) {
            PlayerAlertEvent alertEvent = new PlayerAlertEvent(PlayerAlertEvent.AlertType.RELEASE, player, ChatColor.AQUA + "was caught using a " + ChatColor.RED + type.getName() + ".");
            this.plugin.getServer().getPluginManager().callEvent(alertEvent);
        }
    }
}
