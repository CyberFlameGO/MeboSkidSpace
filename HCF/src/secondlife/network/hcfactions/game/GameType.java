package secondlife.network.hcfactions.game;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import secondlife.network.hcfactions.game.events.GameManager;
import secondlife.network.hcfactions.game.type.KothType;

@Getter
public enum GameType {

	KOTH("KoTH", new KothType());

	private GameManager eventType;
    private String displayName;

	GameType(String displayName, GameManager eventType) {
        this.displayName = displayName;
        this.eventType = eventType;
    }

    private static ImmutableMap<String, GameType> byDisplayName;

    static {
        ImmutableMap.Builder<String, GameType> builder = new ImmutableBiMap.Builder<>();
        
        for(GameType eventType : values()) {
            builder.put(eventType.displayName.toLowerCase(), eventType);
        }

        byDisplayName = builder.build();
    }
}
