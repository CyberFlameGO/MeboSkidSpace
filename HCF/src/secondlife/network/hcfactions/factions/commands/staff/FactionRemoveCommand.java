package secondlife.network.hcfactions.factions.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
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
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;

public class FactionRemoveCommand extends SubCommand {

    private ConversationFactory factory;
    
	public FactionRemoveCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "delete", "forcedisband", "forceremove" };
		this.permission = Permission.OP_PERMISSION;
		
		this.factory = new ConversationFactory(plugin).withFirstPrompt(new RemoveAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {			
        if(args.length < 2) {
            sender.sendMessage(Color.translate("&cUsage: /f delete <all|faction>"));
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
        	sender.sendMessage(HCFUtils.FACTION_NOT_FOUND);
        	return;
        }

        if(RegisterHandler.getInstancee().getFactionManager().removeFaction(faction, sender)) {
            Command.broadcastCommandMessage(sender, Color.translate("&eSuccsesfuly disbanded faction &d" + faction.getName()));
        }
    }

    private static class RemoveAllPrompt extends StringPrompt {

        public RemoveAllPrompt(HCF plugin) {
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All factions" + ChatColor.YELLOW + " will be cleared. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String string) {
            switch (string.toLowerCase()) {
            case "yes": {
                for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
                	RegisterHandler.getInstancee().getFactionManager().removeFaction(faction, Bukkit.getConsoleSender());
                }

                Conversable conversable = context.getForWhom();
                Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "All factions have been disbanded" + (conversable instanceof CommandSender ? " by " + ((CommandSender) conversable).getName() : "") + '.');

                return Prompt.END_OF_CONVERSATION;
            }
            case "no": {
                context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of disbanding all factions.");
                
                return Prompt.END_OF_CONVERSATION;
            } default: {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of disbanding all factions cancelled.");
                
                return Prompt.END_OF_CONVERSATION;
            }
            }
        }
    }
}
