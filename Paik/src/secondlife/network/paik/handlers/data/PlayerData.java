package secondlife.network.paik.handlers.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.ICheck;
import secondlife.network.paik.check.impl.aimassist.*;
import secondlife.network.paik.check.impl.autoclicker.*;
import secondlife.network.paik.check.impl.badpackets.*;
import secondlife.network.paik.check.impl.fly.FlyA;
import secondlife.network.paik.check.impl.fly.FlyB;
import secondlife.network.paik.check.impl.fly.FlyC;
import secondlife.network.paik.check.impl.inventory.*;
import secondlife.network.paik.check.impl.killaura.*;
import secondlife.network.paik.check.impl.phase.PhaseA;
import secondlife.network.paik.check.impl.phase.PhaseB;
import secondlife.network.paik.check.impl.range.RangeA;
import secondlife.network.paik.check.impl.scaffold.ScaffoldA;
import secondlife.network.paik.check.impl.scaffold.ScaffoldB;
import secondlife.network.paik.check.impl.scaffold.ScaffoldC;
import secondlife.network.paik.check.impl.step.StepA;
import secondlife.network.paik.check.impl.timer.TimerA;
import secondlife.network.paik.check.impl.vclip.VClipA;
import secondlife.network.paik.check.impl.vclip.VClipB;
import secondlife.network.paik.check.impl.velocity.VelocityA;
import secondlife.network.paik.check.impl.velocity.VelocityB;
import secondlife.network.paik.check.impl.velocity.VelocityC;
import secondlife.network.paik.check.impl.wtap.WTapA;
import secondlife.network.paik.check.impl.wtap.WTapB;
import secondlife.network.paik.client.EnumClientType;
import secondlife.network.paik.utilties.CustomLocation;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class PlayerData {

	private static Map<Class<? extends ICheck>, Constructor<? extends ICheck>> CONSTRUCTORS;
	public static Class<? extends ICheck>[] CHECKS;

	private Map<UUID, List<CustomLocation>> recentPlayerPackets;
	private Map<ICheck, Set<Long>> checkViolationTimes;

	private Map<Class<? extends ICheck>, ICheck> checkMap;
	private Map<Integer, Long> keepAliveTimes;
	private Map<ICheck, Double> checkVlMap;

	private Set<BlockPosition> fakeBlocks = new HashSet<>();

	private Set<UUID> playersWatching;
	private List<String> logs;
	private Set<String> filteredPhrases;
	private Set<String> phrasesListeningTo;
	private Set<CustomLocation> teleportLocations;
	private Map<String, String> forgeMods;
	private StringBuilder sniffedPacketBuilder;
	private CustomLocation lastMovePacket;
	private EnumClientType client;
	private UUID lastTarget;
	private double misplace;
	private boolean allowTeleport;
	private boolean inventoryOpen;
	private boolean setInventoryOpen;
	private boolean sendingVape;
	private boolean attackedSinceVelocity;
	private boolean underBlock;
	private boolean sprinting;
	private boolean inLiquid;
	private boolean instantBreakDigging;
	private boolean fakeDigging;
	private boolean onGround;
	private boolean sniffing;
	private boolean onStairs;
	private boolean onCarpet;
	private boolean placing;
	private boolean banning;
	private boolean digging;
	private boolean inWeb;
	private boolean onIce;
	private boolean wasUnderBlock;
	private boolean wasOnGround;
	private boolean wasInLiquid;
	private boolean wasInWeb;
	private double lastGroundY;
	private double velocityX;
	private double velocityY;
	private double velocityZ;
	private long lastDelayedMovePacket;
	private long lastAnimationPacket;
	private long lastAttackPacket;
	private long lastVelocity;
	private long ping;
	private long interact;
	private int velocityH;
	private int velocityV;
	private int lastCps;
	private int movementsSinceIce;
	private int movementsSinceUnderBlock;

	public PlayerData(Paik plugin) {
		this.recentPlayerPackets = new HashMap<>();
		this.checkViolationTimes = new HashMap<>();
		this.checkMap = new HashMap<>();
		this.keepAliveTimes = new HashMap<>();
		this.checkVlMap = new HashMap<>();
		this.playersWatching = new HashSet<>();
		this.logs = new ArrayList<>();
		this.filteredPhrases = new HashSet<>();
		this.phrasesListeningTo = new HashSet<>();
		this.teleportLocations = Collections.newSetFromMap(new ConcurrentHashMap<CustomLocation, Boolean>());
		this.sniffedPacketBuilder = new StringBuilder();
		this.client = EnumClientType.VANILLA;
		this.banning = false;

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
				PlayerData.CONSTRUCTORS.keySet().stream().map(o -> (Class<? extends ICheck>) o).forEach(check -> {
					Constructor<? extends ICheck> constructor = PlayerData.CONSTRUCTORS.get(check);

					try {
						this.checkMap.put(check, constructor.newInstance(plugin, this));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}));
	}

	public <T extends ICheck> T getCheck(Class<T> clazz) {
		return (T) this.checkMap.get(clazz);
	}

	public CustomLocation getLastPlayerPacket(UUID playerUUID, int index) {
		List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);

		if(customLocations != null && customLocations.size() > index) {
			return customLocations.get(customLocations.size() - index);
		}

		return null;
	}

	public void addPlayerPacket(UUID playerUUID, CustomLocation customLocation) {
		List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);

		if(customLocations == null) {
			customLocations = new ArrayList<>();
		}

		if(customLocations.size() == 20) {
			customLocations.remove(0);
		}

		customLocations.add(customLocation);

		this.recentPlayerPackets.put(playerUUID, customLocations);
	}

	public void addTeleportLocation(CustomLocation teleportLocation) {
		this.teleportLocations.add(teleportLocation);
	}

	public boolean allowTeleport(CustomLocation teleportLocation) {
		for(CustomLocation customLocation : this.teleportLocations) {
			double delta = Math.pow(teleportLocation.getX() - customLocation.getX(), 2.0) +
			                     Math.pow(teleportLocation.getZ() - customLocation.getZ(), 2.0);
			if(delta <= 0.005) {
				this.teleportLocations.remove(customLocation);
				return true;
			}
		}

		return false;
	}

	public double getCheckVl(ICheck check) {
		if(!this.checkVlMap.containsKey(check)) this.checkVlMap.put(check, 0.0);

		return this.checkVlMap.get(check);
	}

	public void setCheckVl(double vl, ICheck check) {
		if(vl < 0.0) vl = 0.0;

		this.checkVlMap.put(check, vl);
	}

	public boolean keepAliveExists(int id) {
		return this.keepAliveTimes.containsKey(id);
	}

	public long getKeepAliveTime(int id) {
		return this.keepAliveTimes.get(id);
	}

	public void removeKeepAliveTime(int id) {
		this.keepAliveTimes.remove(id);
	}

	public void addKeepAliveTime(int id) {
		this.keepAliveTimes.put(id, System.currentTimeMillis());
	}

	public int getViolations(ICheck check, Long time) {
		Set<Long> timestamps = this.checkViolationTimes.get(check);

		if(timestamps != null) {
			int violations = 0;

			for(long timestamp : timestamps) {
				if(System.currentTimeMillis() - timestamp <= time) {
					++violations;
				}
			}

			return violations;
		}

		return 0;
	}

	public void addViolation(ICheck check) {
		Set<Long> timestamps = this.checkViolationTimes.get(check);

		if(timestamps == null) timestamps = new HashSet<>();

		timestamps.add(System.currentTimeMillis());

		this.checkViolationTimes.put(check, timestamps);
	}

	static {
		CHECKS = new Class[]{
				AimAssistA.class, AimAssistB.class /*AimAssistC.class*/, AimAssistD.class,
				/*AimAssistE.class,*/

				/*AutoClickerA.class, */AutoClickerB.class, /*, AutoClickerC.class, AutoClickerD.class,*/
				/*AutoClickerE.class, */AutoClickerF.class, AutoClickerG.class, AutoClickerH.class,
				AutoClickerI.class, AutoClickerJ.class, AutoClickerK.class, AutoClickerK.class,
				AutoClickerL.class,

				BadPacketsA.class, BadPacketsB.class, BadPacketsC.class, BadPacketsD.class,
				BadPacketsE.class, BadPacketsF.class/*, BadPacketsG.class*/, BadPacketsH.class,
				BadPacketsI.class, BadPacketsJ.class, BadPacketsK.class, BadPacketsL.class,

				FlyA.class, FlyB.class, FlyC.class,

				/*InventoryA.class, InventoryB.class, */InventoryC.class, InventoryD.class,
				InventoryE.class, InventoryF.class, /*InventoryG.class*/

				KillAuraA.class, /*KillAuraB.class, KillAuraC.class, */KillAuraD.class,
				/*KillAuraE.class, */KillAuraF.class, KillAuraG.class, KillAuraH.class,
				/*KillAuraI.class,*/ KillAuraJ.class, KillAuraK.class, KillAuraL.class,
				KillAuraM.class, KillAuraN.class, KillAuraO.class, KillAuraP.class,
				KillAuraQ.class, KillAuraR.class, KillAuraS.class,

				RangeA.class,

				TimerA.class,

				/*VelocityA.class, VelocityB.class, VelocityC.class,*/

				/*WTapA.class, WTapB.class,

				ScaffoldA.class, ScaffoldB.class, ScaffoldC.class,*/

				//StepA.class,

				//PhaseA.class, PhaseB.class,
				//VClipA.class, VClipB.class,
		};

		CONSTRUCTORS = new ConcurrentHashMap<>();

		for(Class<? extends ICheck> check : PlayerData.CHECKS) {
			try {
				PlayerData.CONSTRUCTORS.put(check, check.getConstructor(Paik.class, PlayerData.class));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}
}
