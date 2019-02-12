package secondlife.network.practice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.utilties.CC;

public class SpawnCommand extends Command {

	public SpawnCommand() {
		super("spawn");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if(!(sender instanceof Player)) return true;


		Player player = (Player) sender;

		PracticeData playerData = PracticeData.getByName(player.getName());

		if (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.FFA) {
			player.sendMessage(CC.RED + "You can't do this in your current state.");
			return true;
		}

		switch(alias.toLowerCase()) {
			case "spawn":
				PracticeData.sendToSpawnAndReset(player);
				break;
		}
		return true;
	}

}
