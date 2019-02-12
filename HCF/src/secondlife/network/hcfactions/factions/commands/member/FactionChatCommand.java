package secondlife.network.hcfactions.factions.commands.member;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.utils.struction.ChatChannel;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

public class FactionChatCommand extends SubCommand {

	public FactionChatCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "chat", "c" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
        PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

        if(playerFaction == null) {
            player.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        FactionMember member = playerFaction.getMember(player.getName());
        ChatChannel currentChannel = member.getChatChannel();
        ChatChannel parsed = args.length >= 2 ? ChatChannel.parse(args[1], null) : currentChannel.getRotation();

        if(parsed == null && currentChannel != ChatChannel.PUBLIC) {
            Collection<Player> recipients = playerFaction.getOnlinePlayers();
            
            if(currentChannel == ChatChannel.ALLIANCE) {
                for(PlayerFaction ally : playerFaction.getAlliedFactions()) {
                    recipients.addAll(ally.getOnlinePlayers());
                }
            }

            String format = String.format(currentChannel.getRawFormat(player), "", StringUtils.join(args, ' ', 1, args.length));
            
            for(Player recipient : recipients) {
                recipient.sendMessage(format);
            }
            
            return;
        }

        ChatChannel newChannel = parsed == null ? currentChannel.getRotation() : parsed;
        
        member.setChatChannel(newChannel);
        player.sendMessage(Color.translate("&eYou are now in &d" + newChannel.getDisplayName().toLowerCase() + " &echat mode."));
    }
}