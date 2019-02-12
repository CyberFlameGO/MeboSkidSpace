package secondlife.network.practice.handlers;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerShutdownEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.oitc.OITCPlayer;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.kit.PlayerKit;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.party.Party;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.utilties.PlayerUtil;
import secondlife.network.vituz.handlers.events.RebootStartEvent;
import secondlife.network.vituz.handlers.events.RebootStopEvent;
import secondlife.network.vituz.providers.nametags.VituzNametagHandler;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.PlayerUtils;

import java.util.Map;
import java.util.UUID;

public class PlayerHandler implements Listener {

	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onRebootStart(RebootStartEvent event) {
		plugin.getKitManager().setRankedReboot(true);
	}

	@EventHandler
	public void onRebootStop(RebootStopEvent event) {
		plugin.getKitManager().setRankedReboot(false);
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.GOLDEN_APPLE) {
			if (!event.getItem().hasItemMeta()
					|| !event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
				return;
			}

			PracticeData playerData = PracticeData.getByName(event.getPlayer().getName());

			if (playerData.getPlayerState() == PlayerState.FIGHTING) {
				Player player = event.getPlayer();
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
				player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
			}
		}
	}

	@EventHandler
	public void onServerShutdown(ServerShutdownEvent event) {
		for (Match match : plugin.getMatchManager().getMatches().values()) {
			match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
			match.getOriginalBlockChanges().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
			match.getEntitiesToRemove().forEach(Entity::remove);
		}
	}

	@EventHandler
	public void onRegenerate(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
			return;
		}

		Player player = (Player) event.getEntity();

		PracticeData playerData = PracticeData.getByName(player.getName());
		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
			if (match.getKit().isBuild()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		PracticeData.sendToSpawnAndReset(player);

		player.teleport(plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if(!PlayerUtils.isMongoConnected(event)) return;

		PracticeData data = PracticeData.getByName(event.getName());

		if (!data.isLoaded()) {
			data.load();
		}

		if (!data.isLoaded()) {
			PlayerUtils.kick(event);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		PracticeData playerData = PracticeData.getByName(player.getName());

		switch (playerData.getPlayerState()) {
			case FIGHTING:
				this.plugin.getMatchManager().removeFighter(player, playerData, false);
				break;
			case SPECTATING:
				this.plugin.getMatchManager().removeSpectator(player);
				break;
			case EDITING:
				this.plugin.getEditorManager().removeEditor(player.getUniqueId());
				break;
			case QUEUE:
				if (party == null) {
					this.plugin.getQueueManager().removePlayerFromQueue(player);
				} else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
					this.plugin.getQueueManager().removePartyFromQueue(party);
				}
				break;
			case FFA:
				//this.plugin.getFfaManager().removePlayer(player);
				break;
			case EVENT:
				PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
				if (practiceEvent != null) {
					practiceEvent.leave(player);
				}
				break;
		}

		this.plugin.getTournamentManager().leaveTournament(player);
		this.plugin.getPartyManager().leaveParty(player);

		this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
		this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());

		playerData.save();
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		PracticeData playerData = PracticeData.getByName(player.getName());

		if (playerData == null) {
			return;
		}

		switch (playerData.getPlayerState()) {
			case FIGHTING:
				this.plugin.getMatchManager().removeFighter(player, playerData, false);
				break;
			case SPECTATING:
				this.plugin.getMatchManager().removeSpectator(player);
				break;
			case EDITING:
				this.plugin.getEditorManager().removeEditor(player.getUniqueId());
				break;
			case QUEUE:
				if (party == null) {
					this.plugin.getQueueManager().removePlayerFromQueue(player);
				} else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
					this.plugin.getQueueManager().removePartyFromQueue(party);
				}
				break;
			case FFA:
				//this.plugin.getFfaManager().removePlayer(player);
				break;
			case EVENT:
				PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
				if (practiceEvent != null) {
					practiceEvent.leave(player);
				}
				break;
		}

		this.plugin.getTournamentManager().leaveTournament(player);
		this.plugin.getPartyManager().leaveParty(player);

		this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
		this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		PracticeData playerData = PracticeData.getByName(player.getName());

		if (playerData.getPlayerState() == PlayerState.SPECTATING) {
			event.setCancelled(true);
		}

		if (event.getAction().name().endsWith("_BLOCK")) {
			if (event.getClickedBlock().getType().name().contains("SIGN")
					&& event.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (ChatColor.stripColor(sign.getLine(1)).equals("[Soup]")) {
					event.setCancelled(true);

					Inventory inventory = this.plugin.getServer().createInventory(null, 54,
							ChatColor.DARK_GRAY + "Soup Refill");

					for (int i = 0; i < 54; i++) {
						inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
					}

					event.getPlayer().openInventory(inventory);
				}
			}
			if (event.getClickedBlock().getType() == Material.CHEST
					|| event.getClickedBlock().getType() == Material.ENDER_CHEST) {
				event.setCancelled(true);
			}
		}

		if (event.getAction().name().startsWith("RIGHT_")) {
			ItemStack item = event.getItem();
			Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

			switch (playerData.getPlayerState()) {
				case LOADING:
					player.sendMessage(
							CC.RED + "You must wait until your player data has loaded before you can use items.");
					break;
				case FFA:
					if (item == null) {
						return;
					}

					switch (item.getType()) {
						case MUSHROOM_SOUP:
							if (player.getHealth() <= 19.0D && !player.isDead()) {
								if (player.getHealth() < 20.0D || player.getFoodLevel() < 20) {
									player.getItemInHand().setType(Material.BOWL);
								}

								player.setHealth(player.getHealth() + 7.0D > 20.0D ? 20.0D : player.getHealth() +
										7.0D);
								player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
								player.setSaturation(12.8F);
								player.updateInventory();
							}
							break;
					}
					break;
				case FIGHTING:
					if (item == null) {
						return;
					}
					Match match = this.plugin.getMatchManager().getMatch(playerData);

					switch (item.getType()) {
						case MUSHROOM_SOUP:
							if (player.getHealth() <= 19.0D && !player.isDead()) {
								if (player.getHealth() < 20.0D || player.getFoodLevel() < 20) {
									player.getItemInHand().setType(Material.BOWL);
								}
								player.setHealth(player.getHealth() + 7.0D > 20.0D ? 20.0D : player.getHealth() +
										7.0D);
								player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
								player.setSaturation(12.8F);
								player.updateInventory();
							}
							break;
						case ENCHANTED_BOOK:
							Kit kit = match.getKit();
							PlayerInventory inventory = player.getInventory();

							int kitIndex = inventory.getHeldItemSlot();
							if (kitIndex == 8) {
								kit.applyToPlayer(player);
							} else {
								Map<Integer, PlayerKit> kits = playerData.getPlayerKits(kit.getName());

								PlayerKit playerKit = kits.get(kitIndex + 1);

								if (playerKit != null) {
									playerKit.applyToPlayer(player);
								}
							}
							break;
					}
					break;
				case SPAWN:
					if (item == null) {
						return;
					}

					switch (item.getType()) {
						case DIAMOND_SWORD:
							if(plugin.getKitManager().isRankedReboot()) {
								player.sendMessage(Color.translate("&cServer will reboot soon, therefore you can't join Premium queue until the server is done rebooting!"));
								return;
							}

							if (party != null) {
								player.sendMessage(CC.RED + "You can't join the Premium Queue while in a party.");
								return;
							}

							if (playerData.getPremiumMatches() <= 0) {
								player.sendMessage(CC.SECONDARY + "You don't have any " +
										CC.PRIMARY + "Premium Matches " +
										CC.SECONDARY + "remaining! Purchase more here: " +
										CC.PRIMARY + "https://store.secondlife.network");
								return;
							}

							player.openInventory(this.plugin.getInventoryManager().getJoinPremiumInventory().getCurrentPage());
							break;
						case IRON_SWORD:
							if(plugin.getKitManager().isRankedReboot()) {
								player.sendMessage(Color.translate("&cServer will reboot soon, therefore you can't join Premium queue until the server is done rebooting!"));
								return;
							}

							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
								return;
							}
							/*if(party != null) {
								player.sendMessage(Color.translate("&c2v2 Ranked Queue will be added soon!"));
								return;
							}*/

							player.openInventory(this.plugin.getInventoryManager().getRankedInventory().getCurrentPage());
							break;
						case STONE_SWORD:
							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
								return;
							}

							player.openInventory(this.plugin.getInventoryManager().getUnrankedInventory().getCurrentPage());
							break;
						case EMERALD:
							player.openInventory(plugin.getLeaderboards().getLeaderboardInventory());
							break;
						case BLAZE_POWDER:
							UUID rematching = this.plugin.getMatchManager().getRematcher(player.getUniqueId());
							Player rematcher = this.plugin.getServer().getPlayer(rematching);

							if (rematcher == null) {
								player.sendMessage(CC.RED + "Player is no longer online.");
								return;
							}

							if (this.plugin.getMatchManager()
									.getMatchRequest(rematcher.getUniqueId(), player.getUniqueId()) != null) {
								this.plugin.getServer().dispatchCommand(player, "accept " + rematcher.getName());
							} else {
								this.plugin.getServer().dispatchCommand(player, "duel " + rematcher.getName());
							}
							break;
						case PAPER:
							if (this.plugin.getMatchManager().isRematching(player.getUniqueId())) {
								this.plugin.getServer().dispatchCommand(player, "inv " + this.plugin.getMatchManager().getRematcherInventory(player.getUniqueId()));
							}
							break;
						case NAME_TAG:
							this.plugin.getPartyManager().createParty(player);
							break;
						case BOOK:
							player.openInventory(this.plugin.getInventoryManager().getEditorInventory().getCurrentPage());
							break;
						case WATCH:
							player.performCommand("settings");
							break;
						case DIAMOND_AXE:
							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can start events.");
								return;
							}
							player.openInventory(this.plugin.getInventoryManager().getPartyEventInventory().getCurrentPage());
							break;
						case IRON_AXE:
							if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
								player.sendMessage(CC.RED + "Only the party leader can start events.");
								return;
							}
							player.openInventory(this.plugin.getInventoryManager().getPartyInventory().getCurrentPage());
							break;
						case SKULL_ITEM:
							player.performCommand("party info");
							break;
						case NETHER_STAR:
							this.plugin.getPartyManager().leaveParty(player);
							this.plugin.getTournamentManager().leaveTournament(player);
							break;
					}
					break;
				case QUEUE:
					if (item == null) {
						return;
					}
					if (item.getType() == Material.REDSTONE) {
						if (party == null) {
							this.plugin.getQueueManager().removePlayerFromQueue(player);
						} else {
							this.plugin.getQueueManager().removePartyFromQueue(party);
						}
					}
					break;
				case SPECTATING:
					if (item == null) {
						return;
					}

					if(item.getType() == Material.NETHER_STAR) {
						if(this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
							this.plugin.getEventManager().removeSpectator(player);
						} else if (party == null) {
							this.plugin.getMatchManager().removeSpectator(player);
						} else {
							this.plugin.getPartyManager().leaveParty(player);
						}
					}
					break;
				case EDITING:
					if (event.getClickedBlock() == null) {
						return;
					}
					switch (event.getClickedBlock().getType()) {
						case WALL_SIGN:
						case SIGN:
						case SIGN_POST:
							this.plugin.getEditorManager().removeEditor(player.getUniqueId());
							PracticeData.sendToSpawnAndReset(player);
							break;
						case CHEST:
							Kit kit = this.plugin.getKitManager()
									.getKit(this.plugin.getEditorManager().getEditingKit(player.getUniqueId()));
							//Check if the edit kit contents are empty before opening the inventory.
							if (kit.getKitEditContents()[0] != null) {
								Inventory editorInventory = this.plugin.getServer().createInventory(null, 36);

								editorInventory.setContents(kit.getKitEditContents());
								player.openInventory(editorInventory);
								event.setCancelled(true);
							}
							break;
						case ANVIL:
							player.openInventory(
									this.plugin.getInventoryManager().getEditingKitInventory(player.getUniqueId()).getCurrentPage());
							event.setCancelled(true);
							break;
					}
					break;
				case EVENT:
					if (item == null) {
						return;
					}

					PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);

					if (item.getType() == Material.NETHER_STAR) {
						if(practiceEvent != null) {
							practiceEvent.leave(player);
						}
					} else if (item.getType() == Material.FIREBALL) {
						if (practiceEvent != null && practiceEvent instanceof ParkourEvent) {
							((ParkourEvent) practiceEvent).toggleVisibility(player);
						}
					}

					break;
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PracticeData playerData = PracticeData.getByName(player.getName());
		Material drop = event.getItemDrop().getItemStack().getType();

		switch (playerData.getPlayerState()) {
			case FFA:
				if (drop != Material.BOWL) {
					event.setCancelled(true);
				} else {
					event.getItemDrop().remove();
				}
				break;
			case FIGHTING:
				if (drop == Material.ENCHANTED_BOOK) {
					event.setCancelled(true);
				} else if (drop == Material.GLASS_BOTTLE) {
					event.getItemDrop().remove();
				} else {
					Match match = this.plugin.getMatchManager().getMatch(event.getPlayer().getUniqueId());

					this.plugin.getMatchManager().addDroppedItem(match, event.getItemDrop());
				}
				break;
			default:
				event.setCancelled(true);
				break;
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		PracticeData playerData = PracticeData.getByName(player.getName());

		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

			if (match.getEntitiesToRemove().contains(event.getItem())) {
				match.removeEntityToRemove(event.getItem());
			} else {
				event.setCancelled(true);
			}
		} else if (playerData.getPlayerState() != PlayerState.FFA) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PracticeData playerData = PracticeData.getByName(player.getName());

		switch (playerData.getPlayerState()) {
			case FIGHTING:
				if(plugin.getMatchManager().getMatch(playerData).getKit().isBedWars()) {
					if(playerData.getBed() == null) {
						this.plugin.getMatchManager().removeFighter(player, playerData, true);
					} else {
						plugin.getMatchManager().giveKits(player, plugin.getMatchManager().getMatch(playerData).getKit());
						playerData.setBedwarsRespawn(4);
					}
				} else {
					this.plugin.getMatchManager().removeFighter(player, playerData, true);
				}
				break;
			case EVENT:
				PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);

				if (currentEvent != null) {

					if(currentEvent instanceof OITCEvent) {
						OITCEvent oitcEvent = (OITCEvent) currentEvent;
						OITCPlayer oitcKiller = oitcEvent.getPlayer(player.getKiller());
						OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
						oitcPlayer.setLastKiller(oitcKiller);
						PlayerUtil.respawnPlayer(event);
						break;
					}

					currentEvent.onDeath().accept(player);
				}
				break;
			case FFA:
				/*for (ItemStack item : player.getInventory().getContents()) {
					if (item != null && item.getType() == Material.MUSHROOM_SOUP) {
						this.plugin.getFfaManager().getItemTracker().put(player.getWorld().dropItemNaturally(player
								.getLocation(), item), System.currentTimeMillis());
					}
				}

				this.plugin.getFfaManager().getKillStreakTracker().put(player.getUniqueId(), 0);

				String deathMessage = CC.PRIMARY + player.getName() + CC.SECONDARY + " was ";
				if (player.getKiller() == null) {
					deathMessage += "killed.";
				} else {
					deathMessage += "slain by " + CC.PRIMARY + player.getKiller().getName();

					int ks = this.plugin.getFfaManager().getKillStreakTracker().compute(player.getKiller()
							.getUniqueId(), (k, v) -> (v == null ? 0 : v) + 1);

					for (KillStreak killStreak : this.plugin.getFfaManager().getKillStreaks()) {
						if (killStreak.getStreaks().contains(ks)) {

							killStreak.giveKillStreak(player.getKiller());

							for (PracticeData data : PracticeData.getPlayerDatas()) {
								if (data.getPlayerState() == PlayerState.FFA) {
									deathMessage += "\n" + CC.PRIMARY + player.getKiller().getName() + CC.SECONDARY +
											" is on a " + CC.PRIMARY + ks + CC.SECONDARY + " kill streak!";
								}
							}
							break;
						}
					}
				}

				for (PracticeData data : PracticeData.getPlayerDatas()) {
					if (data.getPlayerState() == PlayerState.FFA) {
						Player ffaPlayer = this.plugin.getServer().getPlayer(data.getUniqueId());
						ffaPlayer.sendMessage(deathMessage);
					}
				}

				this.plugin.getServer().getScheduler().runTask(this.plugin, ()
						-> this.plugin.getFfaManager().removePlayer(event.getEntity()));*/
				break;
		}

		event.setDroppedExp(0);
		event.setDeathMessage(null);
		event.getDrops().clear();
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		PracticeData playerData = PracticeData.getByName(player.getName());

		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

			if (match == null) {
				event.setCancelled(true);
				return;
			}

			if (match.getKit() == null) {
				event.setCancelled(true);
				return;
			}

			if (match.getKit().isParkour() || match.getKit().isSumo() || this.plugin.getEventManager().getEventPlaying(player) != null) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();
			PracticeData shooterData = PracticeData.getByName(shooter.getName());

			if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
				Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());

				match.addEntityToRemove(event.getEntity());
			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();
			PracticeData shooterData = PracticeData.getByName(shooter.getName());

			if (shooterData != null) {
				if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
					Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());

					match.removeEntityToRemove(event.getEntity());

					if (event.getEntityType() == EntityType.ARROW) {
						event.getEntity().remove();
					}
				}
			}
		}
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		if(event.isCancelled()) return;

		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if(item.getType() == Material.POTION) {
			new BukkitRunnable() {
				public void run() {
					player.setItemInHand(new ItemStack(Material.AIR));

					player.updateInventory();
				}
			}.runTaskLater(plugin, 1L);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		PracticeData playerData = PracticeData.getByName(player.getName());

		switch (playerData.getPlayerState()) {

			case EVENT:
				PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);

				if (currentEvent != null) {

					if (currentEvent instanceof OITCEvent) {
						event.setRespawnLocation(player.getLocation());
						currentEvent.onDeath().accept(player);
					}
				}
		}
	}
}
