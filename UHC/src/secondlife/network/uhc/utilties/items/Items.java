package secondlife.network.uhc.utilties.items;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.vituz.utilties.Color;

import java.util.ArrayList;
import java.util.List;

public class Items {
	
	@SuppressWarnings("deprecation")
	public static ItemStack getBack() {
		ItemStack stack = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&c&lBack"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to go back."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getUHCPractice() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lPractice"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to open"));
		lore.add(Color.translate("&euhcpractice inventory"));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getStartInventory() {
		ItemStack stack = new ItemStack(Material.COOKED_BEEF);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lStarter Inventory"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eClick this item to set"));
		lore.add(Color.translate("&estarter inventory when uhc starts."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getEnablePractice() {
		ItemStack stack = new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData());
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lToggle UHCPractice ON"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to enable UHCPracttice."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getRates() {
		ItemStack stack = new ItemStack(Material.APPLE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lRates"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eClick this item to edit rates."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getDisablePractice() {
		ItemStack stack = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lToggle UHCPractice OFF"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to enable UHCPracttice."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getCurrentScenarios() {
		ItemStack stack = new ItemStack(Material.CHEST);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lCurrent Scenarios"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to open"));
		lore.add(Color.translate("&ecurrent scenarios inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getToggleScenarios() {
		ItemStack stack = new ItemStack(Material.CHEST);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lToggle Scenarios"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to open"));
		lore.add(Color.translate("&etoggle scenarios inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getDisableAllScenarios() {
		ItemStack stack = new ItemStack(Material.REDSTONE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lDisable All Scenarios"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to disable"));
		lore.add(Color.translate("&eall scenarios."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getParties() {
		ItemStack stack = new ItemStack(Material.PAPER);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lParties"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to open"));
		lore.add(Color.translate("&eParties inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getPartiesEnable() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lEnable Parties"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to enable Parties."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getScenarios() {
		ItemStack stack = new ItemStack(Material.CHEST);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lScenarios"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to open"));
		lore.add(Color.translate("&escenarios inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getOptions() {
		ItemStack stack = new ItemStack(Material.WATCH);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lOptions"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to open"));
		lore.add(Color.translate("&eoptions inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getMaxPlayers() {
		ItemStack stack = new ItemStack(Material.IRON_HELMET);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lMax Players"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click to open"));
		lore.add(Color.translate("&emax players inventory."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}

	public static ItemStack getConfigItem() {
		ItemStack stack = new ItemStack(Material.BOOK);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lEdit Config/Scenarios"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eRight click this item"));
		lore.add(Color.translate("&eto edit configs scenarios etc.."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getMaxOnlinePlus50() {
		ItemStack stack = new ItemStack(Material.DIAMOND);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lMax Players &f+ 50"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e50 slots to max players."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getMaxOnlinePlus10() {
		ItemStack stack = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lMax Players &f+ 10"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e10 slots to max players."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getMaxOnlinePlus5() {
		ItemStack stack = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lMax Players &f+ 5"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e5 slots to max players."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getCurrentMaxPlayers() {
		ItemStack stack = new ItemStack(Material.SKULL_ITEM);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lCurrent Max Players &f- " + Bukkit.getMaxPlayers()));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eHold this item to"));
		lore.add(Color.translate("&esee max players."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getMaxOnlineMinus50() {
		ItemStack stack = new ItemStack(Material.DIAMOND);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lMax Players &f- 50"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e50 slots from max players."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getMaxOnlineMinus10() {
		ItemStack stack = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lMax Players &f- 10"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e10 slots from max players."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getMaxOnlineMinus5() {
		ItemStack stack = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lMax Players &f- 5"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e5 slots from max players."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getAppleRateMinus1() {
		ItemStack stack = new ItemStack(Material.APPLE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lApple Rate &f- 1%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e1% from apple rate."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getAppleRateMinus2() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1, ((short) 0));
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lApple Rate &f- 2%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e2% from apple rate."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getAppleRatePlus1() {
		ItemStack stack = new ItemStack(Material.APPLE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lApple Rate &f+ 1%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e1% to apple rate."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getAppleRatePlus2() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1, ((short) 0));
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lApple Rate &f+ 2%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e2% to apple rate."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getShearsRateMinus1() {
		ItemStack stack = new ItemStack(Material.APPLE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lShears Rate &f- 1%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e1% from shears."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getShearsRateMinus2() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1, ((short) 0));
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lShears Rate &f- 2%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e2% from shears."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getShearsRatePlus1() {
		ItemStack stack = new ItemStack(Material.APPLE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lShears Rate &f+ 1%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e1% to shears."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getShearsRatePlus2() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1, ((short) 0));
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lShears Rate &f+ 2%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e2% to shears."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getPartiesOpenMoreInfo() {
		ItemStack stack = new ItemStack(Material.PAPER);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lOpen for more info"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to open"));
		lore.add(Color.translate("&efor more info."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	
	public static ItemStack getPartiesDisable() {
		ItemStack stack = new ItemStack(Material.GOLD_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lDisable Parties"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to disable Parties."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getIsPartiesEnabled() {
		ItemStack stack = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lTeam Size &e- &f" + UHCUtils.isPartiesEnabled()));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eHold this item to see"));
		lore.add(Color.translate("&ecurrent party size."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getIsUHCPracticeEnabled() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lPractice &e- &f" + UHCUtils.isUHCPracticeEnabled()));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eHold this item to see"));
		lore.add(Color.translate("&eis uhcpractice enabled."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getTeamAdd1() {
		ItemStack stack = new ItemStack(Material.DIAMOND);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lTeam Size &f+ 1"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to add"));
		lore.add(Color.translate("&e1 party to party size."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getTeamRemove1() {
		ItemStack stack = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lTeam Size &f- 1"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eLeft Click to remove"));
		lore.add(Color.translate("&e1 party from party size."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getAppleRate() {
		ItemStack stack = new ItemStack(Material.APPLE);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lApple Rate &f- " + UHC.getInstance().getGameManager().getAppleRate() + ".0%"));
		
		List<String> lore = new ArrayList<String>();
		lore.add(Color.translate("&eHold this item to see"));
		lore.add(Color.translate("&ecurrent apple rate."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getShears() {
		ItemStack stack = new ItemStack(Material.SHEARS);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(Color.translate("&5&lShears &f- Enabled"));
		
		List<String> lore = new ArrayList<>();
		lore.add(Color.translate("&eHold this item to see"));
		lore.add(Color.translate("&eis shears enabled."));
		lore.add("");
		lore.add(Color.translate("&eRate " + UHC.getInstance().getGameManager().getShearsRate() + ".0%"));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
}