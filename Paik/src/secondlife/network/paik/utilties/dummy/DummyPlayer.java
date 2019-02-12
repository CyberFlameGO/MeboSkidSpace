package secondlife.network.paik.utilties.dummy;

import java.util.*;
import com.mojang.authlib.*;
import net.minecraft.server.v1_8_R3.*;

public class DummyPlayer extends EntityPlayer {
    
    public DummyPlayer(Entity entity, String name) {
        super(MinecraftServer.getServer(), (WorldServer)entity.getWorld(), new GameProfile(UUID.randomUUID(), name), (PlayerInteractManager)new DummyPlayerInteractManager(entity.getWorld()));
    }
}
