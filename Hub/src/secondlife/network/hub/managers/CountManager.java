package secondlife.network.hub.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import secondlife.network.hub.Hub;
import secondlife.network.hub.utilties.Manager;
import secondlife.network.vituz.utilties.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Marko on 28.03.2018.
 */

@Getter
public class CountManager extends Manager implements PluginMessageListener {

    private int globalCount = 0;

    public CountManager(Hub plugin) {
        super(plugin);

        handleRegisterServers();

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    private void handleRegisterServers() {
        Tasks.runAsyncTimer(() -> {
            sendToBungee("PlayerCount", "ALL");
        }, 20L, 20L);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals("BungeeCord")) return;

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subchannel = input.readUTF();

        if(!subchannel.equals("PlayerCount")) return;

        String serverName = input.readUTF();
        int playerCount = input.readInt();

        if(serverName.equalsIgnoreCase("ALL")) {
            globalCount = playerCount;
        }
    }

    public void sendToBungee(String channel, String sub) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF(channel);
            out.writeUTF(sub);
        } catch(IOException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().sendPluginMessage(Hub.getInstance(), "BungeeCord", b.toByteArray());
    }
}
