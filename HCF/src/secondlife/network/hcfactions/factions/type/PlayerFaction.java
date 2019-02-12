package secondlife.network.hcfactions.factions.type;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.enums.FactionLeaveEnum;
import secondlife.network.hcfactions.factions.utils.events.*;
import secondlife.network.hcfactions.factions.utils.struction.Raidable;
import secondlife.network.hcfactions.factions.utils.struction.RegenStatus;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.GenericUtils;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@Getter
@Setter
public class PlayerFaction extends ClaimableFaction implements Raidable {

    public Pattern regex = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

    private Map<UUID, Relation> requestedRelations = new HashMap<>();
    private Map<UUID, Relation> relations = new HashMap<>();
    private Map<String, FactionMember> members = new HashMap<>();
    private Set<String> invitedPlayerNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    private String home, announcement;
    private int balance, kothCaptures, conquestCaptures, citadelCaptures, points;
    private long regenCooldownTimestamp, lastDtrUpdateTimestamp;
    private double deathsUntilRaidable = 1.0D;
    private Double maxDeathsUntilRaidable;

    private UUID[] EMPTY_UUID_ARRAY = {};

    public PlayerFaction(String name) {
        super(name);
    }

    public PlayerFaction(String name, UUID uuid) {
        super(name, uuid);
    }

    public PlayerFaction(Map<String, Object> map) {
        super(map);

        for(Map.Entry<String, FactionMember> entry : GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet()) {
            if(entry.getValue() != null) {
                this.members.put(entry.getKey(), entry.getValue());
            }
        }

        this.invitedPlayerNames.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));

        Object object = map.get("home");
        if(object != null) this.home = (String) object;

        object = map.get("announcement");
        if(object != null) this.announcement = (String) object;

        for(Map.Entry<String, String> entry : GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet()) {
            relations.put(UUID.fromString(entry.getKey()), Relation.valueOf(entry.getValue()));
        }

        for(Map.Entry<String, String> entry : GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet()) {
            requestedRelations.put(UUID.fromString(entry.getKey()), Relation.valueOf(entry.getValue()));
        }

        this.balance = (Integer) map.get("balance");
        this.kothCaptures = (Integer) map.get("kothCaptures");
        this.conquestCaptures = (Integer) map.get("conquestCaptures");
        this.citadelCaptures = (Integer) map.get("citadelCaptures");
        this.points = (Integer) map.get("points");
        this.deathsUntilRaidable = (Double) map.get("deathsUntilRaidable");
        this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
        this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();

        Map<String, String> relationSaveMap = new HashMap<>(relations.size());
        for(Map.Entry<UUID, Relation> entry : relations.entrySet()) {
            relationSaveMap.put(entry.getKey().toString(), entry.getValue().name());
        }

        map.put("relations", relationSaveMap);

        Map<String, String> requestedRelationsSaveMap = new HashMap<>(requestedRelations.size());
        for(Map.Entry<UUID, Relation> entry : requestedRelations.entrySet()) {
            requestedRelationsSaveMap.put(entry.getKey().toString(), entry.getValue().name());
        }

        map.put("requestedRelations", requestedRelationsSaveMap);

        Set<Map.Entry<String, FactionMember>> entrySet = this.members.entrySet();
        Map<String, FactionMember> saveMap = new LinkedHashMap<>(this.members.size());
        for(Map.Entry<String, FactionMember> entry : entrySet) {
            saveMap.put(entry.getKey(), entry.getValue());
        }

        map.put("members", saveMap);

        map.put("invitedPlayerNames", new ArrayList<>(invitedPlayerNames));

        if(home != null) map.put("home", home);
        if(announcement != null) map.put("announcement", announcement);

        map.put("balance", balance);
        map.put("kothCaptures", kothCaptures);
        map.put("conquestCaptures", conquestCaptures);
        map.put("citadelCaptures", citadelCaptures);
        map.put("points", points);
        map.put("deathsUntilRaidable", deathsUntilRaidable);
        map.put("regenCooldownTimestamp", Long.toString(regenCooldownTimestamp));
        map.put("lastDtrUpdateTimestamp", Long.toString(lastDtrUpdateTimestamp));

        return map;
    }

    public boolean addMember(CommandSender sender, Player player, String playerUUID, FactionMember factionMember) {
        if(members.containsKey(playerUUID)) return false;

        FactionPlayerJoinEvent eventPre = new FactionPlayerJoinEvent(sender, player, playerUUID, this);
        Bukkit.getPluginManager().callEvent(eventPre);

        if(eventPre.isCancelled()) return false;

        lastDtrUpdateTimestamp = System.currentTimeMillis();
        invitedPlayerNames.remove(factionMember.getName());

        members.put(playerUUID, factionMember);

        Bukkit.getPluginManager().callEvent(new FactionPlayerJoinedEvent(sender, player, playerUUID, this));

        return true;
    }

    public boolean removeMember(CommandSender sender, Player player, String playerUUID, boolean kick) {
        if(!this.members.containsKey(playerUUID)) return true;

        FactionPlayerLeaveEvent preEvent = new FactionPlayerLeaveEvent(sender, player, playerUUID, this, FactionLeaveEnum.LEAVE, kick, false);
        Bukkit.getPluginManager().callEvent(preEvent);

        if(preEvent.isCancelled()) return false;


        this.members.remove(playerUUID);
        this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));

        FactionPlayerLeftEvent event = new FactionPlayerLeftEvent(sender, player, playerUUID, this, FactionLeaveEnum.LEAVE, kick, false);
        Bukkit.getPluginManager().callEvent(event);

        return true;
    }

    public Collection<UUID> getAllied() {
        return Maps.filterValues(relations, new Predicate<Relation>() {
            public boolean apply(Relation relation) {
                return relation == Relation.ALLY;
            }
        }).keySet();
    }

    public List<PlayerFaction> getAlliedFactions() {
        Collection<UUID> allied = getAllied();
        Iterator<UUID> iterator = allied.iterator();
        List<PlayerFaction> results = new ArrayList<>(allied.size());

        while(iterator.hasNext()) {
            Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(iterator.next());

            if(faction instanceof PlayerFaction) {
                results.add((PlayerFaction) faction);
            } else
                iterator.remove();
        }

        return results;
    }

    public Set<Player> getOnlinePlayers(CommandSender sender) {
        Set<Player> toReturn = new HashSet<>();

        for(Entry entry : getOnlineMembers(sender).entrySet()) {
            toReturn.add(Bukkit.getPlayer((String) entry.getKey()));
        }

        return toReturn;
    }

    public Map<String, FactionMember> getOnlineMembers(CommandSender sender) {
        Player senderPlayer = sender instanceof Player ? ((Player) sender) : null;
        Map<String, FactionMember> results = new HashMap<>();
        for (Map.Entry<String, FactionMember> entry : members.entrySet()) {
            Player target = Bukkit.getPlayer(entry.getKey());
            if (target == null || (senderPlayer != null && !senderPlayer.canSee(target))) {
                continue;
            }

            results.put(entry.getKey(), entry.getValue());
        }

        return results;
    }

    public FactionMember getLeader() {
        Map<String, FactionMember> members = this.members;

        for(Map.Entry<String, FactionMember> entry : members.entrySet()) {
            if(entry.getValue().getRole() == Role.LEADER) return entry.getValue();
        }

        return null;
    }

    public boolean isMember(String name) {
        for(FactionMember member : members.values()) {
            if(name.equalsIgnoreCase(member.getName())) {
                return true;
            }
        }

        return false;
    }

    public boolean isLeader(String name) {
        for(FactionMember member : members.values()) {
            if(member.getRole() == Role.LEADER) {
                if(name.equalsIgnoreCase(member.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCoLeader(String name) {
        for(FactionMember member : members.values()) {
            if(member.getRole() == Role.COLEADER) {
                if(name.equalsIgnoreCase(member.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCaptain(String name) {
        for(FactionMember member : members.values()) {
            if(member.getRole() == Role.CAPTAIN) {
                if(name.equalsIgnoreCase(member.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public FactionMember getMember(String memberName) {
        FactionMember factionMember = this.members.get(memberName);

        return factionMember;
    }

    public double getDeathsUntilRaidable(boolean updateLastCheck) {
        if(updateLastCheck) this.updateDeathsUntilRaidable();

        return deathsUntilRaidable;
    }

    public ChatColor getDtrColour() {
        this.updateDeathsUntilRaidable();

        if(deathsUntilRaidable < 0) {
            return ChatColor.RED;
        } else if(deathsUntilRaidable < 1) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.GREEN;
        }
    }

    public ChatColor getTabDtrColour() {
        this.updateDeathsUntilRaidable();

        if(deathsUntilRaidable < 0) {
            return ChatColor.RED;
        } else if(deathsUntilRaidable < 1) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.RESET;
        }
    }

    private void updateDeathsUntilRaidable() {
        if(this.getRegenStatus() == RegenStatus.REGENERATING) {
            long now = System.currentTimeMillis();
            long millisPassed = now - this.lastDtrUpdateTimestamp;

            if(millisPassed >= HCFConfiguration.dtrIncrementBetweenUpdate) {
                long remainder = millisPassed % HCFConfiguration.dtrUpdate;
                int multiplier = (int) (((double) millisPassed + remainder) / HCFConfiguration.dtrUpdate);
                double increase = multiplier * HCFConfiguration.dtrIncrementBetweenUpdate;

                this.lastDtrUpdateTimestamp = now - remainder;
                this.setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
            }
        }
    }

    private double setDeathsUntilRaidable(double deathsUntilRaidable, boolean limit) {
        deathsUntilRaidable = Math.round(deathsUntilRaidable * 100.0) / 100.0;

        if(limit) deathsUntilRaidable = Math.min(deathsUntilRaidable, getMaximumDeathsUntilRaidable());

        if(Math.abs(deathsUntilRaidable - this.deathsUntilRaidable) != 0) {
            FactionDTRChangeEvent event = new FactionDTRChangeEvent(FactionDTRChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getPluginManager().callEvent(event);

            if(!event.isCancelled()) {
                deathsUntilRaidable = Math.round(event.getNewDtr() * 100.0) / 100.0;

                if(deathsUntilRaidable > 0 && this.deathsUntilRaidable <= 0) {
                    Msg.logConsole("&4Faction &c" + getName() + " &4is is now raidable!");
                }

                this.lastDtrUpdateTimestamp = System.currentTimeMillis();

                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }

        return this.deathsUntilRaidable;
    }

    @Override
    public void setRemainingRegenerationTime(long millis) {
        long systemMillis = System.currentTimeMillis();
        this.regenCooldownTimestamp = systemMillis + millis;

        this.lastDtrUpdateTimestamp = systemMillis + (HCFConfiguration.dtrUpdate * 2);
    }

    @Override
    public RegenStatus getRegenStatus() {
        if (getRemainingRegenerationTime() > 0L) {
            return RegenStatus.PAUSED;
        } else if (getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable) {
            return RegenStatus.REGENERATING;
        } else {
            return RegenStatus.FULL;
        }
    }

    @Override
    public void printDetails(CommandSender sender) {
        String leaderName = null;

        Set<String> allyNames = new HashSet<>(HCFConfiguration.maxAllysPerFaction);
        for(Map.Entry<UUID, Relation> entry : relations.entrySet()) {
            Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(entry.getKey());

            if(faction instanceof PlayerFaction) {
                PlayerFaction ally = (PlayerFaction) faction;
                allyNames.add(Color.translate(ally.getDisplayName(sender) + "&7[&f" + ally.getOnlinePlayers(sender).size() + "&7/&f" + ally.members.size() + "&7]"));
            }
        }

        Set<String> memberNames = new HashSet<>();
        Set<String> captainNames = new HashSet<>();
		HashSet<String> coleaderNames = new HashSet<>();

        for (Entry<String, FactionMember> entry : this.members.entrySet()) {
            FactionMember factionMember = entry.getValue();
            Player target = factionMember.toOnlinePlayer();

            HCFData data = HCFData.getByName(entry.getKey());

            int kills;
            ChatColor color;

            if (target == null) {
                kills = 0;
                color = ChatColor.GRAY;
            } else {
                if (data == null) return;

                kills = data.getKills();

                color = ChatColor.GREEN;
            }

            String memberName = color + factionMember.getName() + Color.translate("&7[&f" + kills + "&7]");

            switch (factionMember.getRole()) {
                case LEADER: {
                    leaderName = memberName;
                    continue;
                }
                case COLEADER: {
                    coleaderNames.add(memberName);
                    continue;
                }
                case CAPTAIN: {
                    captainNames.add(memberName);
                    continue;
                }
                case MEMBER: {
                    memberNames.add(memberName);
                }
            }
        }

        sender.sendMessage(HCFUtils.BIG_LINE);

		if(home == null) {
            sender.sendMessage(Color.translate("&9" + getDisplayName(sender) + " &7[&f" + getOnlinePlayers(sender).size() + "&7/&d" + members.size() + "&7] &3- &eHQ: &dNone"));
        } else {
            Location homeLocation = secondlife.network.vituz.utilties.StringUtils.destringifyLocation(this.home);

            int x = homeLocation.getBlockX();
            int z = homeLocation.getBlockZ();

            sender.sendMessage(Color.translate("&9" + getDisplayName(sender) + " &7[&f" + getOnlinePlayers(sender).size() + "&7/&d" + members.size() + "&7] &3- &eHQ: &7(&d" + x + " &7|&d " + z + "&7)"));
        }

        if(!allyNames.isEmpty()) {
            sender.sendMessage(Color.translate("&eAllies: &c" + StringUtils.join(allyNames, "&7, ")));
        }

        if(leaderName != null) {
            sender.sendMessage(Color.translate("&eLeader: &c" + leaderName));
        }

		if(!coleaderNames.isEmpty()) {
			sender.sendMessage(Color.translate("&eCo-Leaders: &c" + StringUtils.join(coleaderNames, "&7, ")));
		}

        if(!captainNames.isEmpty()) {
            sender.sendMessage(Color.translate("&eCaptains: &c" + StringUtils.join(captainNames, "&7, ")));
        }

        if(!memberNames.isEmpty()) {
            sender.sendMessage(Color.translate("&eMembers: &c" + StringUtils.join(memberNames, "&7, ")));
        }

        if(sender instanceof Player) {
            Faction faction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction((Player) sender);

            if(faction != null && faction.equals(this) && announcement != null) {
                sender.sendMessage(Color.translate("&eAnnouncement: &c" + announcement));
            }
        }

        sender.sendMessage(Color.translate("&eBalance: &9$" + balance));

        if(this.points > 0) {
            sender.sendMessage(Color.translate("&ePoints: &9" + points));
        }

        if(this.kothCaptures > 0) {
        	sender.sendMessage(Color.translate("&eKoTH Captures: &9" + this.kothCaptures));
        }

        if(this.conquestCaptures > 0) {
        	sender.sendMessage(Color.translate("&eConquest Captures: &9" + this.conquestCaptures));
        }

        if(this.citadelCaptures > 0) {
        	sender.sendMessage(Color.translate("&eCitadel Captures: &9" + this.citadelCaptures));
        }

		FastDateFormat date = FastDateFormat.getInstance("dd.MM HH:mm:ss", TimeZone.getTimeZone("Europe/Zagreb"), Locale.ENGLISH);

        sender.sendMessage(Color.translate("&eFounded on: &9" +  date.format(this.getCreationMillis())));
        sender.sendMessage(Color.translate("&eDeaths until Raidable: " + getRegenStatus().getSymbol() + getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)) + Color.translate("&7/" + getDtrColour() +  JavaUtils.format(getMaximumDeathsUntilRaidable()))));

        long dtrRegenRemaining = getRemainingRegenerationTime();

        if(dtrRegenRemaining > 0L) {
            sender.sendMessage(Color.translate("&eTime Until Regen: &9" + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true)));
        }

        sender.sendMessage(HCFUtils.BIG_LINE);
    }

    public void broadcast(String message) {
        broadcast(Color.translate(message), EMPTY_UUID_ARRAY);
    }

    public void broadcast(String[] messages) {
        broadcast(messages, EMPTY_UUID_ARRAY);
    }

    public void broadcast(String message, UUID... ignore) {
        this.broadcast(new String[] { message }, ignore);
    }

    public void broadcast(String[] messages, UUID... ignore) {
        Collection<Player> players = getOnlinePlayers();
        Collection<UUID> ignores = ignore.length == 0 ? Collections.emptySet() : Sets.newHashSet(ignore);

        for(Player player : players) {
            if(!ignores.contains(player.getUniqueId())) {
                player.sendMessage(messages);
            }
        }
    }

    public boolean setMember(String playerUUID, FactionMember factionMember) {
        return this.setMember(null, playerUUID, factionMember, false);
    }
    
    public boolean setMember(String playerUUID, FactionMember factionMember, boolean force) {
        return this.setMember(null, playerUUID, factionMember, force);
    }
    
    public boolean setMember(Player player, FactionMember factionMember) {
        return this.setMember(player, player.getName(), factionMember, false);
    }
    
    public boolean setMember(Player player, FactionMember factionMember, boolean force) {
        return this.setMember(player, player.getName(), factionMember, force);
    }
    
    private boolean setMember(Player player, String playerUUID, FactionMember factionMember, boolean force) {
        if(factionMember == null) {
            if(!force) {
                FactionPlayerLeaveEvent event = (player == null) ? new FactionPlayerLeaveEvent(player, player, playerUUID, this, FactionLeaveEnum.LEAVE, force, force) : new FactionPlayerLeaveEvent(player, player, playerUUID, this, FactionLeaveEnum.LEAVE, force, force);
                Bukkit.getPluginManager().callEvent(event);
                
                if(event.isCancelled())  return false;
            }
            
            this.members.remove(playerUUID);
            this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));
            
            FactionPlayerLeftEvent event2 = (player == null) ? new FactionPlayerLeftEvent(player, player, playerUUID, this, FactionLeaveEnum.LEAVE, force, force) : new FactionPlayerLeftEvent(player, player, playerUUID, null, null, force, force);
            Bukkit.getPluginManager().callEvent(event2);
            
            return true;
        }
       
        FactionPlayerJoinedEvent eventPre = (player == null) ? new FactionPlayerJoinedEvent(player, player, playerUUID, this) : new FactionPlayerJoinedEvent(player, player, playerUUID, this);
        Bukkit.getPluginManager().callEvent(eventPre);
       
        this.lastDtrUpdateTimestamp = System.currentTimeMillis();
      
        this.invitedPlayerNames.remove(factionMember.getName());
        this.members.put(playerUUID, factionMember);
        return true;
    }
    
    public Map<String, FactionMember> getMembers() {
        return ImmutableMap.copyOf(members);
    }

    public Set<Player> getOnlinePlayers() {
        return getOnlinePlayers(null);
    }

    public Map<String, FactionMember> getOnlineMembers() {
        return getOnlineMembers(null);
    }
    
    public FactionMember getMember(Player player) {
        return this.getMember(player.getName());
    }

    @Override
    public boolean isRaidable() {
        return deathsUntilRaidable <= 0;
    }

    @Override
    public double getDeathsUntilRaidable() {
        return this.getDeathsUntilRaidable(true);
    }

    @Override
    public double getMaximumDeathsUntilRaidable() {
        if(members.size() == 1) return 1.1;

        return Math.min(HCFConfiguration.maxDtr, members.size() * 0.9);
    }
    
    @Override
    public double setDeathsUntilRaidable(double deathsUntilRaidable) {
        return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
    }
    
    @Override
    public long getRemainingRegenerationTime() {
        return regenCooldownTimestamp == 0L ? 0L : regenCooldownTimestamp - System.currentTimeMillis();
    }

    public double getDeathsUntilRaidablee() {
        return deathsUntilRaidable;
    }

    public void setDeathsUntilRaidablee(double dtr) {
        deathsUntilRaidable = dtr;
    }
}
