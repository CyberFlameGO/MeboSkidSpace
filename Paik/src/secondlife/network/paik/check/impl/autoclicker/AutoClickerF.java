package secondlife.network.paik.check.impl.autoclicker;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

import java.util.Deque;
import java.util.LinkedList;

public class AutoClickerF extends PacketCheck {

    private Deque<Integer> recentCounts = new LinkedList<>();
    private BlockPosition lastBlock;
    private int flyingCount;
    
    public AutoClickerF(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Auto-Clicker (Check 6)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;

            if(blockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                if(this.lastBlock != null && this.lastBlock.equals(blockDig.a())) {
                    double vl = this.getVl();
                    this.recentCounts.addLast(this.flyingCount);

                    if(this.recentCounts.size() == 20) {
                        double average = 0.0;

                        for(int i : this.recentCounts) {
                            average += i;
                        }

                        average /= this.recentCounts.size();
                        double stdDev = 0.0;

                        for(int j : this.recentCounts) {
                            stdDev += Math.pow(j - average, 2.0);
                        }

                        stdDev /= this.recentCounts.size();
                        stdDev = Math.sqrt(stdDev);

                        if(stdDev < 0.45 && ++vl >= 3.0) {
                            if(this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("STD %.2f. VL %.1f.", stdDev, vl), false) && !this.playerData.isBanning() && vl >= 6.0) {
                                this.ban(player);
                            }
                        } else {
                            vl -= 0.5;
                        }

                        this.recentCounts.clear();
                    }

                    this.setVl(vl);
                }

                this.flyingCount = 0;
            } else if(blockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                this.lastBlock = blockDig.a();
            }
        } else if(packet instanceof PacketPlayInFlying) {
            ++this.flyingCount;
        }
    }
}
