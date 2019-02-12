package secondlife.network.practice.runnable;

import lombok.RequiredArgsConstructor;
import secondlife.network.practice.player.PracticeData;

@RequiredArgsConstructor
public class SaveDataRunnable implements Runnable {

	@Override
	public void run() {
		for(PracticeData data : PracticeData.getPlayerDatas().values()) {
			data.save();
		}
	}

}
