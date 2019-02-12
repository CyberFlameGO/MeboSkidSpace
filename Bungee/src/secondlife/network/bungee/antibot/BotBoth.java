package secondlife.network.bungee.antibot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BotBoth {

	public static ArrayList<String> starts = new ArrayList<String>();
	public static ArrayList<String> ends = new ArrayList<String>();
	public static ArrayList<String> nicks = new ArrayList<String>();
	public static ArrayList<String> pings = new ArrayList<String>();
	public static ArrayList<String> players = new ArrayList<String>();
	public static ArrayList<BotAttack> attacks = new ArrayList<BotAttack>();
	
	public static File f;
	public static File Log;
	public static File nickfile;
	public static int joins;
	public static int timeout;

	public static boolean isFakeNickname(String name) {
		if(starts(name) != null) {
			name = name.substring(starts(name).length());
			
			if(starts(name) != null) {
				name = name.substring(starts(name).length());
				
				for(String s : BotBoth.ends) {
					if(name.equals(s)) return true;
				}
			}
		}
		
		return false;
	}

	private static String starts(String what) {
		for(String s : BotBoth.starts) {
			if(what.startsWith(s)) return s;
		}
		
		return null;
	}

	public static String getTime() {
		LocalTime lt = LocalTime.now();
		String h;
		
		if(lt.getHour() < 10) {
			h = "0" + lt.getHour();
		} else {
			h = new StringBuilder(String.valueOf(lt.getHour())).toString();
		}
		
		String m;
		
		if(lt.getMinute() < 10) {
			m = "0" + lt.getMinute();
		} else {
			m = new StringBuilder(String.valueOf(lt.getMinute())).toString();
		}
		
		String s;
		
		if(lt.getSecond() < 10) {
			s = "0" + lt.getSecond();
		} else {
			s = new StringBuilder(String.valueOf(lt.getSecond())).toString();
		}
		
		String t = String.valueOf(String.valueOf(h)) + ":" + m + ":" + s;
		
		return t;
	}

	public static void log(String name, String IP) {
		try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(BotBoth.Log, true));
			LocalDate date = LocalDate.now();
			
			buf.write(String.valueOf(String.valueOf(date.getDayOfMonth())) + "." + date.getMonth() + "." + date.getYear() + " " + getTime() + " - " + name + " - " + IP);
			buf.write(System.getProperty("line.separator"));
			buf.close();
		} catch (Exception e) {}
	}

	public static void load(File file) {
		file.mkdirs();
		
		BotBoth.f = new File(file, "players.yml");
		BotBoth.Log = new File(file, "Connections.log");
		BotBoth.nickfile = new File(file, "nicks.txt");
		
		if(!BotBoth.Log.exists()) {
			try {
				BotBoth.Log.createNewFile();
			} catch (Exception e) {}
		}
		
		if(!BotBoth.f.exists()) {
			try {
				BotBoth.f.createNewFile();
			} catch (Exception e) {}
		} try {
			URL website = new URL("http://craftplex.eu/download/nicks.txt");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream("plugins/AntiBot/nicks.txt");
			fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (Exception ex3) {}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(BotBoth.nickfile));
			
			while(true) {
				String line = br.readLine();
				
				if(line == null || line.isEmpty()) break;
				
				BotBoth.nicks.add(line);
			}
			
			br.close();
		} catch (Exception e) {}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(BotBoth.f));
			
			while(true) {
				String line = br.readLine();
				
				if(line == null || line.isEmpty()) break;
				
				BotBoth.players.add(line);
			}
			
			br.close();
		} catch (Exception ex5) {}
		
		BotBoth.nickfile.delete();
		
		BotBoth.starts.add("_Itz");
		BotBoth.starts.add("Actor");
		BotBoth.starts.add("Beach");
		BotBoth.starts.add("Build");
		BotBoth.starts.add("Craft");
		BotBoth.starts.add("Crazy");
		BotBoth.starts.add("Elder");
		BotBoth.starts.add("Games");
		BotBoth.starts.add("Hello");
		BotBoth.starts.add("Hyder");
		BotBoth.starts.add("Hydra");
		BotBoth.starts.add("Hydro");
		BotBoth.starts.add("Hyper");
		BotBoth.starts.add("Kills");
		BotBoth.starts.add("Nitro");
		BotBoth.starts.add("Plays");
		BotBoth.starts.add("Slime");
		BotBoth.starts.add("Super");
		BotBoth.starts.add("Tower");
		BotBoth.starts.add("Worms");
		
		BotBoth.ends.add("11");
		BotBoth.ends.add("50");
		BotBoth.ends.add("69");
		BotBoth.ends.add("99");
		BotBoth.ends.add("HD");
		BotBoth.ends.add("LP");
		BotBoth.ends.add("XD");
		BotBoth.ends.add("YT");
	}

	public static int getLength(List<String> list) {
		for(int i = 1; i < 17; ++i) {
			int num = 0;
			
			for(String s : list) {
				if(s.length() == i) ++num;
			}
			
			if(num > 2) return i;
		}
		
		return 0;
	}

	public static String getNickType(List<String> list) {
		int nicks = 0;
	
		for(String s : list) {
			if(getNickType(s).equals("nicks")) {
				++nicks;
			}
		}
		
		if(nicks > 2) {
			return "nicks";
		}
		
		return "null";
	}

	public static String getNickType(String name) {
		if(BotBoth.nicks.contains(name)) {
			return "nicks";
		}
		
		return "null";
	}

	public static void addPlayer(String p) {
		if(!BotBoth.players.contains(p)) {
			try {
				BufferedWriter buf = new BufferedWriter(new FileWriter(BotBoth.f, true));
				
				buf.write(p);
				buf.write(System.getProperty("line.separator"));
				
				buf.close();
			} catch (Exception e) {}
			
			BotBoth.players.add(p);
		}
	}

	public static boolean isNew(String p) {
		return BotBoth.players.contains(p);
	}

	public static boolean pingedServer(String ip) {
		return BotBoth.pings.contains(ip);
	}

	public static void cancelAttack(long name) {
		for(BotAttack a : BotBoth.attacks) {
			if(a.getName() == name) BotBoth.attacks.remove(a);
		}
		
		if(BotBoth.attacks.isEmpty()) BotBoth.pings.clear();
	}
}
