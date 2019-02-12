package secondlife.network.vituz.ranks.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.ranks.grant.Grant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

@Getter
public class RankSubscriber {
	
    private JedisPubSub jedisPubSub;
    private Jedis jedis;
    
    public RankSubscriber() {
        jedis = new Jedis(Vituz.getInstance().getDatabaseManager().getAddres(), 6379);
        
        if(Vituz.getInstance().getConfig().getBoolean("DATABASE.AUTHENTICATION.ENABLED")) {
            jedis.auth(Vituz.getInstance().getConfig().getString("DATABASE.AUTHENTICATION.PASSWORD"));
        }
        
        this.subscribe();
    }
    
    private void subscribe() {
        jedisPubSub = this.get();
        
        new Thread(() -> jedis.subscribe(jedisPubSub, "ranks")).start();
    }
    
    private JedisPubSub get() {
        return new JedisPubSub() {
            public void onMessage(String channel, String message) {
                if(channel.equalsIgnoreCase("ranks")) {
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    RankSubscriberAction action = RankSubscriberAction.valueOf(object.get("action").getAsString());
                    JsonObject payload = object.get("payload").getAsJsonObject();
                    
                    if(action == RankSubscriberAction.DELETE_PLAYER_PERMISSION) {
                        Player player = Bukkit.getPlayer(payload.get("name").getAsString());
                       
                        if(player != null) {
                            RankData data = RankData.getByName(player.getName());
                           
                            if(data != null) {
                                String permission = payload.get("permission").getAsString();
                                data.getPermissions().remove(permission);
                                data.setupAtatchment();
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.ADD_PLAYER_PERMISSION) {
                        Player player = Bukkit.getPlayer(payload.get("name").getAsString());
                       
                        if(player != null) {
                            RankData data = RankData.getByName(player.getName());
                           
                            if(data != null) {
                                String permission = payload.get("permission").getAsString();
                               
                                data.getPermissions().add(permission);
                                data.setupAtatchment();
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.ADD_RANK_PERMISSION) {
                        Rank rank;
                       
                        try {
                            rank = Rank.getByUuid(UUID.fromString(payload.get("rank").getAsString()));
                        } catch(Exception e) {
                            rank = Rank.getByName(payload.get("rank").getAsString());
                            
                            if(rank == null) throw new IllegalArgumentException("Invalid rank parameter");
                        }
                        
                        if(rank != null) {
                            String permission = payload.get("permission").getAsString();
                            rank.getPermissions().add(permission);
                            Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                            
                            if(player != null) {
                                player.sendMessage(ChatColor.GREEN + "Permission '" + permission + "' successfully added to rank named '" + rank.getData().getName() + "'.");
                            }
                            
                            for(RankData data : RankData.getProfiles().values()) {
                                if(data.getActiveGrant().getRank().getUuid().equals(rank.getUuid())) {
                                    Bukkit.broadcastMessage("Player has " + rank.getData().getName() + ", updating permissions");
                                   
                                    data.setupAtatchment();
                                } else {
                                    Bukkit.broadcastMessage("Player doesn't have " + rank.getData().getName() + " rank");
                                }
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.ADD_INHERITANCE) {
                        Rank rank;
                        
                        try {
                            rank = Rank.getByUuid(UUID.fromString(payload.get("rank").getAsString()));
                        } catch(Exception e) {
                            rank = Rank.getByName(payload.get("rank").getAsString());
                            if (rank == null) {
                                throw new IllegalArgumentException("Invalid rank parameter");
                            }
                        }
                        
                        Rank inheritance;
                        
                        try {
                            inheritance = Rank.getByUuid(UUID.fromString(payload.get("inheritance").getAsString()));
                        } catch (Exception e) {
                            inheritance = Rank.getByName(payload.get("inheritance").getAsString());
                            
                            if(inheritance == null) throw new IllegalArgumentException("Invalid inheritance parameter");
                        }
                        
                        if(rank != null && inheritance != null) {
                            Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                            rank.getInheritance().add(inheritance.getUuid());
                            
                            if(player != null) {
                                player.sendMessage(ChatColor.GREEN + "RankCommand '" + rank.getData().getName() + "' will now inherit '" + inheritance.getData().getName() + "'.");
                            }
                            
                            for(RankData data : RankData.getProfiles().values()) {
                                if(data.getActiveGrant().getRank().getUuid().equals(rank.getUuid())) {
                                    data.setupAtatchment();
                                }
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.DELETE_INHERITANCE) {
                        Rank rank;
                       
                        try {
                            rank = Rank.getByUuid(UUID.fromString(payload.get("rank").getAsString()));
                        } catch(Exception e) {
                            rank = Rank.getByName(payload.get("rank").getAsString());
                           
                            if(rank == null) throw new IllegalArgumentException("Invalid rank parameter");
                        }
                        
                        Rank inheritance;
                       
                        try {
                            inheritance = Rank.getByUuid(UUID.fromString(payload.get("inheritance").getAsString()));
                        } catch(Exception e) {
                            inheritance = Rank.getByName(payload.get("inheritance").getAsString());
                           
                            if(inheritance == null) throw new IllegalArgumentException("Invalid inheritance parameter");
                        }
                        
                        if(rank != null && inheritance != null) {
                            Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                            rank.getInheritance().remove(inheritance.getUuid());
                           
                            if(player != null) {
                                player.sendMessage(ChatColor.GREEN + "RankCommand '" + rank.getData().getName() + "' will no longer inherit '" + inheritance.getData().getName() + "'.");
                            }
                            
                            for(RankData data : RankData.getProfiles().values()) {
                                if(data.getActiveGrant().getRank().getUuid().equals(rank.getUuid())) {
                                    data.setupAtatchment();
                                }
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.DELETE_RANK_PERMISSION) {
                        Rank rank;
                        
                        try {
                            rank = Rank.getByUuid(UUID.fromString(payload.get("rank").getAsString()));
                        } catch (Exception e) {
                            rank = Rank.getByName(payload.get("rank").getAsString());
                            
                            if(rank == null) throw new IllegalArgumentException("Invalid rank parameter");
                        }
                        
                        if(rank != null) {
                            String permission = payload.get("permission").getAsString();
                            rank.getPermissions().remove(permission);
                            Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                            
                            if(player != null) {
                                player.sendMessage(ChatColor.GREEN + "Permission '" + permission + "' successfully removed from rank named '" + rank.getData().getName() + "'.");
                            }
                            
                            for(RankData data : RankData.getProfiles().values()) {
                                if(data.getActiveGrant().getRank().getUuid().equals(rank.getUuid())) {
                                    data.setupAtatchment();
                                }
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.DELETE_GRANT) {
                        Player player = Bukkit.getPlayer(payload.get("name").getAsString());
                       
                        if(player != null) {
                            RankData data = RankData.getByName(player.getName());
                            
                            if(!data.getActiveGrant().getRank().getData().isDefaultRank()) {
                                data.getActiveGrant().setActive(false);
                                Rank rank = Rank.getDefaultRank();
                               
                                if(rank != null) {
                                    player.sendMessage(ChatColor.GREEN + "Your rank has been set to " + rank.getData().getColorPrefix() + rank.getData().getName() + ChatColor.GREEN + ".");
                                }
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.ADD_GRANT) {
                        JsonObject grant = payload.get("grant").getAsJsonObject();
                        Player player = Bukkit.getPlayer(payload.get("name").getAsString());
                        
                        if(player != null) {
                            RankData RankData4 = RankData.getByName(player.getName());
                            Rank rank;
                           
                            try {
                                rank = Rank.getByUuid(UUID.fromString(grant.get("rank").getAsString()));
                            } catch(Exception e) {
                                rank = Rank.getByName(grant.get("rank").getAsString());
                                
                                if(rank == null) throw new IllegalArgumentException("Invalid rank parameter");
                            }
                            
                            if(rank != null) {
                                String issuer = grant.has("issuer") ? grant.get("issuer").getAsString() : null;
                                
                                for(Grant other : RankData4.getGrants()) {
                                    if(!other.getRank().getData().isDefaultRank() && !other.isExpired()) {
                                        other.setActive(false);
                                    }
                                }
                                
                                Grant newGrant = new Grant(issuer, rank, grant.get("datedAdded").getAsLong(), grant.get("duration").getAsLong(), grant.get("reason").getAsString(), true);
                                
                                RankData4.getGrants().add(newGrant);
                                player.sendMessage(ChatColor.GREEN + "Your rank has been set to " + newGrant.getRank().getData().getColorPrefix() + newGrant.getRank().getData().getName() + ChatColor.GREEN + ".");
                            }
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.IMPORT_RANKS) {
                        Rank.getRanks().clear();
                        Iterator<RankData> iterator = RankData.getProfiles().values().iterator();
                       
                        while(iterator.hasNext()) {
                            RankData RankData = iterator.next();
                            Player player = RankData.getPlayer();
                           
                            if(player != null && RankData.getAttachment() != null) {
                                player.removeAttachment(RankData.getAttachment());
                            }
                            
                            iterator.remove();
                        }
                       
                        for(Player online : Bukkit.getOnlinePlayers()) {
                            new RankData(online.getName());
                        }
                        
                        Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                       
                        if(player != null) {
                            player.sendMessage(ChatColor.GREEN + "Ranks successfully imported.");
                        }

                        Vituz.getInstance().getRankManager().load();
                        return;
                    }
                    
                    if(action == RankSubscriberAction.ADD_RANK) {
                        String name = payload.get("name").getAsString();
                        Rank rank = new Rank(UUID.randomUUID(), new ArrayList<>(), new ArrayList<>(), new secondlife.network.vituz.ranks.RankData(name));
                        Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                       
                        if(player != null) {
                            player.sendMessage(ChatColor.GREEN + "RankCommand named '" + rank.getData().getName() + "' successfully created.");
                        }
                        
                        return;
                    }
                    
                    if(action == RankSubscriberAction.DELETE_RANK) {
                        Rank rank = Rank.getByName(payload.get("rank").getAsString());
                       
                        if(rank != null) {
                            Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                         
                            if(player != null) {
                                player.sendMessage(ChatColor.GREEN + "RankCommand named '" + rank.getData().getName() + "' successfully deleted.");
                            }
                            
                            Rank.getRanks().remove(rank);
                        }
                    }
                   
                    if(action == RankSubscriberAction.SET_RANK_PREFIX) {
                        Rank rank = Rank.getByName(payload.get("rank").getAsString());
                      
                        if(rank != null) {
                            Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                            rank.getData().setPrefix(payload.get("prefix").getAsString());
                            
                            if(player != null) {
                                player.sendMessage(ChatColor.GREEN + "RankCommand named '" + rank.getData().getName() + "' prefix successfully changed.");
                            }
                        }
                    }
                    
                    if(action == RankSubscriberAction.SET_RANK_SUFFIX) {
                        Rank rank = Rank.getByName(payload.get("rank").getAsString());
                       
                        if(rank != null) {
                            Player player = Bukkit.getPlayer(payload.get("player").getAsString());
                            rank.getData().setSuffix(payload.get("suffix").getAsString());
                           
                            if(player != null) {
                                player.sendMessage(ChatColor.GREEN + "RankCommand named '" + rank.getData().getName() + "' suffix successfully changed.");
                            }
                        }
                    }
                }
            }
        };
    }
}
