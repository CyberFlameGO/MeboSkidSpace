package secondlife.network.overpass.managers;


import lombok.Getter;
import secondlife.network.overpass.Overpass;
import secondlife.network.overpass.utilties.Manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko on 22.07.2018.
 */

@Getter
public class OverpassManager extends Manager {

    private List<String> users = new ArrayList<>();

    public OverpassManager(Overpass plugin) {
        super(plugin);
    }

    public void handleAddToList(String name) {
        if(!users.contains(name)) {
            users.add(name);
        }
    }

    public void handleRemoveFromList(String name) {
        if(users.contains(name)) {
            users.remove(name);
        }
    }
}
