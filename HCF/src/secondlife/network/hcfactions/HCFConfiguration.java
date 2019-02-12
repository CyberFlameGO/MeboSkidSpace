package secondlife.network.hcfactions;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;

import secondlife.network.hcfactions.utilties.Handler;
import secondlife.network.hcfactions.utilties.file.ConfigFile;

public class HCFConfiguration extends Handler {
	
	public static int subclaimNameMinCharacters;
    public static int subclaimNameMaxCharacters;
    public static int factionNameMinCharacters;
    public static int factionNameMaxCharacters;
    public static int maxMembers;
    public static int roadMinHeight; 
    public static int roadMaxHeight; 
    public static int maxAllysPerFaction;
    public static int maxClaimsPerFaction;
    public static int conquestDeathLoss;
    public static int conquestWinPoints;
    public static int warzoneRadius;
    public static int citadelResetTime;
    
    public static ChatColor teammateColor;
    public static ChatColor allyColor;
    public static ChatColor captainColor;
    public static ChatColor enemyColor;
    public static ChatColor spawnColor;
    public static ChatColor roadColor;
    public static ChatColor warzoneColor;
    public static ChatColor wildernessColor;
    
    public static boolean kitMap;
    public static boolean disableObsidianGenerators;
    public static boolean allowClaimingOnRoads;
    
    public static double dtrIncrementBetweenUpdate;
    public static double maxDtr;
    
    public static String dtrWordsBetweenUpdate;
    
    public static long dtrUpdate;
    
    public static Map<World.Environment, Integer> bordersizes = new EnumMap<>(World.Environment.class);
	
    public HCFConfiguration(HCF plugin) {
		super(plugin);
		
		kitMap = ConfigFile.configuration.getBoolean("kitmap");
		
		if(kitMap) {
			bordersizes.put(World.Environment.NORMAL, 2500);
			bordersizes.put(World.Environment.NETHER, 50);
			bordersizes.put(World.Environment.THE_END, 1000);
			
			warzoneRadius = 200;
			maxMembers = 30;
			maxClaimsPerFaction = 2;
			dtrWordsBetweenUpdate = DurationFormatUtils.formatDurationWords(1, true, true);
			dtrUpdate = 1;
			dtrIncrementBetweenUpdate = 1;
			maxDtr = 6.0;
		} else {
			bordersizes.put(World.Environment.NORMAL, 3000);
			bordersizes.put(World.Environment.NETHER, 1000);
			bordersizes.put(World.Environment.THE_END, 1000);
			
			citadelResetTime = 17;
			warzoneRadius = 1000;
			maxMembers = 5;
			maxClaimsPerFaction = 8;
			dtrWordsBetweenUpdate = DurationFormatUtils.formatDurationWords(45000, true, true);
			dtrUpdate = 45000;
			dtrIncrementBetweenUpdate = 0.1;
			maxDtr = 4.0;
		}
		
		subclaimNameMinCharacters = 3;
		subclaimNameMaxCharacters = 16;
		factionNameMinCharacters = 3;
		factionNameMaxCharacters = 16;
		
		roadMinHeight = 0;
		roadMaxHeight = 256;
		
		conquestDeathLoss = 20;
		conquestWinPoints = 300;
		
		maxAllysPerFaction = 0;
		
		teammateColor = ChatColor.DARK_GREEN;
		allyColor = ChatColor.LIGHT_PURPLE;
		captainColor = ChatColor.LIGHT_PURPLE;
		enemyColor = ChatColor.YELLOW;
		spawnColor = ChatColor.GREEN;
		roadColor = ChatColor.RED;
		warzoneColor = ChatColor.RED;
		wildernessColor = ChatColor.GRAY;
		
		allowClaimingOnRoads = false;
		disableObsidianGenerators = false;
    }
}
