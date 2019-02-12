package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.cuboid.Cuboid;

public class FactionClaimforCommand extends SubCommand {
	
	public FactionClaimforCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "claimfor" };
		this.permission = Permission.OP_PERMISSION;
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length < 2) {
			player.sendMessage(Color.translate("&cUsage: /f claimfor <name>"));
			return;
		}
		
		Faction targetFaction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

		if(!(targetFaction instanceof ClaimableFaction)) {
			player.sendMessage(Color.translate("&cClaimable faction named " + args[1] + " not found."));
			return;
		}
		
		WorldEditPlugin worldEditPlugin = RegisterHandler.getInstancee().getWorldEdit();
		
		if(worldEditPlugin == null) {
			player.sendMessage(Color.translate("&cYou must have WorldEdit to do this."));
			return;
		}
		
		Selection selection = worldEditPlugin.getSelection(player);
		
		if(selection == null) {
			player.sendMessage(Color.translate("&cYou must have WorldEdit to do this."));
			return;
		}
		
		ClaimableFaction claimableFaction = (ClaimableFaction) targetFaction;

		Cuboid cuboid = new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint());
		ClaimZone claim = new ClaimZone(claimableFaction, cuboid);
		claimableFaction.addClaim(claim, null);

		RegisterHandler.getInstancee().getFactionManager().updateFaction(claimableFaction);

		player.sendMessage(Color.translate("&eYou have succsesfuly claimed this land for &d" + targetFaction.getName() + "&e."));
	}
}
