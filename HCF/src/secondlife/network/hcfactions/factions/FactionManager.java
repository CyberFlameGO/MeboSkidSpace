package secondlife.network.hcfactions.factions;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.utils.enums.ClaimChangeEnum;
import secondlife.network.hcfactions.factions.type.PlayerFaction;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface FactionManager {

    Faction getFactionAt(Location location);
    
    Faction getFactionAt(Block block);
    
    Faction getFactionAt(World world, int x, int z);
    
    Faction getFaction(String factionName);

    Faction getFaction(UUID uuid);
    
    Faction getContainingFaction(String id);

    PlayerFaction getContainingPlayerFaction(String search);

    PlayerFaction getPlayerFaction(Player player);

    PlayerFaction getPlayerFaction(String uuid);

    long max_dtr_regen_millis = TimeUnit.HOURS.toMillis(3L);
    
    String max_dtr_regen_words = DurationFormatUtils.formatDurationWords(max_dtr_regen_millis, true, true);

    Map<String, ?> getFactionNameMap();

    Collection<Faction> getFactions();
    
    ClaimZone getClaimAt(Location location);
    
    ClaimZone getClaimAt(World world, int x, int z);

    boolean createFaction(Faction faction, CommandSender sender);

    boolean removeFaction(Faction faction, CommandSender sender);

    void reloadFactionData();
    
    void saveFactionData();

    void cacheClaim(ClaimZone claimZone, ClaimChangeEnum changeEnum);

    void updateFaction(Faction faction);
}