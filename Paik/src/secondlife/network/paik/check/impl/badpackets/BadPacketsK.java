package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsK extends PacketCheck {

    public BadPacketsK(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 11)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity)packet;

            if(useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                Entity targetEntity = useEntity.a(((CraftPlayer)player).getHandle().getWorld());

                if(targetEntity instanceof EntityPlayer) {
                    Vec3D vec3D = useEntity.b();

                    if((Math.abs(vec3D.a) > 0.41 || Math.abs(vec3D.b) > 1.91 || Math.abs(vec3D.c) > 0.41) && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                        int violations = this.playerData.getViolations(this, 60000L);

                        if(!this.playerData.isBanning() && violations > 2) {
                            this.ban(player);
                        }
                    }
                }
            }
        }
    }
}
