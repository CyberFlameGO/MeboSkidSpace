package secondlife.network.overpass.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import secondlife.network.overpass.Overpass;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PremiumManager implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals("Premium")) return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        try {
            String subchannel = in.readUTF();
            if(subchannel.equals("Auth")) {
                String name = in.readUTF();
                boolean isPremium = in.readBoolean();

                if(isPremium) {
                    Overpass.getInstance().getOverpassManager().handleAddToList(name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
