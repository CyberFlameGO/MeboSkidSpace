package secondlife.network.paik.check.impl.autoclicker;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.checks.PacketCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

public class AutoClickerJ extends PacketCheck {

	private int stage;

	public AutoClickerJ(Paik plugin, PlayerData playerData) {
		super(plugin, playerData, "Auto-Clicker (Check 10)");
	}

	@Override
	public void handleCheck(Player player, Packet packet) {
		if(this.stage == 0) {
			if(packet instanceof PacketPlayInArmAnimation) {
				++this.stage;
			}
		} else if(packet instanceof PacketPlayInBlockDig) {
			if(this.playerData.getFakeBlocks().contains(((PacketPlayInBlockDig) packet).a())) return;

			double vl = this.getVl();

			PacketPlayInBlockDig.EnumPlayerDigType digType = ((PacketPlayInBlockDig) packet).c();

			if(digType == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
				if(this.stage == 1) {
					++this.stage;
				} else {
					this.stage = 0;
				}
			} else if(digType == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
				if(this.stage == 2) {
					if((vl += 1.4) >= 15.0 &&
					    this.alert(PlayerAlertEvent.AlertType.RELEASE, player, String.format("VL %.2f.", vl), true) &&
					    !this.playerData.isBanning() && vl >= 50.0) {
						this.ban(player);
					}
				} else {
					this.stage = 0;
					vl -= 0.25;
				}
			} else {
				this.stage = 0;
			}

			this.setVl(vl);
		} else {
			this.stage = 0;
		}
	}
}
