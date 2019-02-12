package secondlife.network.paik.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class BadPacketsA extends PacketCheck {

    private int streak;
    
    public BadPacketsA(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Packets (Check 1)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if(packet instanceof PacketPlayInFlying) {
            if(((PacketPlayInFlying)packet).g()) {
                this.streak = 0;
            } else if(++this.streak > 20 && this.alert(PlayerAlertEvent.AlertType.RELEASE, player, "", false) && !this.playerData.isBanning()) {
                this.ban(player);
            }
        } else if(packet instanceof PacketPlayInSteerVehicle) {
            this.streak = 0;
        }
    }
}
