package secondlife.network.hub.utilties.profile;

import secondlife.network.hub.Hub;
import secondlife.network.vituz.utilties.Msg;

public class RemoveThread extends Thread {

    @Override
    public void run() {
        Msg.logConsole("&cStarted with cleaning duplicated datas!");

        BukkitProfileUtils.getPlayers().forEach(player -> {
            if (!Hub.getInstance().getStorage().isPlayerReal(player)) {
                Msg.sendMessage("&cRemoved data &l" + player.getName() + "&c.");
                BukkitProfileUtils.getByPlayerFile(player).delete();
            }
        });

        Msg.logConsole("&aFinished with cleaning duplicated datas!");
    }
}
