package secondlife.network.victions.utilities;

import java.util.Random;

/**
 * Created by Marko on 28.07.2018.
 */
public class FactionsUtils {

    private static Random random = new Random();

    public static int random(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
