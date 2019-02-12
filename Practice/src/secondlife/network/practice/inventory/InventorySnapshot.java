package secondlife.network.practice.inventory;

import secondlife.network.practice.Practice;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.inventory.InventoryUI;
import secondlife.network.practice.utilties.MathUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import secondlife.network.vituz.utilties.ItemBuilder;
import secondlife.network.vituz.utilties.Msg;

import java.util.*;

@Getter
public class InventorySnapshot {

	private final InventoryUI inventoryUI;
	private final ItemStack[] originalInventory;
	private final ItemStack[] originalArmor;

	@Getter
	private final UUID snapshotId = UUID.randomUUID();

	public InventorySnapshot(Player player, Match match) {
		ItemStack[] contents = player.getInventory().getContents();
		ItemStack[] armor = player.getInventory().getArmorContents();

		this.originalInventory = contents;
		this.originalArmor = armor;

		PracticeData playerData = PracticeData.getByName(player.getName());

		double health = player.getHealth();
		double food = (double) player.getFoodLevel();

		List<String> potionEffectStrings = new ArrayList<>();

		for (PotionEffect potionEffect : player.getActivePotionEffects()) {
			String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
			String effectName = potionEffect.getType().getName().toLowerCase();
			String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());

			effectName = effectName.replace('_', ' ');
			effectName = effectName.substring(0, 1).toUpperCase() + effectName.substring(1);

			potionEffectStrings.add(CC.PRIMARY + effectName + " " + romanNumeral + CC.SECONDARY + " (" + duration + ")");
		}

		this.inventoryUI = new InventoryUI(player.getName(),6);

		for (int i = 0; i < 9; i++) {
			this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
			this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
			this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
			this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
		}

		boolean potionMatch = false;
		boolean soupMatch = false;

		for (ItemStack item : match.getKit().getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType() == Material.MUSHROOM_SOUP) {
				soupMatch = true;
				break;
			} else if (item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
				potionMatch = true;
				break;
			}
		}

		if (potionMatch) {
			int potCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();

			this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(new ItemBuilder(Material.POTION).name("&eHealth Potins: &d" + potCount).amount(potCount).durability(16421).lore("&eMissed Potions: &d" + playerData.getMissedPots()).build()));
		} else if (soupMatch) {
			int soupCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();

			this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(
					new ItemBuilder(Material.MUSHROOM_SOUP).name("&eRemaining Soups: &d" + soupCount).amount(soupCount).durability(16421).build()));
		}

		this.inventoryUI.setItem(48,
				new InventoryUI.EmptyClickableItem(
						new ItemBuilder(Material.SKULL_ITEM).name("&eHearts: &d" + MathUtil.roundToHalves(health / 2.0D) + "/ 10 " + Msg.HEART).amount((int) Math.round(health / 2.0D)).build()));

		this.inventoryUI.setItem(49,
				new InventoryUI.EmptyClickableItem(
						new ItemBuilder(Material.COOKED_BEEF).name("&eHunger: &d" + MathUtil.roundToHalves(food / 2.0D) + " / 10 " + Msg.HEART).amount((int) Math.round(food / 2.0D)).build()));

		this.inventoryUI.setItem(50,
				new InventoryUI.EmptyClickableItem(
						new ItemBuilder(Material.BREWING_STAND_ITEM).name("&ePotion Effects").lore(Arrays.asList(potionEffectStrings.toArray(new String[] {}))).amount(potionEffectStrings.size()).build()));

		this.inventoryUI.setItem(51, new InventoryUI.EmptyClickableItem(
				new ItemBuilder(Material.DIAMOND_SWORD).name("&eStatistics").
						lore("&eLongest Combo: &d" + playerData.getLongestCombo() + " Hit").
						lore("&eTotal Hits: &d" + playerData.getHits() + " Hit" + (playerData.getHits() > 1 ? "s" : "")).build()));

		if (!match.isParty()) {
			for (int i = 0; i < 2; i++) {
				this.inventoryUI.setItem(i == 0 ? 53 : 45, new InventoryUI.AbstractClickableItem(
						new ItemBuilder(Material.PAPER).name("&eView Other Inventory").lore("&eClick to view the other inventory").build()) {
					@Override
					public void onClick(InventoryClickEvent inventoryClickEvent) {
						Player clicker = (Player) inventoryClickEvent.getWhoClicked();

						if (Practice.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
							clicker.closeInventory();
							Practice.getInstance().getServer().dispatchCommand(clicker, "inv " + Practice.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId()));
						}
					}
				});
			}
		}

		for (int i = 36; i < 40; i++) {
			this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
		}
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		JSONObject inventoryObject = new JSONObject();
		for (int i = 0; i < this.originalInventory.length; i++) {
			inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
		}
		object.put("inventory", inventoryObject);

		JSONObject armourObject = new JSONObject();
		for (int i = 0; i < this.originalArmor.length; i++) {
			armourObject.put(i, this.encodeItem(this.originalArmor[i]));
		}
		object.put("armour", armourObject);

		return object;
	}

	private JSONObject encodeItem(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return null;
		}

		JSONObject object = new JSONObject();
		object.put("material", itemStack.getType().name());
		object.put("durability", itemStack.getDurability());
		object.put("amount", itemStack.getAmount());

		JSONObject enchants = new JSONObject();
		for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
		}
		object.put("enchants", enchants);

		return object;
	}

}
