package com.wicam.numberlineweb.client.Letris;

import java.util.HashMap;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.wicam.numberlineweb.client.GameView;
import com.wicam.numberlineweb.client.KeyboardDummy;
import com.wicam.numberlineweb.client.MobileDeviceChecker;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * View of the LeTris game.
 * @author timfissler
 *
 */

// TODO How could I draw the grid and a letter block?
// TODO Add descriptions.
// TODO How can the view be more efficient?

public class LetrisGameView extends GameView {

	private final HorizontalPanel motherPanel = new HorizontalPanel();
	private final AbsolutePanel gamePanel = new AbsolutePanel();
	private final AbsolutePanel pointsPanel = new AbsolutePanel();
	private KeyboardDummy kbd;

	protected final HTML explanationText = new HTML();
	protected final HTML feedBackText = new HTML();
//	private final HTML canvas = new HTML("<div id='canvas' style='width:600px;height:400px;border-right:solid #333 1px'></div>");
	private final HTML canvasScore = new HTML("<div id='canvas' style='width:150px;height:30px;'></div>");
	private final HTML pointsText = new HTML("<div style='font-size:30px;color:black'>Punkte</div>");
	final FlexTable playerNamesFlexTable = new FlexTable();

	protected final Button startGameButton = new Button("Spiel Starten");
	private final FocusPanel focusPanel = new FocusPanel();
	private final TextBox textBox = new TextBox();
	
	private LetrisGameCoordinates viewSize = new LetrisGameCoordinates(600, 400);
	private final DrawingArea canvas = new DrawingArea(viewSize.x, viewSize.y);
	private LetrisGameCoordinates modelSize;
	private LetrisGameCoordinateTransform transform;
	private HashMap<String, String> letter2HexColor = new HashMap<String, String>();
	private final int blockSize = 20;
	LetrisGameCoordinates playgroundSize;
	LetrisGameCoordinates playgroundOrigin = new LetrisGameCoordinates(200, 0);
	
	// TODO Add correct sound file.
//	protected Audio descriptionSound = Audio.createIfSupported();

	public LetrisGameView(int numberOfPlayers, LetrisGameController doppelungGameController, int playgroundWidth, int playgroundHeight) {
		super(numberOfPlayers, doppelungGameController);
		this.modelSize = new LetrisGameCoordinates(playgroundWidth, playgroundHeight);
		this.transform = new LetrisGameCoordinateTransform(modelSize, viewSize);
		playgroundSize = new LetrisGameCoordinates(316, 399); //transform.transformModelToView(new LetrisGameCoordinates(modelSize.x, -1));
		init();
		sinkEvents(Event.MOUSEEVENTS);
		this.initWidget(motherPanel);
	}


	private void init() {

		final LetrisGameController letrisGameController = (LetrisGameController) gameController;
		setupLetterColors();
		
		//draw everything
		gamePanel.getElement().getStyle().setPosition(Position.RELATIVE);

		startGameButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				letrisGameController.onStartButtonClick();
//				try {
//					descriptionSound.pause();
//					descriptionSound.setCurrentTime(0);
//				} catch (Exception e) {
//				}
			}
		});
		
		setExplanationText();
		gamePanel.add(explanationText);
		gamePanel.setWidgetPosition(explanationText, 0, 0);
		gamePanel.add(startGameButton);
		gamePanel.setWidgetPosition(startGameButton, 480, 350);
		gamePanel.add(canvas);
		focusPanel.setSize("600px", "400px");

		pointsPanel.add(pointsText);
		pointsPanel.setWidgetPosition(pointsText, 27, 10);
		pointsPanel.add(canvasScore);

		playerNamesFlexTable.setStyleName("playerList");
		playerNamesFlexTable.setCellPadding(5);


		pointsPanel.add(playerNamesFlexTable);

		focusPanel.addKeyDownHandler(letrisGameController);
		focusPanel.addKeyUpHandler(letrisGameController);

		motherPanel.add(gamePanel);
		motherPanel.add(pointsPanel);
		
//		if (Audio.isSupported() && descriptionSound != null) {
//			
//			// TODO Change sound to actual description of the LeTris game.
//			descriptionSound.addSource("desc/Doppelung.ogg", "audio/ogg; codecs=vorbis");
//			descriptionSound.addSource("desc/Doppelung.mp3", "audio/mpeg; codecs=MP3");
//			
//			descriptionSound.play();
//			
//		}
	}
	
	private void setupLetterColors() {
		letter2HexColor.put("A", "#F46943");
		letter2HexColor.put("B", "#87E3D5");
		letter2HexColor.put("C", "#8798D5");
		letter2HexColor.put("D", "#C02EB9");
		letter2HexColor.put("E", "#EF98D8");
		letter2HexColor.put("F", "#44B5DF");
		letter2HexColor.put("G", "#2A90AE");
		letter2HexColor.put("H", "#66C6A0");
		letter2HexColor.put("I", "#5749CB");
		letter2HexColor.put("J", "#8AE720");
		letter2HexColor.put("K", "#A39D46");
		letter2HexColor.put("L", "#964AB2");
		letter2HexColor.put("M", "#F53E88");
		letter2HexColor.put("N", "#4BA1ED");
		letter2HexColor.put("O", "#C3CE3A");
		letter2HexColor.put("P", "#3893A3");
		letter2HexColor.put("Q", "#E41029");
		letter2HexColor.put("R", "#D65D77");
		letter2HexColor.put("S", "#1DD099");
		letter2HexColor.put("T", "#ABCB92");
		letter2HexColor.put("U", "#F49699");
		letter2HexColor.put("V", "#E6A63E");
		letter2HexColor.put("W", "#494C62"); // TODO Change color.
		letter2HexColor.put("X", "#524575");
		letter2HexColor.put("Y", "#893C77");
		letter2HexColor.put("Z", "#ED8EF8"); // TODO Change color.
		letter2HexColor.put("Ä", "#E2306E");
		letter2HexColor.put("Ö", "#0A6318");
		letter2HexColor.put("Ü", "#8CD7DF"); // TODO Change color.
		letter2HexColor.put("ß", "#F4C162"); // TODO Change color.
	}
	
	protected void setExplanationText() {
		// TODO Enter description of the game.
		explanationText.setHTML("<div style='padding:5px 20px;font-size:25px'><b>LeTris - Beschreibung</b></div>" +
				"<div style='padding:5px 20px;font-size:12px'>" +
				"Enter description of the LeTris game here." +
		"</div>");
	}

	public void showWaitingForOtherPlayer(String msg){
		feedBackText.setHTML("<div style='font-size:25px'>" + msg + "</div>");
		gamePanel.add(feedBackText);
		gamePanel.setWidgetPosition(feedBackText, 150, 180);
	}
	
	/**
	 * Takes the given game state and refreshes the drawing of the playgroundSize with its information. 
	 * @param gameState
	 */
	public void updatePlayground(LetrisGameState gameState) {
		Group grid = drawPlaygroundGrid();
		Group movingLetterBlockImage = drawLetterBlock(gameState.getMovingLetterBlock());
		Group staticLetterBlockImages = new Group();
		for (LetrisGameLetterBlock letterBlock : gameState.getStaticLetterBlocks()) {
			Group letterBlockImage = drawLetterBlock(letterBlock);
			staticLetterBlockImages.add(letterBlockImage);
		}
		canvas.clear();
		canvas.add(grid);
		canvas.add(staticLetterBlockImages);
		canvas.add(movingLetterBlockImage);
	}
	
	/**
	 * Takes the given game state and refreshes the drawing of the opponent's playgroundSize preview.
	 * @param gameState
	 */
	public void updatePreview(LetrisGameState gameState) {
		// TODO Implement this.
	}
	
	/**
	 * Draws the grid lines of the playgroundSize.
	 */
	private Group drawPlaygroundGrid() {
		
		// Create the grid and set the starting point.
		Group grid = new Group();
		
		// Draw horizontal lines.
		for (int i = 0; i <= modelSize.y; i++) {
			// Create line.
			Line l = new Line(playgroundOrigin.x, playgroundOrigin.y + (i * (blockSize + 1)), 
					playgroundOrigin.x + playgroundSize.x, playgroundOrigin.y + (i * (blockSize + 1)));
			// Add color.
			if (i == 0 || i == modelSize.y) {
				l.setStrokeColor("black");
			} else {
				l.setStrokeColor("gray");
			}
			// Add line to the grid.
			grid.add(l);
		}
		
		// Draw vertical lines.
		for (int i = 0; i <= modelSize.x; i++) {
			// Create line.
			Line l = new Line(playgroundOrigin.x + (i * (blockSize + 1)), playgroundOrigin.y,
					playgroundOrigin.x + (i * (blockSize + 1)), playgroundOrigin.y + playgroundSize.y);
			// Add color.
			if (i == 0 || i == modelSize.x) {
				l.setStrokeColor("black");
			} else {
				l.setStrokeColor("gray");
			}
			// Add line to the grid.
			grid.add(l);
		}
		
		return grid;
	}
	
	/**
	 * Draws the given letter block on the playgroundSize.
	 * @param letterBlock
	 */
	private Group drawLetterBlock(LetrisGameLetterBlock letterBlock) {
		Group letterBlockImage = new Group();
		
		String letterStr = letterBlock.getLetter();

		// Get letter color.
		String colorStr = letter2HexColor.get(letterStr);
		
		// Calculate the appropriate coordinates for the view.
		LetrisGameCoordinates viewCoordinates = transform.transformModelToView(new LetrisGameCoordinates(letterBlock.getX(), letterBlock.getY()));
		viewCoordinates.add(playgroundOrigin);
		
		// Draw box.
		Rectangle box = new Rectangle(viewCoordinates.x, viewCoordinates.y, blockSize, blockSize);
		
		// Draw letter.
		Text letter = new Text(0, 0, letterStr);
		letter.setFontSize(17);
		letter.setFillColor(colorStr);
		letter.setStrokeColor(colorStr);
		letter.setFontFamily("Andale Mono");
		
		// Rotate letter.
		// TODO Why leads the rotation to the warnings and why does the letter rotate ever again?
//		switch (letterBlock.getOrientation()) {
//		case EAST:
//			letter.setRotation(0); // -90
//			GWT.log("EAST");
//			break;
//		case WEST:
//			letter.setRotation(0); // 90
//			GWT.log("WEST");
//			break;
//		case NORTH:
//			letter.setRotation(0); // 180
//			GWT.log("NORTH");
//			break;
//		case SOUTH:
//			letter.setRotation(0);
//			GWT.log("SOUTH");
//			break;
//		}
		
		// Center letter.
		int xOffset = (int) Math.floor(((double)blockSize - letter.getTextWidth()) / 2.0);
		int yOffset = (int) Math.floor(((double)blockSize - letter.getTextHeight()) / 2.0) + letter.getTextHeight() - 2;
		letter.setX(viewCoordinates.x + xOffset);
		letter.setY(viewCoordinates.y + yOffset);
		
		letterBlockImage.add(box);
		letterBlockImage.add(letter);
		
		return letterBlockImage;
	}

	/**
	 * Clears the game panel
	 */
	public void clearGamePanel(){
		gamePanel.clear();
		gamePanel.add(this.canvas);
	}

	// TODO: real implementation
	public void showEndScreen(int points){
		gamePanel.clear();
	}

	public void updatePoints(int playerid, int p,String name) {
		playerNamesFlexTable.setHTML(playerid+1, 0, "<div style='font-size:30px;color:" + playerColors[playerid-1] + "'>" + Integer.toString(p) +"<span style='font-size:14px'> " + name +"</span></div>");
	}

	public void deletePlayerFromPointList(int playerid) {
		playerNamesFlexTable.clearCell(playerid, 1);
		playerNamesFlexTable.removeCell(playerid, 1);
	}

	/**
	 * Displays the LeTris game playgroundSize.
	 */
	public void showLetrisGame() {
		gamePanel.clear();
		textBox.setText("");
	
		gamePanel.add(canvas);
		canvas.clear();
		canvas.add(drawPlaygroundGrid());

		gamePanel.add(focusPanel, 0, 0);
		
//		if (players ==2) {
			// TODO Add preview of other player.
//		}
		
		if (MobileDeviceChecker.checkForKeyboard()) {
			kbd = new KeyboardDummy((LetrisGameController)super.gameController);
			gamePanel.add(kbd, 440, 240);
		}

		focusPanel.setFocus(true);
	}

	public boolean isOnCanvas(int y) {
		return y < gamePanel.getOffsetHeight();
	}

}
