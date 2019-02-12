package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class FactionCreateCommand extends SubCommand {

	public FactionCreateCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "create" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

        if(args.length < 2) {
            sender.sendMessage(Color.translate("&cUsage: /f create <name>"));
            return;
        }

        String name = args[1];

        int value = HCFConfiguration.factionNameMinCharacters;

        if(name.length() < value) {
            player.sendMessage(Color.translate("&cFaction names must have at least &l" + value + " &ccharacters!"));
            return;
        }

        value = HCFConfiguration.factionNameMaxCharacters;

        if(name.length() > value) {
            player.sendMessage(Color.translate("&cFaction names cannot be longer than &l" + value + " &ccharacters!"));
            return;
        }

        if(!sender.hasPermission(Permission.OP_PERMISSION)) {
            if(name.equalsIgnoreCase("EOTW")) {
                player.sendMessage(Color.translate("&cInvalid name."));
                return;
            }
        }

        if (!JavaUtils.isAlphanumeric(name)) {
            player.sendMessage(Color.translate("&cInvalid name."));
            return;
        }

        if (RegisterHandler.getInstancee().getFactionManager().getFaction(name) != null) {
            player.sendMessage(Color.translate("&cFaction &l" + name + " &calready exists!"));
            return;
        }

        if (RegisterHandler.getInstancee().getFactionManager().getPlayerFaction((Player) sender) != null) {
            player.sendMessage(HCFUtils.ALREADY_IN_FACTION);
            return;
        }

        RegisterHandler.getInstancee().getFactionManager().createFaction(new PlayerFaction(name, player.getUniqueId()), sender);
    }
}
