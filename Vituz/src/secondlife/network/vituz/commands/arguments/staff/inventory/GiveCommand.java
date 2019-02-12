package secondlife.network.vituz.commands.arguments.staff.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.commands.BaseCommand;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class GiveCommand extends BaseCommand {

	public GiveCommand(Vituz plugin) {
		super(plugin);
		
		this.command = "give";
		this.permission = Permission.ADMIN_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		String amount = "";
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(args.length == 0) {
				player.sendMessage(Color.translate("&cUsage: /give <all|player> <item> <amount>"));
			} else {
				if(args[0].equalsIgnoreCase("all")) {
					if(plugin.getItemDB().getItem(args[1]) == null) {
						player.sendMessage(Color.translate("&cItem or ID not found."));
						return;
					}
					
					for(Player on : Bukkit.getOnlinePlayers()) {
						if(args.length == 2) {
							on.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], plugin.getItemDB().getItem(args[1]).getMaxStackSize()) } );
							
							amount = plugin.getItemDB().getItem(args[1]).getMaxStackSize() + "";
						}
						
						if(args.length == 3) {
							on.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], Integer.parseInt(args[2])) } );
							
							amount = args[2];
						}
					}
									
					Msg.sendMessage(player.getDisplayName() + " &egave all online players &d" + amount + " &eof &d" + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])));
				} else {
					Player target = Bukkit.getPlayer(args[0]);
					
					if(Msg.checkOffline(player, args[0])) return;
					
					if(plugin.getItemDB().getItem(args[1]) == null) {
						player.sendMessage(Color.translate("&cItem or ID not found."));
						return;
					}
					
					if(args.length == 2) {
						if(!target.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], plugin.getItemDB().getItem(args[1]).getMaxStackSize()) } ).isEmpty()) {
							player.sendMessage(Color.translate(target.getDisplayName() + "&c's inventory is full."));
							return;
						}
						
						amount = plugin.getItemDB().getItem(args[1]).getMaxStackSize() + "";
					}
					
					if(args.length == 3) {
						if(!target.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], Integer.parseInt(args[2])) } ).isEmpty()) {
							player.sendMessage(Color.translate(target.getDisplayName() + "&c's inventory is full."));
							return;
						}
						
						amount = args[2];
					}
					
					Msg.log(Bukkit.getConsoleSender(), "Player " + player.getName() + " has given " + target.getName() + " " + amount + " of " + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])) + ".");
					
					player.sendMessage(Color.translate("&eYou have given " + target.getDisplayName() + " &d" + amount + " &eof &d" + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])) + "&e."));
					target.sendMessage(Color.translate("&eYou have received &d" + amount + " &eof &d" + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])) + " &egiven by " + player.getDisplayName() + "&e."));
				}
			}
			
			return;
		}
		
		if(args.length == 0) {
			sender.sendMessage(Color.translate("&cUsage: /give <all|player> <item> <amount>"));
		} else {
			if(args[0].equalsIgnoreCase("all")) {
				if(plugin.getItemDB().getItem(args[1]) == null) {
					sender.sendMessage(Color.translate("&cItem or ID not found."));
					return;
				}
				
				for(Player on : Bukkit.getOnlinePlayers()) {
					if(args.length == 2) {
						on.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], plugin.getItemDB().getItem(args[1]).getMaxStackSize()) } );
						
						amount = plugin.getItemDB().getItem(args[1]).getMaxStackSize() + "";
					}
					
					if(args.length == 3) {
						on.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], Integer.parseInt(args[2])) } );
						
						amount = args[2];
					}
				}
								
				Msg.sendMessage(Msg.CONSOLE + " &egave all online players &d" + amount + " &eof &d" + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])));
			} else {
				Player target = Bukkit.getPlayer(args[0]);
				
				if(Msg.checkOffline(sender, args[0])) return;
				
				if(plugin.getItemDB().getItem(args[1]) == null) {
					sender.sendMessage(Color.translate("&cItem or ID not found."));
					return;
				}
				
				if(args.length == 2) {
					if(!target.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], plugin.getItemDB().getItem(args[1]).getMaxStackSize()) } ).isEmpty()) {
						sender.sendMessage(Color.translate(target.getDisplayName() + "&c's inventory is full."));
						return;
					}
					
					amount = plugin.getItemDB().getItem(args[1]).getMaxStackSize() + "";
				}
				
				if(args.length == 3) {
					if(!target.getInventory().addItem(new ItemStack[] { plugin.getItemDB().getItem(args[1], Integer.parseInt(args[2])) } ).isEmpty()) {
						sender.sendMessage(Color.translate(target.getDisplayName() + "&c's inventory is full."));
						return;
					}
					
					amount = args[2];
				}
				
				Msg.log(Bukkit.getConsoleSender(), "CONSOLE has given " + target.getName() + " " + amount + " of " + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])) + ".");
				
				sender.sendMessage(Color.translate("&eYou have given " + target.getDisplayName() + " &d" + amount + " &eof &d" + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])) + "&e."));
				target.sendMessage(Color.translate("&eYou have received &d" + amount + " &eof &d" + plugin.getItemDB().getName(plugin.getItemDB().getItem(args[1])) + " &egiven by " + Msg.CONSOLE + "&e."));
			}
		}
	}
}
