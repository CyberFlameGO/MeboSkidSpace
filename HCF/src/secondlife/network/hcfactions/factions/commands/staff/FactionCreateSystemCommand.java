package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.system.EndPortalFaction;
import secondlife.network.hcfactions.factions.type.system.RoadFaction;
import secondlife.network.hcfactions.factions.type.system.SpawnFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class FactionCreateSystemCommand extends SubCommand {

    public FactionCreateSystemCommand(HCF plugin) {
        super(plugin);

        this.aliases = new String[] { "createsystem", "cs" };
        this.permission = Permission.OP_PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(Color.translate("&cUsage: /f createsystem <name>"));
            return;
        }

        String name = args[1];

        int value = HCFConfiguration.factionNameMinCharacters;

        if (name.length() < value) {
            sender.sendMessage(Color.translate("&cFaction names must have at least &l" + value + " &ccharacters!"));
            return;
        }

        value = HCFConfiguration.factionNameMaxCharacters;

        if (name.length() > value) {
            sender.sendMessage(Color.translate("&cFaction names cannot be longer than &l" + value + " &ccharacters!"));
            return;
        }

        if (!sender.hasPermission(Permission.OP_PERMISSION)) {
            if (name.equalsIgnoreCase("EOTW")) {
                sender.sendMessage(Color.translate("&cInvalid name."));
                return;
            }
        }

        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(Color.translate("&cInvalid name."));
            return;
        }

        if (RegisterHandler.getInstancee().getFactionManager().getFaction(name) != null) {
            sender.sendMessage(Color.translate("&cFaction &l" + name + " &calready exists!"));
            return;
        }

        if (name.equalsIgnoreCase("Spawn")) {
            RegisterHandler.getInstancee().getFactionManager().createFaction(new SpawnFaction(), sender);
        } else if (name.equalsIgnoreCase("EndPortal")) {
            RegisterHandler.getInstancee().getFactionManager().createFaction(new EndPortalFaction(), sender);
        } else if (name.equalsIgnoreCase("Road")) {
            RegisterHandler.getInstancee().getFactionManager().createFaction(new RoadFaction(), sender);
        } else return;

        RegisterHandler.getInstancee().getFactionManager().updateFaction(Faction.getByName(name.toString()));
        sender.sendMessage(Color.translate("&eYou have created system faction named &d" + name.toString() + "&e!"));
    }
}
