package secondlife.network.vituz.ranks.commands;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.data.RankData;
import secondlife.network.vituz.ranks.grant.Grant;
import secondlife.network.vituz.ranks.grant.procedure.GrantProcedure;
import secondlife.network.vituz.ranks.grant.procedure.GrantProcedureData;
import secondlife.network.vituz.ranks.grant.procedure.GrantRecipient;
import secondlife.network.vituz.ranks.redis.RankPublisher;
import secondlife.network.vituz.ranks.redis.RankSubscriberAction;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.DateUtil;
import secondlife.network.vituz.utilties.Permission;

public class GrantCommand extends BaseCommand {
	
    public GrantCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "grant";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            if(args.length < 4) {
                sender.sendMessage(Color.translate("&cUsage: /grant <player> <rank> <duration> <reason>"));
                return;
            }
            
            Player target = Bukkit.getPlayer(args[0]);

            RankData profile;

            if(target == null) {
                profile = RankData.getByName(args[0]);
            } else {
                profile = RankData.getByName(target.getName());
            }

            if (!profile.isLoaded()) {
                profile.load();
            }

            Rank rank = Rank.getByName(args[1]);
          
            if(rank == null) {
                sender.sendMessage(ChatColor.RED + "Failed to find rank.");
                return;
            }
            
            long duration;
            
            if(args[2].equalsIgnoreCase("perm") || args[2].equalsIgnoreCase("permanent")) {
                duration = 2147483647L;
            } else {
                try {
                    duration = System.currentTimeMillis() - DateUtil.parseDateDiff(args[2], false);
                } catch(Exception e) {
                    sender.sendMessage(Color.translate("&cInvalid duration."));
                    return;
                }
            }
            
            StringBuilder sb = new StringBuilder();
            
            for(int i = 3; i < args.length; ++i) {
                sb.append(args[i]).append(" ");
            }
            
            String reason = sb.toString().trim();

            for(Grant grant : profile.getGrants()) {
                if(!grant.getRank().getData().isDefaultRank() && !grant.isExpired()) {
                    grant.setActive(false);
                }
            }

            Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), duration, reason, true);

            profile.getGrants().add(newGrant);
            profile.setupAtatchment();


            if(target == null) {
                JsonObject object = new JsonObject();
                object.addProperty("action", RankSubscriberAction.ADD_GRANT.name());

                JsonObject payload = new JsonObject();
                payload.addProperty("name", profile.getName());

                JsonObject grant = new JsonObject();
                grant.addProperty("rank", rank.getUuid().toString());
                grant.addProperty("datedAdded", System.currentTimeMillis());
                grant.addProperty("duration", duration);
                grant.addProperty("reason", reason);

                payload.add("grant", grant);
                object.add("payload", payload);

                RankPublisher.write(object.toString());
            } else {
                target.sendMessage(Color.translate("&aYour rank has been set to " + newGrant.getRank().getData().getColorPrefix() + newGrant.getRank().getData().getName() + "."));
            }

            profile.save();
            sender.sendMessage(ChatColor.GREEN + "Grant successfully created.");
        } else {
        	Player player = (Player) sender;
            
        	if(args.length == 0) {
            	player.sendMessage(ChatColor.RED + "Usage: /grant <player>");
                return;
            }
            
            GrantProcedure procedure = GrantProcedure.getByPlayer(player);
            
            if(procedure != null) {
                if(args[0].equalsIgnoreCase("cancel")) {
                	player.sendMessage(" ");
                	player.sendMessage(ChatColor.RED + "Grant procedure cancelled.");
                	player.sendMessage(" ");
                   
                	GrantProcedure.getProcedures().remove(procedure);
                } else if(args[0].equalsIgnoreCase("confirm")) {
                    GrantProcedure.getProcedures().remove(procedure);
                    GrantProcedure.getData().setCreated(System.currentTimeMillis());
                    
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "Grant successfully created.");
                    player.sendMessage(" ");
                    
                    RankData data = RankData.getByName(GrantProcedure.getRecipient().getName());

                    if(!data.isLoaded()) {
                        data.load();
                    }

                    Player player2 = Bukkit.getPlayer(data.getName());

                    if(data.getName() == null || !data.getName().equals(GrantProcedure.getRecipient().getName())) {
                        data.setName(GrantProcedure.getRecipient().getName());
                    }

                    for(Grant grant : data.getGrants()) {
                        if(!grant.getRank().getData().isDefaultRank() && !grant.isExpired()) {
                            grant.setActive(false);
                        }
                    }

                    Grant newgrant = new Grant(player.getName(), GrantProcedure.getData().getRank(), GrantProcedure.getData().getCreated(), GrantProcedure.getData().getDuration(), GrantProcedure.getData().getReason(), true);
                    data.getGrants().add(newgrant);
                    data.setupAtatchment();

                    if(player2 == null) {
                        JsonObject object2 = new JsonObject();
                        object2.addProperty("action", RankSubscriberAction.ADD_GRANT.name());

                        JsonObject payload = new JsonObject();
                        payload.addProperty("name", data.getName());

                        JsonObject grant = new JsonObject();
                        grant.addProperty("issuer", player.getUniqueId().toString());
                        grant.addProperty("rank", GrantProcedure.getData().getRank().getUuid().toString());
                        grant.addProperty("datedAdded", GrantProcedure.getData().getCreated());
                        grant.addProperty("duration", GrantProcedure.getData().getDuration());
                        grant.addProperty("reason", GrantProcedure.getData().getReason());
                        payload.add("grant", grant);
                        object2.add("payload", payload);

                        RankPublisher.write(object2.toString());
                    } else {
                        player2.sendMessage(ChatColor.GREEN + "Your rank has been set to " + newgrant.getRank().getData().getColorPrefix() + newgrant.getRank().getData().getName() + ChatColor.GREEN + ".");
                    }
                    data.save();
                } else {
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.RED + "You're already in a grant procedure.");
                    sender.sendMessage(ChatColor.RED + "Please enter a valid duration or type 'cancel' to cancel.");
                    sender.sendMessage(" ");
                }
                
                return;
            }
            
            if(args[0].equalsIgnoreCase("confirm") || args[0].equalsIgnoreCase("cancel")) return;
            
            Player players = Bukkit.getPlayer(args[0]);

            RankData profile;

            if(players == null) {
                profile = RankData.getByName(args[0]);
            } else {
                profile = RankData.getByName(players.getName());
            }

            if (!profile.isLoaded()) {
                profile.load();
            }

            new GrantProcedure(new GrantRecipient(args[0]), player.getUniqueId(), new GrantProcedureData());
            player.openInventory(GrantProcedure.getInventory());
        }
    }
}
