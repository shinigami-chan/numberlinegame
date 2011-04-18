package com.wicam.numberlineweb.client;

import com.google.gwt.user.client.ui.Composite;

public abstract class GameView extends Composite {

	protected final String[] playerColors = {"red", "blue", "orange", "Magenta", "DarkKhaki"};
	protected int numberOfPlayers;
	
	protected GameView (int numberOfPlayers){
		this.numberOfPlayers = numberOfPlayers;
	}
}