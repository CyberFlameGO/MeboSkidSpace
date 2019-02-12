package secondlife.network.meetuplobby.providers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.meetuplobby.MeetupLobby;
import secondlife.network.vituz.providers.ScoreProvider;
import secondlife.network.vituz.providers.scoreboard.ScoreboardConfiguration;
import secondlife.network.vituz.providers.scoreboard.TitleGetter;
import secondlife.network.vituz.status.ServerData;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardProvider implements ScoreProvider {

	private MeetupLobby plugin = MeetupLobby.getInstance();

	public static ScoreboardConfiguration create() {
		ScoreboardConfiguration sc = new ScoreboardConfiguration();

		sc.setTitleGetter(new TitleGetter("&5&lSecondLife"));
		sc.setScoreGetter(new ScoreboardProvider());

		return sc;
	}

	@Override
	public String[] getScores(Player player) {
		List<String> board = new ArrayList<>();

		board.add("&7&m----------------------");
		board.add("&fOnline: &d" + Bukkit.getOnlinePlayers().size());
		board.add("&fGames and Lobby: &d" + getAll());
		board.add("&fMeetup Global: &d" + getInGame());
		board.add("");
		board.add("&dsecondlife.network");
		board.add("&1&7&m----------------------");

		return board.stream().map(Color::translate).toArray(String[]::new);
	}

	private int getInGame() {
		ServerData data1 = ServerData.getByName("UHCMeetup-1");
		ServerData data2 = ServerData.getByName("UHCMeetup-2");
		ServerData data3 = ServerData.getByName("UHCMeetup-3");
		ServerData data4 = ServerData.getByName("UHCMeetup-4");
		ServerData data5 = ServerData.getByName("UHCMeetup-5");
		ServerData data6 = ServerData.getByName("UHCMeetup-6");

		int count1 = data1 != null ? data1.getOnlinePlayers() : 0;
		int count2 = data2 != null ? data2.getOnlinePlayers() : 0;
		int count3 = data3 != null ? data3.getOnlinePlayers() : 0;
		int count4 = data4 != null ? data4.getOnlinePlayers() : 0;
		int count5 = data5 != null ? data5.getOnlinePlayers() : 0;
		int count6 = data6 != null ? data6.getOnlinePlayers() : 0;

		return count1 + count2 + count3 + count4 + count5 + count6;
	}

	private int getAll() {
		ServerData data1 = ServerData.getByName("UHCMeetup-1");
		ServerData data2 = ServerData.getByName("UHCMeetup-2");
		ServerData data3 = ServerData.getByName("UHCMeetup-3");
		ServerData data4 = ServerData.getByName("UHCMeetup-4");
		ServerData data5 = ServerData.getByName("UHCMeetup-5");
		ServerData data6 = ServerData.getByName("UHCMeetup-6");
		ServerData dataLobby = ServerData.getByName("UHCMeetup-Lobby");

		int count1 = data1 != null ? data1.getOnlinePlayers() : 0;
		int count2 = data2 != null ? data2.getOnlinePlayers() : 0;
		int count3 = data3 != null ? data3.getOnlinePlayers() : 0;
		int count4 = data4 != null ? data4.getOnlinePlayers() : 0;
		int count5 = data5 != null ? data5.getOnlinePlayers() : 0;
		int count6 = data6 != null ? data6.getOnlinePlayers() : 0;
		int count7 = dataLobby != null ? dataLobby.getOnlinePlayers() : 0;

		return count1 + count2 + count3 + count4 + count5 + count6 + count7;
	}
}
