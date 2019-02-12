package secondlife.network.meetupgame;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import secondlife.network.meetupgame.managers.GameManager;
import secondlife.network.meetupgame.managers.InventoryManager;
import secondlife.network.meetupgame.managers.VoteManager;
import secondlife.network.vituz.utilties.config.ConfigFile;

/**
 * Created by Marko on 11.06.2018.
 */

@Getter
public class MeetupGame extends JavaPlugin {

    @Getter private static MeetupGame instance;

    private GameManager gameManager;
    private InventoryManager inventoryManager;
    private VoteManager voteManager;

    private ConfigFile mainConfig, kitsFile;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }
}
