package secondlife.network.paik.check.impl.scaffold;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.paik.utilties.CustomLocation;

public class ScaffoldA extends PacketCheck {

    private BlockPosition lastBlock;
    private float lastYaw;
    private float lastPitch;
    private float lastX;
    private float lastY;
    private float lastZ;
    
    public ScaffoldA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Placement (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace blockPlace = (PacketPlayInBlockPlace)packet;
            BlockPosition blockPosition = blockPlace.a();

            float x = blockPlace.d();
            float y = blockPlace.e();
            float z = blockPlace.f();

            if(this.lastBlock != null && (blockPosition.getX() != this.lastBlock.getX() || blockPosition.getY() != this.lastBlock.getY() || blockPosition.getZ() != this.lastBlock.getZ())) {
                CustomLocation location = this.playerData.getLastMovePacket();
                double vl = this.getVl();

                if(this.lastX == x && this.lastY == y && this.lastZ == z) {
                    float deltaAngle = Math.abs(this.lastYaw - location.getYaw()) + Math.abs(this.lastPitch - location.getPitch());

                    if(deltaAngle > 4.0f && ++vl >= 4.0) {
                        this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("X %.1f. Y %.1f. Z %.1f. DA %.1f. VL %.1f.", x, y, z, deltaAngle, vl), false);
                    }
                } else {
                    vl -= 0.5;
                }

                this.setVl(vl);

                this.lastX = x;
                this.lastY = y;
                this.lastZ = z;
                this.lastYaw = location.getYaw();
                this.lastPitch = location.getPitch();
            }

            this.lastBlock = blockPosition;
        }
    }
}
