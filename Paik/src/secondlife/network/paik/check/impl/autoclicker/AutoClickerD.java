package secondlife.network.paik.check.impl.autoclicker;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class AutoClickerD extends PacketCheck {

    private int movements;
    private int stage;
    
    public AutoClickerD(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Auto-Clicker (Check 4)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        int vl = (int)this.getVl();

        if(this.stage == 0) {
            if(packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            }
        } else if(this.stage == 1) {
            if(packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig)packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(this.stage == 2) {
            if(packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig)packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                if(++vl >= 5) {
                    try {
                        if(this.movements > 10 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "M " + this.movements + ".", true)) {
                            int violations = this.playerData.getViolations(this, 60000L);

                            if(!this.playerData.isBanning() && violations > 4) {
                                this.ban(player);
                            }
                        }
                    } finally {
                        boolean movements = false;

                        this.movements = (movements ? 1 : 0);
                        vl = (movements ? 1 : 0);
                    }
                }

                this.stage = 0;
            } else if(packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            } else {
                boolean b = false;

                this.movements = (b ? 1 : 0);

                vl = (b ? 1 : 0);
                this.stage = (b ? 1 : 0);
            }
        } else if(this.stage == 3) {
            if(packet instanceof PacketPlayInFlying) {
                ++this.stage;
            } else {
                boolean b2 = false;

                this.movements = (b2 ? 1 : 0);
                vl = (b2 ? 1 : 0);
                this.stage = (b2 ? 1 : 0);
            }
        } else if(this.stage == 4) {
            if(packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig)packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                ++this.movements;
                this.stage = 0;
            } else {
                boolean b3 = false;

                this.movements = (b3 ? 1 : 0);
                vl = (b3 ? 1 : 0);
                this.stage = (b3 ? 1 : 0);
            }
        }

        this.setVl(vl);
    }
}
