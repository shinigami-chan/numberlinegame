package com.wicam.numberlineweb.client.BuddyNumber;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;
import com.wicam.numberlineweb.client.GameCoordinator;
import com.wicam.numberlineweb.client.GameState;
import com.wicam.numberlineweb.client.GameTypeSelector;
import com.wicam.numberlineweb.client.NumberLineWeb;
import com.wicam.numberlineweb.client.Player;
import com.wicam.numberlineweb.client.BuddyNumber.BuddyNumberGameCommunicationServiceAsync;
import com.wicam.numberlineweb.client.chat.ChatCommunicationServiceAsync;

public class BuddyNumberGameCoordinator extends GameCoordinator {
	
	private BuddyNumberGameController controller;
	
	
	public BuddyNumberGameCoordinator(BuddyNumberGameCommunicationServiceAsync commServ,
			ChatCommunicationServiceAsync chatCommServ, 
			Panel root, GameTypeSelector gts) {
		super(commServ, chatCommServ, root,gts);
	}
	
	@Override
	public String getGameName() {

		return "Partnerzahlen";

	}
	
	
	
	/**
	 * Initializes the coordinator
	 */
	@Override
	public void init() {

		gameSelector = new BuddyNumberGameSelector(this);
		rootPanel.add(gameSelector);

		t = new Timer() {
			@Override
			public void run() {
				update();
			}
		};

		//main loop-timer
		t.scheduleRepeating(500);
		refreshGameList();
		
		

		GWT.log("BuddyNumber game coordinator loaded.");
	}
	
	
	
	/**
	 * Open a game name 'name'. Call back will get state of opened game
	 * @param name
	 */
	@Override
	public void openGame(GameState gameState) {
		
		GWT.log("opening! in BuddyNumberGameCoord");

		this.numberOfPlayers = gameState.getMaxNumberOfPlayers();
		this.numberOfNPCs = gameState.getNumberOfMaxNPCs();
		
		gameState.setGameOpenedUserId(NumberLineWeb.USERID);
		
		((BuddyNumberGameCommunicationServiceAsync)commServ).openGame(gameState, gameOpenedCallBack);

	}
	
	
	
	/**
	 * Called after our player joined the game.
	 * @param state
	 * @param gameID
	 */
	@Override
	protected void joinedGame(int playerID, int gameID) {

		super.joinedGame(playerID, gameID);
		this.playerID = playerID;

		//construct game
		controller = new BuddyNumberGameController(this);
		
		this.view = new BuddyNumberGameView(controller, numberOfPlayers, numberOfNPCs);
		
		BuddyNumberGameView gameView = (BuddyNumberGameView) view;

		//construct an empty game-state with the given information
		BuddyNumberGameState g = new BuddyNumberGameState();
		g.setGameId(gameID);
		g.setState(-1);
		this.openGame = g;
		update();

		//clear the root panel and draw the game
		rootPanel.clear();
		rootPanel.add(gameView);
		
		if (this.numberOfPlayers > 1){
			this.addChatView();
		}
	}

	
	
	/**
	 * Called after game state was received.
	 * @param gameState The GameState to update
	 */
	@Override
	protected void updateGame(GameState gameState) {
		super.updateGame(gameState);

		BuddyNumberGameState g = (BuddyNumberGameState) gameState;
		BuddyNumberGameView gameView = (BuddyNumberGameView) view;
		//we already have the lates state
		if (g==null) return;
		
		
		switch (g.getState()) {
			//started 
		case 3:
			
			gameView.updateInfoText(g.getPlayerClickedOn(playerID));
			updateViewIngame(g, gameView);
			
			//kritischer moment, setze refreshrate nach oben
			setRefreshRate(200);
			
			break;

			//evaluation, who has won?
		case 5:
			
			updateViewIngame(g, gameView);
			setRefreshRate(1000);
			gameView.setInfoText("Alle richtigen Antworten sind weg.");
				
			break;
			
			// for synchronization
		case 6:
			commServ.updateReadyness(Integer.toString(openGame.getId()) + ":" + Integer.toString(playerID), dummyCallback);
			break;
		}
		



		openGame = g;


	}
	
	
	
	/**
	 * Updates the game view once
	 * @param g state to get data from
	 * @param gameView View to update
	 * 
	 */
	private void updateViewIngame(BuddyNumberGameState g, BuddyNumberGameView gameView) {
		gameView.drawCommunityDigits(g.getCommunityDigits());
		gameView.drawHandDigits(g.getHandDigits(), ((BuddyNumberPlayer)g.getPlayers().get(playerID-1)).getClickedOn());
		
		for (int i = 0; i < g.getPlayers().size(); i++){
			gameView.setPoints(i+1, g.getPlayerPoints(i+1),g.getPlayerName(i+1));
		}
	}
	
	
	
	/**
	 * Sets user name in chat and sets points
	 * info text: "Das Spiel beginnt in wenigen Sekunden"
	 */
	@Override
	protected void handleAwaitingStartState(GameState g){
		BuddyNumberGameView gameView = (BuddyNumberGameView) view;
		setRefreshRate(1000);
		if (this.numberOfPlayers > 1)
			chatC.setUserName(g.getPlayerName(this.playerID));
		for (int i = 0; i < g.getPlayers().size(); i++)
			gameView.setPoints(i+1, 0, g.getPlayerName(i+1));
		int notReady = 0;
		for (Player p : g.getPlayers()) {
			notReady += (p.isReady()) ? 0 : 1;
		}
		gameView.setInfoText("Warte auf " + notReady + " Spieler...");
		//gameView.setInfoText("Das Spiel beginnt in wenigen Sekunden!");
	}
	
	
	
	/**
	 * "Warte auf Spieler..." is displayed on the view
	 * and refresh rate is increased to 2000 ms
	 */
	@Override
	protected void handleWaitingForPlayersState(){
		setRefreshRate(2000);
		((BuddyNumberGameView) view).setInfoText("Warte auf Spieler...");
	}

	
	
	/**
	 * Points are displayed and "Warte auf zweiten/andere Spieler..."
	 */
	@Override
	protected void handleWaitingForOtherPlayersState(GameState g){
		BuddyNumberGameView gameView = (BuddyNumberGameView) view;
		setRefreshRate(2000);
		for (int i = 0; i < g.getPlayers().size(); i++)
			gameView.setPoints(i+1, 0, g.getPlayerName(i+1));
		if (g.getMaxNumberOfPlayers() <= 2)
			gameView.setInfoText("Warte auf zweiten Spieler...");
		else
			gameView.setInfoText("Warte auf andere Spieler...");
	}
	
	/**
	 * Clicked at position (x,y)
	 * @param x x-position
	 * @param y y-position
	 * @param w Widget, that was clicked
	 */
	public void clickAt(Widget w, int x, int y) {
	}
	
	
	/**
	 * @param digit Digit clicked on, format: "value:idInSet:community/hand"
	 */
	public void clickAt(String digit) {
		((BuddyNumberGameCommunicationServiceAsync)commServ).clickedAt(
				Integer.toString(openGame.getId()) + ":" + Integer.toString(playerID) + ":" + digit, updateCallback);
	}
	
	

	/**
	 * Mouse was moved to (x,y)
	 * @param x x-position
	 * @param y y-position
	 * @param w Widget, that was hovered
	 */
	public void mouseMovedTo(Widget w, int x, int y) {
	}

	
	
	/**
	 * User clicked on "Start game"
	 */
	public void startButtonClicked() {
		if (!openGame.isPlayerReady(this.playerID)) {
			commServ.updateReadyness(Integer.toString(openGame.getId()) + ":" + Integer.toString(playerID), dummyCallback);
		}		
	}


}
