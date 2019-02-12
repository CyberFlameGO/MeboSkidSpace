package secondlife.network.paik.check.impl.inventory;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class InventoryG extends PacketCheck {

    private boolean sent;
    private boolean vehicle;
    
    public InventoryG(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Inventory (Check 7)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInFlying) {
            if(this.sent) {
                this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", true);
            }

            boolean b = false;

            this.vehicle = b;
            this.sent = b;
        } else if(packet instanceof PacketPlayInClientCommand && ((PacketPlayInClientCommand)packet).a() == PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            if(this.playerData.isSprinting() && !this.vehicle) {
                this.sent = true;
            }
        } else if(packet instanceof PacketPlayInEntityAction && ((PacketPlayInEntityAction)packet).b() == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
            this.sent = false;
        } else if(packet instanceof PacketPlayInSteerVehicle) {
            this.vehicle = true;
        }
    }
}
