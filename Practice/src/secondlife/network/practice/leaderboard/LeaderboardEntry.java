package secondlife.network.practice.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Created by joeleoli on 22.06.2018.
 */
@Data @AllArgsConstructor
public class LeaderboardEntry {

    private String name;
    private int elo;

}
