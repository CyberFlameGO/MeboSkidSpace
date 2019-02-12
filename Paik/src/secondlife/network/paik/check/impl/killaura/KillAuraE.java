package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.paik.utilties.CustomLocation;

public class KillAuraE extends PacketCheck {

    private long lastAttack;
    private boolean attack;
    
    public KillAuraE(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 5)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        double vl = this.getVl();

        if(packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity)packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && !this.playerData.isAllowTeleport()) {
            CustomLocation lastMovePacket = this.playerData.getLastMovePacket();

            if(lastMovePacket == null) return;

            long delay = System.currentTimeMillis() - lastMovePacket.getTimestamp();

            if(delay <= 25.0) {
                this.lastAttack = System.currentTimeMillis();
                this.attack = true;
            } else {
                vl -= 0.25;
            }
        } else if(packet instanceof PacketPlayInFlying && this.attack) {
            long time = System.currentTimeMillis() - this.lastAttack;

            if(time >= 25L) {
                if(++vl >= 10.0 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("T %s. VL %.2f.", time, vl), false) && !this.playerData.isBanning() && vl >= 20.0) {
                    this.ban(player);
                }
            } else {
                vl -= 0.25;
            }

            this.attack = false;
        }

        this.setVl(vl);
    }
}
