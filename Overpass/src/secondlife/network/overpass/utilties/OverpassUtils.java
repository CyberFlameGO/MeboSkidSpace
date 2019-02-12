package secondlife.network.overpass.utilties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.overpass.Overpass;
import secondlife.network.overpass.utilties.events.LoginEvent;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Permission;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marko on 10.05.2018.
 */
public class OverpassUtils {

    public static String PREFIX = Color.translate("");

    public static boolean isPremium(Player player) {
        if(Overpass.getInstance().getOverpassManager().getUsers().contains(player.getName())) {
            return true;
        }

        return false;
    }

    public static boolean isPasswordValid(String name, String password) {
        if(password.startsWith("123")
                || password.equalsIgnoreCase(name)
                || password.contains("qwertz")
                || password.equalsIgnoreCase("blabla")
                || password.equalsIgnoreCase("kurac")
                || password.equalsIgnoreCase("picka")
                || password.length() < 6) {
            return false;
        }

        return true;
    }

    public static boolean isNicknameValid(String nickname) {
        String ePattern = "[a-zA-Z0-9_]*";

        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(nickname);

        return m.matches();
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static void callLoginEvent(Player player) {
        if(player.hasPermission(Permission.STAFF_PERMISSION)) {
            Bukkit.getPluginManager().callEvent(new LoginEvent(player));
        } else {
            OverpassUtils.sendAuthToBungee(player);
        }
    }

    public static void sendAuthToBungee(Player player) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("AuthChannel");
            out.writeUTF(player.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(Overpass.getInstance(), "Auth", b.toByteArray());
    }
}
