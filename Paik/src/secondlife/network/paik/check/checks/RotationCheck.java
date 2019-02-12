package secondlife.network.paik.check.checks;

import secondlife.network.paik.Paik;
import secondlife.network.paik.check.AbstractCheck;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.vituz.utilties.update.RotationUpdate;

public abstract class RotationCheck extends AbstractCheck<RotationUpdate> {
    
    public RotationCheck(Paik plugin, PlayerData playerData, String name) {
        super(plugin, playerData, RotationUpdate.class, name);
    }
}
