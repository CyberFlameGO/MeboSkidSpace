package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsL extends PacketCheck {

    private boolean sent;
    private boolean vehicle;
    
    public BadPacketsL(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 12)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInFlying) {
            if(this.sent) {
                this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", false);
            }

            boolean b = false;
            this.vehicle = b;
            this.sent = b;
        } else if(packet instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace blockPlace = (PacketPlayInBlockPlace)packet;

            if(blockPlace.getFace() == 255) {
                ItemStack itemStack = blockPlace.getItemStack();

                if(itemStack != null && itemStack.getName().toLowerCase().contains("sword") && this.playerData.isSprinting() && !this.vehicle) {
                    this.sent = true;
                }
            }
        } else if(packet instanceof PacketPlayInEntityAction && ((PacketPlayInEntityAction)packet).b() == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
            this.sent = false;
        } else if(packet instanceof PacketPlayInSteerVehicle) {
            this.vehicle = true;
        }
    }
}
