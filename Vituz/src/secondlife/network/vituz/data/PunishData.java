package secondlife.network.vituz.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.punishments.Punishment;
import secondlife.network.vituz.punishments.PunishmentType;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
public class PunishData {
	
	@Getter private static Map<String, PunishData> profiles = new HashMap<>();
	@Getter private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

	private List<String> alts;
	private List<String> addresses;
	private String address;
	private String name;
	private String realName;
	private List<Punishment> punishments;

	private boolean loaded;

	public PunishData(String name) {
		this.name = name;
		this.punishments = new ArrayList<>();
		this.alts = new ArrayList<>();
		this.addresses = new ArrayList<>();

		profiles.put(name, this);
	}

	public boolean isMuted() {
		for(Punishment punishment : this.punishments) {
			if(punishment.getType() == PunishmentType.MUTE && punishment.isActive()) {
				return true;
			}
		}

		return false;
	}

	public boolean isBanned() {
		for(Punishment punishment : this.punishments) {
			if((punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.TEMPBAN) && punishment.isActive()) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isBlacklisted() {
		for(Punishment punishment : this.punishments) {
			if(punishment.getType() == PunishmentType.BLACKLIST && punishment.isActive()) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isIPBanned() {
		for(Punishment punishment : this.punishments) {
			if(punishment.getType() == PunishmentType.IPBAN && punishment.isActive()) {
				return true;
			}
		}

		return false;
	}

	public Punishment getBannedPunishment() {
		for(Punishment punishment : this.punishments) {
			if((punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.TEMPBAN) && punishment.isActive()) {
				return punishment;
			}
		}
		
		return null;
	}

	public Punishment getMutedPunishment() {
		for(Punishment punishment : this.punishments) {
			if(punishment.getType().equals(PunishmentType.MUTE) && punishment.isActive()) {
				return punishment;
			}
		}
		
		return null;
	}

	public List<Punishment> getPunishmentsByType(PunishmentType type) {
		List<Punishment> toReturn = new ArrayList<>();

		for (Punishment punishment : punishments) {
			if (punishment.getType().name().contains(type.name())) {
				toReturn.add(punishment);
			}
		}

		Map<Punishment, Long> toCompare = new HashMap<>();
		for (Punishment punishment : toReturn) {
			toCompare.put(punishment, punishment.getAddedAt());
		}

		toReturn.sort((punishment, comparison) -> toCompare.get(comparison).compareTo(toCompare.get(punishment)));

		return toReturn;
	}

	public Inventory getPunishmentsInventory(PunishmentType type, int page) {
		int total = (int) Math.ceil(this.getPunishmentsByType(type).size() / 9.0);
		
		Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.RED + WordUtils.capitalize(type.name().toLowerCase()) + "s - " + page + "/" + ((total == 0) ? 1 : total));
		inventory.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name("&cPrevious Page").build());
		inventory.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name("&cNext Page").build());
		inventory.setItem(4, new ItemBuilder(Material.PAPER).name("&cPage " + page + "/" + ((total == 0) ? 1 : total)).lore(Arrays.asList("&ePlayer: &c" + ((this.name != null) ? this.name : "Anonymous"))).build());
		
		for(Punishment punishment : this.getPunishmentsByType(type)) {
			if(this.getPunishmentsByType(type).indexOf(punishment) >= page * 9 - 9 && this.getPunishmentsByType(type).indexOf(punishment) < page * 9) {
				ItemStack item = new ItemBuilder(Material.WOOL).durability(punishment.isActive() ? 5 : 14).name(ChatColor.YELLOW + PunishData.DATE_FORMAT.format(new Date(punishment.getAddedAt()))).build();
				List<String> lore = new ArrayList<>();
				String issuer = "Console";
				
				if(punishment.getAddedBy() != null) {
					PunishData profile = getByName(punishment.getAddedBy());
					
					if(profile.getName() != null) {
						issuer = profile.getName();
					} else {
						issuer = "Anonymous";
					}
				}
				
				String remover = "Console";
				
				if(punishment.getRemovedBy() != null) {
					PunishData profile2 = getByName(punishment.getRemovedBy());
					
					if(profile2.getName() != null) {
						remover = profile2.getName();
					} else {
						remover = "Anonymous";
					}
				}
				
				lore.addAll(Arrays.asList("&7&m------------------------------",
						"&eBy: &c" + issuer,
						"&eReason: &c" + punishment.getReason(),
				        "&eServer: &c" + punishment.getServerName()));
				
				if(punishment.isActive() && punishment.getType() != PunishmentType.BLACKLIST) {
					lore.addAll(Arrays.asList("&eDuration: &c" + punishment.getTimeLeft(),
							"&7&m------------------------------"));
				} else {
					lore.add("&7&m------------------------------");
				}
				
				if(!punishment.isActive() && punishment.getRemovedReason() == null) {
					lore.addAll(Arrays.asList(
							"&eExpired at: &c" + PunishData.DATE_FORMAT.format(new Date(punishment.getAddedAt() + punishment.getDuration())),
							"&7&m------------------------------"));
				} else if (punishment.getRemovedReason() != null) {
					lore.addAll(Arrays.asList("&eRemoved by: &c" + remover,
							"&eRemoved at: &c" + PunishData.DATE_FORMAT.format(new Date(punishment.getRemovedAt())),
							"&eRemoved reason: &c" + punishment.getRemovedReason(),
							"&7&m------------------------------"));
				}
				
				item = new ItemBuilder(item).lore(lore).build();
				
				inventory.setItem(9 + this.getPunishmentsByType(type).indexOf(punishment) % 9, item);
			}
		}
		
		return inventory;
	}

	public void load() {
		Document document = (Document) Vituz.getInstance().getDatabaseManager().getPunishProfiles().find(Filters.eq("name", this.name.toLowerCase())).first();

		if(document != null) {
			for (JsonElement element : new JsonParser().parse(document.getString("punishments")).getAsJsonArray()) {
				JsonObject punishmentDocument = element.getAsJsonObject();

				String addedBy = null;
				if (punishmentDocument.get("addedBy") != null) {
					addedBy = punishmentDocument.get("addedBy").getAsString();
				}

				String removedReason = null;
				if (punishmentDocument.get("removedReason") != null) {
					removedReason = punishmentDocument.get("removedReason").getAsString();
				}

				Punishment punishment = new Punishment(
						PunishmentType.valueOf(punishmentDocument.get("type").getAsString()),
						addedBy,
						punishmentDocument.get("addedAt").getAsLong(),
						punishmentDocument.get("duration").getAsLong(),
						punishmentDocument.get("reason").getAsString(),
						punishmentDocument.get("serverName").getAsString());

				if(removedReason != null) {
					String removedBy = null;

					if(punishmentDocument.get("removedBy") != null) {
						removedBy = punishmentDocument.get("removedBy").getAsString();
					}

					punishment.setRemovedBy(removedBy);
					punishment.setRemovedAt(punishmentDocument.get("removedAt").getAsLong());
					punishment.setRemovedReason(removedReason);
				}

				this.punishments.add(punishment);
			}

			this.realName = document.getString("realName");

			if(document.containsKey("ip")) {
				this.address = document.getString("ip");
			}

			if(document.containsKey("ips")) {
				List<String> addressList = new ArrayList<String>();

				for(String string : document.get("ips").toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
					if(!string.isEmpty()) {
						addressList.add(string);
					}
				}

				this.addresses.addAll(addressList);
			}

			for (String ip : addresses) {
				Vituz.getInstance().getDatabaseManager().getPunishProfiles().find(Filters.eq("ip", ip)).forEach(new Block() {
					@Override
					public void apply(Object obj) {
						Document doc = (Document) obj;
						alts.add(doc.getString("name"));
					}
				});
			}
		}

		this.loaded = true;
	}

	public void save() {
		if(!loaded) return;

		Document document = new Document();
		JsonArray punishmentsDocument = new JsonArray();
		
		for(Punishment punishment : this.punishments) {
			JsonObject punishmentDocument = new JsonObject();
			
			punishmentDocument.addProperty("type", punishment.getType().name());
			
			if(punishment.getAddedBy() != null) {
				punishmentDocument.addProperty("addedBy", punishment.getAddedBy());
			}
			
			punishmentDocument.addProperty("addedAt", punishment.getAddedAt());
			punishmentDocument.addProperty("duration", punishment.getDuration());
			punishmentDocument.addProperty("reason", punishment.getReason());
			punishmentDocument.addProperty("serverName", punishment.getServerName());
			
			if(punishment.getRemovedReason() != null) {
				if(punishment.getRemovedBy() != null) {
					punishmentDocument.addProperty("removedBy", punishment.getRemovedBy());
				}
				
				punishmentDocument.addProperty("removedAt", punishment.getRemovedAt());
				punishmentDocument.addProperty("removedReason", punishment.getRemovedReason());
			}
			
			punishmentsDocument.add(punishmentDocument);
		}

		document.put("name", this.name.toLowerCase());
		document.put("realName", this.name);

		if(this.address != null) {
			document.put("ip", this.address);
		}
		
		if(!this.addresses.isEmpty()) {
			document.put("ips", this.addresses);
		}
		
		document.put("punishments", punishmentsDocument.toString());
		Vituz.getInstance().getDatabaseManager().getPunishProfiles().replaceOne(Filters.eq("name", this.name.toLowerCase()), document, new UpdateOptions().upsert(true));

		profiles.remove(this.name);

		loaded = false;
	}

	public static PunishData getByName(String name) {
		PunishData data = profiles.get(name);

		return data == null ? new PunishData(name) : data;
	}
}