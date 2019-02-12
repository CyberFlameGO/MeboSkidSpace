package secondlife.network.hcfactions.factions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.CaseInsensitiveMap;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.system.WarzoneFaction;
import secondlife.network.hcfactions.factions.type.system.WildernessFaction;
import secondlife.network.hcfactions.factions.utils.enums.ClaimChangeEnum;
import secondlife.network.hcfactions.factions.utils.events.*;
import secondlife.network.hcfactions.factions.utils.struction.ChatChannel;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.utilties.CacheCleanerThread;
import secondlife.network.hcfactions.utilties.ChunkPosition;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.cuboid.CoordinatePair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public abstract class AbstractFactionManager extends Handler implements Listener, FactionManager {

    protected WarzoneFaction warzoneFaction = new WarzoneFaction();
    protected WildernessFaction wildernessFaction = new WildernessFaction();

    protected CacheCleanerThread cacheCleanerThread;
    protected final Table<String, Long, ClaimZone> claimPositionTable;
    protected LoadingCache<CoordinatePair, Optional<ClaimZone>> positionCache;
    protected ConcurrentMap<String, UUID> factionPlayerUuidMap = new ConcurrentHashMap<>();

    protected ConcurrentMap<UUID, Faction> factionUUIDMap = new ConcurrentHashMap<>();
    protected Map<String, UUID> factionNameMap = new CaseInsensitiveMap<>();

    public AbstractFactionManager(HCF plugin) {
        super(plugin);

        this.claimPositionTable = HashBasedTable.create();

        this.positionCache = CacheBuilder.newBuilder().maximumSize(8000L).build(new CacheLoader<CoordinatePair, Optional<ClaimZone>>() {
            public Optional<ClaimZone> load(CoordinatePair coordinatePair) {
                int chunkX = coordinatePair.getX() >> 4;
                int chunkZ = coordinatePair.getZ() >> 4;
                
                int posX = coordinatePair.getX() % 16;
                int posZ = coordinatePair.getZ() % 16;
                
                synchronized(claimPositionTable) {
                    return Optional.ofNullable(claimPositionTable.get(new CoordinatePair(coordinatePair.getWorldName(), chunkX, chunkZ), new ChunkPosition((byte)posX, (byte)posZ)));
                }
            }
        });

        this.cacheCleanerThread = new CacheCleanerThread(1000L, plugin, this.positionCache);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.reloadFactionData();

        this.cacheCleanerThread.start();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(FactionPlayerJoinedEvent event) {
        factionPlayerUuidMap.put(event.getPlayerUUID(), event.getFaction().getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(FactionPlayerLeftEvent event) {
        factionPlayerUuidMap.remove(event.getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(FactionRenameEvent event) {
        factionNameMap.remove(event.getOriginalName());
        factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionClaim(FactionClaimChangedEvent event) {
        for(ClaimZone claim : event.getAffectedClaims()) {
            cacheClaim(claim, event.getCause());
        }
    }

    @Override
    public Map<String, UUID> getFactionNameMap() {
        return this.factionNameMap;
    }

    @Override
    public List<Faction> getFactions() {
        ArrayList<Faction> factions = new ArrayList<>();

        for(Faction faction : this.factionUUIDMap.values()) {
            factions.add(faction);
        }

        return factions;
    }

    @Override
    public ClaimZone getClaimAt(World world, int x, int z) {
        return claimPositionTable.get(world.getName(), LongHash.toLong(x, z));
    }

    @Override
    public ClaimZone getClaimAt(Location location) {
        return getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    @Override
    public Faction getFactionAt(World world, int x, int z) {
        World.Environment environment = world.getEnvironment();
        ClaimZone claim = this.getClaimAt(world, x, z);

        if(claim != null) {
            Faction faction = claim.getFaction();

            if(faction != null) return faction;
        }

        if(environment == World.Environment.THE_END) return warzoneFaction;

        int warzoneRadius = HCFConfiguration.warzoneRadius;

        if(environment == World.Environment.NETHER) warzoneRadius /= 8;

        return (Math.abs(x) > warzoneRadius || Math.abs(z) > warzoneRadius) ? wildernessFaction : warzoneFaction;
    }

    @Override
    public Faction getFactionAt(Location location) {
        return getFactionAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    @Override
    public Faction getFactionAt(Block block) {
        return getFactionAt(block.getLocation());
    }

    @Override
    public Faction getFaction(String factionName) {
        UUID uuid = factionNameMap.get(factionName);
        return uuid == null ? null : factionUUIDMap.get(uuid);
    }

    @Override
    public Faction getFaction(UUID factionUUID) {
        return factionUUIDMap.get(factionUUID);
    }

    @Override
    public PlayerFaction getPlayerFaction(String playerUUID) {
        UUID uuid = factionPlayerUuidMap.get(playerUUID);
        Faction faction = uuid == null ? null : factionUUIDMap.get(uuid);

        return faction instanceof PlayerFaction ? (PlayerFaction) faction : null;
    }

    @Override
    public PlayerFaction getPlayerFaction(Player player) {
        return getPlayerFaction(player.getName());
    }

    @Override
    public PlayerFaction getContainingPlayerFaction(String search) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(search); //TODO: breaking

        return target.hasPlayedBefore() || target.isOnline() ? getPlayerFaction(target.getName()) : null;
    }

    @Override
    public Faction getContainingFaction(String search) {
        Faction faction = getFaction(search);

        if(faction != null)  return faction;

        String playerUUID = Bukkit.getOfflinePlayer(search).getName();

        if(playerUUID != null) return getPlayerFaction(playerUUID);

        return null;
    }

    @Override
    public boolean createFaction(Faction faction, CommandSender sender) {
        if(faction instanceof PlayerFaction && sender instanceof Player) {
            Player player = (Player) sender;
            PlayerFaction playerFaction = (PlayerFaction) faction;

            if(!playerFaction.addMember(sender, player, player.getName(), new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER))) return false;
        }

        if(this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) return false;

        this.factionNameMap.put(faction.getName(), faction.getUniqueID());

        FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);

        Bukkit.getPluginManager().callEvent(createEvent);

        return !createEvent.isCancelled();
    }

    @Override
    public boolean removeFaction(Faction faction, CommandSender sender) {
        if(!this.factionUUIDMap.containsKey(faction.getUniqueID())) return false;

        FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
        Bukkit.getPluginManager().callEvent(removeEvent);

        if(removeEvent.isCancelled()) return false;

        this.factionUUIDMap.remove(faction.getUniqueID());
        this.factionNameMap.remove(faction.getName());

        if(faction instanceof ClaimableFaction) {
            Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeEnum.UNCLAIM, ((ClaimableFaction) faction).getClaims()));
        }

        if(faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;

            for(PlayerFaction ally : playerFaction.getAlliedFactions()) {
                ally.getRelations().remove(faction.getUniqueID());
            }

            for(String uuid : playerFaction.getMembers().keySet()) {
                playerFaction.removeMember(sender, null, uuid, true);
            }
        }

        return true;
    }

    @Override
    public void cacheClaim(ClaimZone claim, ClaimChangeEnum cause) {
        World world = claim.getWorld();

        if(world == null) return;

        int minX = Math.min(claim.getX1(), claim.getX2());
        int maxX = Math.max(claim.getX1(), claim.getX2());

        int minZ = Math.min(claim.getZ1(), claim.getZ2());
        int maxZ = Math.max(claim.getZ1(), claim.getZ2());

        for(int x = minX; x <= maxX; x++) {
            for(int z = minZ; z <= maxZ; z++) {
                if(cause == ClaimChangeEnum.CLAIM) {
                    claimPositionTable.put(world.getName(), LongHash.toLong(x, z), claim);
                } else if(cause == ClaimChangeEnum.UNCLAIM) {
                    claimPositionTable.remove(world.getName(), LongHash.toLong(x, z));
                }
            }
        }
    }

    public void cacheFaction(Faction faction) {
        this.factionNameMap.put(faction.getName(), faction.getUniqueID());
        this.factionUUIDMap.put(faction.getUniqueID(), faction);

        if(faction instanceof ClaimableFaction) {
            ClaimableFaction claimableFaction = (ClaimableFaction)faction;
            for(ClaimZone claim : claimableFaction.getClaims()) {
                this.cacheClaim(claim, ClaimChangeEnum.CLAIM);
            }
        }

        if(faction instanceof PlayerFaction) {
            for(FactionMember factionMember : ((PlayerFaction) faction).getMembers().values()) {
                factionPlayerUuidMap.put(factionMember.getName(), faction.getUniqueID());
            }
        }
    }

    @Override
    public void updateFaction(Faction faction) {}
}