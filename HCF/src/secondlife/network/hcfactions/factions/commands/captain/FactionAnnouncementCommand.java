package secondlife.network.hcfactions.factions.commands.captain;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionAnnouncementCommand extends SubCommand {

    public FactionAnnouncementCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "announcement" };
		this.forPlayerUseOnly = true;
    }

    @Override
	public void execute(CommandSender sender, String[] args) {
    	Player player = (Player) sender;
    	
    	if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f announcement <text>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(playerFaction.getMember(player.getName()).getRole() == Role.MEMBER) {
        	player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        String oldAnnouncement = playerFaction.getAnnouncement();
        String newAnnouncement;
        
        if(args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("remove")) {
            newAnnouncement = null;
        } else {
            newAnnouncement = StringUtils.join(args, ' ', 1, args.length);
        }

        if(oldAnnouncement == null && newAnnouncement == null) {
            player.sendMessage(Color.translate("&cYour faction's announcement is already unset!"));
            return;
        }

        if(oldAnnouncement != null && newAnnouncement != null && oldAnnouncement.equals(newAnnouncement)) {
            player.sendMessage(Color.translate("&cYour faction's announcement is already &l" + newAnnouncement + "&c!"));
            return;
        }

        playerFaction.setAnnouncement(newAnnouncement);

        if(newAnnouncement == null) {
            playerFaction.broadcast("&d" + sender.getName() + " &ehas cleared the faction's announcement.");
            return;
        }

        playerFaction.broadcast("&d" + player.getName() + " &ehas updated the faction's announcement from &d" + (oldAnnouncement != null ? oldAnnouncement : "none") + " &eto &d" + newAnnouncement + "!");
    }
}
