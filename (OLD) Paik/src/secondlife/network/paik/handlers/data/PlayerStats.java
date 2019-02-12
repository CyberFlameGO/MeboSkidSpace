package secondlife.network.paik.handlers.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStats {

	private UUID uuid;

	private ArrayList<String> logs = new ArrayList<String>();

	private Entity lastEntity;

	private int leftClickCPS = 0;
	private int autoclickerVL = 0;
	private int constantCPS = 0;
	private int doubleclick = 0;

	private long click1 = 0;
	private long click2 = 0;

	private int regenVL = 0;
	
	private long bowVL = 0;
	private long pull;

	private int swingAngle = 0;
	private int botHits = 0;
	private int hits = 0;
	private long lastUseEntityPacket;
	private int hitsInvalidPacket = 0;
	private int hitsWhileDead = 0;
	private int noSwingDamageVL = 0;

	private int reachVL;
	private double delta;

	private long lastRegen;

	private int flyAVL = 0;
	private int flyBVL = 0;

	private int fastEatVL = 0;
	private long lastEat;
	
	private boolean inventoryOpen = false;
	private int movesWhileInventoryClosed = 0;
	private int hitsWhileInventoryOpen = 0;
	private int potionsSplashedWhileInventoryOpen = 0;

	private int nofallVL = 0;

	private int noslowFoodVl = 0;
	private int noslowBowVl = 0;
	
	private long sneak1;
	private long sneak2;
	private int sneakVL = 0;
	
	private int speedVL = 0;
	private int speedJumpVL = 0;
	private int speedOtherVL = 0;
	private int speedSlowhopVL = 0;
	private int speedFast = 0;

	private int morePackets = 0;
	private int flyingPackets = 0;
	private int positionPackets = 0;
	private int positionLookPackets = 0;
	private int timerAVL = 0;
	private int timerBVL = 0;
	private int timerCVL = 0;
	
	private int boxer1 = 0;
	private int boxer2 = 0;

	private int lastPing;
	private int pingSpoof = 0;

	private long lastClick;
	private int refill;
	private int refillOther;
	private int lastSlot;

	private int autoblock = 0;
	private int autoblock2 = 0;

	private float lastYaw;
	private float lastPitch;
	private int angle;

	private int invalidInteract = 0;

	private long joined;
	private long lastBlockPlace;
	private long lastBlockBreak;
	private long lastBlockPlacePacket;
	private long lastArmPacket = 0;
	private long lastBlockPacket = 0;
	private long lastBlockDigPacket = 0;

	private int vl = 0;

	private boolean ocmc = false;
	private boolean banned = false;

	
	public PlayerStats(Player player) {
		this.uuid = player.getUniqueId();
	}
}