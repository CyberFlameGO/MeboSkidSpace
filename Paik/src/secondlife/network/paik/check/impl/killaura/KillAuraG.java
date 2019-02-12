package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraG extends PacketCheck {

    private int stage = 0;
    
    public KillAuraG(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 7)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        int calculusStage = this.stage % 6;

        if(calculusStage == 0) {
            if(packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(calculusStage == 1) {
            if(packet instanceof PacketPlayInUseEntity) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(calculusStage == 2) {
            if (packet instanceof PacketPlayInEntityAction) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(calculusStage == 3) {
            if(packet instanceof PacketPlayInFlying) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(calculusStage == 4) {
            if(packet instanceof PacketPlayInEntityAction) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(calculusStage == 5) {
            if(packet instanceof PacketPlayInFlying) {
                if(++this.stage >= 30 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "S " + this.stage + ".", true)) {
                    int violations = this.playerData.getViolations(this, 60000L);

                    if(!this.playerData.isBanning() && violations > 5) {
                        this.ban(player);
                    }
                }
            } else {
                this.stage = 0;
            }
        }
    }
}
