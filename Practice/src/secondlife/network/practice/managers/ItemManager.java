package secondlife.network.practice.managers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import secondlife.network.vituz.utilties.ItemBuilder;

@Getter
public class ItemManager {

	private final ItemStack spawnItems[];
	private final ItemStack queueItems[];
	private final ItemStack partyItems[];
	private final ItemStack tournamentItems[];
	private final ItemStack specItems[];
	private final ItemStack partySpecItems[];
	private final ItemStack eventItems[];

	private final ItemStack defaultBook;

	public ItemManager() {
		this.spawnItems = new ItemStack[]{
				new ItemBuilder(Material.STONE_SWORD).name("&aJoin Unranked Queue").build(),
				new ItemBuilder(Material.IRON_SWORD).name("&eJoin Ranked Queue").build(),
				new ItemBuilder(Material.DIAMOND_SWORD).name("&6Join Premium Queue").build(),
				null,
				new ItemBuilder(Material.NAME_TAG).name("&eCreate Party").build(),
				null,
				new ItemBuilder(Material.EMERALD).name("&bView Leaderboards").build(),
				new ItemBuilder(Material.WATCH).name("&3Edit Settings").build(),
				new ItemBuilder(Material.BOOK).name("&6Edit Kits").build(),
		};
		this.queueItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				new ItemBuilder(Material.REDSTONE).name("&cLeave Queue").build(),
		};
		this.specItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				new ItemBuilder(Material.NETHER_STAR).name("&cLeave Spectator Mode").build(),
		};
		this.partySpecItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				new ItemBuilder(Material.NETHER_STAR).name("&cLeave Party").build(),
		};
		this.tournamentItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				new ItemBuilder(Material.NETHER_STAR).name("&cLeave Tournament").build(),
		};
		this.partyItems = new ItemStack[]{
				new ItemBuilder(Material.STONE_SWORD).name("&aJoin 2v2 Unranked Queue").build(),
				new ItemBuilder(Material.IRON_SWORD).name("&eJoin 2v2 Ranked Queue").build(),
				null,
				new ItemBuilder(Material.SKULL_ITEM).name("&bParty Information").build(),
				new ItemBuilder(Material.DIAMOND_AXE).name("&bStart Party Event").build(),
				new ItemBuilder(Material.IRON_AXE).name("&3Fight Other Party").build(),
				null,
				new ItemBuilder(Material.BOOK).name("&6Edit Kits").build(),
				new ItemBuilder(Material.NETHER_STAR).name("&cLeave Party").build(),
		};
		this.eventItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				new ItemBuilder(Material.NETHER_STAR).name("&cLeave Event").build(),
		};

		this.defaultBook = new ItemBuilder(Material.ENCHANTED_BOOK).name("&eDefault Kit").build();
	}
}
