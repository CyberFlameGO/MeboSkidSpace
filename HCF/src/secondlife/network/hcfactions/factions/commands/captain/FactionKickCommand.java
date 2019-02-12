package secondlife.network.hcfactions.factions.commands.captain;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.game.events.eotw.EOTWHandler;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;

public class FactionKickCommand extends SubCommand {

    public FactionKickCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "kick" };
		this.forPlayerUseOnly = true;
    }
    
	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
 		
		if(args.length < 2) {
			player.sendMessage(Color.translate("&cUsage: /f kick <player>"));
			return;
		}

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

		if(playerFaction == null) {
			player.sendMessage(HCFUtils.NO_FACTION);
			return;
		}

		if(playerFaction.isRaidable() && !EOTWHandler.isEOTW()) {
			player.sendMessage(Color.translate("&cYou cannot kick players while your faction is raidable!"));
			return;
		}
		
		String target = args[1];
		
		if(playerFaction.getMember(target) == null) {
			player.sendMessage(Color.translate("&cYour faction doesn't have a member named &l" + args[1] + "&c!!"));
			return;
		}

		Role selfRole = playerFaction.getMember(player.getName()).getRole();

		if(!(selfRole == Role.CAPTAIN || selfRole == Role.COLEADER || selfRole == Role.LEADER)) {
			player.sendMessage(HCFUtils.INVALID_ROLE);
			return;
		}
		
		Role targetRole = playerFaction.getMember(target).getRole();

		if(targetRole == Role.LEADER) {
			player.sendMessage(HCFUtils.INVALID_ROLE);
			return;
		}

		if(targetRole == Role.CAPTAIN && selfRole == Role.CAPTAIN) {
			player.sendMessage(HCFUtils.INVALID_ROLE);
			return;
		}

		Player onlineTarget = playerFaction.getMember(target).toOnlinePlayer();
		
		if(playerFaction.removeMember(sender, onlineTarget, playerFaction.getMember(target).getName(), true)) {
			if(onlineTarget != null) {
				onlineTarget.sendMessage(Color.translate("&cYou were kicked from the faction by &l" + sender.getName() + "&c!"));

				VituzNametag.reloadPlayer(onlineTarget);
				VituzNametag.reloadOthersFor(onlineTarget);
			}

			playerFaction.broadcast("&d" + args[1] + " &ehas been kicked by &2" + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + "&e!");
		}
	}

    /*public void execute(CommandSender sender, String[] args) {
    	Player player = (Player) sender;
    	
    	if(args.length < 2) {
    		player.sendMessage(Color.translate("&cUsage: /f kick <player>"));
		} else {
			PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);
			
			if(playerFaction == null) {
				player.sendMessage(HCFUtils.NO_FACTION);
			} else if(playerFaction.isRaidable() && !EOTWHandler.isEOTW()) {
				player.sendMessage(Color.translate("&cYou can't kick players while your faction is raidable!"));
			} else {
				FactionMember targetMember = playerFaction.getMember(args[1]);
				
				if(targetMember == null) {
					player.sendMessage(Color.translate("&cYour faction doesn't have a member named &l" + args[1] + "&c!"));
				} else {
					Role selfRole = playerFaction.getMember(player.getUniqueId()).getRole();
					
					if(selfRole == Role.MEMBER) {
						player.sendMessage(HCFUtils.INVALID_ROLE);
					} else {
						Role targetRole = targetMember.getRole();
						
						if(targetRole == Role.LEADER) {
							player.sendMessage(HCFUtils.INVALID_ROLE);
						} else if(targetRole == Role.COLEADER && selfRole != Role.LEADER) {
							player.sendMessage(HCFUtils.INVALID_ROLE);
						} else if(targetRole == Role.CAPTAIN && selfRole == Role.CAPTAIN) {
							player.sendMessage(HCFUtils.INVALID_ROLE);
						} else if(playerFaction.setMember(targetMember.getUniqueID(), null, true)) {
							Player onlineTarget = targetMember.toOnlinePlayer();
							
							if(onlineTarget != null) {
								onlineTarget.sendMessage(Color.translate("&cYou were kicked from the faction by &l" + sender.getName() + "&c!"));
								
								BMNametagHandler.reloadPlayer(onlineTarget);
								BMNametagHandler.reloadOthersFor(onlineTarget);
							}

							playerFaction.broadcast("&d" + args[1] + " &ehas been kicked by &2" + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + "&e!");
						}
					}
				}
			}
		}
	}*/
}
