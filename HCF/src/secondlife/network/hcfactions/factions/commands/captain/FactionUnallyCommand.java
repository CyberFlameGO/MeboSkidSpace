package secondlife.network.hcfactions.factions.commands.captain;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.utils.events.FactionRelationRemoveEvent;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionUnallyCommand extends SubCommand {

    public FactionUnallyCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "unally", "neutral" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(HCFConfiguration.maxAllysPerFaction <= 0) {
        	player.sendMessage(Color.translate("&cAllies are currently disabled."));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f unally <all|name>"));
            return;
        }

        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if (playerFaction.getMember(player.getName()).getRole() == Role.MEMBER) {
            player.sendMessage(HCFUtils.INVALID_ROLE);
            return;
        }

        Relation relation = Relation.ALLY;
        Collection<PlayerFaction> targetFactions = new HashSet<>();

        if (args[1].equalsIgnoreCase("all")) {
            Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
            if (allies.isEmpty()) {
                player.sendMessage(Color.translate("&cYour faction don't have any allies!"));
                return;
            }

            targetFactions.addAll(allies);
        } else {
            Faction searchedFaction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

            if (!(searchedFaction instanceof PlayerFaction)) {
            	player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
                return;
            }

            targetFactions.add((PlayerFaction) searchedFaction);
        }

        for(PlayerFaction targetFaction : targetFactions) {
            if(playerFaction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(playerFaction.getUniqueID()) == null) {
                player.sendMessage(Color.translate("&eYour faction isn't &d" + relation.getDisplayName() + " &ewith " + targetFaction.getDisplayName(playerFaction) + "&e!"));
                return;
            }

            FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(playerFaction, targetFaction, Relation.ALLY);
            Bukkit.getPluginManager().callEvent(event);

            if(event.isCancelled()) {
                player.sendMessage(Color.translate("&cCouldn't drop &l" + relation.getDisplayName() + " &cwith &l" + targetFaction.getDisplayName(playerFaction) + "&c!"));
                return;
            }

            playerFaction.broadcast("&eYour faction has dropped its " + relation.getDisplayName() + " &ewith " + targetFaction.getDisplayName(playerFaction) + "&e");
            targetFaction.broadcast(playerFaction.getDisplayName(targetFaction) + " &ehas dropped their " + relation.getDisplayName() + " &ewith your faction.");
        }
    }
}
