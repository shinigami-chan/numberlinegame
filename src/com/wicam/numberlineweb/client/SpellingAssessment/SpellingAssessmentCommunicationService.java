package com.wicam.numberlineweb.client.SpellingAssessment;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.wicam.numberlineweb.client.GameJoinException;
import com.wicam.numberlineweb.client.GameState;

@RemoteServiceRelativePath("spellingAssessmentCommunication")

/**
 * Math assessment communication service interface.
 * @author timfissler
 *
 */

public interface SpellingAssessmentCommunicationService extends RemoteService {
	
	public void itemPresented(String message);

	public void userAnswered(String message);
	
	public SpellingAssessmentState startAssessment(int userID) throws GameJoinException;
	
	public void endAssessment(int assessmentID);
	
	public ArrayList<String> loadShuffledItemList();
	
	public void userAborted(String message);
	
}