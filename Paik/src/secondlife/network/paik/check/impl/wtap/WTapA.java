package secondlife.network.paik.check.impl.wtap;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

import java.util.Deque;
import java.util.LinkedList;

public class WTapA extends PacketCheck {

    private Deque<Integer> recentCounts = new LinkedList<>();
    private boolean release;
    private int flyingCount;
    
    public WTapA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Tap (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInEntityAction) {
            PacketPlayInEntityAction.EnumPlayerAction playerAction = ((PacketPlayInEntityAction)packet).b();

            if(playerAction == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING) {
                if(this.playerData.getLastAttackPacket() + 1000L > System.currentTimeMillis() && this.flyingCount < 10 && !this.release) {
                    this.recentCounts.add(this.flyingCount);

                    if(this.recentCounts.size() == 20) {
                        double average = 0.0;

                        for(double flyingCount : this.recentCounts) {
                            average += flyingCount;
                        }

                        average /= this.recentCounts.size();
                        double stdDev = 0.0;

                        for(long l : this.recentCounts) {
                            stdDev += Math.pow(l - average, 2.0);
                        }

                        stdDev /= this.recentCounts.size();
                        stdDev = Math.sqrt(stdDev);

                        double vl = this.getVl();

                        if(stdDev == 0.0) {
                            if((vl += 1.2) >= 2.4) {
                                this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("STD %.2f. VL %.2f.", stdDev, vl), false);
                            }
                        } else {
                            vl -= 2.0;
                        }

                        this.setVl(vl);
                        this.recentCounts.clear();
                    }
                }
            } else if(playerAction == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
                this.flyingCount = 0;
            }
        } else if(packet instanceof PacketPlayInFlying) {
            ++this.flyingCount;
            this.release = false;
        } else if(packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig)packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) {
            this.release = true;
        }
    }
}
