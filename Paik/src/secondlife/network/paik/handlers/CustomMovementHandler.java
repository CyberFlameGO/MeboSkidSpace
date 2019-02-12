package secondlife.network.paik.handlers;

import club.minemen.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.check.ICheck;
import secondlife.network.paik.commands.sub.ToggleCommand;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.BlockUtil;
import secondlife.network.paik.utilties.Handler;
import secondlife.network.vituz.data.PunishData;
import secondlife.network.vituz.utilties.update.PositionUpdate;
import secondlife.network.vituz.utilties.update.RotationUpdate;


public class CustomMovementHandler extends Handler implements MovementHandler {

    public CustomMovementHandler(Paik plugin) {
        super(plugin);
    }

    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packet) {
        if(player.getAllowFlight()) return;
        if(player.isInsideVehicle()) return;
        if(to.getY() < 2.0) return;
        if(!player.getWorld().isChunkLoaded(to.getBlockX() >> 4, to.getBlockZ() >> 4)) return;

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if(playerData == null) return;

        PunishData profile = PunishData.getByName(player.getName());

        if(profile == null) return;
        if(profile.isBanned()) return;
        
        playerData.setWasOnGround(playerData.isOnGround());
        playerData.setWasInLiquid(playerData.isInLiquid());
        playerData.setWasUnderBlock(playerData.isUnderBlock());
        playerData.setWasInWeb(playerData.isInWeb());
        playerData.setOnGround(BlockUtil.isOnGround(to, 0) || BlockUtil.isOnGround(to, 1));

        if(!playerData.isOnGround()) {
            positions: for (BlockPosition position : playerData.getFakeBlocks()) {
                int x = position.getX();
                int z = position.getZ();

                int blockX = to.getBlock().getX();
                int blockZ = to.getBlock().getZ();

                for(int xOffset = -1; xOffset <= 1; xOffset++) {
                    for(int zOffset = -1; zOffset <= 1; zOffset++) {
                        if(x == blockX + xOffset && z == blockZ + zOffset) {
                            int y = position.getY();
                            int pY = to.getBlock().getY();

                            if(pY - y <= 1 && pY > y) {
                                playerData.setOnGround(true);
                            }

                            if(playerData.isOnGround()) {
                                break positions;
                            }
                        }
                    }
                }
            }
        }

        if(playerData.isOnGround()) {
            playerData.setLastGroundY(to.getY());
        }

        playerData.setInLiquid(BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1) || BlockUtil.isOnLiquid(to, -1));
        playerData.setInWeb(BlockUtil.isOnWeb(to, 0));
        playerData.setOnIce(BlockUtil.isOnIce(to, 1) || BlockUtil.isOnIce(to, 2));

        if(playerData.isOnIce()) {
            playerData.setMovementsSinceIce(0);
        } else {
            playerData.setMovementsSinceIce(playerData.getMovementsSinceIce() + 1);
        }

        playerData.setOnStairs(BlockUtil.isOnStairs(to, 0) || BlockUtil.isOnStairs(to, 1));
        playerData.setOnCarpet(BlockUtil.isOnCarpet(to, 0) || BlockUtil.isOnCarpet(to, 1));
        playerData.setUnderBlock(BlockUtil.isOnGround(to, -2));

        if(playerData.isUnderBlock()) {
            playerData.setMovementsSinceUnderBlock(0);
        } else {
            playerData.setMovementsSinceUnderBlock(playerData.getMovementsSinceUnderBlock() + 1);
        }

        if(to.getY() != from.getY() && playerData.getVelocityV() > 0) {
            playerData.setVelocityV(playerData.getVelocityV() - 1);
        }

        if(Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ()) > 0.0 && playerData.getVelocityH() > 0) {
            playerData.setVelocityH(playerData.getVelocityH() - 1);
        }

        for(Class<? extends ICheck> checkClass : PlayerData.CHECKS) {
            if(!ToggleCommand.DISABLED_CHECKS.contains(checkClass.getSimpleName().toUpperCase())) {
                ICheck check = playerData.getCheck(checkClass);

                if(check != null && check.getType() == PositionUpdate.class) {
                    check.handleCheck(player, new PositionUpdate(player, to, from, packet));
                }
            }
        }

        if(playerData.getVelocityY() > 0.0 && to.getY() > from.getY()) {
            playerData.setVelocityY(0.0);
        }
    }
    
    public void handleUpdateRotation(Player player, Location to, Location from, PacketPlayInFlying packet) {
        if(player.getAllowFlight()) return;

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if(playerData == null) return;

        for(Class<? extends ICheck> checkClass : PlayerData.CHECKS) {
            if(!ToggleCommand.DISABLED_CHECKS.contains(checkClass.getSimpleName().toUpperCase())) {
                ICheck check = playerData.getCheck(checkClass);

                if(check != null && check.getType() == RotationUpdate.class) {
                    check.handleCheck(player, new RotationUpdate(player, to, from, packet));
                }
            }
        }
    }
}
