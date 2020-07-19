package com.guardian;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.rs.game.World;
import com.rs.utils.Utils;

/**
 * @author KingFox
 */
public class ItemManager implements Serializable {
	private static final long serialVersionUID = -9028730399175475751L;
	
	private static final transient int MAX_SIZE = 30000;
	
	public int itemId;
	public int value;
	public String itemName;
	
	public static ItemManager[] values;
	
	public ItemManager(int itemid, int value, String name) {
		this.itemId = itemid;
		this.value = value;
		this.itemName = name;
	}
	
	private static final String PATH = "data/ItemPrices.ser";
	
	public static void inits() {
		File file = new File(PATH);
		if (file.exists()) {
			try {
				values = (ItemManager[]) loadFile(file);
				return;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		values = new ItemManager[MAX_SIZE];
	}

	public static final Object loadFile(File f) throws IOException, ClassNotFoundException {
		if (!f.exists())
			return null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}
	
	public static final void storeFile(Serializable o, File f) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}
	
	public static void modifyList() {
		values = new ItemManager[MAX_SIZE];
		try {
			BufferedReader in = new BufferedReader(new FileReader("data/pricelist.txt"));
			for(String line; (line = in.readLine()) != null; ) {
				String[] split = line.split(" = ");
				int itemId = Integer.parseInt(split[0]);
				String name = split[1];
				int value = Integer.parseInt(split[2]);
				values[itemId] = new ItemManager(itemId, value, name);
			}
			in.close();
			save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final void save() {
		try {
			storeFile(values, new File(PATH));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static boolean update(int itemId, int newPrice) {
		int old = values[itemId].value;
		String name = values[itemId].itemName;
		values[itemId].value = newPrice;
		boolean increase = newPrice > old;
		int dif = increase ? (newPrice - old) : (old - newPrice);
		String diff = increase == true ? "(<col=FF0000>up "+dif+"</col>)" : "(<col=00FF00>down "+dif+"</col>)";
		World.sendWorldMessage("<img=7><shad=444444>[<col=FF0000>Update</col>] The value of <col=FF0000>"+name+"</col> is now <col=FF0000>"+Utils.formatNumber(newPrice)+"</col> gp. "+diff+"", false);
		save();
		return true;
	}
	
	public static int getPrice(int itemId) {
		return values[itemId].value;
	}
	
}
