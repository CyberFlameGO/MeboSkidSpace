package secondlife.network.vituz.ranks;

import org.bukkit.ChatColor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankData {
	
	private String name;
	private String prefix;
	private String suffix;
	private boolean defaultRank;

	public RankData(String name, String prefix, String suffix, boolean defaultRank) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.defaultRank = defaultRank;
	}

	public RankData(String name) {
		this(name, "&d", "&d", false);
	}

	public String getColorPrefix() {
		if(this.prefix.isEmpty()) return "";
		
		char code = 'f';
		
		for(String string : this.prefix.split("&")) {
			if(!string.isEmpty() && ChatColor.getByChar(string.toCharArray()[0]) != null) code = string.toCharArray()[0];
		}
		
		ChatColor color = ChatColor.getByChar(code);
		
		return color.toString();
	}
}
