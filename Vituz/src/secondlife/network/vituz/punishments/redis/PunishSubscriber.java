package secondlife.network.vituz.punishments.redis;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.punishments.PunishmentQueue;
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.ConfigFile;

@Getter
public class PunishSubscriber {
	
    private JedisPubSub jedisPubSub;
    private Jedis jedis;
    
    public PunishSubscriber() {
        jedis = new Jedis(Vituz.getInstance().getDatabaseManager().getAddres(), 6379);
       
        if(Vituz.getInstance().getConfig().getBoolean("DATABASE.AUTHENTICATION.ENABLED")) {
            jedis.auth(Vituz.getInstance().getConfig().getString("DATABASE.AUTHENTICATION.PASSWORD"));
        }
        
        this.subscribe();
    }
    
    public void subscribe() {
        jedisPubSub = this.get();
        
        new Thread() {
            public void run() {
                jedis.subscribe(jedisPubSub, "punishments");
            }
        }.start();
    }
    
    private JedisPubSub get() {
        return new JedisPubSub() {
            public void onMessage(String channel, String message) {
                if(channel.equalsIgnoreCase("punishments")) {
                    String[] args = message.split(";");
                   
                    if(args.length > 2) {
                        String command = args[0];
                        String subCommand = args[1];
                        String where = args[2];

                        if(where.equalsIgnoreCase(VituzAPI.getServerName())) {
                            switch (command) {
                                case "punishment": {
                                    PunishmentType type = PunishmentType.valueOf(subCommand);

                                    String name = args[3];
                                    String senderName = args[4];
                                    String reason = args[5];
                                    boolean silent = Boolean.valueOf(args[6]);
                                    String tempbanServerName = args[7];

                                    long duration = 2147483647L;

                                    if(args.length == 9) {
                                        duration = Long.valueOf(args[8]);

                                        if(type == PunishmentType.BAN) {
                                            type = PunishmentType.TEMPBAN;
                                        }
                                    }

                                    PunishData profile = PunishData.getByName(name);

                                    Punishment punishment = new Punishment(type, senderName, System.currentTimeMillis() + 500L, duration, reason, tempbanServerName);

                                    Player player = Bukkit.getPlayer(name);

                                    if(player != null && type != PunishmentType.MUTE) {
                                        PunishmentType finalType = type;

                                        new BukkitRunnable() {
                                            public void run() {
                                                player.kickPlayer(finalType.getMessage());

                                                if(finalType == PunishmentType.BLACKLIST) {
                                                    for(Player other : Bukkit.getOnlinePlayers()) {
                                                        if(other.getAddress().getAddress().getHostAddress().equals(player.getAddress().getAddress().getHostAddress())) {
                                                            other.kickPlayer(Color.translate(
                                                                    "&cYour account has been blacklisted from the " + Vituz.getInstance().getEssentialsManager().getServerName() + " Network."
                                                                            + "\n&cThis punishment is in relation to " + player.getName()
                                                                            + ".\n&cThis punishment cannot be appealed."));
                                                        }
                                                    }
                                                }

                                                if(finalType == PunishmentType.IPBAN) {
                                                    for(Player other : Bukkit.getOnlinePlayers()) {
                                                        if(other.getAddress().getAddress().getHostAddress().equals(player.getAddress().getAddress().getHostAddress())) {
                                                            other.kickPlayer(Color.translate(
                                                                    "&cYour account has been ipbanned from the " + Vituz.getInstance().getEssentialsManager().getServerName() + " Network."
                                                                            + "\n&cThis punishment is in relation to " + player.getName()
                                                                            + ".\n&cTo appeal visit " + Vituz.getInstance().getEssentialsManager().getAppealAt()));
                                                        }
                                                    }
                                                }
                                            }
                                        }.runTask(Vituz.getInstance());
                                    }

                                    profile.getPunishments().add(punishment.announce(name, senderName, silent, false));

                                    PunishmentQueue queue = PunishmentQueue.get(name, type);

                                    if(queue != null) {
                                        profile.save();

                                        PunishmentQueue.getQueues().remove(queue);

                                        /*Bukkit.getScheduler().runTask(Vituz.getPlugin(), () -> {
                                            profile.load();
                                        });*/
                                    }
                                }
                                break;

                                case "undo": {
                                    PunishmentType type = PunishmentType.valueOf(subCommand);

                                    String name = args[3];
                                    String senderName = args[4];
                                    String reason = args[5];
                                    String server = args[6];
                                    boolean silent = Boolean.valueOf(args[7]);

                                    new Punishment(type, senderName, System.currentTimeMillis(), 2147483647L,
                                            reason, server).announce(name, senderName, silent, true);

                                    PunishData profile = PunishData.getByName(name);

                                    profile.save();

                                    /*Bukkit.getScheduler().runTask(Vituz.getPlugin(), () -> {
                                        profile.load();
                                    });*/
                                }
                                break;
                            }
                        }
                    }
                }
            }
        };
    }
}
