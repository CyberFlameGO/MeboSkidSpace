package secondlife.network.hcfactions.factions.commands.member;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.hcfactions.utilties.MapSorting;
import secondlife.network.vituz.utilties.Color;

import java.util.*;

public class FactionListCommand extends SubCommand {

    private static int MAX_FACTIONS_PER_PAGE = 10;

	public FactionListCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "list" };
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
        Integer page;

        if(args.length < 2) {
            page = 1;
        } else {
            page = JavaUtils.tryParseInt(args[1]);

            if(page == null) {
                sender.sendMessage(Color.translate("&cInvalid Number."));
                return;
            }
        }

        new BukkitRunnable() {
            public void run() {
                showList(page, sender);
            }
        }.runTaskAsynchronously(this.getInstance());
    }

    private void showList(int pageNumber, CommandSender sender) {
        if (pageNumber < 1) {
            sender.sendMessage(Color.translate("&cYou cannot view a page less than 1."));
            return;
        }

        Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<>();
        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        for(Player target : Bukkit.getOnlinePlayers()) {
            if(senderPlayer == null || senderPlayer.canSee(target)) {
                PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(target);

                if(playerFaction != null) {
                    factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
                }
            }
        }

        Map<Integer, List<BaseComponent[]>> pages = new HashMap<>();
        List<Map.Entry<PlayerFaction, Integer>> sortedMap = MapSorting.sortedValues(factionOnlineMap, Comparator.reverseOrder());

        for(Map.Entry<PlayerFaction, Integer> entry : sortedMap) {
            int currentPage = pages.size();

            List<BaseComponent[]> results = pages.get(currentPage);

            if(results == null || results.size() >= MAX_FACTIONS_PER_PAGE) {
                pages.put(++currentPage, results = new ArrayList<>(MAX_FACTIONS_PER_PAGE));
            }

            PlayerFaction playerFaction = entry.getKey();
            String displayName = playerFaction.getDisplayName(sender);

            int index = results.size() + (currentPage > 1 ? (currentPage - 1) * MAX_FACTIONS_PER_PAGE : 0) + 1;
            ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(displayName).color(net.md_5.bungee.api.ChatColor.DARK_GREEN).
                    event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f show " + playerFaction.getName())).
                    event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.YELLOW + "Click to view " + displayName + ChatColor.YELLOW + '.')
                            .create()));

            // Show online member counts here.
            builder.append(" [" + entry.getValue() + '/' + playerFaction.getMembers().size() + ']', ComponentBuilder.FormatRetention.FORMATTING).
                    color(net.md_5.bungee.api.ChatColor.GRAY);

            // Show DTR rating here.
            builder.append(" [").color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(JavaUtils.format(playerFaction.getDeathsUntilRaidable())).color(fromBukkit(playerFaction.getDtrColour()));
            builder.append('/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable()) + " DTR]").color(net.md_5.bungee.api.ChatColor.GRAY);
            results.add(builder.create());
        }

        int maxPages = pages.size();

        if (pageNumber > maxPages) {
            sender.sendMessage(Color.translate("&cThere " + (maxPages == 1 ? "is only " + maxPages + " page" : "are only " + maxPages + " pages") + "."));
            return;
        }

        sender.sendMessage(Color.translate("&7&m---------------------------------------"));
        sender.sendMessage(Color.translate("&9Faction List &7(Page " + pageNumber + "/" + maxPages + ")"));

        Player player = sender instanceof Player ? (Player) sender : null;
        Collection<BaseComponent[]> components = pages.get(pageNumber);
        for (BaseComponent[] component : components) {
            if (component == null) continue;
            if (player != null) {
                player.spigot().sendMessage(component);
            } else {
                sender.sendMessage(TextComponent.toPlainText(component));
            }
        }

        sender.sendMessage(Color.translate("&7You are currently on &dPage " + pageNumber + '/' + maxPages + "&7."));
        sender.sendMessage(Color.translate("&7To view other pages, use &e/f list <page#>&7."));
        sender.sendMessage(Color.translate("&7&m---------------------------------------"));
    }

    private static net.md_5.bungee.api.ChatColor fromBukkit(ChatColor chatColor) {
        return net.md_5.bungee.api.ChatColor.getByChar(chatColor.getChar());
    }
}
