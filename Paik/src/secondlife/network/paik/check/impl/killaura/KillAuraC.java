package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.paik.utilties.CustomLocation;
import secondlife.network.paik.utilties.MathUtil;

public class KillAuraC extends PacketCheck {
    
    private float lastYaw;
    
    public KillAuraC(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 3)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(this.playerData.getLastTarget() == null) return;

        if(packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying flying = (PacketPlayInFlying)packet;

            if(flying.h() && !this.playerData.isAllowTeleport()) {
                CustomLocation targetLocation = this.playerData.getLastPlayerPacket(this.playerData.getLastTarget(), MathUtil.pingFormula(this.playerData.getPing()));

                if(targetLocation == null) return;

                CustomLocation playerLocation = this.playerData.getLastMovePacket();

                if(playerLocation.getX() == targetLocation.getX()) return;
                if(targetLocation.getZ() == playerLocation.getZ()) return;

                float yaw = flying.d();

                if(yaw != this.lastYaw) {
                    float bodyYaw = MathUtil.getDistanceBetweenAngles(yaw, MathUtil.getRotationFromPosition(playerLocation, targetLocation)[0]);

                    if(bodyYaw == 0.0f && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                        int violations = this.playerData.getViolations(this, 60000L);

                        if(!this.playerData.isBanning() && violations > 5) {
                            this.ban(player);
                        }
                    }
                }

                this.lastYaw = yaw;
            }
        }
    }
}
