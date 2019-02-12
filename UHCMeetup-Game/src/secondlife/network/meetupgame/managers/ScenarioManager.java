package secondlife.network.meetupgame.managers;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import secondlife.network.meetupgame.MeetupGame;
import secondlife.network.meetupgame.scenario.Scenario;
import secondlife.network.meetupgame.scenario.type.*;
import secondlife.network.meetupgame.utilties.Manager;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.item.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class ScenarioManager extends Manager {

	@Getter
	public static List<Scenario> scenarios = new ArrayList<>();

	public ScenarioManager(MeetupGame plugin) {
		super(plugin);

		new BowlessScenario();
		new DefaultScenario();
		new DoNotDisturbScenario();
		new FirelessScenario();
		new HorselessScenario();
		new LongShotsScenario();
		new NineSlotScenario();
		new NoCleanScenario();
		new NoFallDamageScenario();
		new RodlessScenario();
		new SoupScenario();
		new SwitcherooScenario();
		new TimeBombScenario();
		//new WebCageScenario();
	}

	public ItemStack getItem(Scenario scenario) {
		ItemBuilder builder = new ItemBuilder(scenario.getMaterial());
		builder.name((scenario.isEnabled() ? "&a" : "&c") + scenario.getName());

		List<String> lore = new ArrayList<>();

		lore.add("");
		lore.add("&eVotes: &a" + MeetupGame.getInstance().getVoteManager().getVotes().get(scenario));
		lore.add("&eDescription");
		lore.add("");
		for(String text : scenario.getFeatures()) {
			lore.add(" &9&l" + Msg.KRUZIC +"&9 " + text);
		}

		builder.lore(lore);

		return builder.build();
	}

	public static Scenario getByName(String name) {
		for(Scenario scenario : scenarios) {
			if(name.equalsIgnoreCase(scenario.getName())) {
				return scenario;
			}
		}

		return null;
	}
}
