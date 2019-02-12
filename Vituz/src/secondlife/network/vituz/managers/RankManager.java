package secondlife.network.vituz.managers;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.ranks.Rank;
import secondlife.network.vituz.ranks.RankData;
import secondlife.network.vituz.ranks.grant.Grant;
import secondlife.network.vituz.utilties.DateUtil;
import secondlife.network.vituz.utilties.Manager;
import secondlife.network.vituz.utilties.Tasks;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.text.SimpleDateFormat;
import java.util.*;

public class RankManager extends Manager {

    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    
    public RankManager(Vituz plugin) {
        super(plugin);

        load();
        give();
    }
    
    public Inventory getGrantsInventory(secondlife.network.vituz.data.RankData data, String name, int page) {
		int total = (int) Math.ceil(data.getGrants().size() / 9.0);
       
        if(total == 0) total = 1;
        
        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.RED + "Grants - " + page + "/" + total);
        
        inventory.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Previous Page").build());
        inventory.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Next Page").build());
        inventory.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Page " + page + "/" + total).lore(Collections.singletonList(ChatColor.YELLOW + "Player: " + ChatColor.RED + name)).build());

        ArrayList<Grant> toLoop = new ArrayList<>(data.getGrants());
        Collections.reverse(toLoop);

        toLoop.removeIf(grant -> grant.getRank().getData().isDefaultRank());
        
        for(Grant grant2 : toLoop) {
            if(toLoop.indexOf(grant2) >= page * 9 - 9 && toLoop.indexOf(grant2) < page * 9) {
                String end = "";
              
                if(grant2.getDuration() != 2147483647L) {
                    if(grant2.isExpired()) {
                        end = "Expired";
                    } else {
                        Calendar from = Calendar.getInstance();
                        Calendar to = Calendar.getInstance();
                       
                        from.setTime(new Date(System.currentTimeMillis()));
                        to.setTime(new Date(grant2.getDateAdded() + grant2.getDuration()));
                       
                        end = DateUtil.formatDateDiff(from, to);
                    }
                }
                
                String issuerName;
                
                if(grant2.getIssuer() == null) {
                    issuerName = "Console";
                } else {
                    issuerName = secondlife.network.vituz.data.RankData.getByName(grant2.getIssuer()).getName();
                }
                
                inventory.setItem(9 + toLoop.indexOf(grant2) % 9, new ItemBuilder(Material.WOOL).durability((grant2.isActive() && !grant2.isExpired()) ? 5 : 14).name(ChatColor.YELLOW + DATE_FORMAT.format(new Date(grant2.getDateAdded()))).lore(Arrays.asList("&7&m------------------------------", "&eBy: &c" + issuerName, "&eReason: &c" + grant2.getReason(), "&eRank: &c" + grant2.getRank().getData().getName(), "&7&m------------------------------", (grant2.getDuration() == 2147483647L) ? "&eThis is a permanent grant." : ("&eExpires in: &c" + end), "&7&m------------------------------")).build());
            }
        }
        
        return inventory;
    }

    public void load() {
        Block<Document> printDocumentBlock = document -> {
            RankData rankData = new RankData(document.getString("name"));

            rankData.setPrefix(document.getString("prefix"));
            rankData.setSuffix(document.getString("suffix"));
            rankData.setDefaultRank(document.getBoolean("default"));

            Object inheritance = document.get("inheritance");
            Object permissions = document.get("permissions");

            List<UUID> inheritanceList = new ArrayList<>();

            for (String id : inheritance.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
                if (!id.isEmpty()) {
                    inheritanceList.add(UUID.fromString(id));
                }
            }

            List<String> permissionsList = new ArrayList<>();

            for (String id2 : permissions.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
                if (!id2.isEmpty()) {
                    permissionsList.add(id2);
                }
            }

            new Rank(UUID.fromString(document.getString("uuid")), inheritanceList, permissionsList, rankData);
        };

        Vituz.getInstance().getDatabaseManager().getRanksGrants().find().forEach(printDocumentBlock);
    }
    
    public void save() {
        for(Rank rank : Rank.getRanks()) {
            Document document = new Document();
            document.put("uuid", rank.getUuid().toString());
            List<String> inheritance = new ArrayList<>();
           
            for(UUID uuid : rank.getInheritance()) {
                inheritance.add(uuid.toString());
            }
            
            document.put("inheritance", inheritance);
            document.put("permissions", rank.getPermissions());
            document.put("name", rank.getData().getName());
            document.put("prefix", rank.getData().getPrefix());
            document.put("suffix", rank.getData().getSuffix());
            document.put("default", rank.getData().isDefaultRank());

            Vituz.getInstance().getDatabaseManager().getRanksGrants().replaceOne(Filters.eq("uuid", rank.getUuid().toString()), document, new UpdateOptions().upsert(true));
        }
    }

    private void give() {
        Tasks.runTimer(() -> {
            for(secondlife.network.vituz.data.RankData data : secondlife.network.vituz.data.RankData.getProfiles().values()) {
                for(Grant grant : data.getGrants()) {
                    if(grant.isExpired() && grant.isActive()) {
                        grant.setActive(false);
                        data.setupAtatchment();

                        Player player = Bukkit.getPlayer(data.getName());

                        if(player == null) continue;

                        player.sendMessage(ChatColor.GREEN + "Your rank has been set to " + data.getActiveGrant().getRank().getData().getColorPrefix() + data.getActiveGrant().getRank().getData().getName() + ChatColor.GREEN + ".");
                    }
                }
            }
        }, 20L, 20L);
    }
}
