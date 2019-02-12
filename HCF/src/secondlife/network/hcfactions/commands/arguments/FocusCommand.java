package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FocusCommand extends BaseCommand {

	public static Map<PlayerFaction, UUID> focus = new HashMap<PlayerFaction, UUID>();
	
	public FocusCommand(HCF plugin) {
		super(plugin);
		
		this.command = "focus";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(HCFData.getByName(player.getName()).isEvent()) {
			player.sendMessage(Color.translate("&cYou can't do this in your current state."));
			return;
		}

		if(args.length == 1) {
			PlayerFaction faction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

			if(faction == null) {
				player.sendMessage(HCFUtils.NO_FACTION);
				return;
			}

			if(faction.getMember(player.getName()).getRole() == Role.MEMBER) {
				player.sendMessage(HCFUtils.INVALID_ROLE);
				return;
			}

			Player target = Bukkit.getPlayer(args[0]);

			if(Msg.checkOffline(player, args[0])) return;
			
			if(focus.containsKey(faction)) {
				if(focus.get(faction).equals(target.getUniqueId())) {
					Player oldTarget = Bukkit.getPlayer(FocusCommand.focus.get(faction));

					if(oldTarget != null) {
						for(Player member : faction.getOnlinePlayers()) {
							VituzNametag.reloadPlayer(oldTarget);
							VituzNametag.reloadOthersFor(member);
						}
					}

					focus.remove(faction);

					this.sendUnFocusFactionMessage(faction, target);
				} else {
					for(Player member : faction.getOnlinePlayers()) {
						if(target != member) {
							Player oldTarget = Bukkit.getPlayer(focus.get(faction));

							if(oldTarget != null) {
								VituzNametag.reloadPlayer(oldTarget);
								VituzNametag.reloadOthersFor(member);
							}

							focus.put(faction, target.getUniqueId());
							
							this.sendFactionMessage(faction, target);
							this.broadcastFocus(faction);
						} else {
							player.sendMessage(Color.translate("&cYou can't focus faction members."));
						}
					}
				}
			} else {
				for(Player member : faction.getOnlinePlayers()) {
					if(target != member) {
						focus.put(faction, target.getUniqueId());
						this.sendFactionMessage(faction, target);
						this.broadcastFocus(faction);
					} else {
						player.sendMessage(Color.translate("&cYou can't focus faction members."));
					}
				}
			}
		} else {
			sendUsage(sender);
		}
	}
	
	
	public void broadcastFocus(PlayerFaction faction) {
		if(FocusCommand.focus.containsKey(faction)) {
			Player target = Bukkit.getPlayer(FocusCommand.focus.get(faction));
			
			if(target == null) return;
			
			for(Player others : faction.getOnlinePlayers()) {
				VituzNametag.reloadPlayer(target);
				VituzNametag.reloadOthersFor(others);
			}
		}
	}

	public void sendFactionMessage(PlayerFaction faction, Player target) {
		faction.broadcast("");
		faction.broadcast("&7&l* &b&lYour faction is now focusing on &a&l" + target.getName() + "&b&l.");
		faction.broadcast("");
	}
	
	public void sendUnFocusFactionMessage(PlayerFaction faction, Player target) {
		faction.broadcast("");
		faction.broadcast("&7&l* &c&lYour faction is no longer focusing &4&l" + target.getName() + "&c&l.");
		faction.broadcast("");
	}
	
	public void sendUsage(CommandSender sender) {
		sender.sendMessage(Color.translate("&cUsage: /focus <playerName>."));
	}
	
}