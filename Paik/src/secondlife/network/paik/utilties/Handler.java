package secondlife.network.paik.utilties;

import secondlife.network.paik.Paik;

/**
 * Created by Marko on 05.05.2018.
 */
public class Handler {

    protected Paik plugin;

    public Handler(Paik plugin) {
        this.plugin = plugin;
    }

    public Paik getInstance() {
        return plugin;
    }
}
