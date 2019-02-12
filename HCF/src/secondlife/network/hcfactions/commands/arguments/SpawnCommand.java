package secondlife.network.hcfactions.commands.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCFConfiguration;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.data.HCFData;
import secondlife.network.hcfactions.timers.SpawnTagHandler;
import secondlife.network.hcfactions.utilties.file.UtilitiesFile;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

public class SpawnCommand extends BaseCommand {

	public SpawnCommand(HCF plugin) {
		super(plugin);

		this.command = "spawn";
		this.forPlayerUseOnly = true;
	}
	
	public static ConcurrentHashMap<Player, Integer> teleporting = new ConcurrentHashMap<>();

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if(HCFData.getByName(player.getName()).isEvent()) {
			player.sendMessage(Color.translate("&cYou can't do this in your current state."));
			return;
		}

		Location spawn = StringUtils.destringifyLocation(UtilitiesFile.configuration.getString("World-Spawn.world-spawn"));

		if(HCFConfiguration.kitMap) {
			if(player.hasPermission("secondlife.staff")) {
				player.teleport(spawn);
			} else {
				if(SpawnTagHandler.isActive(player)) {
					sender.sendMessage(Color.translate("&cYou cannot spawn while you are Spawn Tagged!"));
					return;
				}
				
				if(teleporting.containsKey(player)) {
					sender.sendMessage(Color.translate("&cYou are already spawning."));
					return;
				}
				
				teleporting.put(player,
						Bukkit.getScheduler().scheduleSyncRepeatingTask(HCF.getInstance(), new Runnable() {
							int i = 20;

							public void run() {
								if (i != 0) {
									player.playSound(player.getLocation(), Sound.NOTE_BASS_DRUM, 1f, 1f);
									player.sendMessage(Color.translate("&e&lSpawning... &ePlease wait &c" + i + " &eseconds."));
									i--;
									return;
								}

								player.teleport(spawn);
								sender.sendMessage(Color.translate("&aSuccsesfuly teleported to spawn."));
								Bukkit.getScheduler().cancelTask(teleporting.get(player));
								teleporting.remove(player);

							}
						}, 0, 20));
			}
		} else {
			if(player.hasPermission("secondlife.staff")) {
				if(args.length == 0) {
					if(spawn == null) {
						System.out.print("SPAWN IS NULL!");
						player.sendMessage(Color.translate("&c&lSpawn is not set please contact a Staff Member!"));
						return;
					}
					
					player.teleport(spawn);
				} else {
					Player target = Bukkit.getPlayer(args[0]);

					if(Msg.checkOffline(player, args[0]));

					target.teleport(spawn);
					target.sendMessage(Color.translate("&aYou have been teleported to spawn by&d " + player.getName()));
					sender.sendMessage(Color.translate("&aYou have succsesfuly teleport &d" + target.getName() + " &ato spawn."));
				}
			} else {
				sender.sendMessage(Color.translate("&cSecondLife HCF does not have a spawn command! You must walk there! Spawn is located at 0,0."));
			}
		}

	}

}
