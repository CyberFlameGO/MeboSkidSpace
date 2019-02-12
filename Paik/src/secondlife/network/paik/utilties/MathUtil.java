package secondlife.network.paik.utilties;

import net.minecraft.server.v1_8_R3.MathHelper;

public class MathUtil {
    
    public static float[] getRotationFromPosition(CustomLocation playerLocation, CustomLocation targetLocation) {
        double xDiff = targetLocation.getX() - playerLocation.getX();
        double zDiff = targetLocation.getZ() - playerLocation.getZ();
        double yDiff = targetLocation.getY() - (playerLocation.getY() + 0.12);
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));

        return new float[] { yaw, pitch };
    }
    
    public static int pingFormula(long ping) {
        return (int)Math.ceil(ping / 2L / 50.0) + 2;
    }
    
    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float distance = Math.abs(angle1 - angle2) % 360.0f;

        if(distance > 180.0f) distance = 360.0f - distance;

        return distance;
    }
}
