package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraK extends PacketCheck {

    private int ticksSinceStage;
    private int streak;
    private int stage;
    
    public KillAuraK(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 11)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInArmAnimation) {
            if(this.stage == 0) {
                this.stage = 1;
            } else {
                boolean b = false;
                this.stage = (b ? 1 : 0);
                this.streak = (b ? 1 : 0);
            }
        } else if(packet instanceof PacketPlayInUseEntity) {
            if(this.stage == 1) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(packet instanceof PacketPlayInFlying.PacketPlayInPositionLook) {
            if(this.stage == 2) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(packet instanceof PacketPlayInFlying.PacketPlayInPosition) {
            if(this.stage == 3) {
                if(++this.streak >= 15) {
                    this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "STR " + this.streak + ".", false);
                }

                this.ticksSinceStage = 0;
            }

            this.stage = 0;
        }

        if(packet instanceof PacketPlayInFlying && ++this.ticksSinceStage > 40) {
            this.streak = 0;
        }
    }
}
