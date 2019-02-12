package secondlife.network.paik.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class CheatUtils {

	public static int random(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt(max - min + 1) + min;
		return randomNum;
	}
	
	public static boolean isOnIce(Player player) {
		
		Location location = player.getLocation();
		location.setY(location.getY() - 1.0);
		
		if(location.getBlock().getType() == Material.ICE && location.subtract(0, 1, 0).getBlock().getType() == Material.ICE) {
			return true;
		}
		
		if(location.getBlock().getType() == Material.PACKED_ICE && location.subtract(0, 1, 0).getBlock().getType() == Material.PACKED_ICE) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isOnSolidBlock(Player player) {

		Location location = player.getLocation();
		
		if(location.getBlock().getType().isSolid()) {
			return true;
		}
		
		location.setY(location.getY() - 1.0);

		if(location.getBlock().getType().isSolid()) {
			return true;
		}
		return false;
	}
	
	public static boolean isOnSnow(Player player) {
		
		Location location = player.getLocation();
		location.setY(location.getY() - 0.1);
		
		if(location.getBlock().getType() == Material.SNOW) {
			return true;
		}
		return false;
	}

	public static boolean isInAir(Player player) {

		Location location = player.getLocation();
		location.setY(location.getY() - 1);

		if(location.getBlock().getType() == Material.AIR && location.subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
			return true;
		}
		return false;
	}
	
    public static boolean isOnHalfBlocks(Player player) {
		
		Location location = player.getLocation();
		location.setY(location.getY() - 0.5);
		
		if(location.getBlock().getType() == Material.STEP
			|| location.getBlock().getType() == Material.WOOD_STEP
			|| location.getBlock().getType() == Material.TRAP_DOOR
			|| location.getBlock().getType() == Material.DAYLIGHT_DETECTOR	
			|| location.getBlock().getType() == Material.SNOW) {
			return true;
		}
		return false;
	}

	public static Byte direction(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return 0xC; // S > E
		} else if (22.5 <= rotation && rotation < 67.5) {
			return 0xE; // SW > SE
		} else if (67.5 <= rotation && rotation < 112.5) {
			return 0x0; // W > E
		} else if (112.5 <= rotation && rotation < 157.5) {
			return 0x2; // NW > SW
		} else if (157.5 <= rotation && rotation < 202.5) {
			return 0x4; // N > W
		} else if (202.5 <= rotation && rotation < 247.5) {
			return 0x6; // NE > NW
		} else if (247.5 <= rotation && rotation < 292.5) {
			return 0x8; // E > N
		} else if (292.5 <= rotation && rotation < 337.5) {
			return 0xA; // SE > NE
		} else if (337.5 <= rotation && rotation < 360.0) {
			return 0xC; // S > E
		} else {
			return null;
		}
	}
    
    public static boolean isInLiquid(Player player) {
		
		Location location = player.getLocation();
		
		if(location.getBlock().getType() == Material.WATER
			|| location.getBlock().getType() == Material.STATIONARY_WATER
			|| location.getBlock().getType() == Material.LAVA
			|| location.getBlock().getType() == Material.STATIONARY_LAVA) {
			return true;
		}
		return false;
	}

    public static boolean isOnSolidBlocks(Player player) {
		
		Location location = player.getLocation();
		location.setY(location.getY() - 1);
		
		if(location.getBlock().getType() == Material.ACACIA_STAIRS || location.getBlock().getType() == Material.BIRCH_WOOD_STAIRS
			|| location.getBlock().getType() == Material.BRICK_STAIRS || location.getBlock().getType() == Material.COBBLESTONE_STAIRS
			|| location.getBlock().getType() == Material.DARK_OAK_STAIRS || location.getBlock().getType() == Material.JUNGLE_WOOD_STAIRS
			|| location.getBlock().getType() == Material.NETHER_BRICK_STAIRS || location.getBlock().getType() == Material.QUARTZ_STAIRS
			|| location.getBlock().getType() == Material.SANDSTONE_STAIRS || location.getBlock().getType() == Material.SMOOTH_STAIRS
			|| location.getBlock().getType() == Material.SPRUCE_WOOD_STAIRS || location.getBlock().getType() == Material.WOOD_STAIRS
			|| location.getBlock().getType() == Material.CHEST || location.getBlock().getType() == Material.TRAPPED_CHEST
			|| location.getBlock().getType() == Material.FENCE || location.getBlock().getType() == Material.IRON_FENCE
			|| location.getBlock().getType() == Material.NETHER_FENCE || location.getBlock().getType() == Material.FENCE_GATE
			|| location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN
			|| location.getBlock().getType() == Material.COBBLE_WALL || location.getBlock().getType() == Material.ENCHANTMENT_TABLE
			|| location.getBlock().getType() == Material.ENDER_PORTAL_FRAME || location.getBlock().getType() == Material.ENDER_CHEST
			|| location.getBlock().getType() == Material.BREWING_STAND || location.getBlock().getType() == Material.CAULDRON
			|| location.getBlock().getType() == Material.HOPPER || location.getBlock().getType() == Material.ANVIL
			|| location.getBlock().getType() == Material.BED_BLOCK || location.getBlock().getType() == Material.SOUL_SAND
			|| location.getBlock().getType() == Material.STAINED_GLASS_PANE || location.getBlock().getType() == Material.THIN_GLASS
			|| location.getBlock().getType() == Material.CACTUS || location.getBlock().getType() == Material.DRAGON_EGG
			|| location.getBlock().getType() == Material.WEB) {
			return false;
		}
		return true;
	}

	public static boolean isUnderBlock(Player player) {

		Location location = player.getLocation();
		location.setY(location.getY() + 2.0);

		if (location.getBlock().getType() != Material.AIR) {
			return true;
		}
		return false;
	}

	public static boolean blocksNear(Player player) {
		return blocksNear(player.getLocation());
	}

	public static boolean blocksNear(Location location) {
		
		boolean nearBlocks = false;
		
		for (Block block : getSurrounding(location.getBlock(), true)) {
			if (block.getType() != Material.AIR) {
				nearBlocks = true;
				break;
			}
		}
		
		for (Block block : getSurrounding(location.getBlock(), false)) {
			if (block.getType() != Material.AIR) {
				nearBlocks = true;
				break;
			}
		}
		
		location.setY(location.getY() - 0.5D);
		
		if (location.getBlock().getType() != Material.AIR) {
			nearBlocks = true;
		}
		
		if (isBlock(location.getBlock().getRelative(BlockFace.DOWN), new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER })) {
			nearBlocks = true;
		}
		
		return nearBlocks;
	}

	public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
		ArrayList<Block> blocks = new ArrayList();
		if (diagonals) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if ((x != 0) || (y != 0) || (z != 0)) {
							blocks.add(block.getRelative(x, y, z));
						}
					}
				}
			}
		} else {
			blocks.add(block.getRelative(BlockFace.UP));
			blocks.add(block.getRelative(BlockFace.DOWN));
			blocks.add(block.getRelative(BlockFace.NORTH));
			blocks.add(block.getRelative(BlockFace.SOUTH));
			blocks.add(block.getRelative(BlockFace.EAST));
			blocks.add(block.getRelative(BlockFace.WEST));
		}
		return blocks;
	}

	public static ArrayList<Block> getSurroundingXZ(Block block) {
		ArrayList<Block> blocks = new ArrayList();
		blocks.add(block.getRelative(BlockFace.NORTH));
		blocks.add(block.getRelative(BlockFace.NORTH_EAST));
		blocks.add(block.getRelative(BlockFace.NORTH_WEST));
		blocks.add(block.getRelative(BlockFace.SOUTH));
		blocks.add(block.getRelative(BlockFace.SOUTH_EAST));
		blocks.add(block.getRelative(BlockFace.SOUTH_WEST));
		blocks.add(block.getRelative(BlockFace.EAST));
		blocks.add(block.getRelative(BlockFace.WEST));
		return blocks;
	}

	public static boolean isBlock(Block block, Material[] materials) {
		Material type = block.getType();
		for (Material m : materials) {
			if (m == type) {
				return true;
			}
		}
		return false;
	}
	
	public static double getHorizontalDistance(Location to, Location from) {
		double x = Math.abs(Math.abs(to.getX()) - Math.abs(from.getX()));
		double z = Math.abs(Math.abs(to.getZ()) - Math.abs(from.getZ()));

		return Math.sqrt(x * x + z * z);
	}
	
	public static double getYDifference(Location to, Location from) {
		return Math.abs(to.getY() - from.getY());
	}
}
