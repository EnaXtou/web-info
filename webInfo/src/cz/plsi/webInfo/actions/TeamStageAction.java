package cz.plsi.webInfo.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

public class TeamStageAction extends RemoteServiceServlet implements TeamStageActionInterface {

	private static final String HELP_STOLEN = "Chytil vás široko a vzal vám heslo.";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#getHelp(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public String getHelp(String teamCode, String helpName, List<String> errors) {
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.size() == 0) {
			return CommonAction.addError("Nesprávný kód týmu.", errors);
		}
		
		Help help = new Help();
		help.setName(helpName);
		List<Help> helpWithName = help.getList();
		
		if (helpWithName.size() == 0) {
			return CommonAction.addError("Nesprávný kód pro nápovědu.", errors);
		}
		
		
		
		TeamStageHelp teamStageHelp = new TeamStageHelp();
		
		String teamName = teamWithCode.get(0).getName();
		teamStageHelp.setTeamName(teamName);
		teamStageHelp.setHelp(helpName);

		List<TeamStageHelp> teamStageHelps = teamStageHelp.getList();
		if (teamStageHelps.size() > 0) {
			return CommonAction.addError("Již použitý kód pro nápovědu.", errors);
		}
		
		
		TeamStage teamStage = new TeamStage();
		teamStage.setTeamName(teamName);
		List<TeamStage> teamStageList = teamStage.getList();
		if (teamStageList.size() == 0) {
			return CommonAction.addError("Ještě nemáte navštívené žádné stanoviště.", errors);
		}
		
		TeamStage currentTeamStage = teamStageList.get(0);
		teamStageHelp = new TeamStageHelp(currentTeamStage, null, null);
		teamStageHelps = teamStageHelp.getList();
		
		
		if (teamStageHelps.size() > 2) {
			return CommonAction.addError("Již máte řešení.", errors);
		} 
		
		String result = null;
		Stage currentStage = new Stage(currentTeamStage.getStageName());
		currentStage = currentStage.getList().get(0);
		
		if (teamStageHelps.size() == 0) {
			//první nápověda
			result = currentStage.getHelp1();
		} 
		
		if (teamStageHelps.size() == 1) {
			//druhá nápověda
			result = currentStage.getHelp2();
		} 
		
		if (teamStageHelps.size() == 2) {
			//řešení
			result = currentStage.getResult();
		}
		
		if (result == null || result.length() == 0) {
			return CommonAction.addError("Pro aktuální stanoviště není nápověda, heslo lze použít znovu.", errors);
		}
		
		team = teamWithCode.get(0);
		
		int helpsCount = team.getHelpsCount();
		if (helpsCount < 0) {
			team = (Team) EMF.find(team); 
			EMF.update(team);
			team.setHelpsCount(++helpsCount);
			EMF.update(team);
			teamStageHelp.setStageName(null);
			teamStageHelp.setHelp(helpName);
			teamStageHelp.setHelpResult(result);
			teamStageHelp.setHelp(false);
			EMF.add(teamStageHelp);
			return HELP_STOLEN;
			
		}
		
		if (teamStageHelps.size() == 2) {
			result = "Řešení: " + result; 
		} else {
			result = "Nápověda: " + result;
		}
			
		teamStageHelp.setHelp(helpName);
		teamStageHelp.setHelpResult(result);
		EMF.add(teamStageHelp);
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#nextStage(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public String nextStage(String teamCode, String stageName, List<String> errors) {
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.isEmpty()) {
			return CommonAction.addError("Nesprávný kód týmu.", errors);
		}
		
		Stage stage = new Stage(stageName);
		List<Stage> stageWithName = stage.getList();
		
		if (stageWithName.isEmpty()) {
			return CommonAction.addError("Nesprávný kód stanoviště.", errors);
		}
		
		String teamName = teamWithCode.get(0).getName();
		Stage currentStage = stageWithName.get(0);
		TeamStage teamStage = new TeamStage(teamName, stageName, currentStage.getNumber());
		List<TeamStage> teamStages = teamStage.getList();
		
		if (!teamStages.isEmpty()) {
			return CommonAction.addError("Tuto stanoviště jste již navštívili.", errors);
		}
		
		EMF.add(teamStage);
		String greetings;
		if (currentStage.getNumber() == -1) {
			greetings = "Díky za účast. Navštivte nás v cíli: " + currentStage.getResult();
		} else {
			greetings = "Tak vás tu vítáme! Plantážníci.";
		}
		return greetings;
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#getResults(java.lang.String)
	 */
	@Override
	public Map<Integer, String> getResults(String teamCode) {
		
		int order = 0;
		TreeMap<Integer, String> results = new TreeMap<>(); 
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.size() == 0) {
			results.put(Integer.valueOf(order++), "Chyba: Nesprávný kód týmu.");
			return results;
		}
		
		TeamStageHelp teamStageHelp = new TeamStageHelp();
		
		String teamName = teamWithCode.get(0).getName();
		teamStageHelp.setTeamName(teamName);
		teamStageHelp = teamStageHelp.getLastHelpResult();
		if (teamStageHelp != null) {
			results.put(Integer.valueOf(order++), teamStageHelp.getHelpResult() + " (st. " + teamStageHelp.getStageName() + ")");
		}
		
		TeamStage teamStage = new TeamStage();
		List<TeamStage> teamStages = teamStage.getList();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		if (!teamStages.isEmpty()) {
			TeamStage winningTeamStage = teamStages.get(0);
			results.put(Integer.valueOf(order++), "Vede tým '" 
									+ winningTeamStage.getTeamName() + "', který byl na "
									+ winningTeamStage.getStageOrder() + ". stanovišti v " 
									+ sdf.format(winningTeamStage.getStageDate()) + ".");
		}

		Map<String, Integer> teamsWithOrder = new HashMap<>();
		int place = 1;
		for (TeamStage actualTeamStage : teamStages) {
			String actualTeamName = actualTeamStage.getTeamName();
			if (!teamsWithOrder.containsKey(actualTeamName)) {
				teamsWithOrder.put(actualTeamName, place++);
			}
		}
		
		if (teamsWithOrder.containsKey(teamName)) {
				place = teamsWithOrder.get(teamName).intValue();
				results.put(Integer.valueOf(-1), "Váš tým je aktuálně na "
						+ place + ". místě.");
		}

		return results;
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#getResults(java.lang.String)
	 */
	@Override
	public TreeSet<TeamStageClient> getTeamsByStageAndStageDate() {
		
		
		TeamStage teamStage = new TeamStage();
		List<TeamStage> teamStages = teamStage.getList();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		
		Map<String, TeamStageClient> teamStagesByTeamName = new HashMap<>(36);
		for (TeamStage actualTeamStage : teamStages) {
			String actualTeamName = actualTeamStage.getTeamName();
			if (!teamStagesByTeamName.containsKey(actualTeamName)) {
				Date date = new Date(actualTeamStage.getStageDate().getTime());
				teamStagesByTeamName.put(actualTeamName,new TeamStageClient(actualTeamStage.getTeamName(),
																	actualTeamStage.getStageName(),
																	actualTeamStage.getStageOrder(),
																	date));
			}
		}

		TreeSet<TeamStageClient> sortedTeamStages = new TreeSet<>();
		for (TeamStageClient teamStageClient : teamStagesByTeamName.values()) {
			sortedTeamStages.add(teamStageClient);
		}
		
		
		return sortedTeamStages;
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#addTeam(java.lang.String)
	 */
	@Override
	public void addTeam(String name, String code) {
		Team team = new Team(name);
		team.setCode(code);
		EMF.add(team);
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#addTeam(java.lang.String)
	 */
	@Override
	public void addStage(int order, String name, String help1, String help2, String result) {
		EMF.add(new Stage(name, order, help1, help2, result));
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#addTeam(java.lang.String)
	 */
	@Override
	public void addHelp(String help) {
		EMF.add(new Help(help));
	}
	
	@Override
	public int loginTeam(String code) {
		if ("rudolfove".equals(code)) {
			return 0;
		}
		Team team = new Team();
		team.setCode(code);
		
		if(team.exists()) {
			return 1;
		}
		
		return -1;
	}
	
	@Override
	public String minusHelp(String teamCode) {
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.size() == 0) {
			return"Chyba: Nesprávný kód týmu.";
			
		}
		
		team = team.getList().get(0);
		team = (Team) EMF.find(team); 
		
		int helpsCount = team.getHelpsCount() - 1;
		team.setHelpsCount(helpsCount);
		EMF.update(team);
		
		return "Tým má aktuálně " + helpsCount + " nápovědu.";
	}
	
	
	
}