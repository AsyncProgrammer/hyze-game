package com.rs.game.player.dialogues.impl;

import com.rs.game.player.content.custom.StarterItems;
import com.rs.game.player.dialogues.Dialogue;

public class NewStarter extends Dialogue {

	int starter;
	int difficulty;
	
	@Override
	public void start() {
		stage = 1;
		sendEntityDialogue(IS_NPC, "Foxtrot Manager", 13768, 9827, 
			"Welcome to Foxtrot, "+player.getDisplayName()+"!",
			"Please, before you begin you will need", 
			"choose your starter set and your difficulty.",
			"You may change your difficulty at a later time.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		
		if (player.getInterfaceManager().containsScreenInter()) {
			player.getInterfaceManager().closeScreenInterface();
		}
		
		if (stage == 1) {
			sendOptionsDialogue("Pick a Starter Type",
					"I would like a Fighter's Starter",
					"I would like an Archer's Starter",
					"I would like a Magician's Starter");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				this.starter = StarterItems.FIGHTER;
				sendNext_1();
			} else if (componentId == OPTION_2) {
				this.starter = StarterItems.ARCHER;
				sendNext_1();
			}else if (componentId == OPTION_3) {
				this.starter = StarterItems.MAGICIAN;
				sendNext_1();
			}
		} else if (stage == 3) {
			sendOptionsDialogue("Select an Option", 
					"Very Easy", 
					"Easy",
					"Normal",
					"Hard",
					"Extreme");
			stage = 4;
		} else if (stage == 4) {
			if (componentId == OPTION_1) {
				this.difficulty = 1;
				sendNext_2();
			} else if (componentId == OPTION_2) {
				this.difficulty = 2;
				sendNext_2();
			} else if (componentId == OPTION_3) {
				this.difficulty = 3;
				sendNext_2();
			} else if (componentId == OPTION_4) {
				this.difficulty = 4;
				sendNext_2();
			} else if (componentId == OPTION_5) {
				this.difficulty = 5;
				sendNext_2();
			}
		} else if (stage == 5) {
			player.setDifficulty(difficulty);
			StarterItems.giveStarter(player, starter);
		}
	}
	
	public void sendNext_1() {
		sendEntityDialogue(IS_NPC, "Guardian Manager", 13768, 9827, 
				"Very good! Now all you need to do is select",
				"your difficulty. Please keep in mind that harder", 
				"difficulties have low exp, but have better drops",
				"while lower difficulties are the opposite!");
		stage = 3;
	}
	
	public void sendNext_2() {
		sendEntityDialogue(IS_NPC, "Guardian Manager", 13768, 9827, 
				"Very good! You've chosen to play on "+diffs[difficulty - 1]+",",
				"Now what are you waiting for? Go out and have some fun!", 
				"Just click the button below and everything will",
				"be given to you! We hope you enjoy your stay!");
		stage = 5;
	}
	
	public static String[] diffs = { "Super Easy", "Easy", "Normal", "Hard", "Extreme" };

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
