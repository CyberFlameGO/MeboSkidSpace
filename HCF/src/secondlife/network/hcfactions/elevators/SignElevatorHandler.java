package secondlife.network.hcfactions.elevators;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;

public class SignElevatorHandler extends Handler implements Listener {
	
	public static String signTitle;

	public SignElevatorHandler(HCF plugin) {
		super(plugin);
		
		signTitle = Color.translate("&4&l[Elevator]");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSignUpdate(SignChangeEvent event) {
		if(!StringUtils.containsIgnoreCase(event.getLine(0), "Elevator")) return;
		
		boolean up;
		
		Player player = event.getPlayer();
		
		if(StringUtils.containsIgnoreCase(event.getLine(1), "Up")) {
			up = true;
		} else {
			if (!StringUtils.containsIgnoreCase(event.getLine(1), "Down")) {
				player.sendMessage(Color.translate("&8[&6&lElevator&8] &eInvalid sign! Needs to be &dUp &eor &dDown&e!"));
				
				fail(event);
				return;
			}
			
			up = false;
		}
		
		event.setLine(0, signTitle);
		event.setLine(1, up ? "Up" : "Down");
		event.setLine(2, "");
		event.setLine(3, "");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(event.getClickedBlock() == null) return;
			
			Block block = event.getClickedBlock();
			
			if(!(block.getState() instanceof Sign)) return;
			
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			
			if(lines[0].equals(signTitle)) {
				boolean up;
				
				if(lines[1].equalsIgnoreCase("Up")) {
					up = true;
				} else {
					if(!lines[1].equalsIgnoreCase("Down")) return;
					
					up = false;
				}
				
				Player player = event.getPlayer();
				
				if(event.useInteractedBlock() == Result.ALLOW) {
					this.signClick(player, sign.getLocation(), up);
				}
			}
		}
	}

	public boolean signClick(Player player, Location signLocation, boolean up) {
		Block block = signLocation.getBlock();
		
		do {
			block = block.getRelative(up ? BlockFace.UP : BlockFace.DOWN);
			
			if(block.getY() > block.getWorld().getMaxHeight() || block.getY() <= 1) {
				player.sendMessage(Color.translate("&8[&6&lElevator&8] &eCould not locate the sign " + (up ? "&aabove" : "&cbelow") + "&e!"));
				return false;
			}
		} while(!isSign(block));
		
		boolean underSafe = isSafe(block.getRelative(BlockFace.DOWN));
		boolean overSafe = isSafe(block.getRelative(BlockFace.UP));
		
		if(!underSafe && !overSafe) {
			player.sendMessage(Color.translate("&8[&6&lElevator&8] &eCould not find a place to teleport by the sign " + (up ? "&aabove" : "&cbelow") + "&e!"));
			return false;
		}
		
		Location location = player.getLocation().clone();
		
		location.setX(block.getX() + 0.5);
		location.setY((double) (block.getY() + (underSafe ? -1 : 0)));
		location.setZ(block.getZ() + 0.5);
		location.setPitch(0.0f);
		
		player.teleport(location);
		
		return true;
	}

	public static boolean isSign(Block block) {
		if(!(block.getState() instanceof Sign)) return false;
		
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		
		return lines[0].equals(signTitle) && (lines[1].equalsIgnoreCase("Up") || lines[1].equalsIgnoreCase("Down"));
	}
	
	public static void fail(SignChangeEvent event) {
		event.setLine(0, signTitle);
		event.setLine(1, ChatColor.RED + "Error");
		event.setLine(2, "");
		event.setLine(3, "");
	}
	
	public static boolean isSafe(Block block) {
		return block != null && !block.getType().isSolid() && block.getType() != Material.GLASS && block.getType() != Material.STAINED_GLASS;
	}
}
