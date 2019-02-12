package secondlife.network.hcf;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Marko on 26.07.2018.
 */

@Getter
public class HCF extends JavaPlugin {

    @Getter
    private static HCF instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }
}
