package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.ActionMessage;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marko on 10.04.2018.
 */
public class FactionPointsCommand extends SubCommand {

    public static final Comparator<PlayerFaction> POINTS_COMPARATOR = Comparator.comparingInt(PlayerFaction::getPoints);

    public FactionPointsCommand(HCF plugin) {
        super(plugin);

        this.aliases = new String[] { "points", "top", "toppoints" };
        this.forPlayerUseOnly = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        List<PlayerFaction> data = new ArrayList<>(RegisterHandler.getInstancee().getFactionManager().getFactions().stream().filter(x -> x instanceof PlayerFaction).map(x -> (PlayerFaction) x).filter(x -> x.getPoints() > 0).collect(Collectors.toSet()));
        Collections.sort(data, POINTS_COMPARATOR);
        Collections.reverse(data);

        sender.sendMessage(HCFUtils.BIG_LINE);
        sender.sendMessage(Color.translate("&5&lFaction Top"));

        for(int i = 0; i < 10; i++) {
            if (i >= data.size()) {
                break;
            }

            PlayerFaction next = data.get(i);

            ActionMessage actionMessage = new ActionMessage();
            actionMessage.addText("&7" + (i + 1) + ") &d" + next.getName() + "&7: &d" + next.getPoints()).setClickEvent(ActionMessage.ClickableType.RunCommand, "/f show " + next.getName()).addHoverText(Color.translate("&eClick view faction of &d" + next.getName()));
            actionMessage.sendToPlayer(((Player) sender));
        }

        sender.sendMessage(HCFUtils.BIG_LINE);
    }
}
