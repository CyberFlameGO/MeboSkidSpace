package secondlife.network.uhc.deathlookup;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.uhc.deathlookup.data.ProfileFight;

@Getter
@Setter
public class DeathLookupData {

    private ProfileFight fight;
    private int page;
    private int index;

}
