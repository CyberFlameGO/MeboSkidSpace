package secondlife.network.vituz.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.inventory.InventorySerialisation;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.io.IOException;
import java.util.*;

@Getter
public class CrateData {

    @Getter public static Set<CrateData> crates = new HashSet<>();

    private String name;
    private List<ItemStack> items;

    public CrateData(String name) {
        this.name = name;
        this.items = new ArrayList<>();

        crates.add(this);
    }

    public ItemStack getKey(int amount) {
        return new ItemBuilder(Material.TRIPWIRE_HOOK).name(ChatColor.BLUE + this.name + " Key").amount(amount).build();
    }

    public void save() {
        Document document = new Document();

        document.put("name", this.name);
        document.put("items", InventorySerialisation.itemStackArrayToJson(this.items.toArray(new ItemStack[0])));

        Vituz.getInstance().getDatabaseManager().getCrateData().replaceOne(Filters.eq("name", this.name), document, new UpdateOptions().upsert(true));
    }

    public static void load() {
        for (Object o : Vituz.getInstance().getDatabaseManager().getCrateData().find()) {
            Document document = (Document) o;
            String name = document.getString("name");
            List<ItemStack> items;

            try {
                items = new ArrayList<>(Arrays.asList(InventorySerialisation.itemStackArrayFromJson(document.getString("items"))));
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            CrateData crate = new CrateData(name);
            crate.getItems().addAll(items);
        }
    }

    public static CrateData getByName(String name) {
        for (CrateData crate : crates) {
            if (crate.getName().equalsIgnoreCase(name)) {
                return crate;
            }
        }
        return null;
    }

    public static CrateData getByKey(ItemStack itemStack) {
        for (CrateData crate : crates) {
            if (crate.getKey(1).isSimilar(itemStack)) {
                return crate;
            }
        }
        return null;
    }
}
