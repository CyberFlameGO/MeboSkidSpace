package secondlife.network.hcfactions.factions;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.factions.utils.struction.ChatChannel;
import secondlife.network.hcfactions.factions.utils.struction.Role;
import secondlife.network.hcfactions.utilties.PacketUtils;
import secondlife.network.vituz.VituzAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class FactionMember implements ConfigurationSerializable {

    private UUID uniqueID;
    private ChatChannel chatChannel;
    private Role role;

    public FactionMember(Player player, ChatChannel chatChannel, Role role) {
        this.uniqueID = player.getUniqueId();
        this.chatChannel = chatChannel;
        this.role = role;
    }

    public FactionMember(Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.chatChannel = PacketUtils.getIfPresent(ChatChannel.class, (String) map.get("chatChannel")).or(ChatChannel.PUBLIC);
        this.role = PacketUtils.getIfPresent(Role.class, (String) map.get("role")).or(Role.MEMBER);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("uniqueID", uniqueID.toString());
        map.put("chatChannel", chatChannel.name());
        map.put("role", role.name());

        return map;
    }

    public String getName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uniqueID);

        return offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline() ? offlinePlayer.getName() : null;
    }

    public int getPing() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uniqueID);

        return VituzAPI.getPing(offlinePlayer.getPlayer());
    }

    public Player toOnlinePlayer() {
        return Bukkit.getPlayer(uniqueID);
    }
}