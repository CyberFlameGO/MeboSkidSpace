package secondlife.network.victions;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.factions.zcore.util.TL;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

/**
 * Created by Marko on 18.07.2018.
 */
public class VictionsAPI {

    @Getter
    private static WorldGuardPlugin worldGuard;

    public static void hook() {
        Plugin worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if(worldGuardPlugin == null || !(worldGuardPlugin instanceof WorldGuardPlugin)) {
            worldGuard = null;
        } else {
            worldGuard = (WorldGuardPlugin) worldGuardPlugin;
        }
    }

    public static boolean isPvPEnabled(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        Vector vector = toVector(location);
        RegionManager regionManager = worldGuard.getRegionManager(world);
        ApplicableRegionSet region = regionManager.getApplicableRegions(vector);

        return region.allows(DefaultFlag.PVP) || region.getFlag(DefaultFlag.PVP) == null;
    }

    public static ProtectedRegion getByProtectedRegion(Location location) {
        for(ProtectedRegion region : worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location)) {
            if(region != null) {
                return region;
            }
        }

        return null;
    }

    public static Faction getByFaction(Player player) {
        return FPlayers.getInstance().getByPlayer(player).getFaction();
    }

    public static Faction getFactionAt(Location location) {
        return Board.getInstance().getFactionAt(new FLocation(location));
    }

    public static boolean isWilderness(Location location) {
        return Board.getInstance().getFactionAt(new FLocation(location)).isWilderness();
    }

    public static boolean isWarzone(Location location) {
        return Board.getInstance().getFactionAt(new FLocation(location)).isWarZone();
    }

    public static boolean isInOwnClaim(Player player) {
        return FPlayers.getInstance().getByPlayer(player).isInOwnTerritory();
    }

    public static boolean isInAllyClaim(Player player) {
        return FPlayers.getInstance().getByPlayer(player).isInAllyTerritory();
    }

    public static boolean isInEnemyClaim(Player player) {
        return FPlayers.getInstance().getByPlayer(player).isInEnemyTerritory();
    }

    public static void updatePower(FPlayer fplayer, Location location) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(location));

        PowerLossEvent localPowerLossEvent = new PowerLossEvent(faction, fplayer);

        if(faction.isWarZone()) {

            if(!Conf.warZonePowerLoss) {
                localPowerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WARZONE.toString());
                localPowerLossEvent.setCancelled(true);
            }

            if(Conf.worldsNoPowerLoss.contains(location.getWorld().getName())) {
                localPowerLossEvent.setMessage(TL.PLAYER_POWER_LOSS_WARZONE.toString());
            }

        } else if(faction.isWilderness() && !Conf.wildernessPowerLoss && !Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
            localPowerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WILDERNESS.toString());
            localPowerLossEvent.setCancelled(true);
        } else if(Conf.worldsNoPowerLoss.contains(location.getWorld().getName())) {
            localPowerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WORLD.toString());
            localPowerLossEvent.setCancelled(true);
        } else if(Conf.peacefulMembersDisablePowerLoss && fplayer.hasFaction() && fplayer.getFaction().isPeaceful()) {
            localPowerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_PEACEFUL.toString());
            localPowerLossEvent.setCancelled(true);
        } else {
            localPowerLossEvent.setMessage(TL.PLAYER_POWER_NOW.toString());
        }

        Bukkit.getPluginManager().callEvent(localPowerLossEvent);

        if(!localPowerLossEvent.isCancelled()) {
            fplayer.onDeath();
        }

        String str = localPowerLossEvent.getMessage();

        if(str != null && !str.isEmpty()) {
            fplayer.msg(str, fplayer.getPowerRounded(), fplayer.getPowerMaxRounded());
        }
    }
}
