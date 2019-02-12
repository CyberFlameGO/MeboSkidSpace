package secondlife.network.hcfactions.stattrack;

import secondlife.network.vituz.utilties.Color;

public enum StatTrackerType {

    WEAPON, ARMOR;

    public String getHeader() {
        return Color.translate("" + (this == WEAPON ? "&6&lKills: &d%COUNT%" : "&6&lDeaths: &d%COUNT%"));
    }

    public String getLine() {
        return Color.translate("" + (this == WEAPON ? "&d%PLAYER%&e killed by &d%KILLER%" : "&d%PLAYER%&e killed by &d%KILLER%"));
    }

}
