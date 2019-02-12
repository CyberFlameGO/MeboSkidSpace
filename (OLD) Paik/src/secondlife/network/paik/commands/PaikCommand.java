package secondlife.network.paik.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.utils.Color;
import secondlife.network.paik.utils.file.ConfigFile;

import java.util.ArrayList;

public class PaikCommand extends zBaseCommand {

	public static ArrayList<String> enabled;
	public static ArrayList<String> disabled;
	
	public PaikCommand(Paik plugin) {
		super(plugin);

		enabled = new ArrayList<String>();
		disabled = new ArrayList<String>();
		
		this.command = "paik";
		this.permission = "secondlife.op";
		this.forPlayerUseOnly = true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Color.translate("&cUsage: /paik <list|enable|disable|autobans|anticheat>"));
			return;
		}
		
		this.getEnabledChecks();
		
		if(args[0].equalsIgnoreCase("list")) {
			player.sendMessage(Color.translate("&eChecks&7: &breach, autoclicker, "
					+ "killaura.dead, killaura.bot, killaura.packet, killaura.angle, killaura.wall"
					+ "fastbow, crash, custompayload, pingspoof, "
					+ "regen, flyA, flyB, fasteat, "
					+ "groundspoof, inventory.move, inventory.killaura, "
					+ "inventory.autopotion, noslowdown.shooting, noslowdown.eating, "
					+ "speed, timer.standing, timer.moving, "
					+ "timer.looking, crash, sneak, "
					+ "impossiblepitch, autoblock, refill, "
					+ "invalidinteract, morepackets"));
			player.sendMessage(Color.translate(""));
			player.sendMessage(Color.translate(""));
			player.sendMessage(Color.translate(""));
			player.sendMessage(Color.translate("&aEnabled&7:&e " + enabled.toString().replace("[", "").replace("]", "").replace(",", "&7,&e")));
			player.sendMessage(Color.translate(""));
			player.sendMessage(Color.translate("&cDisabled&7:&e " + disabled.toString().replace("[", "").replace("]", "").replace(",", "&7,&e")));
		}
		
		if(args[0].equalsIgnoreCase("enable")) {
			if(args.length == 1) {
				player.sendMessage(Color.translate("Usage: /paik enable <check>"));
				return;
			}
			
			String check = args[1];
			
			if(ConfigFile.configuration.getBoolean("checks." + check)) {
				player.sendMessage(Color.translate("&cThat check is already enabled!"));
				return;
			}
			
			ConfigFile.configuration.set("checks." + check, true);
			ConfigFile.save();
		}
		
		if(args[0].equalsIgnoreCase("disable")) {
			if(args.length == 1) {
				player.sendMessage(Color.translate("Usage: /paik disable <check>"));
				return;
			}
			
			String check = args[1];
			
			if(!ConfigFile.configuration.getBoolean("checks." + check)) {
				player.sendMessage(Color.translate("&cThat check is already disabled!"));
				return;
			}
			
			ConfigFile.configuration.set("checks." + check, false);
			ConfigFile.save();
		}
		
		if(args[0].equalsIgnoreCase("autobans")) {
			
			if(ConfigFile.configuration.getBoolean("autobans")) {
				ConfigFile.configuration.set("autobans", false);
				player.sendMessage(Color.translate("&cYou have sucessfully &4disabled &cautobans!"));
			} else {
				ConfigFile.configuration.set("autobans", true);
				player.sendMessage(Color.translate("&cYou have sucessfully &aenabled &cautobans!"));
			}
			
			ConfigFile.save();
		}
		
		if(args[0].equalsIgnoreCase("anticheat")) {
			
			if(ConfigFile.configuration.getBoolean("enabled")) {
				ConfigFile.configuration.set("enabled", false);
				player.sendMessage(Color.translate("&cAntiCheat has been &4disabled"));
			} else {
				ConfigFile.configuration.set("enabled", true);
				player.sendMessage(Color.translate("&cAntiCheat has been &aenabled"));
			}
			
			ConfigFile.save();
		}
	}
	
	public void getEnabledChecks() {
		enabled.clear();
		disabled.clear();
		
		if(ConfigFile.configuration.getBoolean("checks.reach")) {
			enabled.add("Reach");
		} else {
			disabled.add("Reach");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.autoclicker")) {
			enabled.add("AutoClicker");
		} else {
			disabled.add("AutoClicker");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.fastbow")) {
			enabled.add("FastBow");
		} else {
			disabled.add("FastBow");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.killaura.dead")) {
			enabled.add("KillAura_Dead");
		} else {
			disabled.add("KillAura_Dead");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.killaura.bot")) {
			enabled.add("KillAura_Bot");
		} else {
			disabled.add("KillAura_Bot");
		}

		if(ConfigFile.configuration.getBoolean("checks.killaura.packet")) {
			enabled.add("KillAura_Packet");
		} else {
			disabled.add("KillAura_Packet");
		}

		if(ConfigFile.configuration.getBoolean("checks.killaura.angle")) {
			enabled.add("KillAura_Angle");
		} else {
			disabled.add("KillAura_Angle");
		}

		if(ConfigFile.configuration.getBoolean("checks.killaura.wall")) {
			enabled.add("KillAura_Wall");
		} else {
			disabled.add("KillAura_Wall");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.regen")) {
			enabled.add("Regen");
		} else {
			disabled.add("Regen");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.flyA")) {
			enabled.add("FlyA");
		} else {
			disabled.add("FlyA");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.pingspoof")) {
			enabled.add("PingSpoof");
		} else {
			disabled.add("PingSpoof");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.flyB")) {
			enabled.add("FlyB");
		} else {
			disabled.add("FlyB");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.fasteat")) {
			enabled.add("FastEat");
		} else {
			disabled.add("FastEat");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.groundspoof")) {
			enabled.add("GroundSpoof");
		} else {
			disabled.add("GroundSpoof");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.inventory.move")) {
			enabled.add("Inventory_Move");
		} else {
			disabled.add("Inventory_Move");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.inventory.killaura")) {
			enabled.add("Inventory_Killaura");
		} else {
			disabled.add("Inventory_Killaura");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.inventory.autopotion")) {
			enabled.add("Inventory_AutoPotion");
		} else {
			disabled.add("Inventory_AutoPotion");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.noslowdown.shooting")) {
			enabled.add("NoSlowdown_Shooting");
		} else {
			disabled.add("NoSlowdown_Shooting");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.noslowdown.eating")) {
			enabled.add("NoSlowdown_Eating");
		} else {
			disabled.add("NoSlowdown_Eating");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.sneak")) {
			enabled.add("Sneak");
		} else {
			disabled.add("Sneak");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.speed")) {
			enabled.add("Speed");
		} else {
			disabled.add("Speed");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.timer.standing")) {
			enabled.add("Timer_Standing");
		} else {
			disabled.add("Timer_Standing");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.timer.moving")) {
			enabled.add("Timer_Moving");
		} else {
			disabled.add("Timer_Moving");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.timer.looking")) {
			enabled.add("Timer_Looking");
		} else {
			disabled.add("Timer_Looking");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.crash")) {
			enabled.add("Crash");
		} else {
			disabled.add("Crash");
		}

		if(ConfigFile.configuration.getBoolean("checks.custompayload")) {
			enabled.add("CustomPayload");
		} else {
			disabled.add("CustomPayload");
		}

		if(ConfigFile.configuration.getBoolean("checks.autoblock")) {
			enabled.add("AutoBlock");
		} else {
			disabled.add("AutoBlock");
		}

		if(ConfigFile.configuration.getBoolean("checks.refill")) {
			enabled.add("Refill");
		} else {
			disabled.add("Refill");
		}

		if(ConfigFile.configuration.getBoolean("checks.impossiblepitch")) {
			enabled.add("ImpossiblePitch");
		} else {
			disabled.add("ImpossiblePitch");
		}

		if(ConfigFile.configuration.getBoolean("checks.morepackets")) {
			enabled.add("MorePackets");
		} else {
			disabled.add("MorePackets");
		}

		if(ConfigFile.configuration.getBoolean("checks.invalidinteract")) {
			enabled.add("InvalidInteract");
		} else {
			disabled.add("InvalidInteract");
		}
		
		/*if(ConfigFile.configuration.getBoolean("checks.aimassist")) {
			enabled.add("AimAssist");
		} else {
			disabled.add("AimAssist");
		}
		
		if(ConfigFile.configuration.getBoolean("checks.velocity")) {
			enabled.add("Velocity");
		} else {
			disabled.add("Velocity");
		}*/
	}
}
