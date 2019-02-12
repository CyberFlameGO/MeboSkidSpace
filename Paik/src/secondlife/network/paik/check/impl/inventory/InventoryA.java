package secondlife.network.paik.check.impl.inventory;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class InventoryA extends PacketCheck {

    public InventoryA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Inventory (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInWindowClick && ((PacketPlayInWindowClick)packet).a() == 0 && !this.playerData.isInventoryOpen()) {
            if(this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", true)) {
                int violations = this.playerData.getViolations(this, 60000L);

                if(!this.playerData.isBanning() && violations > 5) {
                    this.ban(player);
                }
            }

            this.playerData.setInventoryOpen(true);
        }
    }
}
