package secondlife.network.hcfactions.commands.arguments;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.commands.BaseCommand;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.FactionManager;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.type.games.CapturableFaction;
import secondlife.network.hcfactions.factions.type.games.EventFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.game.type.KothType;
import secondlife.network.hcfactions.handlers.RegisterHandler;
import secondlife.network.hcfactions.timers.GameHandler;
import secondlife.network.hcfactions.utilties.JavaUtils;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Permission;
import secondlife.network.vituz.utilties.cuboid.Cuboid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventCommand extends BaseCommand {

	public EventCommand(HCF plugin) {
		super(plugin);
		
		this.command = "event";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(player.hasPermission(Permission.OP_PERMISSION)) {
				if(args.length == 0) {
					this.sendUsage(player);
				} else {
					if(args[0].equalsIgnoreCase("create")) {
						if(args.length == 1) {
							player.sendMessage(Color.translate("&cYou must put Event Name!"));
							return;
						}

						if(args.length == 2) {
							player.sendMessage(Color.translate("&cYou must put Event Type!"));
							return;
						}

						Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

						if(faction != null) {
							sender.sendMessage(Color.translate("&cThere is already a faction named " + args[1] + '.'));
							return;
						}

						String upperCase;

						switch(upperCase = args[2].toUpperCase()) {
							case "KOTH": {
								faction = new KothFaction(args[1], null);
								break;
							}
							default: {
								sendUsage(sender);
								return;
							}
						}

						RegisterHandler.getInstancee().getFactionManager().createFaction(faction, sender);

						player.sendMessage(Color.translate("&aFaction of type &l" + args[2] + " &acreated with name &l" + args[1] + "&a."));
					} else if(args[0].equalsIgnoreCase("start")) {
						if(args.length == 1) {
							player.sendMessage(Color.translate("&cYou must put Event Name!"));
							return;
						}

						Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

						if(!(faction instanceof EventFaction)) {
							player.sendMessage(Color.translate("&cThere is no event named &l" + args[1] + "&c!"));
							return;
						}

						if(GameHandler.getGameHandler().tryContesting(((EventFaction) faction), sender)) {
							player.sendMessage(Color.translate("&eYou have started &d" + faction.getName() + "&e!"));
						}
					} else if(args[0].equalsIgnoreCase("stop")) {
						GameHandler.stopCooldown();

						Msg.sendMessage("&d" + sender.getName() + " &ehas cancelled &d" + (GameHandler.getEventFaction() == null ? "the active event" : GameHandler.getEventFaction().getName() + "&e") + "&e!");
						GameHandler.getGameHandler().getActiveKoths().remove(GameHandler.getEventFaction());
					} else if(args[0].equalsIgnoreCase("claimfor")) {
						if(args.length == 1) {
							player.sendMessage(Color.translate("&cYou must put Event Name!"));
							return;
						}

						WorldEditPlugin worldEdit = RegisterHandler.getInstancee().getWorldEdit();

						if(worldEdit == null) {
							player.sendMessage(Color.translate("&cWorldEdit must be installed to set event claims."));
							return;
						}

						Selection selection = worldEdit.getSelection(player);

						if(selection == null) {
							player.sendMessage(Color.translate("&cYou must make a WorldEdit selection to do this."));
							return;
						}

						if(selection.getWidth() < 5 || selection.getLength() < 5) {
							player.sendMessage(Color.translate("&cEvent claim areas must be at least &l" + 5 + "&cx&l" + 5 + "&c!"));
							return;
						}

						Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

						if(!(faction instanceof EventFaction)) {
							player.sendMessage(Color.translate("&cThere is not an event faction named &l" + args[1] + "&c!"));
							return;
						}

						((EventFaction) faction).setClaim(new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()), player);

						player.sendMessage(Color.translate("&eYou have updated the claim for event &d" + faction.getName() + "&e!"));
					} else if(args[0].equalsIgnoreCase("setcapzone")) {
						if(args.length == 1) {
							player.sendMessage(Color.translate("&cYou must put Event Name!"));
							return;
						}

						WorldEditPlugin worldEdit = RegisterHandler.getInstancee().getWorldEdit();

						if(worldEdit == null) {
							player.sendMessage(Color.translate("&cWorldEdit must be installed to set event claims."));
							return;
						}

						Selection selection = worldEdit.getSelection(player);

						if(selection.getWidth() < CaptureZone.MINIMUM_SIZE_AREA || selection.getLength() < CaptureZone.MINIMUM_SIZE_AREA) {
							player.sendMessage(Color.translate("&cCapzones must be at least &l" + 5 + "&cx&l" + 5 + "&c!"));
							return;
						}

						Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

						if(!(faction instanceof CapturableFaction)) {
							player.sendMessage(Color.translate("&cThere is not a capturable faction named &l" + args[1] + "&c!"));
							return;
						}

						CapturableFaction capturableFaction = (CapturableFaction) faction;
						Collection<ClaimZone> claims = capturableFaction.getClaims();

						if(claims.isEmpty()) {
							player.sendMessage(Color.translate("&cCapture zones can only be inside the event claim!"));
							return;
						}

						ClaimZone claim = new ClaimZone(faction, selection.getMinimumPoint(), selection.getMaximumPoint());

						World world = claim.getWorld();

						int minimumX = claim.getMinimumX();
						int maximumX = claim.getMaximumX();

						int minimumZ = claim.getMinimumZ();
						int maximumZ = claim.getMaximumZ();

						FactionManager factionManager = RegisterHandler.getInstancee().getFactionManager();

						for(int x = minimumX; x <= maximumX; x++) {
							for(int z = minimumZ; z <= maximumZ; z++) {
								Faction factionAt = factionManager.getFactionAt(world, x, z);

								if(factionAt != capturableFaction) {
									player.sendMessage(Color.translate("&cCapture zones can only be inside the event claim!"));
									return;
								}
							}
						}

						CaptureZone captureZone;

						if (capturableFaction instanceof KothFaction) {
							((KothFaction) capturableFaction).setCaptureZone(captureZone = new CaptureZone(capturableFaction.getName(), claim, KothType.default_cap_millis));
						} else {
							player.sendMessage(Color.translate("&cYou can only set capture zones for Conquest or KoTH factions."));
							return;
						}

						player.sendMessage(Color.translate("&eYou have set capture zone &d" + captureZone.getDisplayName() + " &efor faction &d" + faction.getName() + "&e!"));
					} else if(args[0].equalsIgnoreCase("tp")) {
						if(args.length == 1) {
							player.sendMessage(Color.translate("&cYou must put Event Name!"));
							return;
						}

						Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

						if(!(faction instanceof KothFaction)) {
							player.sendMessage(Color.translate("&cThere is no event named &l" + args[1] + "&c!"));
							return;
						}

						Location loc = ((KothFaction) faction).getCaptureZone().getCuboid().getCenter();

						player.teleport(loc);

						player.sendMessage(Color.translate("&eYou have been teleported to &d" + args[1] + " &eevent!"));
					} else if(args[0].equalsIgnoreCase("setcaptime")) {
						if(args.length == 1) {
							player.sendMessage(Color.translate("&cYou must put Event Name!"));
							return;
						}

						Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

						if(faction == null || !(faction instanceof KothFaction)) {
							player.sendMessage(Color.translate("&cThere is no a KoTH arena named &l" + args[1] + "&c!"));
							return;
						}

						long duration = JavaUtils.parse(StringUtils.join(args, ' ', 2, args.length));

						if(duration == -1L) {
							player.sendMessage(Color.translate("&cInvalid Duration."));
							return;
						}

						KothFaction kothFaction = (KothFaction) faction;
						CaptureZone captureZone = kothFaction.getCaptureZone();

						if(captureZone == null) {
							player.sendMessage(Color.translate("&c&l" + kothFaction.getDisplayName(sender) + " &cdoesn't have a capture zone set yet!"));
							return;
						}

						if(captureZone.isActive() && duration < captureZone.getRemainingCaptureMillis()) {
							captureZone.setRemainingCaptureMillis(duration);
						}

						captureZone.setDefaultCaptureMillis(duration);
						player.sendMessage(Color.translate("&eYou have set the capture delay of KoTH arena &d" + kothFaction.getDisplayName(sender) + " &eto &d" + DurationFormatUtils.formatDurationWords(duration, true, true) + "&e!"));
					} else if(args[0].equalsIgnoreCase("list")) {
						List<String> all = new ArrayList<String>();

						for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
							if(faction instanceof KothFaction) {
								all.add(faction.getName());
							}
						}

						player.sendMessage(Color.translate("&eEvent List&7: &d" + all.toString().replace("[", "").replace("]", "").replace(",", "&e,&d")));
					}

					return;
				}
			}
		} else {
			if(args.length == 0) {
				this.sendUsage(sender);
			} else {
				if(args[0].equalsIgnoreCase("create")) {
					if(args.length == 1) {
						sender.sendMessage(Color.translate("&cYou must put Event Name!"));
						return;
					}
					
					if(args.length == 2) {
						sender.sendMessage(Color.translate("&cYou must put Event Type!"));
						return;
					}

					Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

					if(faction != null) {
						sender.sendMessage(Color.translate("&cThere is already a faction named " + args[1] + '.'));
						return;
					}

					String upperCase = args[2].toUpperCase();

					switch(upperCase) {
						case "KOTH": {
							faction = new KothFaction(args[1], null);
							break;
						}
						default: {
							sendUsage(sender);
							return;
						}
					}

					RegisterHandler.getInstancee().getFactionManager().createFaction(faction, sender);

					sender.sendMessage(Color.translate("&aFaction of type &l" + args[2] + " &acreated with name &l" + args[1] + "&a."));
				} else if(args[0].equalsIgnoreCase("start")) {
					if(args.length == 1) {
						sender.sendMessage(Color.translate("&cYou must put Event Name!"));
						return;
					}
					
					Faction faction = RegisterHandler.getInstancee().getFactionManager().getFaction(args[1]);

					if(!(faction instanceof EventFaction)) {
						sender.sendMessage(Color.translate("&cThere is no event named &l" + args[1] + "&c!"));
						return;
					}

					if(GameHandler.getGameHandler().tryContesting(((EventFaction) faction), sender)) {
						sender.sendMessage(Color.translate("&eYou have started &d" + faction.getName() + "&e!"));
						GameHandler.getGameHandler().getActiveKoths().add((KothFaction) faction);
					}
				} else if(args[0].equalsIgnoreCase("stop")) {
					Msg.sendMessage("*&d" + sender.getName() + " &ehas cancelled &d" + (GameHandler.getEventFaction() == null ? "the active event" : GameHandler.getEventFaction().getName() + "&e") + "&e!");
					GameHandler.getGameHandler().getActiveKoths().remove(GameHandler.getEventFaction());
				} else if(args[0].equalsIgnoreCase("list")) {
					List<String> all = new ArrayList<>();
					
					for(Faction faction : RegisterHandler.getInstancee().getFactionManager().getFactions()) {
						if(faction instanceof KothFaction) {
							all.add(faction.getName());
						}
					}

					sender.sendMessage(Color.translate("&eCurrent Events&7: &d" + all.toString().replace("[", "").replace("]", "").replace(",", "&e,&d")));
				}
			}
		}
	}
	
	public void sendUsage(CommandSender sender) {
		sender.sendMessage(Color.translate("&6&lEvent Help&7:"));
		sender.sendMessage(Color.translate(" &e/event create <name> <type> &7- &dCreate an Event!"));
		sender.sendMessage(Color.translate(" &e/event start <name> &7- &dStart an Event!"));
		sender.sendMessage(Color.translate(" &e/event stop &7- &dStop an active Event!"));
		sender.sendMessage(Color.translate(" &e/event claimfor <name> &7- &dClaim an Event!"));
		sender.sendMessage(Color.translate(" &e/event setcapzone <name> &7- &dSetCapZone of a Event!"));
		sender.sendMessage(Color.translate(" &e/event tp <name> &7- &dTeleport to the Event!"));
		sender.sendMessage(Color.translate(" &e/event setcaptime <name> <time> &7- &dSet Cap Time of Event!"));
		sender.sendMessage(Color.translate(" &e/event list &7- &dSee all Events!"));
		sender.sendMessage(Color.translate(" &e/event regen &7- &dRegen a Citadel event!"));
		sender.sendMessage(Color.translate(" &e/event resetchesttime &7- &dResets a chest time for a Citadel event!"));
	}

}
