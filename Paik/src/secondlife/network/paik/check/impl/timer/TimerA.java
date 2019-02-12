package secondlife.network.paik.check.impl.timer;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

import java.util.Deque;
import java.util.LinkedList;

public class TimerA extends PacketCheck {

    private Deque<Long> delays = new LinkedList<>();
    private long lastPacketTime;
    
    public TimerA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Timer");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInFlying && !this.playerData.isAllowTeleport() && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L) {
            this.delays.add(System.currentTimeMillis() - this.lastPacketTime);

            if(this.delays.size() == 40) {
                double average = 0.0;

                for(long l : this.delays) {
                    average += l;
                }

                average /= this.delays.size();
                double vl = this.getVl();

                if(average <= 49.0) {
                    if((vl += 1.25) >= 4.0 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("AVG %.3f. R %.2f. VL %.2f.", average, 50.0 / average, vl), false) && !this.playerData.isBanning() && vl >= 20.0) {
                        this.ban(player);
                    }
                } else {
                    vl -= 0.5;
                }

                this.setVl(vl);
                this.delays.clear();
            }

            this.lastPacketTime = System.currentTimeMillis();
        }
    }
}
