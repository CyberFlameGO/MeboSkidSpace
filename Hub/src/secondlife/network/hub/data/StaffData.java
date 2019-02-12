package secondlife.network.hub.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marko on 28.03.2018.
 */

@Getter
@Setter
public class StaffData {

	@Getter
	private static Map<String, StaffData> stafDatas = new HashMap<>();
	
	private String name;
	private String group;
	
	private String password;
	private String lastLogin;
	private String currentIp;

	private boolean loaded;

	public StaffData(String name) {
		this.name = name;
		this.group = "";
		this.password = "";
		this.lastLogin = "";
		this.currentIp = "";

		stafDatas.put(this.name, this);
	}

	public void save() {
		if(!loaded) return;

		Document document = new Document();
		document.put("name", name.toLowerCase());

		Player player = Bukkit.getPlayer(this.name);

		if(player != null) {
			document.put("rank_name", VituzAPI.getRankName(player.getName()));
		}

		document.put("password", this.password);
		document.put("last_login", this.lastLogin);
		document.put("current_ip", this.currentIp);

		Vituz.getInstance().getDatabaseManager().getSecurityProfiles().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));

		stafDatas.remove(this.name);
	}

	public void load() {
		Document document = (Document) Vituz.getInstance().getDatabaseManager().getSecurityProfiles().find(Filters.eq("name", name.toLowerCase())).first();

		if (document != null) {
			if(document.containsKey("rank_name")) {
				this.group = document.getString("rank_name");
			}

			this.password = document.getString("password");
			this.lastLogin = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss").format(new Date());
			this.currentIp = Bukkit.getPlayer(this.name).getAddress().getHostName();
		}

		this.loaded = true;
	}

	public static StaffData getByName(String name) {
		StaffData data = stafDatas.get(name);

		return data == null ? new StaffData(name) : data;
	}
}
