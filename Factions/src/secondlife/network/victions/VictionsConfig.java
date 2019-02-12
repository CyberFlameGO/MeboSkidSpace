package secondlife.network.victions;

import lombok.Getter;

/**
 * Created by Marko on 18.07.2018.
 */
public class VictionsConfig {

    @Getter private static boolean tntExplosion = Victions.getInstance().getMainConfig().getBoolean("tntExplosion");
    @Getter private static boolean tnTExplosionDamage = Victions.getInstance().getMainConfig().getBoolean("tnTExplosionDamage");
    @Getter private static boolean creeperExplosion = Victions.getInstance().getMainConfig().getBoolean("creeperExplosion");
    @Getter private static boolean creeperExplosionDamage = Victions.getInstance().getMainConfig().getBoolean("creeperExplosionDamage");
    @Getter private static boolean waterSponge = Victions.getInstance().getMainConfig().getBoolean("waterSponge");
    @Getter private static boolean lavaSponge = Victions.getInstance().getMainConfig().getBoolean("lavaSponge");
}
