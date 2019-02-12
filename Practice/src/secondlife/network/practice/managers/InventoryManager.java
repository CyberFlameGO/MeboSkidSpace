package secondlife.network.practice.managers;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import secondlife.network.practice.Practice;
import secondlife.network.practice.arena.Arena;
import secondlife.network.practice.inventory.InventorySnapshot;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.kit.PlayerKit;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchTeam;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.queue.QueueType;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.inventory.InventoryUI;
import secondlife.network.practice.utilties.inventory.ItemUtil;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.utilties.ActionMessage;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.ItemBuilder;

import java.util.*;

public class InventoryManager {
	private static final String MORE_PLAYERS = CC.RED +
			"You need more 2 or more players in your party to start an event.";

	private final Practice plugin = Practice.getInstance();

	@Getter
	private final InventoryUI unrankedInventory = new InventoryUI(CC.PRIMARY + "Select an Unranked Kit", true,2);
	@Getter
	private final InventoryUI rankedInventory = new InventoryUI(CC.PRIMARY + "Select a Ranked Kit", true,2);
	@Getter
	private final InventoryUI editorInventory = new InventoryUI(CC.PRIMARY + "Select an Editable Kit", true,2);
	@Getter
	private final InventoryUI duelInventory = new InventoryUI(CC.PRIMARY + "Select a Duel Kit", true,2);
	@Getter
	private final InventoryUI partySplitInventory = new InventoryUI(CC.PRIMARY + "Select a Party Split Kit", true,2);
	@Getter
	private final InventoryUI partyFFAInventory = new InventoryUI(CC.PRIMARY + "Select a Party FFA Kit", true,2);
	@Getter
	private final InventoryUI redroverInventory = new InventoryUI(CC.PRIMARY + "Select a Redrover Kit", true,2);
	@Getter
	private final InventoryUI joinPremiumInventory = new InventoryUI(CC.PRIMARY + "Confirm Joining Premium", true,1);
	@Getter
	private final InventoryUI partyEventInventory = new InventoryUI(CC.PRIMARY + "Select an Event", true,1);
	@Getter
	private final InventoryUI partyInventory = new InventoryUI(CC.PRIMARY + "Duel a Party", true,6);

	private final Map<String, InventoryUI> duelMapInventories = new HashMap<>();
	private final Map<String, InventoryUI> partySplitMapInventories = new HashMap<>();
	private final Map<String, InventoryUI> partyFFAMapInventories = new HashMap<>();
	private final Map<String, InventoryUI> redroverMapInventories = new HashMap<>();
	//private final Map<String, InventoryUI> duelRoundInventories = new HashMap<>();

	private final Map<UUID, InventoryUI> editorInventories = new HashMap<>();
	private final Map<UUID, InventorySnapshot> snapshots = new HashMap<>();

	public InventoryManager() {
		this.setupInventories();
		this.plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateInventories, 20L, 20L);
	}

	private void setupInventories() {
		Collection<Kit> kits = this.plugin.getKitManager().getKits();

		for (Kit kit : kits) {
			if (kit.isEnabled()) {
				this.unrankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
					@Override
					public void onClick(InventoryClickEvent event) {
						Player player = (Player) event.getWhoClicked();
						InventoryManager.this.addToQueue(player,
								PracticeData.getByName(player.getName()),
								kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
								QueueType.UNRANKED);
					}
				});
				if (kit.isRanked()) {
					this.rankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
						@Override
						public void onClick(InventoryClickEvent event) {
							Player player = (Player) event.getWhoClicked();
							InventoryManager.this.addToQueue(player,
									PracticeData.getByName(player.getName()),
									kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
									QueueType.RANKED);
						}
					});
				}
				this.editorInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
					@Override
					public void onClick(InventoryClickEvent event) {
						Player player = (Player) event.getWhoClicked();

						InventoryManager.this.plugin.getEditorManager().addEditor(player, kit);
						PracticeData.getByName(player.getName())
								.setPlayerState(PlayerState.EDITING);
					}
				});
				this.duelInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleDuelClick((Player) event.getWhoClicked(), kit);
					}
				});
				this.partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handlePartySplitClick((Player) event.getWhoClicked(), kit);
					}
				});
				this.partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleFFAClick((Player) event.getWhoClicked(), kit);
					}
				});
				this.redroverInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleRedroverClick((Player) event.getWhoClicked(), kit);
					}
				});
			}
		}

		this.partyEventInventory.setItem(2, new InventoryUI.AbstractClickableItem(
				new ItemBuilder(Material.LEASH).name(CC.PRIMARY + "Party Split").build()) {
			@Override
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				player.closeInventory();
				player.openInventory(InventoryManager.this.getPartySplitInventory().getCurrentPage());
			}
		});
		this.partyEventInventory.setItem(4, new InventoryUI.AbstractClickableItem(
				new ItemBuilder(Material.BLAZE_ROD).name(CC.PRIMARY + "Party FFA").build()) {
			@Override
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				player.closeInventory();
				player.openInventory(InventoryManager.this.getPartyFFAInventory().getCurrentPage());
			}
		});
		this.partyEventInventory.setItem(6, new InventoryUI.AbstractClickableItem(
				new ItemBuilder(Material.REDSTONE).name(CC.PRIMARY + "Redrover").build()) {
			@Override
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				player.closeInventory();
				player.openInventory(InventoryManager.this.getRedroverInventory().getCurrentPage());
			}
		});

		for (int i = 0; i < 9; i++) {
			this.joinPremiumInventory.setItem(i, new InventoryUI.AbstractClickableItem(
					new ItemBuilder(Material.DIAMOND).name(CC.PRIMARY + "Confirm Joining Premium").build()) {
				@Override
				public void onClick(InventoryClickEvent event) {
					Player player = (Player) event.getWhoClicked();
					ItemStack item = event.getCurrentItem();
					if (item != null && item.getType() == Material.DIAMOND) {
						InventoryManager.this.plugin.getQueueManager().addPlayerToQueue(
								player, PracticeData.getByName(player.getName()),
								"NoDebuff", QueueType.PREMIUM);
					}
				}
			});
		}

		for (Kit kit : this.plugin.getKitManager().getKits()) {
			InventoryUI duelInventory = new InventoryUI(CC.PRIMARY + "Select a Duel Map", 3);
			InventoryUI partySplitInventory = new InventoryUI(CC.PRIMARY + "Select a Party Split Map", 3);
			InventoryUI partyFFAInventory = new InventoryUI(CC.PRIMARY + "Select a Party FFA Map", 3);
			InventoryUI redroverInventory = new InventoryUI(CC.PRIMARY + "Select a Redrover Map", 3);
			for (Arena arena : this.plugin.getArenaManager().getArenas().values()) {
				if (!arena.isEnabled()) {
					continue;
				}
				if (kit.getExcludedArenas().contains(arena.getName())) {
					continue;
				}
				if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
					continue;
				}
				ItemStack book = new ItemBuilder(Material.BOOK).name(CC.GREEN + arena.getName()).build();

				duelInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleDuelMapClick((Player) event.getWhoClicked(), arena, kit);
					}
				});
				partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handlePartySplitMapClick((Player) event.getWhoClicked(), arena, kit);
					}
				});
				partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handlePartyFFAMapClick((Player) event.getWhoClicked(), arena, kit);
					}
				});
				redroverInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleRedroverMapClick((Player) event.getWhoClicked(), arena, kit);
					}
				});
			}
			this.duelMapInventories.put(kit.getName(), duelInventory);
			this.partySplitMapInventories.put(kit.getName(), partySplitInventory);
			this.partyFFAMapInventories.put(kit.getName(), partyFFAInventory);
			this.redroverMapInventories.put(kit.getName(), redroverInventory);
		}
		/*
		InventoryUI duelRoundChoose = new InventoryUI(CC.PRIMARY + "Select an amount of rounds", 1);
		for (int i = 0; i < 5; i++) {
			// We check if the number is even.
			if (i % 2 == 0) {
				int rounds = i + 1;
				duelRoundChoose.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.BOOK, CC.GREEN + "Best of " + rounds + "!")) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleRoundChooseClick((Player) event.getWhoClicked(), kit, rounds);
					}
				});
			}
		}
		duelRoundInventories.put(kit.getName(), duelRoundChoose);
		*/
	}

	private void updateInventories() {
		for (int i = 0; i < 18; i++) {
			InventoryUI.ClickableItem unrankedItem = this.unrankedInventory.getItem(i);
			if (unrankedItem != null) {
				unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UNRANKED));
				this.unrankedInventory.setItem(i, unrankedItem);
			}

			InventoryUI.ClickableItem rankedItem = this.rankedInventory.getItem(i);
			if (rankedItem != null) {
				rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
				this.rankedInventory.setItem(i, rankedItem);
			}
		}
	}

	private ItemStack updateQueueLore(ItemStack itemStack, QueueType type) {
		if (itemStack == null) {
			return null;
		}
		String ladder;

		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
			ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
		} else {
			return null;
		}
		int queueSize = this.plugin.getQueueManager().getQueueSize(ladder, type);
		int inGameSize = this.plugin.getMatchManager().getFighters(ladder, type);

		return new ItemBuilder(itemStack).amount(inGameSize > 64 ? 64 : inGameSize).lore(Arrays.asList(
				CC.PRIMARY + "Playing: " + CC.SECONDARY + inGameSize,
				CC.PRIMARY + "Queued: " + CC.SECONDARY + queueSize)).build();
	}

	private void addToQueue(Player player, PracticeData playerData, Kit kit, Party party, QueueType queueType) {
		if (kit != null) {
			if (party == null) {
				this.plugin.getQueueManager().addPlayerToQueue(player, playerData, kit.getName(), queueType);
			} else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
				this.plugin.getQueueManager().addPartyToQueue(player, party, kit.getName(), queueType);
			}
		}
	}

	public void addSnapshot(InventorySnapshot snapshot) {
		this.snapshots.put(snapshot.getSnapshotId(), snapshot);

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
				InventoryManager.this.removeSnapshot(snapshot.getSnapshotId()), 20L * 30L);
	}

	public void removeSnapshot(UUID snapshotId) {
		InventorySnapshot snapshot = this.snapshots.get(snapshotId);
		if (snapshot != null) {
			//UIListener.INVENTORIES.remove(snapshot.getInventoryUI());
			this.snapshots.remove(snapshotId);
		}
	}

	public InventorySnapshot getSnapshot(UUID snapshotId) {
		return this.snapshots.get(snapshotId);
	}

	public void addParty(Player player) {
		ItemStack skull = new ItemBuilder(Material.SKULL_ITEM).name(CC.PRIMARY + player.getName() + " (&d1&e)").build();

		this.partyInventory.addItem(new InventoryUI.AbstractClickableItem(skull) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.closeInventory();

				if (inventoryClickEvent.getWhoClicked() instanceof Player) {
					Player sender = (Player) inventoryClickEvent.getWhoClicked();
					sender.performCommand("duel " + player.getName());
				}
			}
		});
	}

	public void updateParty(Party party) {
		Player player = this.plugin.getServer().getPlayer(party.getLeader());

		for (int i = 0; i < this.partyInventory.getSize(); i++) {
			InventoryUI.ClickableItem item = this.partyInventory.getItem(i);

			if (item != null) {
				ItemStack stack = item.getItemStack();

				if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
					List<String> lores = new ArrayList<>();

					party.members().forEach(member -> lores.add(CC.PRIMARY + member.getName()));

					ItemUtil.reloreItem(stack, lores.toArray(new String[0]));
					ItemUtil.renameItem(stack,
							CC.PRIMARY + player.getName() + " (" + CC.SECONDARY + party.getMembers().size() + CC.PRIMARY + ")");
					item.setItemStack(stack);
					break;
				}
			}
		}
	}

	public void removeParty(Party party) {
		Player player = this.plugin.getServer().getPlayer(party.getLeader());

		for (int i = 0; i < this.partyInventory.getSize(); i++) {
			InventoryUI.ClickableItem item = this.partyInventory.getItem(i);

			if (item != null) {
				ItemStack stack = item.getItemStack();

				if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
					this.partyInventory.removeItem(i);
					break;
				}
			}
		}
	}

	public void addEditingKitInventory(Player player, Kit kit) {
		PracticeData playerData = PracticeData.getByName(player.getName());

		Map<Integer, PlayerKit> kitMap = playerData.getPlayerKits(kit.getName());

		InventoryUI inventory = new InventoryUI(CC.PRIMARY + "Editing Kit Layout", 4);

		for (int i = 1; i <= 7; i++) {
			ItemStack save = new ItemBuilder(Material.CHEST).name("&eSave Kit &d" + kit.getName() + " #" + i).build();

			ItemStack load = new ItemBuilder(Material.BOOK).name("&eLoad Kit &d" + kit.getName() + " #" + i).build();

			ItemStack rename = new ItemBuilder(Material.NAME_TAG).name("&eRename Kit &d" + kit.getName() + " #" + i).build();

			ItemStack delete = new ItemBuilder(Material.FLINT).name("&eDelete Kit &d" + kit.getName() + " #" + i).build();

			inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
				@Override
				public void onClick(InventoryClickEvent event) {
					int kitIndex = event.getSlot();
					InventoryManager.this.handleSavingKit(player, playerData, kit, kitMap, kitIndex);
					inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
						@Override
						public void onClick(InventoryClickEvent event) {
							InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
						}
					});
					inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
						@Override
						public void onClick(InventoryClickEvent event) {
							InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
						}
					});
					inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
						@Override
						public void onClick(InventoryClickEvent event) {
							InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
						}
					});
				}
			});

			final int kitIndex = i;

			if (kitMap != null && kitMap.containsKey(kitIndex)) {
				inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
					}
				});
				inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
					}
				});
				inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
					}
				});
			}
		}

		this.editorInventories.put(player.getUniqueId(), inventory);
	}

	public void removeEditingKitInventory(UUID uuid) {
		InventoryUI inventoryUI = this.editorInventories.get(uuid);
		if (inventoryUI != null) {
			this.editorInventories.remove(uuid);
		}
	}

	public InventoryUI getEditingKitInventory(UUID uuid) {
		return this.editorInventories.get(uuid);
	}

	private void handleSavingKit(Player player, PracticeData playerData, Kit kit, Map<Integer, PlayerKit> kitMap, int kitIndex) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			kitMap.get(kitIndex).setContents(player.getInventory().getContents().clone());
			player.sendMessage(
					CC.PRIMARY + "Successfully saved kit " + CC.SECONDARY + kitIndex + CC.PRIMARY + ".");
			return;
		}

		PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, player.getInventory().getContents().clone(),
				kit.getName() + " Kit " + kitIndex);
		playerData.addPlayerKit(kitIndex, playerKit);

		player.sendMessage(CC.PRIMARY + "Successfully saved kit " + CC.SECONDARY + kitIndex + CC.PRIMARY + ".");
	}

	private void handleLoadKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			ItemStack[] contents = kitMap.get(kitIndex).getContents();
			for (ItemStack itemStack : contents) {
				if (itemStack != null) {
					if (itemStack.getAmount() <= 0) {
						itemStack.setAmount(1);
					}
				}
			}
			player.getInventory().setContents(contents);
			player.updateInventory();
		}
	}

	private void handleRenamingKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));

			player.closeInventory();
			player.sendMessage(CC.PRIMARY + "Enter a name for this kit (chat colors are also applicable).");
		}
	}

	private void handleDeleteKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap, InventoryUI inventory) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());

			kitMap.remove(kitIndex);

			player.sendMessage(
					CC.PRIMARY + "Successfully removed kit " + CC.SECONDARY + kitIndex + CC.PRIMARY + ".");

			inventory.setItem(kitIndex + 1, 2, null);
			inventory.setItem(kitIndex + 1, 3, null);
			inventory.setItem(kitIndex + 1, 4, null);
		}
	}

	private void handleDuelClick(Player player, Kit kit) {
		PracticeData playerData = PracticeData.getByName(player.getName());

		Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());

		if (selected == null) {
			player.sendMessage(Color.translate("&cPlayer not found!"));
			return;
		}

		PracticeData targetData = PracticeData.getByName(selected.getName());
		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "Player isn't in spawn.");
			return;
		}

		Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		boolean partyDuel = party != null;

		if (partyDuel) {
			if (targetParty == null) {
				player.sendMessage(CC.RED + "That player isn't in a party.");
				return;
			}
		}


		if (secondlife.network.vituz.handlers.data.PlayerData.getByName(player.getName()) != null && VituzAPI.getRankName(player.getName()).equals("Xenon")) {
			player.closeInventory();
			player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
			return;
		}

		if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) !=
				null) {
			player.sendMessage(CC.RED + "You already sent a match request to that player. Please wait until it expires.");
			return;
		}

		Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
		if (arena == null) {
			player.sendMessage(CC.RED + "No available arenas found.");
			return;
		}

		this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
	}

	private void handlePartySplitClick(Player player, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}
		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {
			if (secondlife.network.vituz.handlers.data.PlayerData.getByName(player.getName()) != null && VituzAPI.getRankName(player.getName()).equals("Xenon")) {
				player.closeInventory();
				player.openInventory(this.partySplitMapInventories.get(kit.getName()).getCurrentPage());
				return;
			}

			Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
			if (arena == null) {
				player.sendMessage(CC.RED + "No available arenas found.");
				return;
			}

			if(kit.getName().equals("BedWars")) {
				player.sendMessage(CC.RED + "Sorry but you can't use this kit in split mode!");
				return;
			}

			this.createPartySplitMatch(party, arena, kit);
		}
	}

	private void handleFFAClick(Player player, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}
		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {
			if (secondlife.network.vituz.handlers.data.PlayerData.getByName(player.getName()) != null && VituzAPI.getRankName(player.getName()).equals("Xenon")) {
				player.closeInventory();
				player.openInventory(this.partyFFAMapInventories.get(kit.getName()).getCurrentPage());
				return;
			}

			Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
			if (arena == null) {
				player.sendMessage(CC.RED + "No available arenas found.");
				return;
			}

			if(kit.getName().equals("Parkour")) {
				player.sendMessage(CC.RED + "Sorry but you can't use this kit in FFA mode!");
				return;
			}

			if(kit.getName().equals("BedWars")) {
				player.sendMessage(CC.RED + "Sorry but you can't use this kit in FFA mode!");
				return;
			}

			this.createFFAMatch(party, arena, kit);
		}
	}

	private void handleRedroverClick(Player player, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}
		player.closeInventory();
		if (party.getMembers().size() < 4) {
			player.sendMessage(CC.RED + "You need more 4 or more players in your party to start an event.");
		} else {
			if (secondlife.network.vituz.handlers.data.PlayerData.getByName(player.getName()) != null && VituzAPI.getRankName(player.getName()).equals("Xenon")) {
				player.closeInventory();
				player.openInventory(this.redroverMapInventories.get(kit.getName()).getCurrentPage());
				return;
			}

			Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
			if (arena == null) {
				player.sendMessage(CC.RED + "No available arenas found.");
				return;
			}

			this.createRedroverMatch(party, arena, kit);
		}
	}

	private void handleDuelMapClick(Player player, Arena arena, Kit kit) {
		PracticeData playerData = PracticeData.getByName(player.getName());
		Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
		if (selected == null) {
			player.sendMessage(Color.translate("&cPlayer not found!"));
			return;
		}

		PracticeData targetData = PracticeData.getByName(selected.getName());
		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(CC.RED + "Player isn't in spawn.");
			return;
		}

		Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		boolean partyDuel = party != null;
		if (partyDuel && targetParty == null) {
			player.sendMessage(CC.RED + "That player isn't in a party.");
			return;
		}
		if (InventoryManager.this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
			player.sendMessage(
					CC.RED + "You already sent a match request to that player. Please wait until it expires.");
			return;
		}

		this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
	}

	private void handleRedroverMapClick(Player player, Arena arena, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}
		player.closeInventory();
		if (party.getMembers().size() < 4) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {
			this.createRedroverMatch(party, arena, kit);
		}
	}

	private void handlePartyFFAMapClick(Player player, Arena arena, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}

		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {
			this.createFFAMatch(party, arena, kit);
		}
	}

	private void handlePartySplitMapClick(Player player, Arena arena, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}

		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {
			this.createPartySplitMatch(party, arena, kit);
		}
	}

	private void sendDuel(Player player, Player selected, Kit kit, boolean partyDuel, Party party, Party targetParty, Arena arena) {
		this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel);

		player.closeInventory();

		ActionMessage requestMessage = new ActionMessage();


		requestMessage.addText(CC.SECONDARY + player.getName() + CC.PRIMARY + " has sent you a " + (partyDuel ? "party" : "")
						+ "duel request" + (kit.getName() != null ? " with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY
						: "")
						+ (arena == null ? "" : " on arena " + CC.SECONDARY + arena.getName()) + CC.PRIMARY + ". "
						+ CC.GREEN + "[Accept]").addHoverText(CC.GREEN + "Click to accept").setClickEvent(ActionMessage.ClickableType.RunCommand, "/accept " + player.getName() + " " + kit.getName());

		if (partyDuel) {
			targetParty.members().forEach(requestMessage::sendToPlayer);

			party.broadcast(CC.PRIMARY + "Sent a party duel request to " + CC.SECONDARY + selected.getName()
					+ CC.PRIMARY + "'s party with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY
					+ (arena == null ? "" : CC.PRIMARY + " on arena " + arena.getName()) + ".");
		} else {
			requestMessage.sendToPlayer(selected);
			player.sendMessage(
					CC.PRIMARY + "Sent a duel request to " + CC.SECONDARY + selected.getName() + CC.PRIMARY
							+ " with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY
							+ (arena == null ? "" : CC.PRIMARY + " on arena " + arena.getName()) + ".");
		}
	}

	private void createPartySplitMatch(Party party, Arena arena, Kit kit) {
		MatchTeam[] teams = party.split();
		Match match = new Match(arena, kit, QueueType.UNRANKED, teams);
		Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
		Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());

		match.broadcast(CC.PRIMARY + "Starting a party split match with kit " + CC.SECONDARY + kit.getName()
				+ CC.PRIMARY + " and arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + " between "
				+ CC.SECONDARY + leaderA.getName() + CC.PRIMARY + "'s team and " + CC.SECONDARY + leaderB.getName()
				+ CC.PRIMARY + "'s team.");

		this.plugin.getMatchManager().createMatch(match);
	}

	private void createFFAMatch(Party party, Arena arena, Kit kit) {
		MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList(party.getMembers()), 0);
		Match match = new Match(arena, kit, QueueType.UNRANKED, team);

		match.broadcast(CC.PRIMARY + "Starting a party FFA match with kit " + CC.SECONDARY
				+ kit.getName() + CC.PRIMARY + " and arena " + CC.SECONDARY + arena.getName()
				+ CC.PRIMARY + ".");

		this.plugin.getMatchManager().createMatch(match);
	}

	private void createRedroverMatch(Party party, Arena arena, Kit kit) {
		MatchTeam[] teams = party.split();
		Match match = new Match(arena, kit, QueueType.UNRANKED, teams);
		Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
		Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());

		match.broadcast(CC.PRIMARY + "Starting a redrover match with kit " + CC.SECONDARY + kit.getName()
				+ CC.PRIMARY + " and arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + " between "
				+ CC.SECONDARY + leaderA.getName() + CC.PRIMARY + "'s team and " + CC.SECONDARY + leaderB.getName()
				+ CC.PRIMARY + "'s team.");

		this.plugin.getMatchManager().createMatch(match);
	}
}
