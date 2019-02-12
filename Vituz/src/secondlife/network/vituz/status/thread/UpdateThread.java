package secondlife.network.vituz.status.thread;

import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.status.jedis.JedisAction;
import secondlife.network.vituz.status.jedis.JedisSubscriber;

public class UpdateThread extends Thread {

    @Override
    public void run() {
        while (true) {
            Vituz.getInstance().getDatabaseManager().getPublisher().write(
                    JedisSubscriber.INDEPENDENT,
                    JedisAction.UPDATE,
                    Vituz.getInstance().getDatabaseManager().getVituzServer().getServerData());

            try {
                Thread.sleep(2500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
