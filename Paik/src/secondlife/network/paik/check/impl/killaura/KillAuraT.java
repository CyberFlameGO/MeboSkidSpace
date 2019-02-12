package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;

public class KillAuraT extends PacketCheck {

    private int entityId;
    private boolean sent;
    
    public KillAuraT(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 20)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInUseEntity) {
            if(!this.sent) {
                this.sent = true;
            }
        } else if(packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
