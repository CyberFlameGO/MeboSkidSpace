package secondlife.network.practice.runnable;

import org.bukkit.block.Block;
import secondlife.network.practice.Practice;
import secondlife.network.practice.arena.Arena;
import secondlife.network.practice.arena.StandaloneArena;
import secondlife.network.practice.runnable.DuplicateArenaRunnable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import secondlife.network.practice.utilties.CustomLocation;
import secondlife.network.vituz.utilties.Msg;

/**
 * @since 11/25/2017
 */
@Getter
@AllArgsConstructor
public class ArenaCommandRunnable implements Runnable {

	private final Practice plugin;
	private final Arena copiedArena;

	private int times;

	@Override
	public void run() {
		this.duplicateArena(this.copiedArena, 2500, 2500);
	}

	private void duplicateArena(Arena arena, int offsetX, int offsetZ) {

		new DuplicateArenaRunnable(this.plugin, arena, offsetX, offsetZ, 500, 500) {
			@Override
			public void onComplete() {
				double minX = arena.getMin().getX() + this.getOffsetX();
				double minZ = arena.getMin().getZ() + this.getOffsetZ();
				double maxX = arena.getMax().getX() + this.getOffsetX();
				double maxZ = arena.getMax().getZ() + this.getOffsetZ();

				double aX = arena.getA().getX() + this.getOffsetX();
				double aZ = arena.getA().getZ() + this.getOffsetZ();
				double bX = arena.getB().getX() + this.getOffsetX();
				double bZ = arena.getB().getZ() + this.getOffsetZ();

				double aBX = arena.getABed().getX() + this.getOffsetX();
				double aBZ = arena.getABed().getZ() + this.getOffsetZ();
				double bBX = arena.getBBed().getX() + this.getOffsetX();
				double bBZ = arena.getBBed().getZ() + this.getOffsetZ();

				CustomLocation min = new CustomLocation(minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
				CustomLocation max = new CustomLocation(maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
				CustomLocation a = new CustomLocation(aX, arena.getA().getY(), aZ, arena.getA().getYaw(), arena.getA().getPitch());
				CustomLocation b = new CustomLocation(bX, arena.getB().getY(), bZ, arena.getA().getYaw(), arena.getA().getPitch());

				CustomLocation aBed = new CustomLocation(aBX, arena.getABed().getY(), aBZ);
				CustomLocation bBed = new CustomLocation(bBX, arena.getBBed().getY(), bBZ);

				StandaloneArena standaloneArena;

				if(isBedwars(arena)) {
					standaloneArena = new StandaloneArena(a, b, min, max, aBed, bBed);
				} else {
					standaloneArena = new StandaloneArena(a, b, min, max);
				}

				arena.addStandaloneArena(standaloneArena);
				arena.addAvailableArena(standaloneArena);

				if (--ArenaCommandRunnable.this.times > 0) {
					Msg.sendMessage("&ePlaced a standalone arena of &d"  + arena.getName() + " &eat &d" + (int) minX + "&e, &d" + (int) minZ
							+ "&e. &d" + ArenaCommandRunnable.this.times + " &earenas remaining.");
					ArenaCommandRunnable.this.duplicateArena(arena, (int) Math.round(maxX), (int) Math.round(maxZ));
				} else {
					Msg.sendMessage("&eFinished pasting &d" + ArenaCommandRunnable.this.copiedArena.getName() + "'s &estandalone arenas.");
					ArenaCommandRunnable.this.plugin.getArenaManager().setGeneratingArenaRunnables(ArenaCommandRunnable.this.plugin.getArenaManager().getGeneratingArenaRunnables() - 1);
					this.getPlugin().getArenaManager().reloadArenas();
				}
			}
		}.run();
	}

	private boolean isBedwars(Arena arena) {
		if(arena.getABed() != null && arena.getBBed() != null) {
			return true;
		}

		return false;
	}
}
