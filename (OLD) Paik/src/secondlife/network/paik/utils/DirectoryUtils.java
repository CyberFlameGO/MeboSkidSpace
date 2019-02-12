package secondlife.network.paik.utils;

import java.io.File;

import secondlife.network.paik.Paik;

public class DirectoryUtils {

	public static void registerDirectory() {
		if(!Paik.getInstance().getDataFolder().exists()) {
			Paik.getInstance().getDataFolder().mkdir();
		}
		
		File logs = new File(Paik.getInstance().getDataFolder(), "logs");
	    if(!logs.exists()) {
	    	logs.mkdir();
	    }
	}
}
