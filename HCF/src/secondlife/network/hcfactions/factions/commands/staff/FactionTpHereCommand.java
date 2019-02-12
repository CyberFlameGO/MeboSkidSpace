package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class FactionTpHereCommand extends SubCommand {

    public FactionTpHereCommand(HCF plugin) {
        super(plugin);

        this.aliases = new String[] { "tphere" };
        this.permission = Permission.OP_PERMISSION;
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f tphere <faction>"));
            return;
        }

        Faction faction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

        if(faction == null) {
            sender.sendMessage(HCFUtils.FACTION_NOT_FOUND);
            return;
        }

        if(!(faction instanceof PlayerFaction)) {
            sender.sendMessage(Color.translate("&cYou can teleport player factions!"));
            return;
        }

        for(Player online : ((PlayerFaction) faction).getOnlinePlayers()) {
            online.teleport(player.getLocation());
        }

        player.sendMessage(Color.translate("&eYou have teleported &d" + faction.getName() + " &eto yourself!"));
    }
}
