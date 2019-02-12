package secondlife.network.paik.client;

import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import secondlife.network.paik.Paik;
import secondlife.network.paik.handlers.data.PlayerData;
import secondlife.network.paik.utilties.events.player.PlayerAlertEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class ClientManager {

    private static Map<String, String> BLACKLISTED_MODS = ImmutableMap.of(
            "MouseTweaks",
            "Mouse Tweaks",
            "Particle Mod",
            "Particle Mod");

    private Set<PayloadClientType> payloadClients;
    private Set<ModClientType> modClients;
    private Paik plugin;

    public ClientManager(Paik plugin) {
        this.payloadClients = new HashSet<>();
        this.modClients = new HashSet<>();
        this.plugin = plugin;

        this.modClients.add(new ModClientType("Ethylene", "ethylene", null));
        this.modClients.add(new ModClientType("Ghost Client (Generic)", "gc", null));
        this.modClients.add(new ModClientType("Merge Aimbot", "Aimbot", null));
        this.modClients.add(new ModClientType("Cracked Vape v2.49", "mergeclient", null));
        this.modClients.add(new ModClientType("Cracked Vape v2.50", "wigger", null));
        this.modClients.add(new ModClientType("OpenComputers", "OpenComputers", "1.0"));
        this.modClients.add(new ModClientType("Schematica Reach", "Schematica", "1.7.6.git"));
        this.modClients.add(new ModClientType("TimeChanger Misplace", "timechanger", "1.0 "));
        this.modClients.add(new ModClientType("TcpNoDelay Clients", "TcpNoDelayMod-2.0", "1.0"));

        this.payloadClients.add(new PayloadClientType("Cracked Vape v2.06", "LOLIMAHCKER", true));
        this.payloadClients.add(new PayloadClientType("Cracked Merge", "cock", true));
        this.payloadClients.add(new PayloadClientType("BspkrsCore Client 1", "customGuiOpenBspkrs", true));
        this.payloadClients.add(new PayloadClientType("BspkrsCore Client 2", "0SO1Lk2KASxzsd", true));
        this.payloadClients.add(new PayloadClientType("BspkrsCore Client 3", "mincraftpvphcker", true));
        this.payloadClients.add(new PayloadClientType("Cracked Incognito", "lmaohax", true));
        this.payloadClients.add(new PayloadClientType("Old TimeChanger Misplace", "MCnetHandler", true));
        this.payloadClients.add(new PayloadClientType("OCMC", "OCMC", false));
        this.payloadClients.add(new PayloadClientType("CheatBreaker", "CB-Client", false));
        this.payloadClients.add(new PayloadClientType("Cosmic Client", "CC", false));
        this.payloadClients.add(new PayloadClientType("Labymod", "LABYMOD", false));
        this.payloadClients.add(new PayloadClientType("Private", "n", true));
        this.payloadClients.add(new PayloadClientType("Random", "0SSxzsd", true));
    }

    public void onModList(PlayerData playerData, Player player, Map<String, String> mods) {
        this.modClients.forEach(clientType -> {
            if(clientType.getModVersion() == null) {
                if(mods.containsKey(clientType.getModId())) {}
            } else if(clientType.getModVersion().equals(mods.get(clientType.getModId()))) {}
            return;
        });

        if(!playerData.getClient().isHacked()) {
            playerData.setClient(EnumClientType.FORGE);
        }

        this.checkCheats(playerData, player);
        StringJoiner blacklisted = new StringJoiner(", ");
        boolean kick = false;

        for(String modId : ClientManager.BLACKLISTED_MODS.keySet()) {
            if(mods.containsKey(modId)) {
                blacklisted.add(ClientManager.BLACKLISTED_MODS.get(modId));
                kick = true;
            }
        }

        if(kick) {
            player.kickPlayer(ChatColor.RED + "[Paik] Unrestricted Forge Mod" + (blacklisted.toString().contains(", ") ? "s" : "") + " Detected." +
                    "\nPlease remove the following mods:" +
                    "\n" + blacklisted.toString());
        }
    }

    private void checkCheats(PlayerData playerData, Player player) {
        if(playerData.getClient().isHacked()) {
            PlayerAlertEvent event = new PlayerAlertEvent(PlayerAlertEvent.AlertType.RELEASE, player, ChatColor.AQUA + " was caught using a " + ChatColor.RED + playerData.getClient().getName() + ".");

            this.plugin.getServer().getPluginManager().callEvent(event);
        }
    }
}
