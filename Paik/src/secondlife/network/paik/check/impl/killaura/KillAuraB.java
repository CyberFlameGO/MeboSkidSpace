package secondlife.network.paik.check.impl.killaura;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.BlockUtil;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class KillAuraB extends PacketCheck {

	private boolean sent;
	private boolean failed;
	private int movements;

	public KillAuraB(Paik plugin, PlayerData playerData) {
		super(plugin, playerData, "Kill-Aura (Check 2)");
	}

	@Override
	public void handleCheck(Player player, Packet packet) {
		if(!BlockUtil.checkMaterial(player) && this.playerData.isDigging() && !this.playerData.isInstantBreakDigging() &&
		    System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L &&
		    this.playerData.getLastMovePacket() != null &&
		    System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L &&
				System.currentTimeMillis() - this.playerData.getInteract() > 100) {

			int vl = (int) this.getVl();

			if(packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
				this.movements = 0;
				vl = 0;
			} else if(packet instanceof PacketPlayInArmAnimation && this.movements >= 2) {
				if(this.sent) {
					if(!this.failed) {
						if(++vl >= 5) {
							this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, "VL " + vl + ".", false);
						}

						this.failed = true;
					}
				} else {
					this.sent = true;
				}
			} else if(packet instanceof PacketPlayInFlying) {
				boolean b = false;

				this.failed = b;
				this.sent = b;

				++this.movements;
			}

			this.setVl(vl);
		}
	}
}
