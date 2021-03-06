package com.wicam.numberlineweb.server.VowelGame.DoppelungGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import javax.servlet.http.HttpServletRequest;

import com.wicam.numberlineweb.client.GameOpenException;
import com.wicam.numberlineweb.client.GameState;
import com.wicam.numberlineweb.client.VowelGame.DoppelungGame.DoppelungGameController;
import com.wicam.numberlineweb.client.VowelGame.DoppelungGame.DoppelungGameState;
import com.wicam.numberlineweb.client.VowelGame.DoppelungGame.DoppelungGameCommunicationService;
import com.wicam.numberlineweb.client.VowelGame.ConsonantPoint2D;
import com.wicam.numberlineweb.client.VowelGame.VowelGameWord;
import com.wicam.numberlineweb.server.GameCommunicationServiceServlet;
import com.wicam.numberlineweb.server.SetGameStateTask;
import com.wicam.numberlineweb.server.VowelGame.ResetSoundFeedbackStateTask;
import com.wicam.numberlineweb.server.VowelGame.ResetWordFeedbackStateTask;
import com.wicam.numberlineweb.server.VowelGame.SetDoppelungGameStateTask;
import com.wicam.numberlineweb.server.VowelGame.UpdateMcCoordsTimerTask;
import com.wicam.numberlineweb.server.logging.DoppelungGameHandicap;
import com.wicam.numberlineweb.server.logging.GameLogger.LogActionTrigger;
import com.wicam.numberlineweb.server.logging.GameLogger.LogActionType;

public class DoppelungGameCommunicationServiceServlet extends
		GameCommunicationServiceServlet implements
		DoppelungGameCommunicationService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7200332323767902482L;

	private int TIMER_SPEED = 40;
	private int SPACE_SPEED = 4;
	// private Timer mcTimer = null;
	private Map<Integer, Timer> mcTimerMap = new HashMap<Integer, Timer>();
	protected HashMap<Integer, Iterator<VowelGameWord>> wordLists = new HashMap<Integer, Iterator<VowelGameWord>>();
	protected boolean miniGameStartsWhenShortVowel = true;

	public DoppelungGameCommunicationServiceServlet() {

		super("doppelunggame");
		this.handicapAdjustment = new DoppelungGameHandicap();

	}

	public DoppelungGameCommunicationServiceServlet(String internalName) {

		super(internalName);
		this.handicapAdjustment = new DoppelungGameHandicap();

	}

	@Override
	public GameState openGame(GameState g) throws GameOpenException {

		// initialize game list

		GameState gameState = super.openGame(g);

		wordLists.put(
				gameState.getId(),
				DoppelungGameWordList
						.createWordList()
						.subList(
								0,
								((DoppelungGameState) gameState)
										.getNumberOfWordsToPlay()).iterator());

		// set first word
		((DoppelungGameState) gameState).setCurWord(getNextWord(currentId));

		return gameState;

	}

	public boolean hasNextWord(int gameid) {
		return wordLists.get(gameid).hasNext();
	}

	public VowelGameWord getNextWord(int gameid) {
		if (hasNextWord(gameid))
			return wordLists.get(gameid).next();
		else
			return null;
	}

	@Override
	public GameState buttonClicked(String ids) {

		int gameid = Integer.parseInt(ids.split(":")[0]);
		int bottonid = Integer.parseInt(ids.split(":")[2]);
		int playerid = getPlayerId(gameid);

		DoppelungGameState g = (DoppelungGameState) getGameById(gameid);

		Timer t = new Timer(true);
		// mcTimer = new Timer(true);
		this.mcTimerMap.put(gameid, new Timer(true));

		g.setShowSoundFeedback(playerid, true);
		g.incSoundTries(playerid);

		// short vowel
		boolean isShortVowel = false;
		boolean isAnswerCorrect = false;

		if (g.getCurWord().isShortVowel()) {

			isShortVowel = true;

			if (bottonid == DoppelungGameController.SHORTVOWELBUTTON) {
				g.setAnswer(playerid, true);
				isAnswerCorrect = true;
			} else
				g.setAnswer(playerid, false);
		}
		// long vowel
		else {
			if (bottonid == DoppelungGameController.LONGVOWELBUTTON) {
				g.setAnswer(playerid, true);
				isAnswerCorrect = true;
			} else
				g.setAnswer(playerid, false);
		}

		String type = "";
		if (isShortVowel)
			type = "short";
		else
			type = "long";

		HttpServletRequest request = this.getThreadLocalRequest();
		if (request != null) {
			HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> map = (HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>) request
					.getSession().getAttribute("game2pid2uid");
			int uid = map.get(internalName).get(gameid).get(playerid);
			String params = "{word: \"" + g.getCurWord().getWordString()
					+ "\", " + "vowel_type: \"" + type + "\", "
					+ "has_answered_correctly: " + isAnswerCorrect + "}";
			this.gameId2Logger.get(gameid).log(gameid, uid,
					System.currentTimeMillis(),
					LogActionType.DOPPELUNGGAME_WORD_CATEGORIZED, params,
					this.getClass().getName(), LogActionTrigger.USER);
		}

		// next step if correct answer or already tried once
		if (g.hasCorrectlyAnswered(playerid) || g.getSoundTries(playerid) >= 2) {
			// check if other player has already answered
			if (g.getPlayerCount() == 1
					|| g.hasCorrectlyAnswered((playerid % 2 + 1))
					|| g.getSoundTries((playerid % 2 + 1)) >= 2) {
				int feedbackTime = 500;
				if (g.getSoundTries(playerid) < 2)
					feedbackTime = 3000;

				// short vowel: start short vowel sub game
				if (miniGameStartsWhenShortVowel == g.getCurWord()
						.isShortVowel()) {
					initializeMovingConsonantsPoints(gameid);
					t.schedule(new SetGameStateTask(gameid, 5, this),
							feedbackTime);

					// initialize moving consonants timer
					// mcTimer.schedule(new UpdateMcCoordsTimerTask(g.getId(),
					// SPACE_SPEED, this), feedbackTime, TIMER_SPEED);
					this.mcTimerMap.get(gameid).schedule(
							new UpdateMcCoordsTimerTask(g.getId(), SPACE_SPEED,
									this), feedbackTime, TIMER_SPEED);
				}
				// long vowel: show next word if has next
				else {
					// points for correct answer
					if (g.hasCorrectlyAnswered(playerid)
							&& g.getSoundTries(playerid) < 2)
						g.setPlayerPoints(playerid,
								g.getPlayerPoints(playerid) + 5);
					if (this.hasNextWord(gameid)) {
						t.schedule(new SetDoppelungGameStateTask(gameid, 21,
								this), feedbackTime);
					} else {
						this.endGame(gameid);
					}
				}
			}
			// wait for other player
			else {
				// add points for correct answer
				if (!g.getCurWord().isShortVowel()) {
					if (g.hasCorrectlyAnswered(playerid)
							&& g.getSoundTries(playerid) < 2)
						g.setPlayerPoints(playerid,
								g.getPlayerPoints(playerid) + 5);
				}
			}
		}
		// retry
		else {
			t.schedule(new ResetSoundFeedbackStateTask(gameid, playerid, 3,
					this), 3000);
		}
		g.setServerSendTime(System.currentTimeMillis());
		return g;

	}

	private void initializeMovingConsonantsPoints(int gameid) {
		DoppelungGameState g = (DoppelungGameState) getGameById(gameid);
		ArrayList<ConsonantPoint2D> movingConsonantsCoords = new ArrayList<ConsonantPoint2D>();
		ArrayList<String> consonantPairList = getConsonantsList(gameid);
		int oldX = -100;
		for (int i = 0; i < g.getNumberOfConsonants(); i++) {
			ConsonantPoint2D point = new ConsonantPoint2D();
			int newX = 50 - (int) (Math.random() * 7)
					+ (int) (Math.random() * 9) % 9 * 50;
			while (Math.abs(oldX - newX) < 100) {
				newX = 50 - (int) (Math.random() * 7)
						+ (int) (Math.random() * 9) % 9 * 50;
			}
			point.setX(newX);
			point.setY(-i * 50 - 50);
			point.setConsonant(consonantPairList.get(i));
			movingConsonantsCoords.add(point);
			oldX = newX;
		}
		g.setMovingConsonantsCoords(movingConsonantsCoords);
	}

	protected ArrayList<String> getConsonantsList(int gameid) {
		DoppelungGameState g = (DoppelungGameState) getGameById(gameid);
		return new DoppelungGameConsonantPairListCreater()
				.createTargetConsonantList(g.getCurWord().getConsonantPair(),
						g.getNumberOfCorrectConsonants(),
						g.getNumberOfConsonants());
	}

	@Override
	public GameState updatePoints(String ids) {
		int gameid = Integer.parseInt(ids.split(":")[0]);
		int playerid = getPlayerId(gameid);
		String consonants = ids.split(":")[2];
		int mcid = Integer.parseInt(ids.split(":")[3]);

		int points = 0;
		DoppelungGameState g = (DoppelungGameState) getGameById(gameid);

		if (consonants.equals(g.getCurWord().getConsonantPair()))
			points = g.hasCorrectlyAnswered(playerid) ? 2 : 1;
		else
			points = -1;

		int newPoints = g.getPlayerPoints(playerid) + points;
		if (newPoints < 0)
			newPoints = 0;

		g.setPlayerPoints(playerid, newPoints);
		g.getMovingConsonantsCoords().get(mcid).setRemoved(true);
		g.setServerSendTime(System.currentTimeMillis());
		return g;
	}

	@Override
	public GameState wordEntered(String word) {
		int gameid = Integer.parseInt(word.split(":")[0]);
		int playerid = getPlayerId(gameid);
		String enteredWord = word.split(":")[2];

		DoppelungGameState g = (DoppelungGameState) getGameById(gameid);
		g.setShowWordFeedback(playerid, true);
		g.incWordTries(playerid);

		HttpServletRequest request = this.getThreadLocalRequest();
		if (request != null) {
			HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> map = (HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>) request
					.getSession().getAttribute("game2pid2uid");
			int uid = map.get(internalName).get(gameid).get(playerid);
			String params = "{correct_word: \""
					+ g.getCurWord().getWordString() + "\", "
					+ "word_entered: \"" + enteredWord + "\"}";
			this.gameId2Logger.get(gameid).log(gameid, uid,
					System.currentTimeMillis(),
					LogActionType.DOPPELUNGGAME_WORD_ENTERED, params,
					this.getClass().getName(), LogActionTrigger.USER);
		}

		if (enteredWord.equals(g.getCurWord().getWordString()))
			g.setAnswer(playerid, true);
		else
			g.setAnswer(playerid, false);

		Timer t = new Timer(true);
		if (g.hasCorrectlyAnswered(playerid) || g.getWordTries(playerid) >= 2) {
			// add points for correct answer
			if (g.getWordTries(playerid) < 2)
				g.setPlayerPoints(playerid, g.getPlayerPoints(playerid) + 2);

			// check if other player has already answered
			if (g.getPlayerCount() == 1
					|| g.hasCorrectlyAnswered((playerid % 2 + 1))
					|| g.getWordTries((playerid % 2 + 1)) >= 2) {
				if (this.hasNextWord(gameid)) {
					int feedbackTime = 500;
					if (g.getWordTries(playerid) < 2)
						feedbackTime = 3000;
					t.schedule(new SetDoppelungGameStateTask(gameid, 21, this),
							feedbackTime);
				} else
					this.endGame(gameid);
			}
		}
		// retry
		else {
			t.schedule(
					new ResetWordFeedbackStateTask(gameid, playerid, 6, this),
					3000);
		}
		g.setServerSendTime(System.currentTimeMillis());
		return g;
	}

	@Override
	public GameState enableWordInput(String ids) {
		int gameid = Integer.parseInt(ids.split(":")[0]);
		int playerid = getPlayerId(gameid);
		DoppelungGameState g = (DoppelungGameState) getGameById(gameid);
		// reset answer of sound choice
		g.setAnswer(playerid, false);
		g.setEndedShortVowelGame(playerid, true);

		// wait for other player
		if (g.getPlayerCount() == 1
				|| g.isEndedShortVowelGame(playerid % 2 + 1)) {
			// mcTimer.cancel();
			this.mcTimerMap.get(gameid).cancel();
			Timer t = new Timer(true);
			t.schedule(new SetGameStateTask(gameid, 6, this), 500);
			this.setChanged(gameid);
		}
		g.setServerSendTime(System.currentTimeMillis());
		return g;
	}

	@Override
	public GameState updatePlayerPos(String ids) {
		int gameid = Integer.parseInt(ids.split(":")[0]);
		int playerid = getPlayerId(gameid);
		int x = Integer.parseInt(ids.split(":")[2]);
		int y = Integer.parseInt(ids.split(":")[3]);
		int pingid = Integer.parseInt(ids.split(":")[4]);

		DoppelungGameState g = (DoppelungGameState) getGameById(gameid);
		g.setPlayerPosX(playerid, x);
		g.setPlayerPosY(playerid, y);
		this.setChanged(gameid);
		g.setServerSendTime(System.currentTimeMillis());

		g.setPingId(pingid);

		setUpToDate(gameid, playerid);
		super.resetUpdateTimer(playerid, gameid);

		return g;
	}

	public void setMiniGameStartsWhenShortVowel(
			boolean miniGameStartsWhenShortVowel) {
		this.miniGameStartsWhenShortVowel = miniGameStartsWhenShortVowel;
	}

	@Override
	public String getGameProperties(GameState g) {
		// TODO Auto-generated method stub
		return null;
	}

}
