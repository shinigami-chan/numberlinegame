package com.wicam.numberlineweb.server.OverTen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import javax.servlet.http.HttpServletRequest;

import com.google.gwt.core.shared.GWT;
import com.wicam.numberlineweb.client.GameOpenException;
import com.wicam.numberlineweb.client.GameState;
import com.wicam.numberlineweb.client.OverTen.OverTenCalculation;
import com.wicam.numberlineweb.client.OverTen.OverTenDigit;
import com.wicam.numberlineweb.client.OverTen.OverTenGameCommunicationService;
import com.wicam.numberlineweb.client.OverTen.OverTenGameState;
import com.wicam.numberlineweb.client.OverTen.OverTenPlayer;
import com.wicam.numberlineweb.client.OverTen.OverTenCalculation.Sign;
import com.wicam.numberlineweb.server.GameCommunicationServiceServlet;

public class OverTenGameCommunicationServiceServlet extends
		GameCommunicationServiceServlet implements
		OverTenGameCommunicationService {

	// Used for GWT
	private static final long serialVersionUID = 7200332323767902482L;

	// Random generator
	private Random rand = new Random();

	// All possible summands
	private int[] possibleSummands = { 2, 3, 4, 5, 6, 7, 8, 9 };

	private ArrayList<Integer> npcIds = new ArrayList<Integer>();

	public final static String[] playerColors = {"blue", "Magenta", "orange", "DarkGreen", "DarkCyan"};

	public OverTenGameCommunicationServiceServlet() {

		super("overTen");

	}

	@Override
	protected void addNPC(GameState game) {
		int playerid = game.addPlayer("NPC", -2);
		npcIds.add(playerid);
		this.npcs.add(new OverTenNPC(this, game.getId(), playerid));
	}

	@Override
	protected boolean isNPC(int playerId) {
		return npcIds.contains(playerId);
	}

	/**
	 * @param state
	 *            OverTenGameState to alter
	 * @return The new OverTenGameState
	 */
	public OverTenGameState newCalcs(OverTenGameState state) {

		// Reset players
		for (int i = 0; i < state.getPlayers().size(); i++) {
			state.setPlayerCalculation(i + 1, new OverTenCalculation());
		}

		// Reset digits
		state.getCommunityDigits().clear();
		state.getCalculations().clear();

		// generate 3 summations over ten with solutions
		while (state.getCalculations().size() < 3) {

			int a = 0;
			int b = 0;
			while (a + b <= 10) {
				a = 2 + rand.nextInt(8); // [2;9]
				b = 2 + rand.nextInt(8); // [2;9]
			}

			OverTenCalculation c = new OverTenCalculation(Sign.ADD, a, b);
			if (!state.getCalculations().contains(c)) {
				// add calculation
				state.getCalculations().add(c);
				// add solutions
				int first = 10 - a;
				state.getCommunityDigits().add(new OverTenDigit(first));
				state.getCommunityDigits().add(new OverTenDigit(b - first));
			}

		}

		// fill with random digits
		while (state.getCommunityDigits().size() < 12) {
			state.getCommunityDigits().add(new OverTenDigit(getRandomDigit()));
		}

		Collections.shuffle(state.getCommunityDigits());
		Collections.shuffle(state.getCalculations());

		// increment the round-counter
		state.setRound(state.getRound() + 1);

		return state;
	}

	/**
	 * @param calcs
	 *            All calculations
	 * @return Checks, if the current GameState has a Solution left
	 */
	private boolean hasSolution(ArrayList<OverTenCalculation> calcs,
			ArrayList<OverTenDigit> digits) {

		for (OverTenCalculation calc : calcs) {
			int i = 0;
			for (OverTenDigit d1 : digits) {
				int j = 0;
				for (OverTenDigit d2 : digits) {

					if (i != j && !d1.isTaken() && !d2.isTaken()
							&& isCorrect(calc, d1, d2)) { // different not taken
															// digits && correct
						return true;
					}

					j++;
				}
				i++;
			}
		}

		return false;
	}

	private boolean isCorrect(OverTenCalculation c, OverTenDigit first,
			OverTenDigit second) {

		try {
			return (c.getFirst() + first.getValue() == 10)
					&& (10 + second.getValue() == c.calc());
		} catch (ArithmeticException e) {
			return false;
		}

	}

	/**
	 * @return Returns a random divisor
	 */
	private int getRandomDigit() {
		return this.possibleSummands[this.rand
				.nextInt(this.possibleSummands.length)];
	}

	/**
	 * @param g
	 *            OverTenGameState
	 * @param correct
	 *            The result will be "correct"
	 * @return Returns all available "correct" answer-ids as "id1:id2"
	 */
	public ArrayList<String> getSpecificAnswers(OverTenGameState g,
			boolean correct) {
		ArrayList<String> res = new ArrayList<String>();

		for (OverTenCalculation calc : g.getCalculations()) {
			int i = 0;
			for (OverTenDigit d1 : g.getCommunityDigits()) {
				int j = 0;
				for (OverTenDigit d2 : g.getCommunityDigits()) {

					if (i != j && !d1.isTaken() && !d2.isTaken()
							&& !d1.isChosen() && !d2.isChosen()
							&& isCorrect(calc, d1, d2) == correct) { // different
																		// not
																		// taken
																		// digits
																		// &&
																		// correct
						res.add("" + i + ":" + j);
					}

					j++;
				}
				i++;
			}
		}

		return res;
	}

	/**
	 * @param state
	 *            id of the player
	 * @return Checks, if the player has already chosen a calculation
	 */
	private boolean playerHasCalc(OverTenGameState g, int playerID) {
		return !((OverTenPlayer) g.getPlayers().get(playerID - 1))
				.getCalculation().equals(new OverTenCalculation());
	}

	/**
	 * @param state
	 *            id of the player
	 * @return Checks, if the player has already chosen a community-digit
	 */
	private boolean playerHasFirstDigit(OverTenGameState g, int playerID) {
		return ((OverTenPlayer) g.getPlayers().get(playerID - 1))
				.getFirstDigit() != -1;
	}

	/**
	 * @param clicked
	 *            gameid:playerid:clickedAnswer
	 * @return New OverTenGameState
	 */
	@Override
	synchronized public OverTenGameState clickedAt(String clicked) {

		int gameid = Integer.parseInt(clicked.split(":")[0]);
		return clickedAt(clicked, getPlayerId(gameid));

	}

	/**
	 * User has clicked.
	 * 
	 * @param clicked
	 *            gameid:playerid:value:index:com/calc
	 * @param playerid
	 *            Playerid
	 * @return New OverTenGameState
	 */
	synchronized public OverTenGameState clickedAt(String clicked, int playerid) {

		int gameid = Integer.parseInt(clicked.split(":")[0]);

		String calc = clicked.split(":")[2];
		int value = 0;
		boolean clickedOnDigit = false;
		if (calc.length() == 1) {
			value = Integer.parseInt(calc);
			clickedOnDigit = true;
		}

		int index = Integer.parseInt(clicked.split(":")[3]);
		String from = clicked.split(":")[4];

		OverTenGameState g = (OverTenGameState) this.getGameById(gameid);

		HttpServletRequest request = this.getThreadLocalRequest();
		int uid = -2;
		if (request != null) {
			HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> map = (HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>) request
					.getSession().getAttribute("game2pid2uid");
			uid = map.get(internalName).get(gameid).get(playerid);
		}

		if (from.equals("calc")) { // first click

			if (g.getPlayerDigit(playerid) > -1) {
				// Unset the chosen digit
				g.getCommunityDigits().get(g.getPlayerDigit(playerid))
						.setChosen(false);
				// remove all data (chosen, calc) from player
				((OverTenPlayer) g.getPlayers().get(playerid)).reset();
			}

			g.setPlayerCalculation(
					playerid,
					new OverTenCalculation(Sign.ADD, Integer.parseInt(calc
							.split(" ")[0]),
							Integer.parseInt(calc.split(" ")[2])));
			this.setGameState(g, 3);

		} else if (from.equals("com") && playerHasCalc(g, playerid)
				&& !playerHasFirstDigit(g, playerid)) {

			if (!g.getCommunityDigits().get(index).isTaken()
					&& !g.getCommunityDigits().get(index).isChosen()) {
				g.setPlayerDigit(playerid, index);
				g.getCommunityDigits().get(index).setChosen(true);
			}

			this.setGameState(g, 3);

		} else if (from.equals("com") && playerHasCalc(g, playerid)
				&& playerHasFirstDigit(g, playerid)) { // user has chosen a
														// hand-digit

			if (!g.getCommunityDigits().get(index).isTaken()) { // Nobody was
																// faster

				if (isCorrect(g.getPlayerCalculation(playerid), g
						.getCommunityDigits().get(g.getPlayerDigit(playerid)),
						g.getCommunityDigits().get(index))) {

					g.setDigitTaken(g.getCommunityDigits(),
							g.getPlayerDigit(playerid), playerColors[g
									.getPlayers().get(playerid - 1)
									.getColorId()]);
					g.setDigitTaken(g.getCommunityDigits(), index,
							playerColors[g.getPlayers().get(playerid - 1)
									.getColorId()]);

					this.getGameById(gameid)
							.setPlayerPoints(
									playerid,
									this.getGameById(gameid).getPlayerPoints(
											playerid) + 1);

				} else {

					g.getCommunityDigits().get(g.getPlayerDigit(playerid))
							.setChosen(false);
					g.getCommunityDigits().get(index).setChosen(false);
					this.getGameById(gameid)
							.setPlayerPoints(
									playerid,
									this.getGameById(gameid).getPlayerPoints(
											playerid) - 1);

				}

				if (hasSolution(g.getCalculations(), g.getCommunityDigits())) {

					this.setGameState(this.getGameById(gameid), 3);
					this.setChanged(gameid);

				} else {

					g.setAllDigitsTakenIn(g.getCommunityDigits(), true);
					g.setAllCalculationsTakenIn(g.getCalculations(), true);

					this.setGameState(this.getGameById(gameid), 5);

					if (g.getRound() >= g.getMaxRound()) {
						this.endGame(gameid);
					} else {
						this.showNextItem(gameid);
					}

				}

			} else {
				// TODO message: someone was faster...
			}

			// enable calculation again, clear chosen digit
			((OverTenPlayer) g.getPlayers().get(playerid - 1)).reset();

		} else {
			// TODO message: choose hand-digit first
		}

		g.setServerSendTime(System.currentTimeMillis());
		return g;
	}

	/**
	 * @param playerid
	 *            NPC-ID
	 * @param clicked
	 *            gameid:playerid:firstID:secondID
	 * @return Could NPC click? If not, the answer was taken
	 */
	public boolean npcClicked(String clicked, Boolean npcWasRight) {
		int gameid = Integer.parseInt(clicked.split(":")[0]);
		int playerid = Integer.parseInt(clicked.split(":")[1]);
		int firstID = Integer.parseInt(clicked.split(":")[2]);
		int secondID = Integer.parseInt(clicked.split(":")[3]);
		OverTenGameState g = (OverTenGameState) this.getGameById(gameid);

		System.out.println("NPC clicked " + npcWasRight + " on " + firstID
				+ " and " + secondID);

		if (!g.getCommunityDigits().get(firstID).isTaken()
				&& !g.getCommunityDigits().get(secondID).isTaken()) { // Nobody
																		// was
																		// faster

			if (npcWasRight) {
				g.setDigitTaken(g.getCommunityDigits(), firstID, playerColors[g
						.getPlayers().get(playerid - 1).getColorId()]);
				g.setDigitTaken(g.getCommunityDigits(), secondID,
						playerColors[g.getPlayers().get(playerid - 1)
								.getColorId()]);
				this.getGameById(gameid).setPlayerPoints(playerid,
						this.getGameById(gameid).getPlayerPoints(playerid) + 1);
			} else {
				this.getGameById(gameid).setPlayerPoints(playerid,
						this.getGameById(gameid).getPlayerPoints(playerid) - 1);
			}

			if (hasSolution(g.getCalculations(), g.getCommunityDigits())) {
				this.setGameState(this.getGameById(gameid), 3);
				this.setChanged(gameid);
			} else {
				g.setAllDigitsTakenIn(g.getCommunityDigits(), true);
				g.setAllCalculationsTakenIn(g.getCalculations(), true);

				this.setGameState(this.getGameById(gameid), 5);

				if (g.getRound() >= g.getMaxRound()) {
					this.endGame(gameid);
				} else {
					this.showNextItem(gameid);
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public GameState openGame(GameState g) throws GameOpenException {

		g.setServerSendTime(System.currentTimeMillis());
		GWT.log("before opening game");
		GameState retGameState = super.openGame(g);
		GWT.log("after opening game");
		// return super.openGame(g);

		newCalcs((OverTenGameState) g);

		return retGameState;

	}

	public void showNextItem(int id) {

		Timer t = new Timer(true);
		t.schedule(new OverTenGameStateTask(id, 6, this), 6000);
	}

	@Override
	public String getGameProperties(GameState gameState) {

		OverTenGameState numberlineGameState = (OverTenGameState) gameState;

		String gamePropertiesStr = "{";

		gamePropertiesStr += "num_players : "
				+ numberlineGameState.getPlayerCount() + ", ";

		// TODO gameproperties

		gamePropertiesStr += "}";

		return gamePropertiesStr;

	}

}
