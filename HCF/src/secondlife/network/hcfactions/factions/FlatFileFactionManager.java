package secondlife.network.hcfactions.factions;

import org.bukkit.configuration.MemorySection;
import secondlife.network.hcfactions.HCF;
import secondlife.network.hcfactions.factions.type.system.RoadFaction;
import secondlife.network.hcfactions.factions.type.system.SpawnFaction;
import secondlife.network.vituz.utilties.Msg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlatFileFactionManager extends AbstractFactionManager {

    public FlatFileFactionManager(HCF plugin) {
        super(plugin);
    }
    
    @Override
    public void reloadFactionData() {
        factionNameMap.clear();

        Object object = HCF.getInstance().getFactions().get("factions");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;

            for (String factionName : section.getKeys(false)) {
                Object next = HCF.getInstance().getFactions().get(section.getCurrentPath() + '.' + factionName);

                if(next instanceof Faction) {
                    cacheFaction((Faction)next);
                }
            }
        } else if(object instanceof List) {
            List<?> list = (List<?>)object;
            for(Object next2 : list) {
                if(next2 instanceof Faction) {
                    cacheFaction((Faction)next2);
                }
            }
        }

        Set<Faction> adding = new HashSet<>();

        if(!factionNameMap.containsKey("Road")) {
            adding.add(new RoadFaction());
        }

        if(!factionNameMap.containsKey("Spawn")) {
            adding.add(new SpawnFaction());
        }

        for(Faction added : adding) {
            cacheFaction(added);
            Msg.sendMessage("&aAdded &2" + added.getName() + " &afaction.");
        }
    }

    @Override
    public void saveFactionData() {
        HCF.getInstance().getFactions().set("factions", new ArrayList<>(factionUUIDMap.values()));
        HCF.getInstance().getFactions().save();
    }
}
