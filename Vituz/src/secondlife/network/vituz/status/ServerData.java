package secondlife.network.vituz.status;

import lombok.Data;
import lombok.Getter;
import secondlife.network.vituz.utilties.Color;

import java.util.HashSet;
import java.util.Set;

@Data
public class ServerData {

    @Getter
    private static Set<ServerData> servers = new HashSet<>();

    private String name;
    private String motd;
    private int onlinePlayers;
    private int maximumPlayers;
    private boolean whitelisted;
    private long lastUpdate;
    private double tps;

    public ServerData(String name) {
        this.name = name;

        servers.add(this);
    }

    public boolean isOnline() {
        return System.currentTimeMillis() - this.lastUpdate < 15000L;
    }

    public static ServerData getByName(String name) {
        for(ServerData server : servers) {
            if(server.getName().equalsIgnoreCase(name)) {
                return server;
            }
        }

        return null;
    }

    public String getTranslatedStatus() {
        String status;

        if(isOnline() && !isWhitelisted()) {
            status = Color.translate("&aOnline");
        } else if(isOnline() && isWhitelisted()) {
            status = Color.translate("&eWhitelisted");
        } else {
            status = Color.translate("&cOffline");
        }

        return status;
    }
}
