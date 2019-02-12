package secondlife.network.hcfactions.factions.commands.captain;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.utils.events.FactionRelationCreateEvent;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionAllyCommand extends SubCommand {

    private static Relation relation = Relation.ALLY;

    public FactionAllyCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "ally", "truce" };
		this.forPlayerUseOnly = true;
    }
    
    @Override
	public void execute(CommandSender sender, String[] args) {
    	Player player = (Player) sender;
    	
    	 if(HCFConfiguration.maxAllysPerFaction <= 0) {
             player.sendMessage(Color.translate("&cAllies are currently disabled."));
             return;
         }

         if(args.length < 2) {
             player.sendMessage(Color.translate("&cUsage: /f ally <faction>"));
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

         Faction containingFaction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

         if(!(containingFaction instanceof PlayerFaction)) {
             player.sendMessage(HCFUtils.FACTION_NOT_FOUND);
             return;
         }

         PlayerFaction targetFaction = (PlayerFaction) containingFaction;

         if(playerFaction == targetFaction) {
             player.sendMessage(Color.translate("&cYou can't send requests to your own faction!"));
             return;
         }

         Collection<UUID> allied = playerFaction.getAllied();

         if(allied.size() >= HCFConfiguration.maxAllysPerFaction) {
             player.sendMessage(Color.translate("&cYour faction has already reached the alliance limit."));
             return;
         }

         if(targetFaction.getAllied().size() >= HCFConfiguration.maxAllysPerFaction) {
             player.sendMessage(Color.translate("&c&l" + targetFaction.getDisplayName(sender) + " &chas reached their maximum alliance limit!"));
             return;
         }

         if(allied.contains(targetFaction.getUniqueID())) {
             player.sendMessage(Color.translate("&cYour faction is already in relation ship with &l" + relation.getDisplayName() + targetFaction.getDisplayName(playerFaction) + "&c!"));
             return;
         }

         if(targetFaction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null) {
             FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, targetFaction, relation);
             Bukkit.getPluginManager().callEvent(event);

             targetFaction.getRelations().put(playerFaction.getUniqueID(), relation);
             targetFaction.broadcast("&eYour faction is now &d" + relation.getDisplayName() + " &ewith &d" + playerFaction.getDisplayName(targetFaction) + "&e!");

             playerFaction.getRelations().put(targetFaction.getUniqueID(), relation);
             playerFaction.broadcast("&eYour faction is now &d" + relation.getDisplayName() + " &ewith &d" + targetFaction.getDisplayName(playerFaction) + "&e!");
             return;
         }

         if(playerFaction.getRequestedRelations().putIfAbsent(targetFaction.getUniqueID(), relation) != null) {
             player.sendMessage("&eYour faction has already requested to " + relation.getDisplayName() + " &ewith " + targetFaction.getDisplayName(playerFaction) + "&e!");
             return;
         }

         playerFaction.broadcast(targetFaction.getDisplayName(playerFaction) + " &ewere informed that you wish to be &d" + relation.getDisplayName());
         targetFaction.broadcast(playerFaction.getDisplayName(targetFaction) + " &ehas sent a request to be " + relation.getDisplayName() + ". Use " + HCFConfiguration.allyColor + "&e/faction ally " + playerFaction.getName() + " &eto accept!");
    }
}
