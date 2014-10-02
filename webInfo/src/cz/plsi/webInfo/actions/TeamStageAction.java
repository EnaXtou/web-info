package cz.plsi.webInfo.actions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.plsi.webInfo.client.TeamStageActionInterface;
import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.Help;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStageHelp;

public class TeamStageAction extends RemoteServiceServlet implements TeamStageActionInterface {

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
		TeamStage teamStage = new TeamStage(teamName, stageName, stageWithName.get(0).getNumber());
		List<TeamStage> teamStages = teamStage.getList();
		
		if (!teamStages.isEmpty()) {
			return CommonAction.addError("Tuto stanoviště jste již navštívili.", errors);
		}
		
		EMF.add(teamStage);
		return "Tak vás tu vítáme! Plantážníci.";
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
			results.put(Integer.valueOf(order++), teamStageHelp.getHelpResult());
		}
		
		TeamStage teamStage = new TeamStage();
		List<TeamStage> teamStages = teamStage.getList();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		if (!teamStages.isEmpty()) {
			TeamStage winningTeamStage = teamStages.get(0);
			results.put(Integer.valueOf(order++), "Vede tým '" 
									+ winningTeamStage.getTeamName() + "', který byl na "
									+ winningTeamStage.getStageOrder() + ". stanovišti v " 
									+ sdf.format(winningTeamStage.getStageDate()) + ".");
		}
		
		for (int i = 0; i < teamStages.size(); i++) {
			TeamStage currentTeamStage = teamStages.get(i);
			if (currentTeamStage.getTeamName().equals(teamName)) {
				results.put(Integer.valueOf(-1), "Váš tým je aktuálně na "
						+ (i + 1) + ". místě.");
				break;
			}
		}

		return results;
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
		if ("Rudolfove".equals(code)) {
			return 0;
		}
		Team team = new Team();
		team.setCode(code);
		
		if(team.exists()) {
			return 1;
		}
		
		return -1;
		
	}
	
	
	
}