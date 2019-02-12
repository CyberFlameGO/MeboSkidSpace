package secondlife.network.paik.check.impl.inventory;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class InventoryD extends PacketCheck {

    private int stage = 0;
    
    public InventoryD(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Inventory (Check 4)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(this.stage == 0) {
            if(packet instanceof PacketPlayInClientCommand && ((PacketPlayInClientCommand)packet).a() == PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                ++this.stage;
            }
        } else if(this.stage == 1) {
            if(packet instanceof PacketPlayInFlying.PacketPlayInLook) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if(this.stage == 2) {
            if(packet instanceof PacketPlayInFlying.PacketPlayInLook) {
                this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "", false);
            }

            this.stage = 0;
        }
    }
}
