package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class FactionClearclaimsCommand extends SubCommand {

    private ConversationFactory factory;

	public FactionClearclaimsCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "clearclaims" };
		this.permission = Permission.OP_PERMISSION;
		
		this.factory = new ConversationFactory(plugin).withFirstPrompt(new ClaimClearAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {		
		if(args.length < 2) {
			sender.sendMessage(Color.translate("&cUsage: /f clearclaims <player|faction|all>"));
            return;
        }

        if(args[1].equalsIgnoreCase("all")) {
            if(!(sender instanceof ConsoleCommandSender)) {
            	sender.sendMessage(Msg.NO_CONSOLE);
                return;
            }

            Conversable conversable = (Conversable) sender;
            conversable.beginConversation(factory.buildConversation(conversable));
            return;
        }

        Faction faction = RegisterHandler.getInstancee().getFactionManager().getContainingFaction(args[1]);

        if(faction == null) {
        	sender.sendMessage(HCFUtils.NO_FACTION);
            return;
        }

        if(faction instanceof ClaimableFaction) {
            ClaimableFaction claimableFaction = (ClaimableFaction) faction;
            claimableFaction.removeClaims(claimableFaction.getClaims(), sender);

            RegisterHandler.getInstancee().getFactionManager().updateFaction(claimableFaction);

            if(claimableFaction instanceof PlayerFaction) {
                ((PlayerFaction) claimableFaction).broadcast("&eYour claims have been forcefully wiped by &d" + sender.getName() + "&e!");
            }
        }

        sender.sendMessage(Color.translate("&eClaim belonging to &d" + faction.getName() + " &ehas been forcefully wiped."));
    }

    private static class ClaimClearAllPrompt extends StringPrompt {

        public ClaimClearAllPrompt(HCF plugin) {
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All claims" + ChatColor.YELLOW + " will be cleared. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String string) {
            switch(string.toLowerCase()) {
            case "yes": {
                for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
                    if(faction instanceof ClaimableFaction) {
                        ClaimableFaction claimableFaction = (ClaimableFaction) faction;
                        
                        claimableFaction.removeClaims(claimableFaction.getClaims(), Bukkit.getConsoleSender());
                    }
                }

                Conversable conversable = context.getForWhom();
                Bukkit.broadcastMessage(Color.translate("&eAll claims have been cleared &d" + (conversable instanceof CommandSender ? " by " + ((CommandSender) conversable).getName() : "")));

                return Prompt.END_OF_CONVERSATION;
            }
            
            case "no": {
                context.getForWhom().sendRawMessage("Cancelled the process of clearing all faction claims.");
                return Prompt.END_OF_CONVERSATION;
            } default: {
                context.getForWhom().sendRawMessage("Unrecognized response. Process of clearing all faction claims cancelled.");
                return Prompt.END_OF_CONVERSATION;
            }
            }
        }
    }
}
