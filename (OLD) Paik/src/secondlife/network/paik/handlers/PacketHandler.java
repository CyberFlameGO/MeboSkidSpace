package secondlife.network.paik.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.checks.combat.AutoBlock;
import secondlife.network.paik.checks.combat.Killaura;
import secondlife.network.paik.checks.combat.Reach;
import secondlife.network.paik.checks.movement.Inventory;
import secondlife.network.paik.checks.movement.Sneak;
import secondlife.network.paik.checks.movement.Timer;
import secondlife.network.paik.checks.other.*;
import secondlife.network.paik.handlers.data.PlayerStats;
import secondlife.network.paik.handlers.data.PlayerStatsHandler;

public class PacketHandler extends PacketAdapter {
	
	public PacketHandler(Paik plugin) {
		super(plugin, ListenerPriority.HIGH, new PacketType[] {
				PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.KEEP_ALIVE, PacketType.Play.Client.CLOSE_WINDOW,
				PacketType.Play.Client.CUSTOM_PAYLOAD, PacketType.Play.Client.BLOCK_PLACE, PacketType.Play.Client.BLOCK_DIG,
				PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.ARM_ANIMATION,
				PacketType.Play.Client.POSITION, PacketType.Play.Client.FLYING, PacketType.Play.Client.WINDOW_CLICK,
				PacketType.Play.Client.ENTITY_ACTION,

		        PacketType.Play.Server.ENTITY_METADATA });
	}

	public void onPacketReceiving(PacketEvent event) {
		Player player = event.getPlayer();
		if(player == null) return;

		PlayerStats stats = PlayerStatsHandler.getStats(player);
		if(stats == null) return;

		PacketType PACKET_TYPE = event.getPacketType();

		if(PACKET_TYPE == PacketType.Play.Client.ENTITY_ACTION) {
			this.handleEntityActionPacket(player, stats, event);
		}

		if(PACKET_TYPE == PacketType.Play.Client.USE_ENTITY) {
			this.handleUseEntityPacket(player, stats, event);
		}

		if(PACKET_TYPE == PacketType.Play.Client.KEEP_ALIVE) {
			this.handleKeepAlivePacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.CLOSE_WINDOW) {
			this.handleWindowClosePacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.CUSTOM_PAYLOAD) {
			this.handleCustomPayloadPacket(player, stats, event);
		}

		if(PACKET_TYPE == PacketType.Play.Client.BLOCK_PLACE) {
			this.handleBlockPlacePacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.BLOCK_DIG) {
			this.handleBlockDigPacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.LOOK) {
			this.handleLookAndPositionLookPacket(player, stats, event);
			this.handlePositionLookPacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.POSITION_LOOK) {
			this.handleLookAndPositionLookPacket(player, stats, event);
			this.handlePositionLookPacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.POSITION) {
			this.handlePositionPacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.FLYING) {
			this.handleFlyingPacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.ARM_ANIMATION) {
			this.handleArmAnimationPacket(player, stats);
		}

		if(PACKET_TYPE == PacketType.Play.Client.WINDOW_CLICK) {
			this.handleWindowClickPacket(player, stats, event);
		}
	}

	public void onPacketSending(PacketEvent event) {
		Player player = event.getPlayer();
		if(player == null) return;

		PlayerStats stats = PlayerStatsHandler.getStats(player);
		if(stats == null) return;

		PacketType PACKET_TYPE = event.getPacketType();

		if(PACKET_TYPE == PacketType.Play.Server.ENTITY_METADATA) {
			this.handleEntityMetadataPacket(player, event);
		}
	}

	public void handleWindowClosePacket(Player player, PlayerStats stats) {
		Inventory.handleInventoryClose(player, stats);
	}

	public void handleUseEntityPacket(Player player, PlayerStats stats, PacketEvent event) {
		EnumWrappers.EntityUseAction type;
		try {
			type = event.getPacket().getEntityUseActions().read(0);
		} catch (Exception ex) {
			return;
		}

		if(type != EntityUseAction.ATTACK) return;

		int entityID = event.getPacket().getIntegers().read(0);

		stats.setLastUseEntityPacket(System.currentTimeMillis());

		Reach.handleReachCheck(player, stats, entityID);
		Killaura.handleKillauraBotCheck(player, entityID);

		Entity hit = null;

		if(player.getWorld().getEntities().size() > 0) {
			for(Entity entity : player.getWorld().getEntities()) {
				if(entity.getEntityId() == entityID) {
					hit = entity;
				}
			}
		}

		if(hit == null) return;

		stats.setLastEntity(hit);
	}

	public void handleKeepAlivePacket(Player player, PlayerStats stats) {
		PingSpoof.handlePingSpoof(player, stats);
	}

	public void handleCustomPayloadPacket(Player player, PlayerStats stats, PacketEvent event) {
		String message = event.getPacket().getStrings().read(0);

		CustomPayload.handleCustomPayload(player, stats, message);
	}

	public void handleBlockPlacePacket(Player player, PlayerStats stats) {
		stats.setLastBlockPlacePacket(System.currentTimeMillis());
		stats.setAutoblock2(stats.getAutoblock2() + 1);

		AutoBlock.handleAutoBlockPlace(player, stats);
		Crash.handleBlockPlaceCrash(player, stats);
	}

	public void handleBlockDigPacket(Player player, PlayerStats stats) {
		stats.setLastBlockDigPacket(System.currentTimeMillis());
		stats.setAutoblock2(stats.getAutoblock2() + 1);

		AutoBlock.handleAutoBlockDig(player, stats);
		AutoBlock.handleAutoBlock(player, stats);
	}

	public void handleLookAndPositionLookPacket(Player player, PlayerStats stats, PacketEvent event) {
		float yaw = event.getPacket().getFloat().read(0);
		float pitch = event.getPacket().getFloat().read(1);

		ImpossiblePitch.handleImpossiblePitch(player, stats, pitch);
		Killaura.handleKillauraAngle(player, stats, yaw, pitch);
	}

	public void handlePositionLookPacket(Player player, PlayerStats stats) {
		Timer.handleTimerPositionLook(player, stats);
	}

	public void handlePositionPacket(Player player, PlayerStats stats) {
		stats.setMorePackets(stats.getMorePackets() + 1);

		Timer.handleTimerPosition(player, stats);
	}

	public void handleFlyingPacket(Player player, PlayerStats stats) {
		Timer.handleTimerFlying(player, stats);
	}

	public void handleArmAnimationPacket(Player player, PlayerStats stats) {
		Crash.handleAnimationCrash(player, stats);
		Killaura.handleKillauraWall(player, stats);
	}

	public void handleWindowClickPacket(Player player, PlayerStats stats, PacketEvent event) {
		Refill.handleRefill(player, stats, event);
	}

	public void handleEntityActionPacket(Player player, PlayerStats stats, PacketEvent event) {
		int actionId = event.getPacket().getIntegers().read(1);

		if(actionId == 1 || actionId == 2) {
			Sneak.handleSneak(player, stats);
		}
	}

	public void handleEntityMetadataPacket(Player player, PacketEvent event) {
		Nametags.handleHealthRandomizer(player, event);
	}
}
