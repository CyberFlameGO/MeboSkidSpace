package secondlife.network.paik.check.checks;

import net.minecraft.server.v1_8_R3.Packet;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.AbstractCheck;
import secondlife.network.paik.handlers.data.PlayerData;

public abstract class PacketCheck extends AbstractCheck<Packet> {
    
    public PacketCheck(Paik plugin, PlayerData playerData, String name) {
        super(plugin, playerData, Packet.class, name);
    }
}
