package secondlife.network.uhc.deathlookup.data.killer.type;

import lombok.Getter;
import secondlife.network.uhc.deathlookup.data.ProfileFightEnvironment;
import secondlife.network.uhc.deathlookup.data.killer.ProfileFightKiller;

public class ProfileFightEnvironmentKiller extends ProfileFightKiller {

    @Getter private final ProfileFightEnvironment type;

    public ProfileFightEnvironmentKiller(ProfileFightEnvironment type) {
        super(null, null);

        this.type = type;
    }
}
