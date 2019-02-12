package secondlife.network.hcfactions.factions.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hcfactions.factions.claim.ClaimHandler;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.visualise.VisualBlockData;
import secondlife.network.vituz.visualise.VisualType;
import secondlife.network.vituz.visualise.VisualiseHandler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class LandMap {

    private static int FACTION_MAP_RADIUS_BLOCKS = 22;

	public static boolean updateMap(Player player, VisualType visualType, boolean inform) {
        Location location = player.getLocation();
        World world = player.getWorld();
        
        int locationX = location.getBlockX();
        int locationZ = location.getBlockZ();

        int minimumX = locationX - FACTION_MAP_RADIUS_BLOCKS;
        int minimumZ = locationZ - FACTION_MAP_RADIUS_BLOCKS;
        int maximumX = locationX + FACTION_MAP_RADIUS_BLOCKS;
        int maximumZ = locationZ + FACTION_MAP_RADIUS_BLOCKS;

        LinkedHashSet<ClaimZone> board = new LinkedHashSet<ClaimZone>();

        for(int x = minimumX; x <= maximumX; x++) {
            for(int z = minimumZ; z <= maximumZ; z++) {
            	ClaimZone claim = RegisterHandler.getInstancee().getFactionManager().getClaimAt(world, x, z);
            	
				if(claim != null) board.add(claim);
            }
        }

        if(board.isEmpty()) {
            player.sendMessage(Color.translate("&cNothing to visualise for &l" + visualType.name().toLowerCase() + " &cwithin &l" + FACTION_MAP_RADIUS_BLOCKS + " &cblocks of you!"));
            return false;
        }

        for(ClaimZone claim : board) {
            int maxHeight = Math.min(world.getMaxHeight(), ClaimHandler.MAX_CLAIM_HEIGHT);
            Location[] corners = claim.getCornerLocations();
            List<Location> shown = new ArrayList<>(maxHeight * corners.length);
            
            for(Location corner : corners) {
                for(int y = 0; y < maxHeight; y++) {
                    shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
                }
            }

			Map<Location, VisualBlockData> dataMap = VisualiseHandler.generate(player, shown, visualType, true);
            if(dataMap.isEmpty()) continue;
           
            String materialName = Vituz.getInstance().getItemDB().getName(new ItemStack(dataMap.entrySet().iterator().next().getValue().getItemType(), 1));

            if(inform) {
                player.sendMessage(Color.translate("&d" + claim.getFaction().getDisplayName(player) + " &eowns land &d" + claim.getName() + " &7(&edisplayed with &d" + materialName + "&7)&e!"));
            }
        }

        return true;
    }
}
