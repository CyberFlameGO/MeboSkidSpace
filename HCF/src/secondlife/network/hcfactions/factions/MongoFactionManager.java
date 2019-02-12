package secondlife.network.hcfactions.factions;

import com.mongodb.client.model.Filters;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.utils.events.FactionRemoveEvent;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Msg;

public class MongoFactionManager extends AbstractFactionManager {

    public MongoFactionManager(HCF plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionRemove(FactionRemoveEvent event) {
        removeFaction(event.getFaction());
    }

    public void removeFaction(Faction faction) {
        Vituz.getInstance().getDatabaseManager().getFactions().deleteOne(Filters.eq("uuid", faction.getUniqueID().toString()));
    }

    // LOAD
    @Override
    public void reloadFactionData() {
       /* this.getFactionNameMap().clear();

        Vituz.getInstance().getDatabaseManager().getFactions().find().forEach((Block) obj -> {
            Document document = (Document) obj;

            Faction faction = null;

            switch (document.getString("type")) {
                case "PLAYER_FACTION": {
                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    String name = document.getString("name");

                    faction = new PlayerFaction(name, uuid);

                    PlayerFaction playerFaction = (PlayerFaction) faction;

                    String announcement = null;
                    int balance = document.getInteger("balance");
                    int kothCaptures = document.getInteger("kothCaptures");
                    int citadelCaptures = document.getInteger("citadelCaptures");
                    int conquestCaptures = document.getInteger("conquestCaptures");
                    int points = document.getInteger("points");
                    double deathsUntilRaidable = document.getDouble("deathsUntilRaidable");
                    long regenCooldownTimestamp = document.getLong("regenCooldownTimestamp");
                    long lastDtrUpdateTimestamp = document.getLong("lastDtrUpdateTimestamp");

                    if (document.containsKey("announcement")) {
                        announcement = document.getString("announcement");
                    }

                    playerFaction.setAnnouncement(announcement);
                    playerFaction.setBalance(balance);
                    playerFaction.setKothCaptures(kothCaptures);
                    playerFaction.setConquestCaptures(conquestCaptures);
                    playerFaction.setCitadelCaptures(citadelCaptures);
                    playerFaction.setPoints(points);
                    playerFaction.setDeathsUntilRaidablee(deathsUntilRaidable);
                    playerFaction.setRegenCooldownTimestamp(regenCooldownTimestamp);
                    playerFaction.setLastDtrUpdateTimestamp(lastDtrUpdateTimestamp);

                   /* try {
                        JSONArray members = (JSONArray) new JSONParser().parse(document.getString("members"));

                        for (Object o : members) {
                            JSONObject member = (JSONObject) o;

                            UUID memberUUID = UUID.fromString((String) member.get("uuid"));

                            //FactionMember factionMember = new FactionMember();
                            //factionMember.setUuid(memberUUID);
                            //factionMember.setName((String) member.get("name"));
                            factionMember.setRole(Role.valueOf((String) member.get("role")));

                            playerFaction.setMember(factionMember.getName(), factionMember);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String home = null;

                    if (document.containsKey("home")) {
                        home = document.getString("home");
                    }

                    playerFaction.setHome(home);
                }
                break;

                case "KOTH_FACTION": {
                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    String name = document.getString("name");

                    faction = new KothFaction(name, uuid);
                }
                break;

                case "SPAWN_FACTION": {
                    UUID uuid = UUID.fromString(document.getString("uuid"));

                    boolean hasUUID = uuid != null;

                    faction = new SpawnFaction(hasUUID ? uuid : null);
                }
                break;

                case "END_PORTAL_FACTION": {
                    UUID uuid = UUID.fromString(document.getString("uuid"));

                    boolean hasUUID = uuid != null;

                    faction = new EndPortalFaction(hasUUID ? uuid : null);
                }
                break;

                case "WARZONE_FACTION": {
                    UUID uuid = UUID.fromString(document.getString("uuid"));

                    boolean hasUUID = uuid != null;

                    faction = new WarzoneFaction(hasUUID ? uuid : null);
                }
                break;

                case "WILDERNESS_FACTION": {
                    UUID uuid = UUID.fromString(document.getString("uuid"));

                    boolean hasUUID = uuid != null;

                    faction = new WildernessFaction(hasUUID ? uuid : null);
                }
                break;

                case "ROAD_FACTION": {
                    UUID uuid = UUID.fromString(document.getString("uuid"));

                    boolean hasUUID = uuid != null;

                    faction = new RoadFaction(hasUUID ? uuid : null);
                }
                break;
            }

            if (faction instanceof ClaimableFaction) {
                try {
                    JSONArray claims = (JSONArray) new JSONParser().parse(document.getString("claims"));

                    for (Object o : claims) {
                        JSONObject claim = (JSONObject) o;

                        World world = Bukkit.getWorld((String) claim.get("world"));

                        if (world == null) {
                            continue;
                        }

                        int x1 = ((Long) claim.get("x1")).intValue();
                        int x2 = ((Long) claim.get("x2")).intValue();
                        int y1 = ((Long) claim.get("y1")).intValue();
                        int y2 = ((Long) claim.get("y2")).intValue();
                        int z1 = ((Long) claim.get("z1")).intValue();
                        int z2 = ((Long) claim.get("z2")).intValue();

                        ClaimZone claimZone = new ClaimZone(faction, world, x1, y1, z1, x2, y2, z2);

                        ((ClaimableFaction) faction).addClaim(claimZone, null);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (faction instanceof KothFaction) {
                try {
                    JSONArray claims = (JSONArray) new JSONParser().parse(document.getString("captureZone"));

                    for (Object o : claims) {
                        JSONObject claim = (JSONObject) o;

                        World world = Bukkit.getServer().getWorld((String) claim.get("world"));

                        if (world == null) {
                            continue;
                        }

                        int x1 = ((Long) claim.get("x1")).intValue();
                        int x2 = ((Long) claim.get("x2")).intValue();
                        int y1 = ((Long) claim.get("y1")).intValue();
                        int y2 = ((Long) claim.get("y2")).intValue();
                        int z1 = ((Long) claim.get("z1")).intValue();
                        int z2 = ((Long) claim.get("z2")).intValue();

                        ClaimZone claimZone = new ClaimZone(faction, world, x1, y1, z1, x2, y2, z2);

                        CaptureZone captureZone = new CaptureZone(document.getString("name"), claimZone, document.getLong("capTime"));

                        KothFaction kothFaction = (KothFaction) faction;

                        kothFaction.setCaptureZone(captureZone);
                        kothFaction.getCaptureZone().setDefaultCaptureMillis(document.getLong("capTime"));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (faction != null) {
                getFactionNameMap().put(faction.getName(), faction.getUniqueID());
                getFactionUUIDMap().put(faction.getUniqueID(), faction);
            }
        });*/
    }

    // SAVE
    @Override
    public void updateFaction(Faction faction) {
        /*if(!(Faction.getFactions().isEmpty())) {
            if(faction.getType() == null) {
                Msg.logConsole("&c&lType is null!");
                return;
            }

            Document document = new Document();
            document.put("uuid", faction.getUniqueID().toString());
            document.put("name", faction.getName());
            document.put("type", faction.getType());
            document.put("safezone", faction.isSafezone());

            if (faction instanceof PlayerFaction) {
                PlayerFaction playerFaction = (PlayerFaction) faction;

                document.put("announcement", playerFaction.getAnnouncement());
                document.put("balance", playerFaction.getBalance());
                document.put("kothCaptures", playerFaction.getKothCaptures());
                document.put("conquestCaptures", playerFaction.getConquestCaptures());
                document.put("citadelCaptures", playerFaction.getCitadelCaptures());
                document.put("points", playerFaction.getPoints());
                document.put("deathsUntilRaidable", playerFaction.getDeathsUntilRaidablee());
                document.put("regenCooldownTimestamp", playerFaction.getRegenCooldownTimestamp());
                document.put("lastDtrUpdateTimestamp", playerFaction.getLastDtrUpdateTimestamp());

                JSONArray members = new JSONArray();

                for (FactionMember factionMember : playerFaction.getMembers().values()) {
                    JSONObject member = new JSONObject();
                    member.put("name", factionMember.getName());
                    member.put("role", factionMember.getRole().toString());

                    members.add(member);
                }

                document.put("members", members.toJSONString());

                if(playerFaction.getHome() != null) {
                    document.put("home", playerFaction.getHome());
                }
            }

            if (faction instanceof ClaimableFaction) {
                ClaimableFaction claimableFaction = (ClaimableFaction) faction;
                JSONArray claims = new JSONArray();

                for (ClaimZone claimZone : claimableFaction.getClaims()) {
                    JSONObject claim = new JSONObject();
                    claim.put("world", claimZone.getWorldName());
                    claim.put("x1", claimZone.getX1());
                    claim.put("x2", claimZone.getX2());
                    claim.put("y1", claimZone.getY1());
                    claim.put("y2", claimZone.getY2());
                    claim.put("z1", claimZone.getZ1());
                    claim.put("z2", claimZone.getZ2());

                    claims.add(claim);
                }

                document.put("claims", claims.toJSONString());
            }

            if(faction instanceof KothFaction) {
                KothFaction kothFaction = (KothFaction) faction;
                JSONArray claims = new JSONArray();

                for (CaptureZone claimZone : kothFaction.getCaptureZones()) {
                    JSONObject claim = new JSONObject();

                    claim.put("world", claimZone.getCuboid().getWorldName());
                    claim.put("x1", claimZone.getCuboid().getX1());
                    claim.put("x2", claimZone.getCuboid().getX2());
                    claim.put("y1", claimZone.getCuboid().getY1());
                    claim.put("y2", claimZone.getCuboid().getY2());
                    claim.put("z1", claimZone.getCuboid().getZ1());
                    claim.put("z2", claimZone.getCuboid().getZ2());

                    claims.add(claim);
                }

                document.put("captureZone", claims.toJSONString());
                document.put("capTime", kothFaction.getCaptureZone().getDefaultCaptureMillis());
            }

            Vituz.getInstance().getDatabaseManager().getFactions().replaceOne(Filters.eq("uuid", faction.getUniqueID().toString()), document, new UpdateOptions().upsert(true));
        }*/
    }

    public void updateAll() {
        for (Faction faction : Faction.getFactions()) {
            updateFaction(faction);
        }
    }

    @Override
    public void saveFactionData() {
        Msg.logConsole("&ePreparing to save " + Faction.getFactions().size() + " factions!");

        updateAll();

        Msg.logConsole("&aSuccessfully saved " + Faction.getFactions().size() + " factions!");
    }
}
