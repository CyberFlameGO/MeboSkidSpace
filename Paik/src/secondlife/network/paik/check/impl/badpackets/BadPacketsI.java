package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsI extends PacketCheck {

    private float lastYaw;
    private float lastPitch;
    private boolean ignore;
    
    public BadPacketsI(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 9)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying flying = (PacketPlayInFlying)packet;

            if(!flying.g() && flying.h()) {
                if(this.lastYaw == flying.d() && this.lastPitch == flying.e()) {
                    if(!this.ignore) {
                        this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", false);
                    }

                    this.ignore = false;
                }

                this.lastYaw = flying.d();
                this.lastPitch = flying.e();
            } else {
                this.ignore = true;
            }
        } else if(packet instanceof PacketPlayInSteerVehicle) {
            this.ignore = true;
        }
    }
}
