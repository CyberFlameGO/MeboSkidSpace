package secondlife.network.vituz.providers.tab;

import lombok.Getter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import secondlife.network.vituz.utilties.Color;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class TabLayout {

    private static AtomicReference<Object> TAB_LAYOUT_1_8 = new AtomicReference();
    private static AtomicReference<Object> TAB_LAYOUT_DEFAULT = new AtomicReference();
    private static String[] ZERO_VALUE_STRING = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
    private static String[] ZERO_VALUE_STRING_18 = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
    private static Map<String, TabLayout> tabLayouts = new HashMap();
    private static List<String> emptyStrings = new ArrayList();

    public static int WIDTH = 3;
    public static int HEIGHT = 20;
    private String[] tabNames;
    private int[] tabPings;
    private boolean is18;

    private TabLayout(boolean is18) {
        this(is18, false);
    }

    private TabLayout(boolean is18, boolean fill) {
        this.is18 = is18;
        this.tabNames = (is18 ? ZERO_VALUE_STRING_18.clone() : ZERO_VALUE_STRING.clone());
        this.tabPings = (is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT]);

        if(fill) {
            for(int i = 0; i < this.tabNames.length; ++i) {
                this.tabNames[i] = generateEmpty();
                this.tabPings[i] = 0;
            }
        }

        Arrays.sort(this.tabNames);
    }

    public void forceSet(int pos, String name) {
        this.tabNames[pos] = Color.translate(name);
        this.tabPings[pos] = 0;
    }

    public void forceSet(int x, int y, String name) {
        int pos = this.is18 ? (y + x * TabLayout.HEIGHT) : (x + y * TabLayout.WIDTH);

        this.tabNames[pos] = Color.translate(name);
        this.tabPings[pos] = 0;
    }

    public void set(int x, int y, String name, int ping) {
        if(!this.validate(x, y, true)) return;
        
        int pos = this.is18 ? (y + x * HEIGHT) : (x + y * WIDTH);

        this.tabNames[pos] = Color.translate(name);
        this.tabPings[pos] = ping;
    }
    
    public void set(int x, int y, String name) {
        this.set(x, y, name, 0);
    }
    
    public void set(int x, int y, Player player) {
		this.set(x, y, player.getName(), ((CraftPlayer) player).getHandle().ping);
    }

    public String getStringAt(int x, int y) {
        this.validate(x, y);
        
        int pos = this.is18 ? (y + x * HEIGHT) : (x + y * WIDTH);
        
        return this.tabNames[pos];
    }

    public int getPingAt(int x, int y) {
        this.validate(x, y);
        
        int pos = this.is18 ? (y + x * HEIGHT) : (x + y * WIDTH);
        
        return this.tabPings[pos];
    }
    
    public boolean validate(int x, int y, boolean silent) {
        if(x >= WIDTH) {
            if(!silent) {
                throw new IllegalArgumentException("x >= WIDTH (" + WIDTH + ")");
            }

            return false;
        } else {
            if(y < HEIGHT) return true;

            if(!silent) {
                throw new IllegalArgumentException("y >= HEIGHT (" + HEIGHT + ")");
            }

            return false;
        }
    }

    public boolean validate(int x, int y) {
        return this.validate(x, y, false);
    }
    
    private static String generateEmpty() {
        String colorChars = "abcdefghijpqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        
        for(int i = 0; i < 8; ++i) {
            builder.append('§').append(colorChars.charAt(new Random().nextInt(colorChars.length())));
        }
        
        String s = builder.toString();
        
        if(emptyStrings.contains(s)) return generateEmpty();
        
        emptyStrings.add(s);
        return s;
    }

    public void reset() {
        this.tabNames = (this.is18 ? ZERO_VALUE_STRING_18.clone() : ZERO_VALUE_STRING.clone());
        this.tabPings = (this.is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT]);
    }
    
    public static TabLayout create(Player player) {
        if(tabLayouts.containsKey(player.getName())) {
            TabLayout layout = tabLayouts.get(player.getName());
          
            layout.reset();
            return layout;
        }

        tabLayouts.put(player.getName(), new TabLayout(TabUtils.is18(player)));

        return tabLayouts.get(player.getName());
    }

    public static void remove(Player player) {
        tabLayouts.remove(player.getName());
    }

    public static TabLayout createEmpty(Player player) {
        if(TabUtils.is18(player)) {
            return getTabLayout1_8();
        }
        
        return getTabLayoutDefault();
    }

    public static TabLayout getTabLayout1_8() {
        Object value = TAB_LAYOUT_1_8.get();
        
        if(value == null) {
            synchronized(TAB_LAYOUT_1_8) {
                value = TAB_LAYOUT_1_8.get();

                if(value == null) {
                	TabLayout actualValue = new TabLayout(true, true);

                	value = ((actualValue == null) ? TAB_LAYOUT_1_8 : actualValue);

                    TAB_LAYOUT_1_8.set(value);
                }
            }
        }
        
		return (TabLayout) ((value == TAB_LAYOUT_1_8) ? null : value);
    }

    public static TabLayout getTabLayoutDefault() {
        Object value = TAB_LAYOUT_DEFAULT.get();
       
        if(value == null) {
            synchronized(TAB_LAYOUT_DEFAULT) {
                value = TAB_LAYOUT_DEFAULT.get();

                if(value == null) {
                    TabLayout actualValue = new TabLayout(false, true);

                    value = ((actualValue == null) ? TAB_LAYOUT_DEFAULT : actualValue);

                    TAB_LAYOUT_DEFAULT.set(value);
                }
            }
        }
        
		return (TabLayout) ((value == TAB_LAYOUT_DEFAULT) ? null : value);
    }
}
