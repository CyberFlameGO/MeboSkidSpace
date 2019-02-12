package secondlife.network.uhc.commands.arguments;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import secondlife.network.uhc.UHC;
import secondlife.network.uhc.commands.BaseCommand;
import secondlife.network.uhc.listeners.MultiSpawnListener;
import secondlife.network.uhc.managers.GameManager;
import secondlife.network.uhc.managers.PartyManager;
import secondlife.network.uhc.managers.PlayerManager;
import secondlife.network.uhc.managers.ScenarioManager;
import secondlife.network.uhc.player.UHCData;
import secondlife.network.uhc.scenario.Scenario;
import secondlife.network.uhc.scenario.type.*;
import secondlife.network.uhc.state.GameState;
import secondlife.network.uhc.tasks.BorderTask;
import secondlife.network.uhc.tasks.GameTask;
import secondlife.network.uhc.utilties.UHCUtils;
import secondlife.network.uhc.utilties.WorldCreator;
import secondlife.network.uhc.utilties.events.GameStopEvent;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.data.ChallengeData;
import secondlife.network.vituz.providers.nametags.VituzNametag;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.NumberUtils;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.inventory.InventoryUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameCommand extends BaseCommand {
	
	public static int i;
	public static List<Player> scatter = new ArrayList<>();

	public GameCommand(UHC plugin) {
		super(plugin);

		this.command = "game";
		this.permission = Permission.OP_PERMISSION;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {		
		if(args.length == 0) {
			this.sendUsage(sender);
		} else {
			if(args[0].equalsIgnoreCase("start")) {
				if(GameManager.getGameState().equals(GameState.SCATTERING) || GameManager.getGameState().equals(GameState.PLAYING)) {
					sender.sendMessage(Color.translate("&cUHC is already running."));
					return;
				}

				GameManager.setGameState(GameState.SCATTERING);

				sender.sendMessage(Color.translate("&eYou have started uhc."));

				for(Player online : Bukkit.getOnlinePlayers()) {
					if(plugin.getPracticeManager().getUsers().contains(online.getUniqueId())) {
						online.getInventory().clear();

						online.getInventory().setHelmet(new ItemStack(Material.AIR));
						online.getInventory().setChestplate(new ItemStack(Material.AIR));
						online.getInventory().setLeggings(new ItemStack(Material.AIR));
						online.getInventory().setBoots(new ItemStack(Material.AIR));

						for(PotionEffect effect : online.getActivePotionEffects()) {
							online.removePotionEffect(effect.getType());
						}
						
						plugin.getPracticeManager().getUsers().remove(online.getUniqueId());
						
						online.getInventory().clear();

						online.getInventory().setHelmet(new ItemStack(Material.AIR));
						online.getInventory().setChestplate(new ItemStack(Material.AIR));
						online.getInventory().setLeggings(new ItemStack(Material.AIR));
						online.getInventory().setBoots(new ItemStack(Material.AIR));

						MultiSpawnListener.randomSpawn(online);

						if (!GameManager.getGameState().equals(GameState.PLAYING) && !UHCUtils.isPlayerInSpecMode(online)) {
							new BukkitRunnable() {
								public void run() {
									UHCUtils.loadLobbyInventory(online);

									online.updateInventory();

									online.setHealth(20.0);
									online.setGameMode(GameMode.ADVENTURE);}
							}.runTaskLater(this.getInstance(), 10L);
						}
					}
				}

				if(plugin.getPracticeManager().isOpen()) {
					Msg.sendMessage("&dPractice &ehas been automatically &cdisabled &ebecause &dScatter&e has &astarted&e.");
				}

				plugin.getPracticeManager().setOpen(false);
				
	            if(PartyManager.isEnabled()) {
	                plugin.getPartyManager().handleAutoPlace();
	            }

				for(Player online : Bukkit.getOnlinePlayers()) {
					if(!UHCUtils.isPlayerInSpecMode(online)) {

						scatter.add(online);
					}
				}

				i = (scatter.size() - 1);

				new BukkitRunnable() {
					public void run() {
						sender.sendMessage(Color.translate("&eScatter has been &astarted&e."));

						scatterAllPlayers();
					}
				}.runTaskLater(this.getInstance(), 80L);
			} else if(args[0].equalsIgnoreCase("stop")) {
				if(!GameManager.getGameState().equals(GameState.SCATTERING) && !GameManager.getGameState().equals(GameState.PLAYING)) {
					sender.sendMessage(Color.translate("&cUHC isn't currently running."));
					return;
				}

				sender.sendMessage(Color.translate("&eStopping UHC..."));

				for(LivingEntity entity : Bukkit.getWorld("uhc_world").getLivingEntities()) {
					if(entity.getType() == EntityType.VILLAGER) {
						entity.remove();
					}
				}

				GameManager.setGameState(GameState.LOBBY);

				UHCData.getUhcDatas().clear();

				plugin.getPartyManager().handleClearParties();

				Bukkit.getServer().getPluginManager().callEvent(new GameStopEvent("Force stopped."));

				new WorldCreator(true, true);

				for(OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
					whitelisted.setWhitelisted(false);
				}

				new BukkitRunnable() {
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reboot 30s");
					}
				}.runTaskLater(this.getInstance(), 20L);
			} else if(args[0].equalsIgnoreCase("forcestop")) {
				for(LivingEntity entity : Bukkit.getWorld("uhc_world").getLivingEntities()) {
					if(entity.getType() == EntityType.VILLAGER) {
						entity.remove();
					}
				}

				GameManager.setGameState(GameState.LOBBY);

				UHCData.getUhcDatas().clear();

				plugin.getPartyManager().handleClearParties();

				Bukkit.getServer().getPluginManager().callEvent(new GameStopEvent("Force stopped."));

				new WorldCreator(true, true);

				for(OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
					whitelisted.setWhitelisted(false);
				}

				new BukkitRunnable() {
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reboot 30s");
					}
				}.runTaskLater(this.getInstance(), 20L);
			} else if(args[0].equalsIgnoreCase("map")) {
	    		if(args.length == 1) {
					sender.sendMessage(Color.translate("&7&m-----------------------"));
					sender.sendMessage(Color.translate("&eMap Generated: &d" + plugin.getGameManager().isGenerated()));
					sender.sendMessage(Color.translate("&7&m-----------------------"));
					return;
	    		}
	            
	    		if(args[1].equalsIgnoreCase("true")) {
	    			 plugin.getGameManager().setMapGenerating(true);
	    		} else if(args[1].equalsIgnoreCase("false")) {
					plugin.getGameManager().setMapGenerating(false);
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb fill cancel");
				} else {
					sender.sendMessage(Color.translate("&7&m-----------------------"));
					sender.sendMessage(Color.translate("&eMap Generated: &d" + plugin.getGameManager().isGenerated()));
					sender.sendMessage(Color.translate("&7&m-----------------------"));
				}
			} else if(args[0].equalsIgnoreCase("stats")) {
				if(args.length < 1) {
					sender.sendMessage(Color.translate("&cUsage: /uhc stats <true|false>"));
					return;
				}
				
				if(args[1].equalsIgnoreCase("true")) {
					plugin.getGameManager().setStats(true);
					
					sender.sendMessage(Color.translate("&eStats has been &aEnabled&e."));
				} else if(args[1].equalsIgnoreCase("false")) {
					plugin.getGameManager().setStats(false);
					
					sender.sendMessage(Color.translate("&eStats has been &cDisabled&e."));
				}
			} else if(args[0].equalsIgnoreCase("pvp")) {
				if(args.length < 1) {
					sender.sendMessage(Color.translate("&cUsage: /uhc pvp <true|false>"));
					return;
				}
				
				if(args[1].equalsIgnoreCase("true")) {
					plugin.getGameManager().setPvP(true);
				} else if(args[1].equalsIgnoreCase("false")) {
					plugin.getGameManager().setPvP(false);
				}
			} else if(args[0].equalsIgnoreCase("time")) {
				if(args.length < 1) {
					sender.sendMessage(Color.translate("&cUsage: /uhc time <time>"));
					return;
				}
				
				int x = Integer.parseInt(args[1]);
				
				if(!NumberUtils.isInteger(args[1])) {
					sender.sendMessage(Color.translate("&cInvalid number."));
					return;
				}
				
				if(x > 1000000000) {
					sender.sendMessage(Color.translate("&cAre you retarded?."));
					return;
				}
				
				GameTask.seconds = x;
				
				sender.sendMessage(Color.translate("&eYou have set &dGame Time &eto the &d" + x + " &eseconds."));
			} else if(args[0].equalsIgnoreCase("unsit")) {
				if(args.length < 1) {
					this.sendUsage(sender);
					return;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				
				if(Msg.checkOffline(sender, args[1])) return;
				
				sender.sendMessage(Color.translate("&eYou have unsited player named &d" + target.getName() + "&e."));
		    	
				this.getInstance().getServer().getScheduler().runTaskLater(this.getInstance(), () -> Vituz.getInstance().getHorseManager().unsitPlayer(target), 3L);
			} else if(args[0].equalsIgnoreCase("list")) {
				sender.sendMessage(Color.translate("&7&m--------------------"));
				sender.sendMessage(Color.translate("&eSpectators: &d" + plugin.getSpectatorManager().getSpectators().size()));
				sender.sendMessage(Color.translate(""));
				sender.sendMessage(Color.translate("&eAlive Players: &d" + PlayerManager.getAlivePlayers() + "&e/&d" + plugin.getGameManager().getInitial()));
				sender.sendMessage(Color.translate("&eOnline: &d" + Bukkit.getOnlinePlayers().size()));
				sender.sendMessage(Color.translate("&7&m--------------------"));
			} else if(args[0].equalsIgnoreCase("aliveplayers") || args[0].equalsIgnoreCase("ap") || args[0].equalsIgnoreCase("alive")) {
				List<String> all = new ArrayList<>();
				
				for(UHCData uhcData : UHCData.getUhcDatas().values()) {
					if(uhcData.isAlive()) {
						all.add(uhcData.getName());
					}
				}
				
				sender.sendMessage(Color.translate("&7&m------------------------"));
				sender.sendMessage(Color.translate(" &eCurrent Alive Players: &d" + PlayerManager.getAlivePlayers() + "&e/&d" + plugin.getGameManager().getInitial()));
				sender.sendMessage("");
				sender.sendMessage(Color.translate(" &3- &d" + all.toString().replace("[", "").replace("]", "").replace(",", "&d,&d")));
				sender.sendMessage(Color.translate("&7&m------------------------"));
			} else if(args[0].equalsIgnoreCase("remove")) {
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
				
				UHCData uhcData = UHCData.getByName(target.getName());
				
		        if(!target.hasPlayedBefore()) {
	                sender.sendMessage(Color.translate("&cThis player never played on this server."));
	                return;
	            }
				
				if(uhcData.isAlive()) {
					uhcData.setAlive(false);

					if (target.isOnline()) {
						if (target.getPlayer().hasPermission(Permission.DONOR_PERMISSION)) {
							plugin.getSpectatorManager().handleEnable(target.getPlayer());
						} else {
							target.getPlayer().kickPlayer(Color.translate("&cYou can't spectate games if you are not donator."));
						}
					}
				} else {
	                sender.sendMessage(Color.translate("&cThis player isn't alive."));
				}
			} else if(args[0].equalsIgnoreCase("saveitems")) {
				plugin.getUtiltiesConfig().set("uhcitems.inventory", InventoryUtils.toBase64(((Player) sender).getInventory()));


				try {
					plugin.getUtiltiesConfig().save(plugin.getUtiltiesConfig().getFile());
				} catch (IOException e) {
					e.printStackTrace();
				}

				((Player) sender).getInventory().clear();
				((Player) sender).getInventory().setHelmet(new ItemStack(Material.AIR));
				((Player) sender).getInventory().setChestplate(new ItemStack(Material.AIR));
				((Player) sender).getInventory().setLeggings(new ItemStack(Material.AIR));
				((Player) sender).getInventory().setBoots(new ItemStack(Material.AIR));

				((Player) sender).sendMessage(Color.translate("&eYou have set &dUHC Start &eitems."));

				plugin.getGameManager().getSaveUsers().remove(((Player) sender).getUniqueId());

				UHCUtils.loadLobbyInventory(((Player) sender));
			} else if(args[0].equalsIgnoreCase("setwins")) {
				if(!(sender instanceof ConsoleCommandSender)) {
					return;
				}

				if(args.length < 3) {
					return;
				}

				Player target = Bukkit.getPlayer(args[1]);

				UHCData data;

				if (target == null) {
					data = UHCData.getByName(args[1]);
				} else {
					data = UHCData.getByName(target.getName());
				}

				if (!data.isLoaded()) {
					data.load();
				}

				if(!NumberUtils.isInteger(args[2])) return;

				int wins = Integer.parseInt(args[2]);

				if(wins >= 100) {
					return;
				}

				data.setWins(wins);
			}
		}
	}
	
	
	public void sendUsage(CommandSender sender) {		
		sender.sendMessage(Color.translate("&cUHC - Help Commands:"));
		sender.sendMessage(Color.translate("&c/game start - Start UHC."));
		sender.sendMessage(Color.translate("&c/game stop - Stop UHC."));
		sender.sendMessage(Color.translate("&c/game map - Check is map generated."));
		sender.sendMessage(Color.translate("&c/game pvp <true|false> - Toggle on/off pvp."));
		sender.sendMessage(Color.translate("&c/game stats <true|false> - Toggle on/off stats."));
		sender.sendMessage(Color.translate("&c/game time <time> - Set UHC Game Time."));
		sender.sendMessage(Color.translate("&c/game list - Check player list."));
		sender.sendMessage(Color.translate("&c/game aliveplayers - Check aliveplayers list."));
		sender.sendMessage(Color.translate("&c/game unsit <playerName> - Unsit player if he is glitched."));
		sender.sendMessage(Color.translate("&c/game remove <playerName> - Remove alive players."));
	}
	
	public void broadcast() {
		new BukkitRunnable() {
			public void run() {
				int i = startsIn();

				if(Arrays.asList(240, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1).contains(i)) {
					Msg.sendMessage("&eThe game will begin in &d" + i + " second" + (i > 1 ? "s" : "") + "&e.");
				}
				
				if(i < 1) {
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(this.getInstance(), 20L, 20L);
	}
	
	public void startGame() {
		plugin.getGameManager().setWorldUsed(true);

		for(Player players : scatter) {
			if(players != null && !players.isEmpty()) {
				players.setFoodLevel(20);
				players.setLevel(0);
				players.setHealth(20.0);

				players.playSound(players.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
			}
		}
		
		Bukkit.getWorld("uhc_world").setPVP(false);
		Bukkit.getWorld("uhc_world").setGameRuleValue("naturalRegeneration", "false");
		
		GameManager.setGameState(GameState.PLAYING);
		plugin.getGameManager().setGenerated(false);
		plugin.getGameManager().setInitial(PlayerManager.getAlivePlayers());

		for(Player online : Bukkit.getOnlinePlayers()) {
			online.setNoDamageTicks(200);
		}

		new GameTask();
		new BorderTask();

		plugin.getGameManager().setBorderTime(false);

		Msg.sendMessage("&eThe game has been started.");

		List<String> all = new ArrayList<>();
		
		for(Scenario scenario : ScenarioManager.scenarios) {
			if(scenario.isEnabled()) {
				all.add(scenario.getName());
			}
		}

		Msg.sendMessage("&eCurrent game scenarios: &d" + all.toString().replace("[", "").replace("]", "").replace(",", "&e, &d"));
		
		if(ScenarioManager.getByName("BestPvE").isEnabled()) {
			BestPVEScenario.enable();
		}

		if(ScenarioManager.getByName("Gone Fishing").isEnabled()) {
			GoneFishingScenario.giveItems();
		}

		if(ScenarioManager.getByName("Nine Slot").isEnabled()) {
			new BukkitRunnable() {
				public void run() {
					for(Player player : Bukkit.getOnlinePlayers()) {
						NineSlotScenario.cleanInventory(player);
					}
				}

			}.runTaskTimer(UHC.getInstance(), 20L, 20L);
		}
		
		if(ScenarioManager.getByName("Limited Enchants").isEnabled()) {
			LimitedEnchantsScenario.spawnEnchantmentTables();
		}
		
		for(Player online : Bukkit.getOnlinePlayers()) {
	    	Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> Vituz.getInstance().getHorseManager().unsitPlayer(online), 3L);
	    	
	    	online.setHealth(20.0);
	    	online.setFireTicks(0);

			InfiniteEnchanterScenario.start(online);
			
	    	online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
	    	
	    	UHCData uhcData = UHCData.getByName(online.getName());
	    	
	    	if(uhcData.isAlive()) uhcData.setPlayed(uhcData.getPlayed() + 1);

			/**
			 * Challange -> (10 Played UHC)
			 */
	    	if(uhcData.getPlayed() == 10) {
				ChallengeData.getByName(online.getName()).setPlayedUHCGames(10);
			}
	    	
			new BukkitRunnable() {
				public void run() {
					if(UHCUtils.isPlayerInSpecMode(online)) {
						for(Player on : Bukkit.getOnlinePlayers()) {
							if(UHCUtils.isPlayerInSpecMode(online)) {
								on.hidePlayer(online);
							}
						}
					}
				}
			}.runTaskLater(this.getInstance(), 5L);
		}
		if(ScenarioManager.getByName("Seasons").isEnabled()) {
			for(Player online : Bukkit.getOnlinePlayers()) {
				VituzNametag.reloadPlayer(online);
				VituzNametag.reloadOthersFor(online);
			}
		}
			
		new BukkitRunnable() {
			public void run() {
				if(ScenarioManager.getByName("Seasons").isEnabled()) {

				}
				
				Msg.sendMessage("&7&m--------------------------");
				Msg.sendMessage("");
				Msg.sendMessage(" &5&l" + Msg.KRUZIC + " &eKRAJ EPIZODE &5&l" + Msg.KRUZIC);
				Msg.sendMessage("");
				Msg.sendMessage("&7&m--------------------------");

				for(Player online : Bukkit.getOnlinePlayers()) {					
			    	online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
				}
			}
		}.runTaskTimerAsynchronously(this.getInstance(), 24000L, 24000L);
	}
	
	public void scatterAllPlayers() {
		broadcast();
		
		new BukkitRunnable() {
			public void run() {
				if(i < 0) {
					this.cancel();
					
					new BukkitRunnable() {
						public void run() {
							startGame();
						}
					}.runTaskLater(UHC.getInstance(), 20L);
 				} else {
					try {
						scatter.get(i);
					} catch (ArrayIndexOutOfBoundsException e) {
						scatter.remove(i);
					} try {
						Player player = scatter.get(i);

						if(player != null) {
							UHCUtils.scatterPlayer(player);

							i--;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Null player scatter.");
					}
				}
			}
		}.runTaskTimer(this.getInstance(), 5L, 5L);
	}
	
    public static int startsIn() {
        return i * 5 / 20;
    }

	public static int getInt() {
		return i;
	}
}
