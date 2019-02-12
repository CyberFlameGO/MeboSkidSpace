package secondlife.network.hcfactions.handlers;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.Faction;
import secondlife.network.hcfactions.factions.FactionManager;
import secondlife.network.hcfactions.factions.FactionMember;
import secondlife.network.hcfactions.factions.FlatFileFactionManager;
import secondlife.network.hcfactions.factions.claim.ClaimHandler;
import secondlife.network.hcfactions.factions.claim.ClaimZone;
import secondlife.network.hcfactions.factions.handlers.FactionHandler;
import secondlife.network.hcfactions.factions.type.ClaimableFaction;
import secondlife.network.hcfactions.factions.type.PlayerFaction;
import secondlife.network.hcfactions.factions.type.games.CapturableFaction;
import secondlife.network.hcfactions.factions.type.system.EndPortalFaction;
import secondlife.network.hcfactions.factions.type.system.RoadFaction;
import secondlife.network.hcfactions.factions.type.system.SpawnFaction;
import secondlife.network.hcfactions.factions.utils.CaptureZone;
import secondlife.network.hcfactions.game.events.faction.KothFaction;
import secondlife.network.hcfactions.utilties.Handler;

@Getter
@Setter
public class RegisterHandler extends Handler {
	
	public static RegisterHandler instance;
	
	public FactionManager factionManager;
	public WorldEditPlugin worldEdit;
	
	public RegisterHandler(HCF plugin) {
		super(plugin);
		
		instance = this;

		new ClaimHandler(plugin);
		new FactionHandler(plugin);

		this.factionManager = new FlatFileFactionManager(plugin);

		this.enable(plugin);
	}
	
	public void enable(HCF plugin) {
		setupItems();
		
        this.worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}

	public void setupItems() {
		this.hrecipe();
		this.precipe();
		this.lrecipe();
		this.brecipe();

		this.mrecipe();
		this.gprecipe();
	}
	
	private void hrecipe() {
		ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET, 1);
		ItemMeta meta = helmet.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "Chain Helmet");
		helmet.setItemMeta(meta);

		ShapedRecipe hrecipe = new ShapedRecipe(helmet);
		hrecipe.shape("@@@", "@ @");
		hrecipe.setIngredient('@', Material.IRON_FENCE);
		Bukkit.getServer().addRecipe(hrecipe);
	}

	private void precipe() {
		ItemStack plate = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
		ItemMeta meta2 = plate.getItemMeta();
		meta2.setDisplayName(ChatColor.WHITE + "Chain Chestplate");
		plate.setItemMeta(meta2);
		ShapedRecipe precipe = new ShapedRecipe(plate);
		precipe.shape("@ @", "@@@", "@@@");
		precipe.setIngredient('@', Material.IRON_FENCE);
		Bukkit.getServer().addRecipe(precipe);
	}

	private void lrecipe() {
		ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
		ItemMeta meta3 = leggings.getItemMeta();
		meta3.setDisplayName(ChatColor.WHITE + "Chain Leggings");
		leggings.setItemMeta(meta3);
		ShapedRecipe lrecipe = new ShapedRecipe(leggings);
		lrecipe.shape("@@@", "@ @", "@ @");
		lrecipe.setIngredient('@', Material.IRON_FENCE);
		Bukkit.getServer().addRecipe(lrecipe);
	}

	private void brecipe() {
		ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
		ItemMeta meta4 = boots.getItemMeta();
		meta4.setDisplayName(ChatColor.WHITE + "Chain Leggings");
		boots.setItemMeta(meta4);

		ShapedRecipe lrecipe = new ShapedRecipe(boots);
		lrecipe.shape("   ", "@ @", "@ @");
		lrecipe.setIngredient('@', Material.IRON_FENCE);
		Bukkit.getServer().addRecipe(lrecipe);
	}
	
	private void mrecipe() {
		ItemStack helmet = new ItemStack(Material.SPECKLED_MELON, 1);
		ShapedRecipe mrecipe = new ShapedRecipe(helmet);
		mrecipe.shape("@@@", "@ @");
		mrecipe.setIngredient('@', Material.SPECKLED_MELON);
		Bukkit.getServer().addRecipe(mrecipe);
	}

	private void gprecipe() {
		ItemStack plate = new ItemStack(Material.SPECKLED_MELON, 1);

		ShapelessRecipe gprecipe = new ShapelessRecipe(plate);
		gprecipe.addIngredient(1, Material.MELON);
		gprecipe.addIngredient(1, Material.GOLD_NUGGET);
		Bukkit.getServer().addRecipe(gprecipe);
	}

	public static void hook() {
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(ClaimZone.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		//ConfigurationSerialization.registerClass(ConquestFaction.class);
		//ConfigurationSerialization.registerClass(CitadelFaction.class);
		//ConfigurationSerialization.registerClass(CitadelCapture.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		//ConfigurationSerialization.registerClass(GlowstoneFaction.class);
	}
	
	public static RegisterHandler getInstancee() {
        return instance;
    }
}
