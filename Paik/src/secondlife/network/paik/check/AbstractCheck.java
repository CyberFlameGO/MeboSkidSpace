package secondlife.network.paik.check;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.PaikAPI;
import secondlife.network.paik.handlers.LogsHandler;
import secondlife.network.paik.handlers.PlayerHandler;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.CustomLocation;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.paik.utilties.events.player.PlayerBanEvent;
import secondlife.network.vituz.data.RankData;

import java.io.IOException;

@Getter
public abstract class AbstractCheck<T> implements ICheck<T> {

    protected Paik plugin;
    protected PlayerData playerData;

    private Class<T> clazz;
    private String name;

    public AbstractCheck(Paik plugin, PlayerData playerData, Class<T> clazz, String name) {
        this.plugin = plugin;
        this.playerData = playerData;
        this.clazz = clazz;
        this.name = name;
    }
    
    protected boolean alert(PlayerAlertEvent.AlertType alertType, Player player, String message, boolean violation) {
        RankData profile = RankData.getByName(player.getName());

        String check = this.name + ((alertType != PlayerAlertEvent.AlertType.RELEASE) ? (" (" + Character.toUpperCase(alertType.name().toLowerCase().charAt(0)) + alertType.name().toLowerCase().substring(1) + ")") : "") + ". ";
      
        PlayerAlertEvent event = new PlayerAlertEvent(alertType, player, ChatColor.AQUA + " was caught using " + ChatColor.RED + check);
        this.plugin.getServer().getPluginManager().callEvent(event);

        try {
            LogsHandler.log(player, "", name, CustomLocation.getLocation(player), PaikAPI.getPing(player), LogsHandler.dc.format(Bukkit.spigot().getTPS()[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PlayerHandler.handleAlert(player, name, CustomLocation.getLocation(player), PaikAPI.getPing(player), Bukkit.spigot().getTPS()[0]);
       
        if(!event.isCancelled()) {
            if(violation) this.playerData.addViolation(this);
            
            return true;
        }
        
        return false;
    }
    
    protected void ban(Player player) {
        RankData profile = RankData.getByName(player.getName());

        if(profile != null) {
            if (profile.getActiveGrant().getRank().getData().getName().equalsIgnoreCase("Partner") || profile.getActiveGrant().getRank().getData().getName().equalsIgnoreCase("Developer") || profile.getActiveGrant().getRank().getData().getName().equalsIgnoreCase("Owner")) return;
        }
        
        this.playerData.setBanning(true);
        
        PlayerBanEvent event = new PlayerBanEvent(player, this.name);
       
        this.plugin.getServer().getPluginManager().callEvent(event);
    }

    @Override
    public Class<? extends T> getType() {
        return this.clazz;
    }

    protected double getVl() {
        return this.playerData.getCheckVl(this);
    }

    protected void setVl(double vl) {
        this.playerData.setCheckVl(vl, this);
    }
}
