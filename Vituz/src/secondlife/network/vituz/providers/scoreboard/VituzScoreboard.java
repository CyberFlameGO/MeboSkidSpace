package secondlife.network.vituz.providers.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.providers.packets.ScoreboardTeamPacketMod;
import secondlife.network.vituz.providers.threads.ScoreboardThread;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.ConfigFile;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VituzScoreboard {

    @Getter
    private static Scoreboard board;

    private Player player;
    private Objective objective;
    private Objective healthName;
    private Map<String, Integer> displayedScores = new HashMap<>();
    private Set<String> sentTeamCreates = new HashSet<>();

    @Getter @Setter private static ScoreboardConfiguration configuration = null;

    private static Map<String, VituzScoreboard> boards = new ConcurrentHashMap<>();
    private static boolean initiated = false;

    public VituzScoreboard(Player player) {
        this.player = player;

        board = Bukkit.getScoreboardManager().getNewScoreboard();

        objective = board.registerNewObjective("SecondLife", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if(Vituz.getInstance().getConfig().getBoolean("HEALTH_BAR")) {
			(healthName = board.registerNewObjective("healthName", "health")).setDisplaySlot(DisplaySlot.BELOW_NAME);
			healthName.setDisplayName(ChatColor.DARK_RED.toString() + Msg.HEART);
		}

        player.setScoreboard(board);
    }

    public static void hook() {
        initiated = true;

        (new ScoreboardThread()).start();
    }

    public static void create(Player player) {
        if(configuration != null) {
            boards.put(player.getName(), new VituzScoreboard(player));
        }
    }

    public static void updateScoreboard(Player player) {
        if(boards.containsKey(player.getName())) {
            boards.get(player.getName()).update();
        }
    }

    public static void remove(Player player) {
        boards.remove(player.getName());
    }

    public void update() {
        String title = configuration.getTitleGetter().getTitle(player);
        String[] lines = configuration.getScoreGetter().getScores(player);
        
        Collection<String> recentlyUpdatedScores = new HashSet<>();
        Collection<String> usedBaseScores = new HashSet<>();
        
        int nextValue = 15;

        Preconditions.checkArgument(lines.length < 16, "Too many lines passed.");
        Preconditions.checkArgument(title.length() < 32, "Title is too long.");

        for(String line : lines) {
            Preconditions.checkArgument(line.length() < 48, "Line '" + line + "' is too long.");
        }

        PlayerData data = PlayerData.getByName(this.player.getName());

        title = title.replace("§5", data.getMainColor());
        title = title.replace("§d", data.getSecondColor());
        title = title.replace("&5", Color.translate(data.getMainColor()));
        title = title.replace("&d", Color.translate(data.getSecondColor()));

        if(!objective.getDisplayName().equals(title)) {
            objective.setDisplayName(title);
        }

        if(lines.length > 15) {
            return;
        }

        for(String line : lines) {
            String[] seperated = separate(line, usedBaseScores);
            String prefix = seperated[0];
            String score = seperated[1];
            String suffix = seperated[2];

            score = score.replace("§5", data.getMainColor());
            score = score.replace("§d", data.getSecondColor());
            score = score.replace("&5", Color.translate(data.getMainColor()));
            score = score.replace("&d", Color.translate(data.getSecondColor()));

            prefix = prefix.replace("§5", data.getMainColor());
            prefix = prefix.replace("§d", data.getSecondColor());
            prefix = prefix.replace("&5", Color.translate(data.getMainColor()));
            prefix = prefix.replace("&d", Color.translate(data.getSecondColor()));

            suffix = suffix.replace("§5", data.getMainColor());
            suffix = suffix.replace("§d", data.getSecondColor());
            suffix = suffix.replace("&5", Color.translate(data.getMainColor()));
            suffix = suffix.replace("&d", Color.translate(data.getSecondColor()));

            recentlyUpdatedScores.add(score);

            if(!sentTeamCreates.contains(score)) {
                createAndAddMember(score);
            }

            if(!displayedScores.containsKey(score) || displayedScores.get(score) != nextValue) {
                setScore(score, nextValue);
            }

            updateScore(score, prefix, suffix);
            nextValue--;
        }

        for(String displayedScore : ImmutableSet.copyOf(displayedScores.keySet())) {
            if(recentlyUpdatedScores.contains(displayedScore)) continue;

            removeScore(displayedScore);
        }
    }

    private void setField(Packet packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndAddMember(String scoreTitle) {
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(scoreTitle, "_", "_", new ArrayList<String>(), 0);
        ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(scoreTitle, Arrays.asList(scoreTitle), 3);

        scoreboardTeamAdd.sendToPlayer(player);
        scoreboardTeamAddMember.sendToPlayer(player);
        sentTeamCreates.add(scoreTitle);
    }

    private void setScore(String score, int value) {
        PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();

        setField(scoreboardScorePacket, "a", score);
        setField(scoreboardScorePacket, "b", objective.getName());
        setField(scoreboardScorePacket, "c", value);
        setField(scoreboardScorePacket, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);

        displayedScores.put(score, value);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(scoreboardScorePacket);
    }

    private void removeScore(String score) {
        displayedScores.remove(score);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(score));
    }

    private void updateScore(String score, String prefix, String suffix) {
        ScoreboardTeamPacketMod scoreboardTeamModify = new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2);
        scoreboardTeamModify.sendToPlayer(player);
    }

    private String[] separate(String line, Collection<String> usedBaseScores) {
        line = Color.translate(line);
        String prefix = "";
        String score = "";
        String suffix = "";

        List<String> working = new ArrayList<>();
        StringBuilder workingStr = new StringBuilder();

        for(char c : line.toCharArray()) {
            if(c == '*' || (workingStr.length() == 16 && working.size() < 3)) {
                working.add(workingStr.toString());
                workingStr = new StringBuilder();

                if(c == '*') continue;
            }

            workingStr.append(c);
        }

        working.add(workingStr.toString());

        switch(working.size()) {
            case 1:
                score = working.get(0);
                break;
            case 2:
                score = working.get(0);
                suffix = working.get(1);
                break;
            case 3:
                prefix = working.get(0);
                score = working.get(1);
                suffix = working.get(2);
                break;
            default:
                Bukkit.getLogger().warning("Failed to separate scoreboard line. Input: " + line);
                break;
        }

        if(usedBaseScores.contains(score)) {
            if(score.length() <= 14) {
                for(ChatColor chatColor : ChatColor.values()) {
                    String possibleScore = chatColor + score;

                    if(!usedBaseScores.contains(possibleScore)) {
                        score = possibleScore;
                        break;
                    }
                }

                if(usedBaseScores.contains(score)) {
                    Bukkit.getLogger().warning("Failed to find alternate color code for: " + score);
                }
            } else {
                Bukkit.getLogger().warning("Found a scoreboard base collision to shift: " + score);
            }
        }

        if(prefix.length() > 16) {
            prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        if(score.length() > 16) {
            score = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        if(suffix.length() > 16) {
            suffix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";
        }

        usedBaseScores.add(score);
        return (new String[]{prefix, score, suffix});
    }
}