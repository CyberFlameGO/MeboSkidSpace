package secondlife.network.vituz.providers.tab;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TabUtils {
	
	private static Map<String, GameProfile> cache = new ConcurrentHashMap<>();

	public static boolean is18(Player player) {
		return ProtocolSupportAPI.getProtocolVersion(player) == ProtocolVersion.MINECRAFT_1_8;
	}

	public static GameProfile getOrCreateProfile(String name, UUID id) {
		GameProfile player = cache.get(name);
		
		if(player == null) {
			player = new GameProfile(id, name);
			player.getProperties().putAll(VituzTab.getDefaultPropertyMap());
			
			cache.put(name, player);
		}
		
		return player;
	}

	public static GameProfile getOrCreateProfile(String name) {
		return getOrCreateProfile(name, new UUID(new Random().nextLong(), new Random().nextLong()));
	}
}
