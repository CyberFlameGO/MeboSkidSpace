package secondlife.network.vituz.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.ranks.grant.Grant;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.utilties.Color;

import java.util.*;

@Getter
@Setter
public class RankData {
	
	@Getter private static Map<String, RankData> profiles = new HashMap<>();

	private List<String> permissions;
	private List<Grant> grants;
	private Player player;
	private String name;
	private String realName;
	private PermissionAttachment attachment;

	private boolean loaded;

	public RankData(String name) {
		this.name = name;

		this.permissions = new ArrayList<>();
		this.grants = new ArrayList<>();

		profiles.put(this.name, this);
	}

	public Grant getActiveGrant() {
		Grant toReturn = null;
		
		for(Grant grant : grants) {
			if(grant.isActive() && !grant.getRank().getData().isDefaultRank()) toReturn = grant;
		}
		
		if(toReturn == null) {
			toReturn = new Grant(null, Rank.getDefaultRank(), System.currentTimeMillis(), 2147483647L, "Default RankCommand", true);
		}
		
		return toReturn;
	}

	public void load() {
		this.player = Bukkit.getPlayer(name);

		Document document = (Document) Vituz.getInstance().getDatabaseManager().getRanksProfiles().find(Filters.eq("name", this.name.toLowerCase())).first();

		if(document != null) {
			for(JsonElement element : new JsonParser().parse(document.getString("grants")).getAsJsonArray()) {
				JsonObject keyGrant = element.getAsJsonObject();
				String issuer = null;

				if(keyGrant.get("issuer") != null) {
					issuer = keyGrant.get("issuer").getAsString();
				}

				long dateAdded = keyGrant.get("dateAdded").getAsLong();
				long duration = keyGrant.get("duration").getAsLong();
				String reason = keyGrant.get("reason").getAsString();
				boolean active = keyGrant.get("active").getAsBoolean();

				Rank rank;

				try {
					rank = Rank.getByUuid(UUID.fromString(keyGrant.get("rank").getAsString()));
				} catch(Exception ex) {
					rank = Rank.getByName(keyGrant.get("rank").getAsString());

					if(rank == null) {
						throw new IllegalArgumentException("Invalid rank parameter");
					}
				}

				if(rank != null) {
					this.grants.add(new Grant(issuer, rank, dateAdded, duration, reason, active));
				}
			}

			this.realName = document.getString("realName");

			//if(document.containsKey("recentName")) {
			//	this.name = document.getString("recentName");
			//}

			List<String> permissionsList = new ArrayList<String>();

			for(String id : document.get("permissions").toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
				if(!id.isEmpty()) {
					permissionsList.add(id);
				}
			}

			this.permissions.addAll(permissionsList);
		}

		boolean hasDefaultRank = false;
		Iterator<Grant> iterator = this.grants.iterator();

		while(iterator.hasNext()) {
			if(iterator.next().getRank().getData().isDefaultRank()) {
				hasDefaultRank = true;
				break;
			}
		}

		if(!hasDefaultRank) {
			this.grants.add(new Grant(null, Rank.getDefaultRank(), System.currentTimeMillis(), 2147483647L, "Default RankCommand", true));
		}

		this.loaded = true;
	}

	public void setupAtatchment() {
		Player player = Bukkit.getPlayer(name);

		if(player != null) {
			attachment = player.addAttachment(Vituz.getInstance());

			Grant activeGrant = getActiveGrant();

			if (!player.getDisplayName().equals(Color.translate(activeGrant.getRank().getData().getPrefix() + player.getName() + activeGrant.getRank().getData().getSuffix()))) {
				player.setDisplayName(Color.translate(activeGrant.getRank().getData().getPrefix() + player.getName() + activeGrant.getRank().getData().getSuffix()));
			}

			for (String permission : attachment.getPermissions().keySet()) {
				attachment.unsetPermission(permission);
			}

			for (Grant grant : grants) {
				if (grant == null) {
					//System.out.println("Grant is null");
					continue;
				}

				if (!(grant.isExpired())) {
					// Rank permissions
					for (String permission : grant.getRank().getPermissions()) {
						attachment.setPermission(permission.replace("-", ""), !permission.startsWith("-"));
						//System.out.println("ADDED RANK PERMISSION " + permission);
					}

					// Permissions of all the ranks that main rank inherits
					for (UUID uuid : grant.getRank().getInheritance()) {
						Rank rank = Rank.getByUuid(uuid);
						if (rank != null) {
							for (String permission : rank.getPermissions()) {
								attachment.setPermission(permission.replace("-", ""), !permission.startsWith("-"));
								//System.out.println("ADDED INHERITANCE PERMISSION " + permission);
							}
						}
					}

				}
			}

			for (String permission : permissions) {
				attachment.setPermission(permission.replace("-", ""), !permission.startsWith("-"));
				//System.out.println("ADDED PLAYER PERMISSION " + permission);
			}

			player.recalculatePermissions();
		}
	}

	public void save() {
		if(!loaded) return;

		Document profileDocument = new Document();
		JsonArray grantsDocument = new JsonArray();

		profileDocument.put("name", this.name.toLowerCase());
		profileDocument.put("realName", name);

		//f(this.name != null) {
//			profileDocument.put("recentName", this.name);
//			profileDocument.put("recentNameLowercase", this.name.toLowerCase());
//		}
		
		for(Grant grant : this.grants) {
			JsonObject grantDocument = new JsonObject();
			
			if(grant.getRank() == null) continue;
			if(grant.getRank().getData().isDefaultRank()) continue;
			
			if(grant.getIssuer() != null) {
				grantDocument.addProperty("issuer", grant.getIssuer());
			}
			
			grantDocument.addProperty("dateAdded", grant.getDateAdded());
			grantDocument.addProperty("duration", grant.getDuration());
			grantDocument.addProperty("reason", grant.getReason());
			grantDocument.addProperty("active", grant.isActive() && !grant.isExpired());
			grantDocument.addProperty("rank", grant.getRank().getUuid().toString());
			grantDocument.addProperty("rankName", grant.getRank().getData().getName());
			grantsDocument.add(grantDocument);
		}
		
		profileDocument.put("grants", grantsDocument.toString());
		profileDocument.put("permissions", this.permissions);

		Vituz.getInstance().getDatabaseManager().getRanksProfiles().replaceOne(Filters.eq("name", this.name.toLowerCase()), profileDocument, new UpdateOptions().upsert(true));

		profiles.remove(this.name);

		loaded = false;
	}

	public static RankData getByName(String name) {
		RankData data = profiles.get(name);

		return data == null ? new RankData(name) : data;
	}
}
