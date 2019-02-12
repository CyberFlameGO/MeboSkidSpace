package secondlife.network.hcfactions.game.events.faction;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.games.CapturableFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.game.GameType;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.utilties.HCFUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class KothFaction extends CapturableFaction implements ConfigurationSerializable {

    protected CaptureZone captureZone;

    public KothFaction(String name, UUID uuid) {
        super(name, uuid);
    }

    public KothFaction(Map<String, Object> map) {
        super(map);

        this.captureZone = (CaptureZone) map.get("captureZone");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();

        map.put("captureZone", captureZone);

        return map;
    }

    @Override
    public List<CaptureZone> getCaptureZones() {
        return captureZone == null ? ImmutableList.of() : ImmutableList.of(captureZone);
    }

    @Override
    public GameType getGameType() {
        return GameType.KOTH;
    }

    @Override
    public void printDetails(CommandSender sender) {
        sender.sendMessage(HCFUtils.BIG_LINE);
        sender.sendMessage(getDisplayName(sender));

        for(ClaimZone claim : claims) {
            Location location = claim.getCenter();
            
            sender.sendMessage(Color.translate("&eLocation: &7(&d" + ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + "&7, &d" + location.getBlockX() + " &7|&d " + location.getBlockZ() + "&7)"));
        }

        if(captureZone != null) {
            long remainingCaptureMillis = captureZone.getRemainingCaptureMillis();
            long defaultCaptureMillis = captureZone.getDefaultCaptureMillis();
            
            if(remainingCaptureMillis > 0L && remainingCaptureMillis != defaultCaptureMillis) {
                sender.sendMessage(Color.translate("&eRemaining: &d" + DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true)));
            }

            sender.sendMessage(Color.translate("&eCapture Delay: &d" + captureZone.getDefaultCaptureWords()));
            
            if(captureZone.getCappingPlayer() != null && sender.hasPermission(Permission.STAFF_PERMISSION)) {
                Player capping = captureZone.getCappingPlayer();
                PlayerFaction playerFaction = RegisterHandler.getInstancee().getFactionManager().getPlayerFaction(capping);
                String factionTag = "[" + (playerFaction == null ? "*" : playerFaction.getName()) + "]";
                
                sender.sendMessage(Color.translate("&eCurrent Capper: &d" + capping.getName() + factionTag));
            }
        }

        sender.sendMessage(HCFUtils.BIG_LINE);
    }
}
