package com.rs.game.player.dialogues.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.custom.TitleHandler;
import com.rs.game.player.content.interfaces.PvmRewards;
import com.rs.game.player.content.interfaces.RunePortal;
import com.rs.game.player.dialogues.Dialogue;

public class AccountManager extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", 
				"View Account Info", 
				"View Achievements",
				"* Removed *",
				"View Title List",
				"<col=FF0000>Change Difficulty</col>");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_1) {
				end();
				RunePortal.sendAccountInfo(player);
			} else if (componentId == OPTION_2) {
				end();
				player.getGoals().openGoalMenu();
			} else if (componentId == OPTION_3) {
				end();
				
			} else if (componentId == OPTION_4) {
				end();
				PvmRewards.openInterface(player);
			} else if (componentId == OPTION_5) {
				openMenu(player);
				sendOptionsDialogue("Select an Option", 
						"Very Easy", 
						"Easy",
						"Normal",
						"Hard",
						"Extreme");
				stage = 2;
			}
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				end();
				changeDifficulty(player, 1);
			} else if (componentId == OPTION_2) {
				end();
				changeDifficulty(player, 2);
			} else if (componentId == OPTION_3) {
				end();
				changeDifficulty(player, 3);
			} else if (componentId == OPTION_4) {
				end();
				changeDifficulty(player, 4);
			} else if (componentId == OPTION_5) {
				end();
				changeDifficulty(player, 5);
			}
		}
		
	}

	public static String[] diffs = { "Super Easy", "Easy", "Normal", "Hard", "Extreme" };
	
	public static boolean changeDifficulty(Player player, int difficulty) {
		if (difficulty == player.getDifficulty()) {
			player.sendMessage("You're already on "+diffs[difficulty - 1]+" difficulty!");
			return false;
		}
		
		if (difficulty > player.getDifficulty() && player.allowChange() == false) {
			player.sendMessage("You are only allowed to decrease your Difficulty Level.");
			return false;
		}
		
		if (player.allowChange() == true)
			player.setAllowChange(false);
		
		player.setDifficulty(difficulty);
		player.sendMessage("You have set your difficulty to "+diffs[difficulty - 1]+".");
		return true;
	}
	
	public static void openMenu(Player player) {
		player.getInterfaceManager().sendInterface(1245);
		player.getPackets().sendIComponentText(1245, 330, "Difficulty Infomation");
		player.getPackets().sendIComponentText(1245, 13, "<u=000000><shad=000000><col=00FF00>Super Easy");
		player.getPackets().sendIComponentText(1245, 14, "Exp: x140, Drops: -10%, DIoD: Disabled");
		player.getPackets().sendIComponentText(1245, 15, "<u=000000><shad=000000><col=FFEE00>Easy");
		player.getPackets().sendIComponentText(1245, 16, "Exp: x110, Drops: -5%, DIoD: Disabled");
		player.getPackets().sendIComponentText(1245, 17, "<u=000000><shad=000000><col=FFAA00>Normal");
		player.getPackets().sendIComponentText(1245, 18, "Exp: x80, Drops: n/a, DIoD: Disabled");
		player.getPackets().sendIComponentText(1245, 19, "<u=000000><shad=000000><col=FF6600>Hard");
		player.getPackets().sendIComponentText(1245, 20, "Exp: x50, Drops: +5%, DIoD: Enabled");
		player.getPackets().sendIComponentText(1245, 21, "<u=000000><shad=000000><col=FF0000>Extreme");
		player.getPackets().sendIComponentText(1245, 22, "Exp: x20, Drops: +10%, DIoD: Enabled");
		player.getPackets().sendIComponentText(1245, 23, "(<col=0000FF>DIoD = Drop Items on Death</col>)");
		return;
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
