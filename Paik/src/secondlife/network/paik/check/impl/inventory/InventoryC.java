package secondlife.network.paik.check.impl.inventory;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;
import secondlife.network.paik.utilties.CustomLocation;

import java.util.Deque;
import java.util.LinkedList;

public class InventoryC extends PacketCheck {
    
    private Deque<Long> delays = new LinkedList<>();
    
    public InventoryC(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Inventory (Check 3)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInWindowClick && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && !this.playerData.isAllowTeleport()) {
            CustomLocation lastMovePacket = this.playerData.getLastMovePacket();

            if(lastMovePacket == null) return;

            long delay = System.currentTimeMillis() - lastMovePacket.getTimestamp();
            this.delays.add(delay);

            if(this.delays.size() == 10) {
                double average = 0.0;

                for(long loopDelay : this.delays) {
                    average += loopDelay;
                }

                average /= this.delays.size();

                this.delays.clear();
                double vl = this.getVl();

                if(average <= 35.0) {
                    if((vl += 1.25) >= 4.0) {
                        if(this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("AVG %.1f. VL %.2f.", average, vl), true)) {
                            if(!this.playerData.isBanning() && vl >= 10.0) {
                                this.ban(player);
                            }
                        } else {
                            vl = 0.0;
                        }
                    }
                } else {
                    vl -= 0.5;
                }

                this.setVl(vl);
            }
        }
    }
}
