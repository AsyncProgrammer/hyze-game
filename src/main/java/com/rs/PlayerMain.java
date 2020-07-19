package com.rs;

import com.rs.utils.Utils;

public class PlayerMain {
	
	private static String[] latestNews = { 
		"Don't forget to ::vote for some extra cash or items!",
		"Remeber to update your highscores by typing ::hs!",
		"Donate to help keep the server alive and help pay for advertisements!", 
		"You can ::prestige once you get all combat skills to 99!",
		"Type ::wiki to go to our offical wiki page for guides and more!",
		"View your achievements by clicking Account Manager on the quest tab!",
		"Register on the ::forums and help keep the community alive!"
	};
	
	public static String getLatestNews() {
		return latestNews[Utils.random(latestNews.length - 1)];
	}
	
}
