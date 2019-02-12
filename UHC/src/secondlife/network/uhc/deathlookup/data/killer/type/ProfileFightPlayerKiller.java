package secondlife.network.uhc.deathlookup.data.killer.type;

import lombok.Getter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import secondlife.network.uhc.deathlookup.data.ProfileFightEffect;
import secondlife.network.uhc.deathlookup.data.killer.ProfileFightKiller;

import java.util.ArrayList;
import java.util.List;

public class ProfileFightPlayerKiller extends ProfileFightKiller {

    @Getter private final String name;
    @Getter private final ItemStack[] contents, armor;
    @Getter private final double health, hunger;
    @Getter private final int ping;
    @Getter private final List<ProfileFightEffect> effects;

    public ProfileFightPlayerKiller(Player player) {
        this(player.getName(), ((CraftPlayer)player).getHandle().ping, player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getHealth(), player.getFoodLevel(), new ArrayList<>());

        for (PotionEffect effect : player.getActivePotionEffects()) {
            effects.add(new ProfileFightEffect(effect));
        }
    }

    public ProfileFightPlayerKiller(String name, int ping, ItemStack[] contents, ItemStack[] armor, double health, double hunger, List<ProfileFightEffect> effects) {
        super(EntityType.PLAYER, name);

        this.ping = ping;
        this.name = name;
        this.contents = contents;
        this.armor = armor;
        this.health = health;
        this.hunger = hunger;
        this.effects = effects;
    }

}
