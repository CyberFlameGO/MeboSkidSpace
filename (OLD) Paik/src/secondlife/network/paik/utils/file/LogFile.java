package secondlife.network.paik.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import secondlife.network.paik.Paik;

public class LogFile {
	
	private File file;
	private String name;
	private List<String> lines = new ArrayList<String>();

	public LogFile(JavaPlugin plugin, String path, String name) {
		this.file = new File(Paik.getInstance().getDataFolder() + path, name + ".txt");
		try {
			this.file.createNewFile();
		} catch (IOException e) {
		}
		this.name = name;

		readTxtFile();
	}

	public void clear() {
		this.lines.clear();
	}

	public void addLine(String line) {
		this.lines.add(line);
	}

	public void write() {
		try {
			FileWriter fw = new FileWriter(this.file, false);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String line : this.lines) {
				bw.write(line);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readTxtFile() {
		this.lines.clear();
		try {
			FileReader fr = new FileReader(this.file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				this.lines.add(line);
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return this.name;
	}

	public String getText() {
		String text = "";
		for (int i = 0; i < this.lines.size(); i++) {
			String line = (String) this.lines.get(i);

			text = text + line + (this.lines.size() - 1 == i ? "" : "\n");
		}
		return text;
	}

	public List<String> getLines() {
		return this.lines;
	}
}