package secondlife.network.bungee.commands;

import java.util.HashMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import secondlife.network.bungee.utils.Color;

public class StaffListCommand extends Command {

	private int total = 0;
	private HashMap<String, String> staff = new HashMap<>();
	
	public StaffListCommand() {
		super("stafflist", "secondlife.staff", "staffl");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("secondlife.staff")) {
			sender.sendMessage(Color.translate("&cNo Permission"));
			return;
		}
		
		if(args.length == 0) {
			
			total = 0;
			
			if(!staff.isEmpty()) {
				staff.clear();
			}
			
			for(ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
				if(online.hasPermission("secondlife.staff")) {
					if(online.getServer() != null && online != null) {
						if(staff.containsKey(online.getServer().getInfo().getName())) {
							staff.put(online.getServer().getInfo().getName(), staff.get(online.getServer().getInfo().getName()) + " " + online.getName());
						} else {
							staff.put(online.getServer().getInfo().getName(), online.getName() + " ");
						}
						total++;
					}
				}
			}
			
			if(staff.isEmpty()) {
				sender.sendMessage(Color.translate("&cThere is no staff online!"));
				return;
			}
			
			sender.sendMessage(Color.translate("&aStaff Online&7:"));
			
			for(ServerInfo server : ProxyServer.getInstance().getServers().values()) {
				if(this.staff.containsKey(server.getName())) {
					sender.sendMessage(Color.translate("&9[&b" + server.getName() + "&9] &6- &c" + staff.get(server.getName().toString())));
				}
			}
			
			sender.sendMessage(Color.translate("&aTotal Staff&7: &c" + total));
		}
	}
}