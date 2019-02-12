package secondlife.network.meetuplobby.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.meetuplobby.MeetupLobby;

/**
 * Created by Marko on 10.06.2018.
 */
public class CheckTask extends BukkitRunnable {

    private MeetupLobby plugin = MeetupLobby.getInstance();

    public CheckTask() {
        runTaskTimer(plugin, 5L, 5L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(plugin.getQueueManager().getSoloQueue().contains(player.getUniqueId())) {
                if(plugin.getQueueManager().getSoloQueue().size() == 16) {
                    plugin.getQueueManager().getSoloQueue().remove(player.getUniqueId());


                }
            } else if(plugin.getQueueManager().getDuoQueue().contains(player.getUniqueId())) {
                if(plugin.getQueueManager().getDuoQueue().size() == 32) {
                    plugin.getQueueManager().getDuoQueue().remove(player.getUniqueId());

                }
            }
        });
    }

    public void sendToServer() {

    }
}
