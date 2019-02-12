package secondlife.network.vituz.providers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import secondlife.network.vituz.providers.nametags.NametagInfo;
import secondlife.network.vituz.providers.nametags.VituzNametag;

@Getter
@AllArgsConstructor
public abstract class NametagProvider {

    private String name;
    private int weight;

    public abstract NametagInfo fetchNametag(Player toRefresh, Player refreshFor);

    public static NametagInfo createNametag(String prefix, String suffix) {
        return (VituzNametag.getOrCreate(prefix, suffix));
    }

    public static class DefaultNametagProvider extends NametagProvider {

        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        @Override
        public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            return (createNametag("", ""));
        }
    }
}