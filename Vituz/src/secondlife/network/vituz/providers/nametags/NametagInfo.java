package secondlife.network.vituz.providers.nametags;

import lombok.Getter;
import secondlife.network.vituz.providers.packets.ScoreboardTeamPacketMod;

import java.util.ArrayList;

@Getter
public class NametagInfo {

    private String name;
    private String prefix;
    private String suffix;
    private ScoreboardTeamPacketMod teamAddPacket;

    protected NametagInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList<String>(), 0);
    }
}