package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraL extends PacketCheck {

    private boolean sent;
    
    public KillAuraL(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Kill-Aura (Check 12)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity)packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            if(this.sent && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                int violations = this.playerData.getViolations(this, 60000L);

                if(!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if(packet instanceof PacketPlayInEntityAction) {
            PacketPlayInEntityAction.EnumPlayerAction action = ((PacketPlayInEntityAction)packet).b();
            if (action == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING || action == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING || action == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING || action == PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING) {
                this.sent = true;
            }
        } else if(packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
