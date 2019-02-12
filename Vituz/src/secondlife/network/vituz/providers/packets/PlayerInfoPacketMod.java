package secondlife.network.vituz.providers.packets;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerInfoPacketMod {

    public static void sendPacketMod(Player player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, GameProfile profile, int ping, WorldSettings.EnumGamemode gamemode, IChatBaseComponent name) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(action);

        packet.addToPlayerInfo(profile, ping, gamemode, name);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
