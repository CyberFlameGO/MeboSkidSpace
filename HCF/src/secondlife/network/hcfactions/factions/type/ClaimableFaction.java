package secondlife.network.hcfactions.factions.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.claim.ClaimHandler;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.utils.enums.ClaimChangeEnum;
import secondlife.network.hcfactions.factions.utils.events.FactionClaimChangeEvent;
import secondlife.network.hcfactions.factions.utils.events.FactionClaimChangedEvent;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.utilties.GenericUtils;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.*;

@Getter
public abstract class ClaimableFaction extends Faction {

    public static ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS = (ImmutableMap.of(Environment.NETHER, "Nether", Environment.NORMAL, "Overworld", Environment.THE_END, "The End"));

    protected List<ClaimZone> claims = new ArrayList<>();

    public ClaimableFaction(String name) {
        super(name);
    }

	public ClaimableFaction(String name, UUID uuid) {
		super(name, uuid);
	}

    public ClaimableFaction(Map<String, Object> map) {
        super(map);

        this.claims.addAll(GenericUtils.createList(map.get("claims"), ClaimZone.class));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();

        map.put("claims", new ArrayList<>(claims));

        return map;
    }

	@Override
	public void printDetails(CommandSender sender) {
		sender.sendMessage(HCFUtils.BIG_LINE);
		sender.sendMessage(getDisplayName(sender));

		for(ClaimZone claim : claims) {
			Location location = claim.getCenter();
			
			sender.sendMessage(Color.translate("&eLocation: &7(&d" + ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + "&7, &d" + location.getBlockX() + " &7|&d " + location.getBlockZ() + "&7)"));
		}
		
		sender.sendMessage(HCFUtils.BIG_LINE);
	}

    public Set<ClaimZone> getClaims() {
        return ImmutableSet.copyOf(claims);
    }

    public boolean addClaim(ClaimZone claim, CommandSender sender) {
        return addClaims(Collections.singleton(claim), sender);
    }

    public boolean addClaims(Collection<ClaimZone> adding, CommandSender sender) {
        if(sender == null) sender = Bukkit.getConsoleSender();

        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeEnum.CLAIM, adding, this);

        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled() || !claims.addAll(adding)) return false;

        Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeEnum.CLAIM, adding));
        return true;
    }

	public boolean removeClaim(ClaimZone claim, CommandSender optionalSender) {
        return removeClaims(Collections.singleton(claim), optionalSender);
    }

    public boolean removeClaims(Collection<ClaimZone> toRemove, CommandSender sender) {
        if(sender == null) sender = Bukkit.getConsoleSender();

        int expected = this.claims.size() - toRemove.size();

        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeEnum.UNCLAIM, new ArrayList<>(toRemove), this);
      
        Bukkit.getPluginManager().callEvent(event);
        
        if(event.isCancelled() || !this.claims.removeAll(toRemove)) return false;
        
        if(expected != this.claims.size())  return false;

        if(this instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) this;

            Location home = secondlife.network.vituz.utilties.StringUtils.destringifyLocation(playerFaction.getHome());

            int refund = 0;
            
            for(ClaimZone claim : toRemove) {
                refund += ClaimHandler.calculatePrice(claim, expected, true);
                
                if(expected > 0)  expected--;

                if(home != null && claim.contains(home)) {
					playerFaction.setHome(null);
					
					playerFaction.broadcast("&c&lYour faction's home was unset as its residing claim was removed!!!");
                    break;
                }
            }

			HCFData data = HCFData.getByName(playerFaction.getLeader().getName());
			data.setBalance(data.getBalance() + refund);

			playerFaction.broadcast("&eFaction leader was refunded &d$" + refund + " &edue to a land unclaim.");
        }

        Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeEnum.UNCLAIM, toRemove));
        return true;
    }
}
