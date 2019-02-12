package secondlife.network.hcfactions.utilties;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class PacketUtils {

    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
        try {
            return (Optional<T>) Optional.of(Enum.valueOf(enumClass, value));
        } catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }
	
    public static <T> T firstNonNull(T first, T second) {
        return (T) ((first != null) ? first : Preconditions.checkNotNull(second));
    }

    public static void resendHeldItemPacket(Player player) {
        sendItemPacketAtHeldSlot(player, getCleanHeldItem(player));
    }

    public static void sendItemPacketAtHeldSlot(Player player, net.minecraft.server.v1_8_R3.ItemStack stack) {
        sendItemPacketAtSlot(player, stack, player.getInventory().getHeldItemSlot());
    }

    public static void sendItemPacketAtSlot(Player player, net.minecraft.server.v1_8_R3.ItemStack stack, int index) {
        sendItemPacketAtSlot(player, stack, index, ((CraftPlayer) player).getHandle().defaultContainer.windowId);
    }

    public static void sendItemPacketAtSlot(Player player, net.minecraft.server.v1_8_R3.ItemStack stack, int index, int windowID) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        
        if(entityPlayer.playerConnection != null) {
            if(index < net.minecraft.server.v1_8_R3.PlayerInventory.getHotbarSize()) {
                index += 36;
            } else if(index > 35) {
                index = 8 - (index - 36);
            }

            entityPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(windowID, index, stack));
        }
    }

    public static net.minecraft.server.v1_8_R3.ItemStack getCleanItem(Inventory inventory, int slot) {
        return ((CraftInventory) inventory).getInventory().getItem(slot);
    }

    public static net.minecraft.server.v1_8_R3.ItemStack getCleanItem(Player player, int slot) {
        return getCleanItem(player.getInventory(), slot);
    }

    public static net.minecraft.server.v1_8_R3.ItemStack getCleanHeldItem(Player player) {
        return getCleanItem(player, player.getInventory().getHeldItemSlot());
    }

}
