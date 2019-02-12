package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.game.events.eotw.EOTWHandler;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.staff.handlers.StaffModeHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.StringUtils;

public class EOTWCommand extends BaseCommand {

	private ConversationFactory factory;	
	public static boolean eotwffa = false;
	
    public EOTWCommand(HCF plugin) {
		super(plugin);
		
		this.command = "eotw";
		this.forPlayerUseOnly = false;
		
        this.factory = new ConversationFactory(plugin).withFirstPrompt(new EotwPrompt()).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
	        if(!(sender instanceof ConsoleCommandSender)) {
	            sender.sendMessage(Msg.NO_PERMISSION);
	            return;
	        }

	        Conversable conversable = (Conversable) sender;
	        conversable.beginConversation(factory.buildConversation(conversable));	
		} else {
			if(args[0].equalsIgnoreCase("ffa")) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(StaffModeHandler.isInStaffMode(player)) return;

					for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
						if(faction instanceof PlayerFaction) {
							RegisterHandler.getInstancee().getFactionManager().removeFaction(faction, sender);
						}
					}

					Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "All factions have been disbanded.");

					Bukkit.setWhitelist(true);

					player.setHealth(20);
					player.setFoodLevel(20);

					player.removePotionEffect(PotionEffectType.SPEED);
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
					player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));

					eotwffa = true;

					Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "FFA players has recived potion effects.");

					Location loc = StringUtils.destringifyLocation(this.getInstance().getConfig().getString("World-Spawn.eotw-ffa"));

					player.teleport(loc);
				}
			}
			
		}
    }

    private static class EotwPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
        	return "Type Yes if you want to active EOTW Timer || Type No if you want to cancel this procces.";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String string) {
            if(string.equalsIgnoreCase("yes")) {
                boolean newStatus = !EOTWHandler.isEOTW(false);
                
                Conversable conversable = context.getForWhom();
                
                if(conversable instanceof CommandSender) {
                    Command.broadcastCommandMessage((CommandSender) conversable, "has set EOTW mode to " + newStatus);
                } else conversable.sendRawMessage(Color.translate("&7has set EOTW mode to " + newStatus));

                EOTWHandler.setEOTW(newStatus);
            } else if(string.equalsIgnoreCase("no")) {
                context.getForWhom().sendRawMessage(Color.translate("&aSuccsesfuly canceled EOTW Timer procces."));
            } else {
                context.getForWhom().sendRawMessage(Color.translate("&cEOTW Cancelled!"));
            }
            
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
