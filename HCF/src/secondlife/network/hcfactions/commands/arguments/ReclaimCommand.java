package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class ReclaimCommand extends BaseCommand {

	public ReclaimCommand(HCF plugin) {
		super(plugin);

		this.command = "reclaim";
		this.permission = Permission.DONOR_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(HCFData.getByName(player.getName()).isEvent()) {
			player.sendMessage(Color.translate("&cYou can't do this in your current state."));
			return;
		}

		HCFData data = HCFData.getByName(player.getName());

		if(args.length == 0) {
			if(!data.isReclaimed()) {
				if(VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Hydrogen")) {
					data.setBalance(data.getBalance() + 2000);
					player.sendMessage(Color.translate("&eYou have been given 2000$"));

					data.setReclaimed(true);
					Msg.sendMessage("&7[&5&lReclaim&7] &d" + player.getName() + " &dhas claimed their &5&lHydrogen Perks &dby typing &d/reclaim&d!");
				} else if (VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Nitrogen")) {
					data.setBalance(data.getBalance() + 4000);
					player.sendMessage(Color.translate("&eYou have been given 4000$"));

					data.setReclaimed(true);
					Msg.sendMessage("&7[&5&lReclaim&7] &d" + player.getName() + " &dhas claimed their &5&lNitrogen Perks &dby typing &d/reclaim&d!");
				} else if (VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Titanium")) {
					data.setBalance(data.getBalance() + 6000);
					player.sendMessage(Color.translate("&eYou have been given 6000$"));

					data.setReclaimed(true);
					Msg.sendMessage("&7[&5&lReclaim&7] &d" + player.getName() + " &dhas claimed their &5&lTitanium Perks &dby typing &d/reclaim&d!");
				} else if (VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Krypton")) {
					data.setBalance(data.getBalance() + 8000);
					player.sendMessage(Color.translate("&eYou have been given 8000$"));

					data.setReclaimed(true);
					Msg.sendMessage("&7[&5&lReclaim&7] &d" + player.getName() + " &dhas claimed their &5&lKrypton Perks &dby typing &d/reclaim&d!");
				} else if (VituzAPI.getRankName(player.getName()).equalsIgnoreCase("Xenon")) {
					data.setBalance(data.getBalance() + 10000);
					player.sendMessage(Color.translate("&eYou have been given 10000$"));

					data.setReclaimed(true);
					Msg.sendMessage("&7[&5&lReclaim&7] &d" + player.getName() + " &dhas claimed their &5&lXenon Perks &dby typing &d/reclaim&d!");
				} else {
					player.sendMessage(Color.translate("&eYou can't do this!"));
				}
			} else {
				player.sendMessage(Color.translate("&cYou have already used reclaim this map!"));
			}
		} else {
			if(args[0].equalsIgnoreCase("set")) {
				if(!player.hasPermission(Permission.ADMIN_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return;
				}

				if(args.length < 3) {
					player.sendMessage(Color.translate("&cUsage: /reclaim set <player> <true|false>"));
					return;
				}

				Player target = Bukkit.getPlayer(args[1]);

				if(Msg.checkOffline(player, args[1])) return;

				HCFData tdata = HCFData.getByName(target.getName());

				if(args[2].equalsIgnoreCase("true")) {
					tdata.setReclaimed(true);

					player.sendMessage(Color.translate("&eReclaim of &d" + args[1] + " &ehas been set to &atrue&d!"));
				} else if(args[2].equalsIgnoreCase("false")) {
					tdata.setReclaimed(false);

					player.sendMessage(Color.translate("&eReclaim of &d" + args[1] + " &ehas been set to &cfalse&d!"));
				}
			} else if(args[0].equalsIgnoreCase("check")) {
				if(!player.hasPermission(Permission.STAFF_PERMISSION)) {
					player.sendMessage(Msg.NO_PERMISSION);
					return;
				}

				if(args.length < 2) {
					player.sendMessage(Color.translate("&cUsage: /reclaim check <player>"));
					return;
				}

				Player target = Bukkit.getPlayer(args[1]);

				if(Msg.checkOffline(player, args[1])) return;

				HCFData tdata = HCFData.getByName(target.getName());

				if(tdata.isReclaimed()) {
					player.sendMessage(Color.translate("&d" + args[1] + " &aalready &eused reclaim this map!"));
				} else {
					player.sendMessage(Color.translate("&d" + args[1] + " &cdidn't &euse reclaim this map!"));
				}
			}
		}
	}

}