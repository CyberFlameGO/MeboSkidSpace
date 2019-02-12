package secondlife.network.vituz.utilties.update;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
public class MovementUpdate {

    private Player player;
    private Location to;
    private Location from;
    private PacketPlayInFlying packet;
    
    public MovementUpdate(Player player, Location to, Location from, PacketPlayInFlying packet) {
        this.player = player;
        this.to = to;
        this.from = from;
        this.packet = packet;
    }
}
