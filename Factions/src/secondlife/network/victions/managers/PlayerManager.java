package secondlife.network.victions.managers;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.victions.Victions;
import secondlife.network.victions.player.FactionsData;
import secondlife.network.victions.utilities.GlowEnchantment;
import secondlife.network.victions.utilities.Manager;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.*;

/**
 * Created by Marko on 14.07.2018.
 */

@Getter
public class PlayerManager extends Manager {

    private Map<UUID, Long> tagged = new HashMap<>();

    public PlayerManager(Victions plugin) {
        super(plugin);
    }

    public void handleToggleJellyLegs(Player player) {
        FactionsData data = FactionsData.getByName(player.getName());
        data.setJellyLegs(!data.isJellyLegs());
        player.sendMessage(Color.translate("&eYou have " + (data.isJellyLegs() ? "&aEnabled" : "&cDisabled") +
                " &ejelly legs."));
    }

    public void handleToggleNightVision(Player player) {
        FactionsData data = FactionsData.getByName(player.getName());
        data.setNightVision(!data.isNightVision());
        player.sendMessage(Color.translate("&eYou have " + (data.isNightVision() ? "&aEnabled" : "&cDisabled") +
                " &enight vision."));

        if(data.isNightVision()) {
            if(!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
            }
        } else {
            if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
    }

    public void handleGiveHarvesterhoe(CommandSender sender, Player target) {
        sender.sendMessage(Color.translate("&eYou gave &dHarvester Hoe &eto &d" + target.getName() + "&e."));
        target.sendMessage(Color.translate("&eYou have received &dHarvester Hoe&e."));

        Enchantment enchantment = GlowEnchantment.getGlow();
        target.getInventory().addItem(new ItemBuilder(Material.DIAMOND_HOE).name("&dHarvester Hoe").lore("&fYou don't need to &dpickup sugarcane").enchantment(enchantment).build());
    }

    public void handleGiveInfusion(CommandSender sender, Player target, String type) {
        if(!type.toLowerCase().contains("5x5") || !type.toLowerCase().contains("3x3")) {
            sender.sendMessage(Color.translate("&cPlease choose &l5x5&c or &l3x3&c!"));
            return;
        }

        sender.sendMessage(Color.translate("&eYou gave &dInfusion Pickaxe " + type + " &eto &d" + target.getName() + "&e."));
        target.sendMessage(Color.translate("&eYou have received &dInfusion Pickaxe " + type + "&e."));

        Enchantment DIG_SPEED = Enchantment.DIG_SPEED;
        Enchantment DURABILITY = Enchantment.DURABILITY;

        target.getInventory().addItem(new ItemBuilder(Material.DIAMOND_HOE).enchantment(DIG_SPEED, 5).enchantment(DURABILITY, 3).name("&dInfusion Pickaxe").lore("&fBreak blocks in &d" + (type.equals("5x5") ? "5x5" : "3x3")).build());
    }

    public void handleToggleFactionFly(Player player) {
        FactionsData data = FactionsData.getByName(player.getName());

        if(isSpawnTagActive(player)) {
            player.sendMessage(Color.translate("&cYou can't use this command while &lSpawn Tag&c is active."));
            return;
        }

        FPlayer factionPlayer = FPlayers.getInstance().getByPlayer(player);

        if(data.isFactionFly()) {
            data.setFactionFly(false);
            player.setAllowFlight(false);
            player.setFlying(false);
        } else {
            if(factionPlayer.getFaction().isWilderness() && !factionPlayer.isInOwnTerritory()) {
                player.sendMessage(Color.translate("&eYou must be in your &dfaction's &eteritory."));
                return;
            }

            data.setFactionFly(true);
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        player.sendMessage(Color.translate("&eYou have " + (data.isFactionFly() ? "&aEnabled" : "&cDisabled") +
                " &efaction fly."));
    }

    public void handlePotionStack(Player player) {
        Map<ItemStack, Integer> potionMap = new HashMap<>();

        for(int i = 0; i < player.getInventory().getSize(); ++i) {
            ItemStack item = player.getInventory().getItem(i);

            if(item != null && item.getType() == Material.POTION && !Potion.fromItemStack(item).isSplash() && item.getDurability() != 0) {
                ItemStack contains = null;

                for(ItemStack stack : potionMap.keySet()) {
                    if(stack.getDurability() == item.getDurability() && stack.getItemMeta().equals(item.getItemMeta())) {
                        contains = stack;
                        break;
                    }
                }

                if(contains != null) {
                    potionMap.put(contains, potionMap.get(contains) + item.getAmount());
                } else {
                    potionMap.put(item, item.getAmount());
                }
            }
        }

        if(potionMap.isEmpty()) {
            player.sendMessage(Color.translate("&cYou don't have any potions to stack."));
            return;
        }

        ItemStack[] items = player.getInventory().getContents();

        for(int j = 0; j < items.length; ++j) {
            if(items[j] != null
                    && items[j].getType() == Material.POTION
                    && !Potion.fromItemStack(items[j]).isSplash()
                    && items[j].getDurability() != 0) {

                player.getInventory().clear(j);
            }
        }

        potionMap.entrySet().forEach(entry -> {
            ItemStack stack = entry.getKey();
            stack.setAmount(entry.getValue());

            player.getInventory().addItem(stack);
        });

        player.updateInventory();
        player.sendMessage(Color.translate("&eYour potions are now stacked."));
    }

    public void handleSellwand(CommandSender sender, Player target, int uses) {
        ItemBuilder builder = new ItemBuilder(Material.DIAMOND_HOE);
        Enchantment enchantment = GlowEnchantment.getGlow();
        builder.enchantment(enchantment);
        builder.name("&dSell Wand");

        List<String> lore = new ArrayList<>();
        lore.add("&fRight click chest with");
        lore.add("&fthis item to sell it's contents.");

        if(uses > 0) {
            lore.add("");
            lore.add("&fUses: &d" + uses);
        }

        builder.lore(lore);

        target.getInventory().addItem(builder.build());

        sender.sendMessage(Color.translate("&eYou gave &dSell Wand &eto &d" + target.getName() + "&e."));
        target.sendMessage(Color.translate("&eYou have received &dSell Wand&e."));
    }

    public void handleChunkBuster(CommandSender sender, Player target, int amount) {
        ItemBuilder builder = new ItemBuilder(Material.DIAMOND_HOE);

        builder.amount(amount);
        builder.name("&dChunk Buster");
        builder.lore("&fPlace it down to start busting chunk.");

        sender.sendMessage(Color.translate("&eYou gave &dChunk Buster &eto &d" + target.getName() + "&e."));
        target.sendMessage(Color.translate("&eYou have received &dChunk Buster&e."));
    }

    public boolean isSpawnTagActive(Player player) {
        return tagged.containsKey(player.getUniqueId()) && System.currentTimeMillis() < tagged.get(player.getUniqueId());
    }

    public void applyTagger(Player tagger, Player other) {
        if(!tagged.containsKey(tagger.getUniqueId())) {
            tagger.sendMessage(Color.translate("&eYou have spawn tagged &d" + other.getName() + " &efor &d20 seconds&e."));
            tagger.sendMessage(Color.translate("&cIf you logout while &lSpawn Tag&c is active you will die!"));
        }

        this.disableFlyAndInvisibility(tagger);

        tagged.put(tagger.getUniqueId(), System.currentTimeMillis() + (20 * 1000));
    }

    public void applyOther(Player tagger, Player other) {
        if(!tagged.containsKey(other.getUniqueId())) {
            other.sendMessage(Color.translate("&eYou have spawn tagged &d" + tagger.getName() + " &efor &d20 seconds&e."));
            other.sendMessage(Color.translate("&cIf you logout while &lSpawn Tag&c is active you will die!"));
        }

        this.disableFlyAndInvisibility(other);

        tagged.put(other.getUniqueId(), System.currentTimeMillis() + (20 * 1000));
    }

    public long getSpawnTagMillisecondsLeft(Player player) {
        if(tagged.containsKey(player.getUniqueId())) {
            return Math.max(tagged.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
        }

        return 0L;
    }

    private void disableFlyAndInvisibility(Player player) {
        if(player.isFlying()) {
            player.setFlying(false);
        }

        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
}
