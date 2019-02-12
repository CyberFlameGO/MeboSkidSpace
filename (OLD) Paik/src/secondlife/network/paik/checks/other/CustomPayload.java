package secondlife.network.paik.checks.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.secondlife.PlayerCheatEvent;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.CheatHandler;
import secondlife.network.paik.utils.LocationUtils;
import secondlife.network.paik.utils.file.ConfigFile;

import java.io.IOException;

public class CustomPayload {

    public static void handleCustomPayload(Player player, PlayerStats stats, String message) {
        if(!ConfigFile.configuration.getBoolean("enabled")) return;
        if(!ConfigFile.configuration.getBoolean("checks.custompayload")) return;

        if(message.equalsIgnoreCase("LOLIMAHCKER")
                || message.equalsIgnoreCase("mincraftpvphcker")
                || message.equalsIgnoreCase("cock")
                || message.equalsIgnoreCase("0SO1Lk2KASxzsd")
                || message.equalsIgnoreCase("MCnetHandler")
                || message.equalsIgnoreCase("customGuiOpenBspkrs")
                || message.equalsIgnoreCase("n") // "PRIVATE" client lmao
                || message.equalsIgnoreCase("lmaohax")
                || message.equalsIgnoreCase("0SSxzsd")) {

            Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(player, LocationUtils.getLocation(player), "CrackedClient", player.getPing(), Bukkit.spigot().getTPS()[0]));
            CheatHandler.handleBan(player);
            return;
        }

        if(message.equalsIgnoreCase("OCMC")) {
            stats.setOcmc(true);
        }

        if(message.startsWith("MC|")
                || message.equals("REGISTER")
                || message.equals("WECUI")
                || message.equals("skinport")
                || message.equals("WDL|INIT")
                || message.equals("MorePlayerModels")
                || message.equals("advancedcapes")
                || message.equals("PERMISSIONSREPL")
                || message.equals("world_info")
                || message.equals("OCMC"))
            return;

        try {
            CheatHandler.logPayload(player, String.valueOf(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
