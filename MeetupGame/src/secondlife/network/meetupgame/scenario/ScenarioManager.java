package secondlife.network.meetupgame.scenario;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.scenario.type.*;
import secondlife.network.meetupgame.utilities.Manager;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Marko on 11.06.2018.
 */
public class ScenarioManager extends Manager {

	@Getter private static Set<Scenario> scenarios = new HashSet<>();

	public ScenarioManager(MeetupGame plugin) {
		super(plugin);

		new BowlessScenario();
		new DefaultScenario();
		new DoNotDisturbScenario();
		new FirelessScenario();
		new NoCleanScenario();
		new RodlessScenario();
		new TimeBombScenario();
	}

	public static ItemStack getScenarioItem(Scenario scenario) {
		ItemStack item = new ItemStack(scenario.getMaterial());
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(Color.translate("&a" + scenario.getName()));

		List<String> lore = new ArrayList<>();

		lore.add(" ");
		lore.add(Color.translate("&eVotes: " + MeetupGame.getInstance().getVoteManager().getScenarioVotes().get(scenario.getName())));
		lore.add(Color.translate("&eDescription"));
		lore.add(" ");

		for(String text : scenario.getFeatures()) {
			lore.add(Color.translate("&9&l" + Msg.KRUZIC +"&9 " + text));
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static Scenario getByScenario(String name) {
		for(Scenario scenario : scenarios) {
			if(name.equalsIgnoreCase(scenario.getName())) {
				return scenario;
			}
		}

		return null;
	}
}
