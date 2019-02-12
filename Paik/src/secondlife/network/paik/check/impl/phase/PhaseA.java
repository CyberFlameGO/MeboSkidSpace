package secondlife.network.paik.check.impl.phase;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PositionCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.CustomLocation;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.vituz.utilties.update.PositionUpdate;

import java.util.Arrays;
import java.util.List;

public class PhaseA extends PositionCheck {

	private static List<Material> PHASE_BLOCKS = Arrays.asList(
			Material.LAVA,
			Material.STATIONARY_LAVA,
			Material.WATER,
			Material.STATIONARY_WATER,
			Material.WATER_LILY,
			Material.LADDER,
			Material.AIR,
			Material.ANVIL,
			Material.RAILS,
			Material.ACTIVATOR_RAIL,
			Material.DETECTOR_RAIL,
			Material.POWERED_RAIL,
			Material.TORCH,
			Material.BED,
			Material.BED_BLOCK,
			Material.BREWING_STAND,
			Material.BREWING_STAND_ITEM
	);

	private CustomLocation lastNotInBlockLocation;
	private boolean inBlock = false;
	private int blocksPhased = 0;

	public PhaseA(Paik plugin, PlayerData playerData) {
		super(plugin, playerData, "Phase (Check 1)");
	}

	@Override
	public void handleCheck(Player player, PositionUpdate update) {
		double vl = this.getVl();
		boolean inBlock = this.inBlock;

		Location to = update.getTo();

		try {
			if(PhaseA.PHASE_BLOCKS.contains(to.getBlock().getType())) {
				this.inBlock = false;
				return;
			}

			if(to.getBlock().getType().name().contains("FENCE") || to.getBlock().getType().name().contains("DOOR") ||
			    !to.getBlock().getType().isSolid()) {
				this.inBlock = false;
				return;
			}

			this.inBlock = true;
			Location from = update.getFrom();

			if(inBlock && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {vl += 1.0 + ++this.blocksPhased / 10.0;
				if(vl > 5.0) {
					this.alert(PlayerAlertEvent.AlertType.DEVELOPMENT, player, String.format("BP %s. VL %.2f.", this.blocksPhased, vl), false);
				}
			}
		} finally {
			if(inBlock && !this.inBlock) {
				this.lastNotInBlockLocation = CustomLocation.fromBukkitLocation(to);
				this.blocksPhased = 0;
				vl -= 0.45;
			}

			this.setVl(vl);
		}
	}
}
