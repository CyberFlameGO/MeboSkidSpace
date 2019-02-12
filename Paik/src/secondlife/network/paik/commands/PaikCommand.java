package secondlife.network.paik.commands;

import secondlife.network.paik.Paik;

public class PaikCommand {

    public Paik main = Paik.getInstance();

    public PaikCommand() {
        main.getFramework().registerCommands(this);
    }

}
