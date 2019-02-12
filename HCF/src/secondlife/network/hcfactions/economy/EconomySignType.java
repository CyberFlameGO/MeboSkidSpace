package secondlife.network.hcfactions.economy;

import secondlife.network.hcfactions.utilties.file.ConfigFile;

import java.util.List;

public enum EconomySignType {

    BUY, SELL;

    public List<String> getSignText() {
        return ConfigFile.getStringList("ECONOMY.SIGN." + name() + "_TEXT");
    }
}
