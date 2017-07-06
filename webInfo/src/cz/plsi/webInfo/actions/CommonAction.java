package cz.plsi.webInfo.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.TeamMessageHistory;

public class CommonAction {
	
	public static String addError(String teamCode, String stageName, String helpName, String error, List<String> errors) {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		
		errors.add(error);
		
		return addMessageToHistory(teamCode, stageName, helpName, "Chyba: " + error);
	}
	
	public static String addMessageToHistory(String teamCode, String stageName, String helpName, String message) {
		if (teamCode != null) {
			//TODO add message to history
			TeamMessageHistory teamMessageHistory = new TeamMessageHistory();
			teamMessageHistory.setTeamCode(teamCode);
			String messageWithRequest = "";
			if (helpName != null) {
				messageWithRequest = "Nápověda(" + helpName + ") - ";
			}
			if (stageName != null) {
				messageWithRequest = "Stanoviště(" + stageName + ") - ";
			}
			
			messageWithRequest += message;
			teamMessageHistory.setMessage(messageWithRequest);
			teamMessageHistory.setMessageDate(new Date());
			EMF.add(teamMessageHistory);
		}
		return message;
	}

}
