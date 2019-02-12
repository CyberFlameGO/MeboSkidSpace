package secondlife.network.vituz.providers.tab;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import secondlife.network.vituz.data.PlayerData;
import secondlife.network.vituz.providers.LayoutProvider;
import secondlife.network.vituz.providers.packets.PlayerInfoPacketMod;
import secondlife.network.vituz.providers.packets.ScoreboardTeamPacketMod;
import secondlife.network.vituz.providers.threads.TabThread;
import secondlife.network.vituz.utilties.Color;

import java.net.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class VituzTab {

    @Getter
    @Setter
    private static LayoutProvider layoutProvider;

    private static boolean initiated = false;
    private static AtomicReference<Object> defaultPropertyMap = new AtomicReference<>();
    private static Map<String, VituzTab> tabs = new ConcurrentHashMap<>();
    private static PropertyMap defaultSkin = fetchSkin();

    private Player player;
    private Map<String, String> previousNames = new HashMap<>();
    private Map<String, Integer> previousPings = new HashMap<>();
    private Set<String> createdTeams = new HashSet<>();
    private TabLayout initialLayout;
    private boolean initiatedTab = false;
    private StringBuilder removeColorCodesBuilder = new StringBuilder();
    
    public VituzTab(Player player) {
        this.player = player;
    }

    public static void hook() {
        initiated = true;

        getDefaultPropertyMap();
        (new TabThread()).start();
    }

    public static void addPlayer(Player player) {
        tabs.put(player.getName(), new VituzTab(player));
    }

    public static void updatePlayer(Player player) {
        if(tabs.containsKey(player.getName())) {
            tabs.get(player.getName()).update();
        }
    }

    public static void removePlayer(Player player) {
        tabs.remove(player.getName());
    }

    private static PropertyMap fetchSkin() {
        GameProfile profile = new GameProfile(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"), "bananasquad");
        HttpAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");

        MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
        GameProfile gameProfile = sessionService.fillProfileProperties(profile, true);

        return gameProfile.getProperties();
    }

    public static PropertyMap getDefaultPropertyMap() {
        Object value = defaultPropertyMap.get();

        if(value == null) {
            synchronized(defaultPropertyMap) {
                value = defaultPropertyMap.get();

                if(value == null) {
                    PropertyMap actualValue = defaultSkin;

                    value = ((actualValue == null) ? defaultPropertyMap : actualValue);

                    defaultPropertyMap.set(value);
                }
            }
        }

        return (PropertyMap)((value == defaultPropertyMap) ? null : value);
    }

    private void init() {
        if(!this.initiatedTab) {
            TabLayout initialLayout = TabLayout.createEmpty(this.player);


            if(!initialLayout.is18()) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    this.updateTabList(player.getName(), 0, ((CraftPlayer) player).getProfile(), PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
                }
            }

            for(String tabNames : initialLayout.getTabNames()) {
                this.updateTabList(tabNames, 0, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);

                String teamName = tabNames.replaceAll("§", "");

                if(!this.createdTeams.contains(teamName)) {
                    this.createAndAddMember(teamName, tabNames);

                    this.createdTeams.add(teamName);
                }
            }

            if(!initialLayout.is18()) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    this.updateTabList(player.getName(), 0, ((CraftPlayer) player).getProfile(), PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
                }
            }
            
            this.initialLayout = initialLayout;
            this.initiatedTab = true;
        }
    }

    protected void update() {
        if(layoutProvider != null) {
            TabLayout tabLayout = layoutProvider.getLayout(this.player);

            if(tabLayout == null) {
                if(this.initiatedTab) {
                    this.reset();
                }
                
                return;
            }
            
            this.init();
            
            for(int y = 0; y < TabLayout.HEIGHT; ++y) {
                for(int x = 0; x < TabLayout.WIDTH; ++x) {
                    String entry = tabLayout.getStringAt(x, y);
                    int ping = tabLayout.getPingAt(x, y);
                    String entryName = this.initialLayout.getStringAt(x, y);
                    
                    this.removeColorCodesBuilder.setLength(0);
                    this.removeColorCodesBuilder.append(entryName);

                    int j = 0;
                    for(int i = 0; i < this.removeColorCodesBuilder.length(); ++i) {
                        if('§' != this.removeColorCodesBuilder.charAt(i)) {
                            this.removeColorCodesBuilder.setCharAt(j++, this.removeColorCodesBuilder.charAt(i));
                        }
                    }
                    
                    this.removeColorCodesBuilder.delete(j, this.removeColorCodesBuilder.length());
                    String teamName = "$" + this.removeColorCodesBuilder.toString();

                    PlayerData data = PlayerData.getByName(this.player.getName());

                    entry = entry.replace("§5", data.getMainColor());
                    entry = entry.replace("§d", data.getSecondColor());
                    entry = entry.replace("&5", Color.translate(data.getMainColor()));
                    entry = entry.replace("&d", Color.translate(data.getSecondColor()));

                    if(this.previousNames.containsKey(entryName)) {
                        if(!this.previousNames.get(entryName).equals(entry)) {
                            this.update(entryName, teamName, entry, ping);
                        } else if(this.previousPings.containsKey(entryName) && this.previousPings.get(entryName) != ping) {
                            this.updateTabList(entryName, ping, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY);
                           
                            this.previousPings.put(entryName, ping);
                        }
                    } else {
                        this.update(entryName, teamName, entry, ping);
                    }
                }
            }
        }
    }
    
    private void reset() {
        this.initiatedTab = false;
        
        for(String names : this.initialLayout.getTabNames()) {
            this.updateTabList(names, 0, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        }
        
		EntityPlayer ePlayer = ((CraftPlayer) this.player).getHandle();
        this.updateTabList(this.player.getName(), ePlayer.ping, ePlayer.getProfile(), PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        int count = 1;
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(this.player == player) continue;
            
            if(count > this.initialLayout.getTabNames().length - 1) break;
            
			ePlayer = ((CraftPlayer) player).getHandle();
            
			this.updateTabList(player.getName(), ePlayer.ping, ePlayer.getProfile(), PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            ++count;
        }
    }
    
    private void update(String entryName, String teamName, String entry, int ping) {
        String[] entryStrings = this.splitString(entry);
       
        String prefix = entryStrings[0];
        String suffix = entryStrings[1];
       
        if(!suffix.isEmpty()) {
            if(prefix.charAt(prefix.length() - 1) == '§') {
                prefix = prefix.substring(0, prefix.length() - 1);
                suffix = '§' + suffix;
            }
            
            String suffixPrefix = ChatColor.RESET.toString();
            
            if(!ChatColor.getLastColors(prefix).isEmpty()) {
                suffixPrefix = ChatColor.getLastColors(prefix);
            }
            
            if(suffix.length() <= 14) {
                suffix = suffixPrefix + suffix;
            } else {
                suffix = suffixPrefix + suffix.substring(0, 14);
            }
        }
        
        this.updateScore(teamName, prefix, suffix);
        this.updateTabList(entryName, ping, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY);
        
        this.previousNames.put(entryName, entry);
        this.previousPings.put(entryName, ping);
    }

    private void createAndAddMember(String name, String member) {
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod("$" + name, "", "", Collections.singletonList(member), 0);
        scoreboardTeamAdd.sendToPlayer(player);
    }

    private void updateScore(String score, String prefix, String suffix) {
        ScoreboardTeamPacketMod scoreboardTeamModify = new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2);
        scoreboardTeamModify.sendToPlayer(this.player);
    }

    private void updateTabList(String name, int ping, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action) {
        this.updateTabList(name, ping, TabUtils.getOrCreateProfile(name), action);
    }

    private void updateTabList(String name, int ping, GameProfile profile, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action) {
        PlayerInfoPacketMod.sendPacketMod(this.player, action, profile, ping, WorldSettings.EnumGamemode.SURVIVAL, IChatBaseComponent.ChatSerializer.a(name));
    }

    private String[] splitString(String line) {
        if(line.length() <= 16) {
            return new String[] { line, "" };
        }

        return new String[] { line.substring(0, 16), line.substring(16, line.length()) };
    }
}
