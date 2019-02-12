package secondlife.network.vituz.providers.scoreboard;

import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import secondlife.network.vituz.utilties.Color;

@NoArgsConstructor
public class TitleGetter {

    private String defaultTitle;
    public TitleGetter(String defaultTitle) {
        this.defaultTitle = Color.translate(defaultTitle);
    }
    String getTitle(Player player) { return defaultTitle; }
}