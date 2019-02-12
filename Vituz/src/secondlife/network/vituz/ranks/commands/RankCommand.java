package secondlife.network.vituz.ranks.commands;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.ranks.RankData;
import secondlife.network.vituz.ranks.redis.RankPublisher;
import secondlife.network.vituz.ranks.redis.RankSubscriberAction;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RankCommand extends BaseCommand {
	
    public RankCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "rank";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cRank - Help Commands"));
	        sender.sendMessage(Color.translate("&c/rank addinher"));
	        sender.sendMessage(Color.translate("&c/rank addperm"));
	        sender.sendMessage(Color.translate("&c/rank create"));
	        sender.sendMessage(Color.translate("&c/rank delete"));
	        sender.sendMessage(Color.translate("&c/rank deleteinher"));
	        sender.sendMessage(Color.translate("&c/rank deletepermission"));
	        sender.sendMessage(Color.translate("&c/rank listpermissions"));
	        sender.sendMessage(Color.translate("&c/rank prefix"));
	        sender.sendMessage(Color.translate("&c/rank suffix"));	
	        sender.sendMessage(Color.translate("&c/rank import"));
		} else {
			if(args[0].equalsIgnoreCase("addinheritance") || args[0].equalsIgnoreCase("addinher")) {
				if(args.length != 3) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank addinheritance <baseRank> <rankBaseWillInherit>");
		            return;
		        }
				
		        Rank rank = Rank.getByName(args[1]);
		      
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        Rank inheritance = Rank.getByName(args[2]);
		       
		        if(inheritance == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[2] + "' not found.");
		            return;
		        }
		        
		        if(rank.getInheritance().contains(inheritance.getUuid())) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + rank.getData().getName() + "' already inherits '" + inheritance.getData().getName() + "'.");
		            return;
		        }
		        
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.ADD_INHERITANCE.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("player", sender.getName());
		        payload.addProperty("rank", rank.getUuid().toString());
		        payload.addProperty("inheritance", inheritance.getUuid().toString());
		        
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("addpermission") || args[0].equalsIgnoreCase("addperm")) {
		        if(args.length != 3) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank addpermission <rank> <permission>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		        
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        String permission = args[2].toLowerCase();
		      
		        if(rank.getPermissions().contains(permission)) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + rank.getData().getName() + "' already has permission node '" + permission + "'.");
		            return;
		        }
		        
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.ADD_RANK_PERMISSION.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("player", sender.getName());
		        payload.addProperty("rank", rank.getUuid().toString());
		        payload.addProperty("permission", permission);
		        
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("createrank") || args[0].equalsIgnoreCase("create")) {
		        if(args.length == 1) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank create <name>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		       
		        if(rank != null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' already exists.");
		            return;
		        }
		        
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.ADD_RANK.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("name", args[1]);
		        payload.addProperty("player", sender.getName());
		        
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("deleterank") || args[0].equalsIgnoreCase("delete")) {
		        if(args.length == 1) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank delete <rank>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		        
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.DELETE_RANK.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("rank", rank.getData().getName());
		        payload.addProperty("player", sender.getName());
		        
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("deleteinheritance") || args[0].equalsIgnoreCase("deleteinher")) {
		        if(args.length != 3) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank deleteinheritance <baseRank> <rankBaseWillInherit>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		        
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        Rank inheritance = Rank.getByName(args[2]);
		        if(inheritance == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[2] + "' not found.");
		            return;
		        }
		        
		        if(!rank.getInheritance().contains(inheritance.getUuid())) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + rank.getData().getName() + "' does not inherit '" + inheritance.getData().getName() + "'.");
		            return;
		        }
		        
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.DELETE_INHERITANCE.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("player", sender.getName());
		        payload.addProperty("rank", rank.getUuid().toString());
		        payload.addProperty("inheritance", inheritance.getUuid().toString());
		        
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("deletepermission") || args[0].equalsIgnoreCase("deleteperm")) {
		        if(args.length != 3) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank deletepermission <rank> <permission>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		        
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        String permission = args[2].toLowerCase();
		        
		        if(!rank.getPermissions().contains(permission)) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + rank.getData().getName() + "' doesn't have permission node '" + permission + "'.");
		            return;
		        }
		        
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.DELETE_RANK_PERMISSION.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("player", sender.getName());
		        payload.addProperty("rank", rank.getUuid().toString());
		        payload.addProperty("permission", permission);
		        
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("listpermissions") || args[0].equalsIgnoreCase("listperm")) {
		        if(args.length != 2) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank listpermissions <rank>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		      
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        sender.sendMessage(ChatColor.GREEN + "Listing permissions of " + rank.getData().getColorPrefix() + rank.getData().getName() + ChatColor.GREEN + ":");
		        sender.sendMessage(ChatColor.GREEN + "Base permissions:");
		        
		        for(String permission : rank.getPermissions()) {
		            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + permission);
		        }
		        
		        for(UUID inheritance : rank.getInheritance()) {
		            Rank other = Rank.getByUuid(inheritance);
		            
		            if(other != null) {
		                int count = 0;
		               
		                for(String permission2 : other.getPermissions()) {
		                    if(!rank.getPermissions().contains(permission2)) {
		                        if(count == 0) {
		                            sender.sendMessage(ChatColor.GREEN + "Permissions inherited from " + other.getData().getColorPrefix() + other.getData().getName() + ChatColor.GREEN + ":");
		                        }
		                        
		                        ++count;
		                        
		                        sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + permission2);
		                    }
		                }
		            }
		        }
			} else if(args[0].equalsIgnoreCase("prefix") || args[0].equalsIgnoreCase("setprefix")) {
		        if(args.length <= 2) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank setprefix <rank> <prefix>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		        
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        StringBuilder sb = new StringBuilder();
		        
		        for(int i = 2; i < args.length; ++i) {
		            sb.append(args[i]).append(" ");
		        }
		        
		        String prefix = sb.toString().trim().replace("\"", "");
		       
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.SET_RANK_PREFIX.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("rank", rank.getData().getName());
		        payload.addProperty("player", sender.getName());
		        payload.addProperty("prefix", prefix);
		       
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("suffix") || args[0].equalsIgnoreCase("setsuffix")) {
		        if(args.length <= 2) {
		            sender.sendMessage(ChatColor.RED + "Usage /rank setsuffix <rank> <suffix>");
		            return;
		        }
		        
		        Rank rank = Rank.getByName(args[1]);
		        
		        if(rank == null) {
		            sender.sendMessage(ChatColor.RED + "Command named '" + args[1] + "' not found.");
		            return;
		        }
		        
		        StringBuilder sb = new StringBuilder();
		        
		        for(int i = 2; i < args.length; ++i) {
		            sb.append(args[i]).append(" ");
		        }
		        
		        String suffix = sb.toString().trim().replace("\"", "");
		        
		        JsonObject object = new JsonObject();
		        object.addProperty("action", RankSubscriberAction.SET_RANK_SUFFIX.name());
		        
		        JsonObject payload = new JsonObject();
		        payload.addProperty("rank", rank.getData().getName());
		        payload.addProperty("player", sender.getName());
		        payload.addProperty("suffix", suffix);
		        
		        object.add("payload", payload);
		        RankPublisher.write(object.toString());
			} else if(args[0].equalsIgnoreCase("import")) {
				if(args.length == 1) {
					ActionMessage actionMessage = new ActionMessage();
					actionMessage.addText("&ePlease confirm this action: ");
					actionMessage.addText("&a&lCONFIRM ").setClickEvent(ActionMessage.ClickableType.RunCommand, "/rank import confirm").addHoverText(Color.translate("&aConfirm this action"));
					actionMessage.sendToPlayer(((Player) sender));
				} else if(args[1].equalsIgnoreCase("confirm")) {
					Rank.getRanks().clear();

					plugin.getDatabaseManager().getRanksProfiles().drop();
					plugin.getDatabaseManager().getRanksGrants().drop();
	
					for(String key : plugin.getRanks().getKeys(false)) {
						String name = Vituz.getInstance().getRanks().getString(key + ".NAME");
						String prefix = Vituz.getInstance().getRanks().getString(key + ".PREFIX", "&d");
						String suffix = Vituz.getInstance().getRanks().getString(key + ".SUFFIX", "&d");
						
						boolean defaultRank = Vituz.getInstance().getRanks().getBoolean(key + ".DEFAULT");
						List<String> permissions = Vituz.getInstance().getRanks().getStringList(key + ".PERMISSIONS");
						
						RankData data = new RankData(name);
						
						data.setPrefix(prefix);
						data.setSuffix(suffix);
						data.setDefaultRank(defaultRank);
						
						new Rank(UUID.randomUUID(), new ArrayList<>(), permissions, data);
					}
					
					for(String key : Vituz.getInstance().getRanks().getKeys(false)) {
						Rank rank = Rank.getByName(Vituz.getInstance().getRanks().getString(key + ".NAME"));
						
						if(rank != null) {
							for(String name2 : Vituz.getInstance().getRanks().getStringList(key + ".INHERITANCE")) {
								Rank other = Rank.getByName(Vituz.getInstance().getRanks().getString(name2 + ".NAME"));
							
								if(other != null) {
									rank.getInheritance().add(other.getUuid());
								}
							}
						}
					}

					plugin.getRankManager().save();

					sender.sendMessage(ChatColor.GREEN + "Processing request..");
				
					new BukkitRunnable() {
						public void run() {
							JsonObject object = new JsonObject();
							object.addProperty("action", RankSubscriberAction.IMPORT_RANKS.name());
							
							JsonObject payload = new JsonObject();
							payload.addProperty("player", sender.getName());
							
							object.add("payload", payload);
							RankPublisher.write(object.toString());
						}
					}.runTaskLater(this.getPlugin(), 40L);
				}
			}
		}
    }
}
