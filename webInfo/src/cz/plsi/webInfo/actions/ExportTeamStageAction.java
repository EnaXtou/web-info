package cz.plsi.webInfo.actions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.plsi.webInfo.client.TeamStageActionInterface;
import cz.plsi.webInfo.client.TeamStageClient;
import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.Help;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStageHelp;

public class ExportTeamStageAction extends RemoteServiceServlet implements ExportStageActionInterface {

	private static final String HELP_STOLEN = "Chytil vás Široko a vzal vám heslo. Pro nápovědu zadejte další heslo.";
	
	//TODO must be change to datastore table
	private static String messageToTeams = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#getResults(java.lang.String)
	 */
	@Override
	public List<String[]> exportTeamsOnStages() {
		
		
		TeamStage teamStage = new TeamStage();
		List<TeamStage> teamStages = teamStage.getList();
		
		teamStage.setStageOrder(TeamStage.TEAM_ENDED_GAME);
		List<TeamStage> teamsThatEnded = teamStage.getList();
		HashSet<String> teamEndedNames = new HashSet<String>(15);
		for (TeamStage teamThatEnded : teamsThatEnded) {
			teamEndedNames.add(teamThatEnded.getTeamName());
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		Map<String, Map<Integer, Date>> teamsOnStages = getTeamsOnStages(teamStages);
		
		
		for (Entry<String, Map<Integer ,Date>> teamOnStages: teamsOnStages.entrySet()) {
			
		}
		// v každém čase získám pořadí týmů 
		// projdu všechny časy a v každém čase uložím do Mapy podle týmů TeamStage, která je menší než aktuálně procházený čas ale větší než aktuální team stage na daném místě..
		
		
		return null;
	}


	private Map<String, Map<Integer, Date>> getTeamsOnStages(List<TeamStage> teamStages) {
		Map<String, Map<Integer ,Date>> teamsOnStages = new HashMap<>();
		
		for (TeamStage actualTeamStage : teamStages) {
			String teamName = actualTeamStage.getTeamName();
			Map<Integer, Date> stagesForTeam;
			if (teamsOnStages.containsKey(teamName)) {
				stagesForTeam = teamsOnStages.get(teamName);
			} else {
				stagesForTeam = new HashMap<>();
				teamsOnStages.put(teamName, stagesForTeam);
			}
			
			stagesForTeam.put(actualTeamStage.getStageOrder(), actualTeamStage.getStageDate());
		}
		return teamsOnStages;
	}
	
}