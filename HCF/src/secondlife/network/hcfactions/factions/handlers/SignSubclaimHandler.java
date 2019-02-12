
package secondlife.network.hcfactions.factions.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.MaterialData;

import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.game.events.eotw.EOTWHandler;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

public class SignSubclaimHandler extends Handler implements Listener {
	
	private static int MAX_SIGN_LINE_CHARS = 16;
	private static String SUBCLAIM_CONVERSION_PREFIX = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[Subclaim]";
	private static List<String> SUBCLAIM_ALIASES = Arrays.asList("SUBCLAIM", "PRIVATE");
	private static Pattern SQUARE_PATTERN_REPLACER = Pattern.compile("\\[|\\]");
	private static BlockFace[] SIGN_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
	
	public SignSubclaimHandler(HCF plugin) {
		super(plugin);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		String[] lines = event.getLines();

		if(!SUBCLAIM_ALIASES.contains(SQUARE_PATTERN_REPLACER.matcher(lines[0].toUpperCase()).replaceAll(""))) return;

		Block block = event.getBlock();
		MaterialData materialData = block.getState().getData();

		if(materialData instanceof org.bukkit.material.Sign) {
			org.bukkit.material.Sign sign = (org.bukkit.material.Sign) materialData;
			
			Block attachedBlock = block.getRelative(sign.getAttachedFace());

			if(this.isSubclaimable(attachedBlock)) {
				Player player = event.getPlayer();
				PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

				if(playerFaction == null) return;

				Faction factionAt = RegisterHandler.getInstancee().getFactionManager().getFactionAt(block.getLocation());

				if(playerFaction == factionAt) {
					if(this.isSubclaimed(attachedBlock)) {
						player.sendMessage(Color.translate("&cThere is already a subclaim sign on this &l" + attachedBlock.getType().toString() + "&c!"));
						return;
					}

					List<String> memberList = new ArrayList<>(3);

					for(int i = 1; i < lines.length; i++) {
						String line = lines[i];
						
						if(StringUtils.isNotBlank(line)) memberList.add(line);
					}

					if(memberList.isEmpty()) {
						player.sendMessage(Color.translate("&cSubclaim signs need to have at least 1 player name inserted!"));
						return;
					}

					boolean leaderChest = lines[1].equals(Role.LEADER.getAstrix()) || lines[1].equalsIgnoreCase("LEADER");

					if(leaderChest) {
						if(playerFaction.getMember(player).getRole() != Role.LEADER) {
							player.sendMessage(HCFUtils.INVALID_ROLE);
							return;
						}

						event.setLine(2, null);
						event.setLine(3, null);
					}

					event.setLine(0, SUBCLAIM_CONVERSION_PREFIX); 

					List<String> actualMembers = memberList.stream().filter(member -> playerFaction.getMember(member) != null).collect(Collectors.toList());
					
					playerFaction.broadcast("&2" + player.getName() + " &ehas created a subclaim on block type &d" + attachedBlock.getType().toString() + " &eat &7(&d" + attachedBlock.getX() + "&7,&d " + attachedBlock.getZ() + "&7) &efor &d" + (leaderChest ? "leaders" : actualMembers.isEmpty() ? "captains" : "members " + "[" + StringUtils.join(actualMembers, ", ") + "]"));
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(EOTWHandler.isEOTW()) return;

		Player player = event.getPlayer();

		if(player.getGameMode() == GameMode.CREATIVE && player.hasPermission(Permission.OP_PERMISSION)) return;
		
		Block block = event.getBlock();
		BlockState state = block.getState();

		Block subclaimObjectBlock = null;

		if(!(state instanceof Sign)) {
			subclaimObjectBlock = block;
		} else {
			Sign sign = (Sign) state;
			MaterialData signData = sign.getData();
			
			if(signData instanceof org.bukkit.material.Sign) {
				org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) signData;
				subclaimObjectBlock = block.getRelative(materialSign.getAttachedFace());
			}
		}

		if(subclaimObjectBlock != null && !this.checkSubclaimIntegrity(player, subclaimObjectBlock)) {
			event.setCancelled(true);
			
			player.sendMessage(Color.translate("&cYou can't break this subclaimed &l" + subclaimObjectBlock.getType().toString() + "&c!"));
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();

		if(player.getGameMode() == GameMode.CREATIVE && player.hasPermission(Permission.OP_PERMISSION)) return;

		if(EOTWHandler.isEOTW()) return;

		Block block = event.getClickedBlock();

		if(!this.isSubclaimable(block)) return;

		if(!this.checkSubclaimIntegrity(player, block)) {
			event.setCancelled(true);
			
			player.sendMessage(Color.translate("&cYou don't have access to this subclaimed &l" + block.getType().toString() + "&c!"));
		}
	}

	private String getShortenedName(String originalName) {
		if(originalName.length() >= MAX_SIGN_LINE_CHARS) {
			originalName = originalName.substring(0, MAX_SIGN_LINE_CHARS);
		}

		return originalName;
	}

	private boolean checkSubclaimIntegrity(Player player, Block subclaimObject) {
		if(!this.isSubclaimable(subclaimObject)) return true;

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

		if(playerFaction == null || playerFaction.isRaidable()) return true;

		Role role = playerFaction.getMember(player).getRole();

		if(role == Role.LEADER) return true;

		if(playerFaction != RegisterHandler.getInstancee().getFactionManager().getFactionAt(subclaimObject)) return true;

		Collection<Sign> attachedSigns = this.getAttachedSigns(subclaimObject);

		if(attachedSigns.isEmpty()) return true;

		boolean hasLooped = false;
		String search = this.getShortenedName(player.getName());

		for(Sign attachedSign : attachedSigns) {
			String[] lines = attachedSign.getLines();

			if(!lines[0].equals(SUBCLAIM_CONVERSION_PREFIX)) continue;

			hasLooped = true;
			
			if(Role.LEADER.getAstrix().equals(lines[1])) continue;
			if(Role.COLEADER.getAstrix().equals(lines[1])) continue;
			if(Role.CAPTAIN.getAstrix().equals(lines[1])) continue;

			if(Role.LEADER.getName().equals(lines[1])) continue;
			if(Role.COLEADER.getName().equals(lines[1])) continue;
			if(Role.CAPTAIN.getName().equals(lines[1])) continue;

			if(role == Role.CAPTAIN) return true;
			if(role == Role.COLEADER) return true;

			for(int i = 1; i < lines.length; i++) {
				if(lines[i].toLowerCase().contains(search.toLowerCase())) return true;
			}
		}

		return !hasLooped;
	}

	public Collection<Sign> getAttachedSigns(Block block) {
		Set<Sign> results = new HashSet<>();
		
		getSignsAround(block, results);

		BlockState state = block.getState();

		if(state instanceof Chest) {
			Inventory chestInventory = ((Chest) state).getInventory();

			if(chestInventory instanceof DoubleChestInventory) {
				DoubleChest doubleChest = ((DoubleChestInventory) chestInventory).getHolder();
				
				Block left = ((Chest) doubleChest.getLeftSide()).getBlock();
				Block right = ((Chest) doubleChest.getRightSide()).getBlock();
				
				getSignsAround(left.equals(block) ? right : left, results);
			}
		}

		return results;
	}

	private Set<Sign> getSignsAround(Block block, Set<Sign> results) {
		for(BlockFace face : SIGN_FACES) {
			Block relative = block.getRelative(face);
			BlockState relativeState = relative.getState();

			if(relativeState instanceof Sign) {
				org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) relativeState.getData();

				if(relative.getRelative(materialSign.getAttachedFace()).equals(block)) {
					results.add((Sign) relative.getState());
				}
			}
		}

		return results;
	}
	
	
	private boolean isSubclaimable(Block block) {
		Material type = block.getType();
		
		return type == Material.FENCE_GATE || type == Material.TRAP_DOOR || block.getState() instanceof InventoryHolder;
	}

	private boolean isSubclaimed(Block block) {
		if(isSubclaimable(block)) {
			Collection<Sign> attachedSigns = getAttachedSigns(block);

			for(Sign attachedSign : attachedSigns) {
				if(attachedSign.getLine(0).equals(SUBCLAIM_CONVERSION_PREFIX)) {
					return false;
				}
			}
		}

		return false;
	}
}
