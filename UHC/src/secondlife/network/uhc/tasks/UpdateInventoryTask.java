package secondlife.network.uhc.tasks;

import secondlife.network.uhc.managers.InventoryManager;

public class UpdateInventoryTask implements Runnable {

    @Override
    public void run() {
        InventoryManager.setupSettings();
        InventoryManager.setupPlayerSettings();
    }
}
