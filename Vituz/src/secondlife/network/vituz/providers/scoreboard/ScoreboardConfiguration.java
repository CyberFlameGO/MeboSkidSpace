package secondlife.network.vituz.providers.scoreboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import secondlife.network.vituz.providers.ScoreProvider;

@Getter
@Setter
@NoArgsConstructor
public class ScoreboardConfiguration {

    private TitleGetter titleGetter;
    private ScoreProvider scoreGetter;
}