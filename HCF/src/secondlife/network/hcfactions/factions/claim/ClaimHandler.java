package secondlife.network.hcfactions.factions.claim;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.FactionManager;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.system.RoadFaction;
import secondlife.network.hcfactions.factions.type.system.WildernessFaction;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.cuboid.Cuboid;
import secondlife.network.vituz.utilties.cuboid.CuboidDirection;
import secondlife.network.vituz.utilties.item.ItemBuilder;
import secondlife.network.vituz.visualise.VisualType;
import secondlife.network.vituz.visualise.VisualiseHandler;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ClaimHandler extends Handler implements Listener {

	public static int MIN_CLAIM_HEIGHT = 0;
	public static int MAX_CLAIM_HEIGHT = 256;

	public static long PILLAR_BUFFER_DELAY_MILLIS = 200L;

	public static ItemStack CLAIM_WAND = new ItemBuilder(Material.DIAMOND_AXE)
			.name("&6&lClaiming Wand")
			.lore("&e&lRight&7/&e&lLeft Click &dBlock")
			.lore("&e&lSelect claim's corners")
			.lore("")
			.lore("&e&lRight Click &dAir")
			.lore("&c&lCancel current claim")
			.lore(" ")
			.lore("&e&lCrouch Left Click &dBlock/Air")
			.lore("&a&lPurchase current claim").build();

	private static int NEXT_PRICE_MULTIPLIER_AREA = 250;
	private static int NEXT_PRICE_MULTIPLIER_CLAIM = 500;

	public static int MIN_CLAIM_RADIUS = 5;
	public static int MAX_CHUNKS_PER_LIMIT = 16;
	public static int CLAIM_BUFFER_RADIUS = 4;
	
	private static double CLAIM_SELL_MULTIPLIER = 0.8;
	private static double CLAIM_PRICE_PER_BLOCK = 0.25;

	public static ConcurrentMap<UUID, ClaimSelection> claimSelectionMap ;

	public ClaimHandler(HCF plugin) {
		super(plugin);

		CacheLoader<UUID, ClaimSelection> loader = new CacheLoader<UUID, ClaimSelection>(){

			public ClaimSelection load(UUID uuid) throws Exception {
				return claimSelectionMap.get(uuid);
			}
		};

		this.claimSelectionMap = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(loader).asMap();
	}

	public static int calculatePrice(Cuboid claim, int currentClaims, boolean selling) {
		if(currentClaims == -1 || !claim.hasBothPositionsSet()) return 0;

		int multiplier = 1;
		int remaining = claim.getArea();
		double price = 0;
		
		while(remaining > 0) {
			if(--remaining % NEXT_PRICE_MULTIPLIER_AREA == 0) multiplier++;

			price += (CLAIM_PRICE_PER_BLOCK * multiplier);
		}

		if(currentClaims != 0) {
			currentClaims = Math.max(currentClaims + (selling ? -1 : 0), 0);
			
			price += (currentClaims * NEXT_PRICE_MULTIPLIER_CLAIM);
		}

		if(selling) price *= CLAIM_SELL_MULTIPLIER;

		return (int) price;
	}

	public static boolean clearClaimSelection(Player player) {
		ClaimSelection claimSelection = ClaimHandler.claimSelectionMap.remove(player.getUniqueId());
		
		if(claimSelection != null) {
			VisualiseHandler.clearVisualBlocks(player, VisualType.PURPLE, null);
			return true;
		}

		return false;
	}

	public static boolean canClaimHere(Player player, Location location) {
		World world = location.getWorld();

		if(world.getEnvironment() != Environment.NORMAL) {
			player.sendMessage(Color.translate("&cYou can't use this command in this world."));
			return false;
		}

		if(!(RegisterHandler.getInstancee().getFactionManager().getFactionAt(location) instanceof WildernessFaction)) {
			player.sendMessage(Color.translate("&cYou are currently in the Warzone and can't claim land here. The Warzone ends at 1000.."));
			return false;
		}

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			player.sendMessage(HCFUtils.NO_FACTION);
			return false;
		}

		if (playerFaction.getMember(player.getName()).getRole() == Role.MEMBER) {
			player.sendMessage(HCFUtils.INVALID_ROLE);
			return false;
		}

		if (playerFaction.getClaims().size() >= HCFConfiguration.maxClaimsPerFaction) {
			player.sendMessage(Color.translate("&cYour faction has maximum claims - &l" + HCFConfiguration.maxClaimsPerFaction + "&c!"));
			return false;
		}

		int locX = location.getBlockX();
		int locZ = location.getBlockZ();

		FactionManager factionManager = RegisterHandler.getInstancee().getFactionManager();
		
		for(int x = locX - CLAIM_BUFFER_RADIUS; x < locX + CLAIM_BUFFER_RADIUS; x++) {
			for(int z = locZ - CLAIM_BUFFER_RADIUS; z < locZ + CLAIM_BUFFER_RADIUS; z++) {
				Faction factionAtNew = factionManager.getFactionAt(world, x, z);
				
				if(!HCFConfiguration.allowClaimingOnRoads && factionAtNew instanceof ClaimableFaction && playerFaction != factionAtNew && !(factionAtNew instanceof RoadFaction)) {
					player.sendMessage(Color.translate("&cThis position contains enemy claims within a &l" + CLAIM_BUFFER_RADIUS + " &cblock buffer radius!"));
					return false;
				}
			}
		}

		return true;
	}

	public static boolean tryPurchasing(Player player, ClaimZone claim) {
		World world = claim.getWorld();

		if(world.getEnvironment() != Environment.NORMAL) {
			player.sendMessage(Color.translate("&cYou can't use this command in this world!"));
			return false;
		}

		PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(player);

		if(playerFaction == null) {
			player.sendMessage(HCFUtils.NO_FACTION);
			return false;
		}

		if(playerFaction.getMember(player.getName()).getRole() == Role.MEMBER) {
			player.sendMessage(HCFUtils.INVALID_ROLE);
			return false;
		}

		if(playerFaction.getClaims().size() >= HCFConfiguration.maxClaimsPerFaction) {
			player.sendMessage(Color.translate("&cYour faction has maximum claims - &l" + HCFConfiguration.maxAllysPerFaction + "&c!"));
			return false;
		}

		int factionBalance = playerFaction.getBalance();
		int claimPrice = calculatePrice(claim, playerFaction.getClaims().size(), false);

		if(claimPrice > factionBalance) {
			player.sendMessage(Color.translate("&cYour faction bank only has &l$" + factionBalance + "&c, the price of this claim is &l$" + claimPrice + "&c!"));
			return false;
		}

		if(claim.getChunks().size() > MAX_CHUNKS_PER_LIMIT) {
			player.sendMessage(Color.translate("&cClaims can't exceed &l" + MAX_CHUNKS_PER_LIMIT + " &cchunks."));
			return false;
		}

		if(claim.getWidth() < MIN_CLAIM_RADIUS || claim.getLength() < MIN_CLAIM_RADIUS) {
			player.sendMessage(Color.translate("&cClaims must be at least &l" + MIN_CLAIM_RADIUS + "&cx&l" + MIN_CLAIM_RADIUS + " &cblocks."));
			return false;
		}

		int minimumX = claim.getMinimumX();
		int maximumX = claim.getMaximumX();
		int minimumZ = claim.getMinimumZ();
		int maximumZ = claim.getMaximumZ();

		FactionManager factionManager = RegisterHandler.getInstancee().getFactionManager();
		
		for(int x = minimumX; x < maximumX; x++) {
			for(int z = minimumZ; z < maximumZ; z++) {
				Faction factionAt = factionManager.getFactionAt(world, x, z);
				
				if(factionAt != null && !(factionAt instanceof WildernessFaction)) {
					player.sendMessage(Color.translate("&cThis claim contains a location not within the &7The Wilderness&c!"));
					return false;
				}
			}
		}

		for(int x = minimumX - CLAIM_BUFFER_RADIUS; x < maximumX + CLAIM_BUFFER_RADIUS; x++) {
			for(int z = minimumZ - CLAIM_BUFFER_RADIUS; z < maximumZ + CLAIM_BUFFER_RADIUS; z++) {
				Faction factionAtNew = factionManager.getFactionAt(world, x, z);
				
				if(!HCFConfiguration.allowClaimingOnRoads && factionAtNew instanceof ClaimableFaction && playerFaction != factionAtNew && !(factionAtNew instanceof RoadFaction)) {
					player.sendMessage(Color.translate("&cThis claim contains enemy claims within a &l" + CLAIM_BUFFER_RADIUS + "&c block buffer radius."));
					return false;
				}
			}
		}

		Location minimum = claim.getMinimumPoint();
		Location maximum = claim.getMaximumPoint();

		Collection<ClaimZone> otherClaims = playerFaction.getClaims();
		boolean conjoined = otherClaims.isEmpty();
		
		if(!conjoined) {
			for(ClaimZone otherClaim : otherClaims) {
				Cuboid outset = otherClaim.clone().outset(CuboidDirection.HORIZONTAL, 1);
				
				if(outset.contains(minimum) || outset.contains(maximum)) {
					conjoined = true;
					break;
				}
			}

			if(!conjoined) {
				player.sendMessage(Color.translate("&cAll claims in your faction must be conjoined."));
				return false;
			}
		}

		claim.setY1(0);
		claim.setY2(256);

		if(!playerFaction.addClaim(claim, player)) return false;
		
		Location center = claim.getCenter();
		
		player.sendMessage(Color.translate("&eYou have succsessfully purchased &dClaim&e for &d$" + claimPrice + "&e!"));
		
		playerFaction.setBalance(factionBalance - claimPrice);
		playerFaction.broadcast(Color.translate("&d" + player.getName() + " &eclaimed land for your faction at &d(" + center.getBlockX() + ", " + center.getBlockZ() + ")"), player.getUniqueId());
		return true;
	}
}
