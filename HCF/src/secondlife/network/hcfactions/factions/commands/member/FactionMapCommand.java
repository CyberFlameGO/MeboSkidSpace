package secondlife.network.hcfactions.factions.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.commands.SubCommand;
import secondlife.network.hcfactions.factions.utils.LandMap;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.PacketUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.visualise.VisualType;
import secondlife.network.vituz.visualise.VisualiseHandler;

public class FactionMapCommand extends SubCommand {

	public FactionMapCommand(HCF plugin) {
		super(plugin);

		this.aliases = new String[] { "map" };
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

        HCFData data = HCFData.getByName(player.getName());
        VisualType visualType;
        
        if(args.length <= 1) {
            visualType = VisualType.LIME;
        } else if((visualType = PacketUtils.getIfPresent(VisualType.class, args[1]).orNull()) == null) {
            player.sendMessage(Color.translate("&cVisual type &l" + args[1] + "&c not found."));
            return;
        }

        boolean newShowingMap = !data.isClaimMap();
        
        if(newShowingMap) {
            if(!LandMap.updateMap(player, visualType, true)) return;
        } else {
			VisualiseHandler.clearVisualBlocks(player, visualType, null);
            
            sender.sendMessage(Color.translate("&cClaim pillars are no longer shown."));
        }

        data.setClaimMap(newShowingMap);
    }
}