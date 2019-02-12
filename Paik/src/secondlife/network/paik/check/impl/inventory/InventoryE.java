package secondlife.network.paik.check.impl.inventory;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class InventoryE extends PacketCheck {
   
    private boolean sent;
    
    public InventoryE(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Inventory (Check 5)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInWindowClick) {
            if(this.sent) {
                this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", true);
            }
        } else if(packet instanceof PacketPlayInClientCommand && ((PacketPlayInClientCommand)packet).a() == PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            this.sent = true;
        } else if(packet instanceof PacketPlayInFlying) {
            this.sent = false;
        }
    }
}
