package secondlife.network.victions.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import secondlife.network.victions.Victions;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.command.Command;
import secondlife.network.vituz.utilties.command.param.Parameter;

/**
 * Created by Marko on 14.07.2018.
 */
public class DonatorCommands {

    private static Victions plugin = Victions.getInstance();

    @Command(names = {"jellylegs", "jl", "jellyl", "jlegs"}, permissionNode = "secondlife.jellylegs")
    public static void handleJellyLegs(Player player) {
        plugin.getPlayerManager().handleToggleJellyLegs(player);
    }

    @Command(names = {"nightvision", "nv", "nightv", "nvision"}, permissionNode = "secondlife.jellylegs")
    public static void handleNightVision(Player player) {
        plugin.getPlayerManager().handleToggleNightVision(player);
    }

    @Command(names = {"tntcraft"}, permissionNode = "secondlife.tntcraft")
    public static void handleTNTCraft(Player player) {
        plugin.getFactionsManager().handleCraftTNT(player);
    }

    @Command(names = {"harvesterhoe"}, permissionNode = "secondlife.op")
    public static void handleHarvesterhoe(CommandSender sender, @Parameter(name = "name") Player target) {
        plugin.getPlayerManager().handleGiveHarvesterhoe(sender, target);
    }

    @Command(names = {"infusion"}, permissionNode = "secondlife.op")
    public static void handleInfusion(CommandSender sender, @Parameter(name = "name") Player target, @Parameter(name = "type") String type) {
        plugin.getPlayerManager().handleGiveInfusion(sender, target, type);
    }

    @Command(names = {"factionfly", "ffly", "facfly"}, permissionNode = "secondlife.factionfly")
    public static void handleFactionFly(Player player) {
        plugin.getPlayerManager().handleToggleFactionFly(player);
    }

    @Command(names = {"potionstack", "ps", "pstack", "potions"}, permissionNode = "secondlife.potionstack")
    public static void handlePotionStack(Player player) {
        plugin.getPlayerManager().handlePotionStack(player);
    }

    @Command(names = {"sellwand", "sellwands"}, permissionNode = "secondlife.op")
    public static void handleSellwand(CommandSender sender) {
        sender.sendMessage(Color.translate("&cUsage: /sellwands give <player> <uses>"));
    }

    @Command(names = {"sellwand give", "sellwands give"}, permissionNode = "secondlife.op")
    public static void handleSellwand(CommandSender sender, @Parameter(name = "name") Player target, @Parameter(name = "uses") int uses) {
        plugin.getPlayerManager().handleSellwand(sender, target, uses);
    }

    @Command(names = {"chunkbuster give", "buster give"}, permissionNode = "secondlife.op")
    public static void handleChunkBuster(CommandSender sender, @Parameter(name = "name") Player target, @Parameter(name = "amount") int amount) {
        plugin.getPlayerManager().handleChunkBuster(sender, target, amount);
    }
}
