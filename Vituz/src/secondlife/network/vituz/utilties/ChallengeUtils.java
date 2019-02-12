package secondlife.network.vituz.utilties;

import org.bukkit.entity.Player;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.data.ChallengeData;

public class ChallengeUtils {

    public static void givePoints(Player player, int startPoints) {
        ChallengeData data = ChallengeData.getByName(player.getName());
        int points = data.getPoints();

        switch(VituzAPI.getRankName(player.getName())) {
            case "Hydrogen":
                points+= (startPoints * 1.1);
                break;
            case "Nitrogen":
                points+= (startPoints * 1.2);
                break;
            case "Titanium":
                points+= (startPoints * 1.3);
                break;
            case "Krypton":
                points+= (startPoints * 1.4);
                break;
            case "Xenon":
                points+= (startPoints * 1.5);
                break;
            case "Media":
                points+= (startPoints * 1.3);
                break;
            case "Partner":
                points+= (startPoints * 1.5);
                break;
            default:
                points+= startPoints;
                break;
        }

        data.setPoints(points);
    }

    public static String getPoints(Player player, int startPoints) {
        switch(VituzAPI.getRankName(player.getName())) {
            case "Hydrogen":
                return "&c" + (startPoints * 1.1) + " (Hydrogen Rank 10% multiplier)&d";
            case "Nitrogen":
                return "&c" + (startPoints * 1.2) + " (Nitrogen Rank 20% multiplier)&d";
            case "Titanium":
                return "&c" + (startPoints * 1.3) + " (Titanium Rank 30% multiplier)&d";
            case "Krypton":
                return "&c" + (startPoints * 1.4) + " (Krypton Rank 40% multiplier)&d";
            case "Xenon":
                return "&c" + (startPoints * 1.5) + " (Xenon Rank 50% multiplier)&d";
            case "Media":
                return "&c" + (startPoints * 1.3) + " (Media Rank 30% multiplier)&d";
            case "Partner":
                return "&c" + (startPoints * 1.5) + " (Partner Rank 50% multiplier)&d";
            default:
                return "&c" + startPoints + "&d";
        }
    }
}
