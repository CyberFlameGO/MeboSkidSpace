package secondlife.network.paik.check.impl.phase;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;

public class PhaseB extends PacketCheck {

    private int stage;
    
    public PhaseB(Paik plugin, PlayerData playerData) {
        super(plugin, playerData, "Phase (Check 2)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        String className = packet.getClass().getSimpleName();

        switch(className) {
            case "PacketPlayInFlying": {
                if(this.stage == 0) {
                    ++this.stage;
                    break;
                }

                this.stage = 0;
                break;
            }
            case "PacketPlayInEntityAction": {
                if(((PacketPlayInEntityAction)packet).b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING) {
                    if(this.stage == 1) {
                        ++this.stage;
                        break;
                    }

                    this.stage = 0;
                    break;
                } else {
                    if(((PacketPlayInEntityAction)packet).b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING && this.stage >= 3) {
                        this.plugin.getAlertsManager().forceAlert(player.getName() + " caught using Phase. " + this.stage);
                        break;
                    }

                    break;
                }
            }
            case "PacketPlayInPosition": {
                if(this.stage >= 2) {
                    ++this.stage;
                    break;
                }

                break;
            }
        }
    }
}
