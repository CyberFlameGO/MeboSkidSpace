package secondlife.network.info.thread;

import secondlife.network.info.InfoPlugin;
import secondlife.network.info.jedis.JedisAction;
import secondlife.network.info.jedis.JedisSubscriber;

public class UpdateThread extends Thread {

    @Override
    public void run() {
        while (true) {
            InfoPlugin.getInstance().getPublisher().write(
                    JedisSubscriber.INDEPENDENT,
                    JedisAction.UPDATE,
                    InfoPlugin.getInstance().getMeetupServer().getServerData());

            try {
                Thread.sleep(20 * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
