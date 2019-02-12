package secondlife.network.practice.managers;

import secondlife.network.practice.utilties.CC;
import secondlife.network.practice.Practice;
import secondlife.network.practice.kit.Kit;
import secondlife.network.practice.kit.PlayerKit;
import secondlife.network.practice.utilties.PlayerUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class EditorManager {
	private final Practice plugin = Practice.getInstance();
	private final Map<UUID, String> editing = new HashMap<>();
	private final Map<UUID, PlayerKit> renaming = new HashMap<>();

	public void addEditor(Player player, Kit kit) {
		this.editing.put(player.getUniqueId(), kit.getName());
		this.plugin.getInventoryManager().addEditingKitInventory(player, kit);

		PlayerUtil.clearPlayer(player);
		player.teleport(this.plugin.getSpawnManager().getEditorLocation().toBukkitLocation());
		player.getInventory().setContents(kit.getContents());
		player.sendMessage(CC.PRIMARY + "Now editing kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + ". Armor will be applied automatically in the kit.");
	}

	public void removeEditor(UUID editor) {
		this.renaming.remove(editor);
		this.editing.remove(editor);
		this.plugin.getInventoryManager().removeEditingKitInventory(editor);
	}

	public String getEditingKit(UUID editor) {
		return this.editing.get(editor);
	}

	public void addRenamingKit(UUID uuid, PlayerKit playerKit) {
		this.renaming.put(uuid, playerKit);
	}

	public void removeRenamingKit(UUID uuid) {
		this.renaming.remove(uuid);
	}

	public PlayerKit getRenamingKit(UUID uuid) {
		return this.renaming.get(uuid);
	}
}
