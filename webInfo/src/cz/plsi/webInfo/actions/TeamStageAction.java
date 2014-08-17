package cz.plsi.webInfo.actions;

import java.util.List;
import java.util.Map;

import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.Help;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStageHelp;

public class TeamStageAction {

	public static String getHelp(String teamCode, String helpName, List<String> errors) {
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.size() == 0) {
			CommonAction.addError("Nesprávný kód týmu.", errors);
		}
		
		Help help = new Help();
		help.setName(helpName);
		List<Help> helpWithName = help.getList();
		
		if (helpWithName.size() == 0) {
			CommonAction.addError("Nesprávný kód pro nápovìdu.", errors);
		}
		
		if (errors != null && errors.size() > 0) {
			return null;
		}
		TeamStageHelp teamStageHelp = new TeamStageHelp();
		
		String teamName = teamWithCode.get(0).getName();
		teamStageHelp.setTeamName(teamName);
		teamStageHelp.setHelp(helpName);

		List<TeamStageHelp> teamStageHelps = teamStageHelp.getList();
		if (teamStageHelps.size() > 0) {
			CommonAction.addError("Již použitý kód pro nápovìdu.", errors);
		}
		
		if (errors != null && errors.size() > 0) {
			return null;
		}
		
		TeamStage teamStage = new TeamStage();
		teamStage.setTeamName(teamName);
		List<TeamStage> teamStageList = teamStage.getList();
		
		TeamStage currentTeamStage = teamStageList.get(0);
		teamStageHelp = new TeamStageHelp(currentTeamStage, null);
		teamStageHelps = teamStageHelp.getList();
		
		
		if (teamStageHelps.size() > 2) {
			CommonAction.addError("Již máte øešení.", errors);
			return null;
		} 
		
		String result = null;
		Stage currentStage = new Stage(currentTeamStage.getStageName());
		currentStage = currentStage.getList().get(0);
		
		if (teamStageHelps.size() == 0) {
			//první nápovìda
			result = currentStage.getHelp1();
		} 
		
		if (teamStageHelps.size() == 1) {
			//druhá nápovìda
			result = currentStage.getHelp2();
		} 
		
		if (teamStageHelps.size() == 2) {
			//øešení
			result = currentStage.getResult();
		} 
		
		teamStageHelp.setHelp(helpName);
		EMF.add(teamStageHelp);
		
		return result;
	}
	
	public static boolean nextStage(String teamCode, String stageName, List<String> errors) {
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.isEmpty()) {
			CommonAction.addError("Nesprávný kód týmu.", errors);
			return false;
		}
		
		Stage stage = new Stage(stageName);
		List<Stage> stageWithName = stage.getList();
		
		if (stageWithName.isEmpty()) {
			CommonAction.addError("Nesprávný kód týmu.", errors);
			return false;
		}
		
		String teamName = teamWithCode.get(0).getName();
		TeamStage teamStage = new TeamStage(teamName, stageName);
		List<TeamStage> teamStages = teamStage.getList();
		
		if (!teamStages.isEmpty()) {
			CommonAction.addError("Tuto stanovištì jste již navštívili.", errors);
			return false;
		}
		
		EMF.add(teamStage);
		return true;
	}
	
	public static Map<Integer, String> getResults(String teamCode) {
		
		return null;
	}
	
	}