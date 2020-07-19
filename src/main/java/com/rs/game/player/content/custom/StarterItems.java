package com.rs.game.player.content.custom;

import com.rs.game.player.Player;

public class StarterItems {

	public static final int FIGHTER = 1;
	public static final int ARCHER = 2;
	public static final int MAGICIAN = 3;
	
	public static void giveStarter(Player player, int classId) {
		if (classId == FIGHTER) {
			player.setClassName("Fighter");
			starterSet(player, FIGHTER);
		}
		
		if (classId == ARCHER) {
			player.setClassName("Archer");
			starterSet(player, ARCHER);
		}
		
		if (classId == MAGICIAN) {
			player.setClassName("Magician");
			starterSet(player, MAGICIAN);
		}
		
		player.getInventory().addItem(995, 1000000);
		player.getInventory().addItem(22340, 1);
		player.getInventory().addItem(1856, 1);
		player.getInventory().refresh();
		player.getInterfaceManager().closeChatBoxInterface();
		player.getPackets().sendGameMessage("Your have chosen the combat class: "+player.getClassName()+".");
	}
	
	
	public static void starterSet(Player player, int set) {
		switch (set) {
		
			case FIGHTER:
				player.getInventory().addItem(1079, 1);
				player.getInventory().addItem(1093, 1);
				player.getInventory().addItem(1127, 1);
				player.getInventory().addItem(1163, 1);
				player.getInventory().addItem(13003, 1);
				player.getInventory().addItem(4131, 1);
				player.getInventory().addItem(14642, 1);
				player.getInventory().addItem(1052, 1);
				player.getInventory().addItem(1333, 1);
				player.getInventory().addItem(380, 100);
				break;
				
			case ARCHER:
				player.getInventory().addItem(1065, 1);
				player.getInventory().addItem(1099, 1);
				player.getInventory().addItem(1135, 1);
				player.getInventory().addItem(2577, 1);
				player.getInventory().addItem(2581, 1);
				player.getInventory().addItem(861, 1);
				player.getInventory().addItem(14642, 1);
				player.getInventory().addItem(892, 250);
				player.getInventory().addItem(380, 100);
				break;
				
			case MAGICIAN:
				player.getInventory().addItem(4089, 1);
				player.getInventory().addItem(4091, 1);
				player.getInventory().addItem(4093, 1);
				player.getInventory().addItem(4095, 1);
				player.getInventory().addItem(4097, 1);
				player.getInventory().addItem(4675, 1);
				player.getInventory().addItem(14642, 1);
				player.getInventory().addItem(554, 250);
				player.getInventory().addItem(555, 250);
				player.getInventory().addItem(556, 250);
				player.getInventory().addItem(557, 250);
				player.getInventory().addItem(558, 250);
				player.getInventory().addItem(559, 250);
				player.getInventory().addItem(380, 100);
				break;
		}
	}
	
}
