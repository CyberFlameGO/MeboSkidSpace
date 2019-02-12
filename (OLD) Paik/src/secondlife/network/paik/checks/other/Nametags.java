package secondlife.network.paik.checks.other;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import secondlife.network.paik.utils.CheatUtils;

public class Nametags {

    public static void handleHealthRandomizer(Player player, PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Entity entity = packet.getEntityModifier(event).read(0);

        if(entity instanceof LivingEntity
                && entity.getType() == EntityType.PLAYER
                && packet.getWatchableCollectionModifier().read(0) != null
                && entity.getUniqueId() != player.getUniqueId()) {
            packet = packet.deepClone();
            event.setPacket(packet);

            WrappedDataWatcher watcher = new WrappedDataWatcher(packet.getWatchableCollectionModifier().read(0));

            if(watcher != null && watcher.getObject(6) != null && watcher.getFloat(6) != 0.0F) {
                float hp = CheatUtils.random(1, 10);

                watcher.setObject(6, hp);
            }

            packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        }
    }
}
