package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.games.EventFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.timers.HomeHandler;
import secondlife.network.hcfactions.timers.SpawnTagHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.StringUtils;

public class FactionHomeCommand extends SubCommand {

	public FactionHomeCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "home" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

        if(args.length >= 2 && args[1].equalsIgnoreCase("set")) {
        	player.sendMessage(Color.translate("&cUsage: /f home"));
            return;
        }
        
        if(SpawnTagHandler.isActive(player)) {
            player.sendMessage(Color.translate("&cYou can't warp whilst your &lSpawn Tag&c timer is active."));
            return;
        }

        if(HomeHandler.isActive(player)) {
            player.sendMessage(Color.translate("&cYour home timer is already active."));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player.getName());

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(playerFaction.getHome() == null) {
            player.sendMessage(Color.translate("&cYour faction doesn't have a home set."));
            return;
        }

        Location home = StringUtils.destringifyLocation(playerFaction.getHome());

        if(home == null) {
            player.sendMessage(Color.translate("&cYour faction doesn't have a home set."));
            return;
        }

        Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(player.getLocation());

        if(factionAt instanceof EventFaction) {
            player.sendMessage(Color.translate("&cYou can't warp whilst in event zones."));
            return;
        }

        
		if(factionAt != playerFaction && factionAt instanceof PlayerFaction) {
			player.sendMessage(Color.translate("&cYou may not warp in enemy claims. Use &l/f stuck&c if you are trapped."));
			return;
		}

        int seconds;
        
        if(factionAt.isSafezone()) {
            seconds = 0;
        } else {
            switch(player.getWorld().getEnvironment()) {
            case THE_END:
                player.sendMessage(Color.translate("&cYou can't teleport to your faction home whilst in &lThe End&c."));
                return;
            case NETHER:
                seconds = 15;
                break;
            default:
                seconds = 10;
                break;
            }
        }

        HomeHandler.teleport(player, home,  seconds, TeleportCause.COMMAND);
    }
}
