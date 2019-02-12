package secondlife.network.hcfactions.factions.commands.captain;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Relation;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.game.events.eotw.EOTWHandler;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.ActionMessage;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.Set;
import java.util.regex.Pattern;

public class FactionInviteCommand extends SubCommand {

    private Pattern nameRegex = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

    public FactionInviteCommand(HCF plugin) {
		super(plugin);
		
		this.aliases = new String[] { "invite", "inv" };
		this.forPlayerUseOnly = true;
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        if(args.length < 2) {
            player.sendMessage(Color.translate("&cUsage: /f invite <player>"));
            return;
        }

        if(!nameRegex.matcher(args[1]).matches()) {
            player.sendMessage(Color.translate("&c&l" + args[1] + " &c is an invalid username!"));
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

        Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
        String name = args[1];

        if(playerFaction.getMember(name) != null) {
            player.sendMessage(Color.translate("&c&l" + name + "&c is already in your faction!"));
            return;
        }

        if(!HCFConfiguration.kitMap && !EOTWHandler.isEOTW() && playerFaction.isRaidable()) {
            player.sendMessage(Color.translate("&cYou may not invite players whilst your faction is raidable!"));
            return;
        }

        if(!invitedPlayerNames.add(name)) {
            player.sendMessage(Color.translate("&c&l" + name + " &chas already been invited!"));
            return;
        }

        Player target = Bukkit.getPlayer(name);
        
        if(target != null) {
            name = target.getName();

            ActionMessage ac = new ActionMessage();
            ac.addText("&d" + sender.getName() + " &ehas invited you to join &d" + playerFaction.getName() + " &eif you want to join them click ");
            ac.addText("&a&lYES").setClickEvent(ActionMessage.ClickableType.RunCommand, "/f accept " + playerFaction.getName()).addHoverText(Color.translate("&eClick this to join them!"));
            ac.addText("&e!");
            ac.sendToPlayer(target);
        }

        playerFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + " &ehas invited &d" + name + " &einto the faction!");
    }
}
