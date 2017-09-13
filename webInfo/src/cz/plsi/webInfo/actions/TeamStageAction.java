package cz.plsi.webInfo.actions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
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

import cz.plsi.webInfo.actions.helper.NumberDateTeam;
import cz.plsi.webInfo.actions.helper.NumberWithDescription;
import cz.plsi.webInfo.actions.helper.TeamBranch;
import cz.plsi.webInfo.client.TeamStageActionInterface;
import cz.plsi.webInfo.client.TeamStageClient;
import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.Help;
import cz.plsi.webInfo.shared.dataStore.entities.MessageToTeams;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;
import cz.plsi.webInfo.shared.dataStore.entities.TeamMessageHistory;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStageHelp;

public class TeamStageAction extends RemoteServiceServlet implements TeamStageActionInterface {

	private static final String RESULT_CODE = "reseni";

	public static final String HELP_STOLEN = "Chytilo vás nějaké monstrum, strachem se vám heslo vytratilo z paměti. Pro nápovědu zadejte další heslo.";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#getHelp(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public String getHelp(String teamCode, String helpName, String branch, List<String> errors) {
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.size() == 0) {
			return CommonAction.addError(null, null, null, "Nesprávný kód týmu.", errors);
		}
		
		TeamStage surrenderTeamStage = new TeamStage();
		surrenderTeamStage.setTeamName(teamWithCode.get(0).getName());
		surrenderTeamStage.setStageOrder(-1);
		if (surrenderTeamStage.getList().size() > 0) {
			return CommonAction.addError(teamCode, null, null, "Již jste vzdali hru a proto nemůžete pokračovat.", errors);
		}
		
		
		Help help = new Help();
		help.setName(helpName);
		List<Help> helpWithName = help.getList();
		
		boolean isResultRequested = RESULT_CODE.equalsIgnoreCase(helpName);
		if (helpWithName.size() == 0 && !isResultRequested) {
			return CommonAction.addError(teamCode, null, helpName, "Nesprávný kód pro nápovědu.", errors);
		}
		
		
		
		TeamStageHelp teamStageHelp = new TeamStageHelp();
		
		String teamName = teamWithCode.get(0).getName();
		teamStageHelp.setTeamName(teamName);
		teamStageHelp.setHelp(helpName);

		List<TeamStageHelp> teamStageHelps = teamStageHelp.getList();
		if (teamStageHelps.size() > 0 && !isResultRequested) {
			return CommonAction.addError(teamCode, null, helpName, "Již použitý kód pro nápovědu.", errors);
		}
		
		
		TeamStage teamStage = new TeamStage();
		teamStage.setTeamName(teamName);
		teamStage.setStageBranch(branch);
		List<TeamStage> teamStageList = teamStage.getList();
		if (teamStageList.size() == 0 && !isResultRequested) {
			return CommonAction.addError(teamCode, null, helpName, "Ještě nemáte navštívené žádné stanoviště.", errors);
		}
		
		TeamStage currentTeamStage = teamStageList.get(0);
		teamStageHelp = new TeamStageHelp(currentTeamStage, null, null);
		teamStageHelps = teamStageHelp.getList();
		
		
		if (hasTeamResult(teamStageHelps)) {
			return CommonAction.addError(teamCode, null, helpName, "Již máte řešení.", errors);
		} 
		
		String result = null;
		Stage currentStage = new Stage(currentTeamStage.getStageName());
		currentStage.setBranch(null);
		currentStage = currentStage.getList().get(0);
		
		if (teamStageHelps.size() == 0 && !isResultRequested) {
			//první nápověda
			result = currentStage.getHelp1();
		} 
		
		boolean isResult = false;
		if (teamStageHelps.size() == 1 && !isResultRequested) {
			//druhá nápověda
			result = currentStage.getHelp2();
			if ((result == null || result.isEmpty()) && currentStage.getTimeToResult() == 0.0) {
				teamStageHelp.setHelpResult(result);
				teamStageHelp.setHelp(false);
				EMF.add(teamStageHelp);
				result = currentStage.getResult();
				isResult = true;
			}
		} 
		
		if (teamStageHelps.size() == 2 || isResultRequested) {
			if (isResultRequested && !isTimeAfterInterval(currentTeamStage.getStageDate(), currentStage.getTimeToResult())) {
				return CommonAction.addError(teamCode, null, helpName, String.format("Nemáte nárok na řešení - neuplynulo %1.0f minut od vyzvednutí šifry.", currentStage.getTimeToResult()), errors);
			}
			result = currentStage.getResult();
			isResult = true;
		}
		
		if (result == null || result.length() == 0) {
			return CommonAction.addError(teamCode, null, helpName, "Pro aktuální stanoviště již není nápověda, heslo lze použít znovu.", errors);
		}
		
		team = teamWithCode.get(0);
		
		int helpsCount = team.getHelpsCount();
		if (helpsCount < 0) {
			team = (Team) EMF.find(team); 	
			//TODO Tenhle update je asi zbytečný
//			EMF.update(team);
			team.setHelpsCount(++helpsCount);
			EMF.update(team);
			teamStageHelp.setStageName(null);
			teamStageHelp.setHelp(helpName);
			teamStageHelp.setHelpResult(result);
			teamStageHelp.setHelp(false);
			EMF.add(teamStageHelp);
			return CommonAction.addMessageToHistory(teamCode, null, helpName, HELP_STOLEN) ;
			
		}
		
		if (isResult) {
			StringBuilder sb = new StringBuilder();
			sb.append("Řešení ");
			sb.append(getCalculatedStageDescription(currentStage));
			sb.append(": ");
			sb.append(result);
			result = sb.toString(); 
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Nápověda ");
			sb.append(getCalculatedStageDescription(currentStage));
			sb.append(": ");
			sb.append(result);
			result = sb.toString();
		}
		
		
		
		teamStageHelp = new TeamStageHelp(teamStageHelp.getTeamStage(), helpName, result);
		if (teamStageHelp.getList().size() == 0) {
			EMF.add(teamStageHelp);
		}
		
		return CommonAction.addMessageToHistory(teamCode, null, helpName, result);
	}

	private boolean hasTeamResult(List<TeamStageHelp> teamStageHelps) {
		boolean hasResult = teamStageHelps.size() > 2;
		if (!hasResult) {
			for (TeamStageHelp tSH : teamStageHelps) {
				if ("reseni".equals(tSH.getHelp())) {
					hasResult = true;
					break;
				}
			}
		}
		return hasResult;
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
			return CommonAction.addError(null, null, null, "Nesprávný kód týmu.", errors);
		}
		
		String teamName = teamWithCode.get(0).getName();
		
		TeamStage surrenderTeamStage = new TeamStage();
		surrenderTeamStage.setTeamName(teamName);
		surrenderTeamStage.setStageOrder(-1);
		if (surrenderTeamStage.getList().size() > 0) {
			return CommonAction.addError(teamCode, stageName, null, "Již jste vzdali hru a proto nemůžete pokračovat.", errors);
		}
		
		
		Stage stage = new Stage(stageName);
		stage.setBranch(null);
		List<Stage> stageWithName = stage.getList();
		if (stageWithName.isEmpty()) {
			return addErrorWrongStageCode(teamCode, stageName, errors);
		}
		
		TeamStage teamStage = new TeamStage();
		teamStage.setTeamName(teamName);
		teamStage.setStageName(stageName);
		List<TeamStage> teamStages = teamStage.getList();
		
		Stage currentStage = stageWithName.get(0);
		TeamStage lastTeamStage = TeamStage.getLastTeamStage(teamName, currentStage.getBranch());
		
		if (!teamStages.isEmpty()) {
			if (currentStage.getNumber() == -1) {
				return CommonAction.addMessageToHistory(teamCode, stageName, null, getGoodBye(currentStage));
			}
			return CommonAction.addError(teamCode, stageName, null, "Tuto stanoviště jste již navštívili.", errors);
		}
		
		// Špatné pořadí
		int stageOrder = lastTeamStage == null ? 0 : lastTeamStage.getStageOrder();
		if (currentStage.getNumber() != -1 && stageOrder != currentStage.getNumber() - 1 && stageOrder != currentStage.getNumber()) {
			return addErrorWrongStageCode(teamCode, stageName, errors);
		}
		
		int constraint = currentStage.getConstraint();
		TeamStage teamStageForCount = new TeamStage(teamName, null, 0);
		long stagesCount = teamStageForCount.count();
		if (constraint > stagesCount) {
			return addErrorWrongStageCode(teamCode, stageName, errors);
		}
		
		teamStage = new TeamStage(teamName, stageName, currentStage.getNumber(), currentStage.getBranch(), currentStage.getDescription());
		
		
		EMF.add(teamStage);
		String greetings;
		if (currentStage.getNumber() == -1) {
			greetings = getGoodBye(currentStage);
		} else {
			greetings = getGreetings(currentStage);
		}
		return CommonAction.addMessageToHistory(teamCode, stageName, null, greetings);
	}

	private String getGreetings(Stage currentStage) {
		StringBuilder sb = new StringBuilder();
		String message = currentStage.getMessage();
		if (message != null && !message.isEmpty()) {
			sb.append(message);
		} else {
			sb.append("Tak vás tu vítáme! Stanoviště ");
			sb.append(getCalculatedStageDescription(currentStage));
				
		}
		return sb.toString();
	}

	public static String getCalculatedStageDescription(Stage currentStage) {
		String description = currentStage.getDescription();
		StringBuilder sb = new StringBuilder();
		if (description != null
				&& !description.isEmpty()) {
			sb.append(description);
		} else {
			sb.append(currentStage.getBranch()).append(".");
			sb.append(currentStage.getNumber());
		}
		return sb.toString();
	}

	private String addErrorWrongStageCode(String teamCode, String stageName, List<String> errors) {
		return CommonAction.addError(teamCode, stageName, null, "Nesprávný kód stanoviště.", errors);
	}

	private String getGoodBye(Stage currentStage) {
		return "Díky za účast. Navštivte nás v cíli: " + currentStage.getResult();
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
		Map<TeamBranch, NumberDateTeam> teamsPositions = getTeamsPositions(teamStages);
		Map<String, NumberDateTeam> teamsPoints = new HashMap<>(50);
		Map<String, NumberDateTeam> teamsInLinear = new HashMap<>(30);
		getPointsAndLinearStages(teamsPositions, teamsPoints, teamsInLinear);
		
		TreeSet<NumberDateTeam> linearOrder = new TreeSet<>(Collections.reverseOrder());
		linearOrder.addAll(teamsInLinear.values());
		TreeSet<NumberDateTeam> pointsOrder = new TreeSet<>(Collections.reverseOrder());
		pointsOrder.addAll(teamsPoints.values());
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		if (!linearOrder.isEmpty()) {
			NumberDateTeam winningTeamStage = linearOrder.first();
			StringBuilder sb = new StringBuilder();
			sb.append("Vede tým '");
			sb.append(winningTeamStage.getTeam());
			sb.append("', který byl na stanovišti ");
			sb.append(winningTeamStage.getStageDescription());
			sb.append(" v ");
			sb.append(sdf.format(winningTeamStage.getDate()));
			sb.append(" (");
			sb.append(winningTeamStage.getNumber() - winningTeamStage.getNumberOfResults());
			sb.append(" vyluštěných šifer).");
			results.put(Integer.valueOf(order++), sb.toString());
		} else if (!pointsOrder.isEmpty()){
			NumberDateTeam winningTeamStage = pointsOrder.first();
			results.put(Integer.valueOf(order++), "Vede tým '" 
					+ winningTeamStage.getTeam() + "', který dosáhl "
					+ winningTeamStage.getNumber() + " ponožek v " 
					+ sdf.format(winningTeamStage.getDate()) + ".");
			
		}

		Map<String, Integer> teamsWithOrder = new HashMap<>();
		int place = 1;
		for (NumberDateTeam teamPositionInLinear : linearOrder) {
			String actualTeamName = teamPositionInLinear.getTeam();
			if (!teamsWithOrder.containsKey(actualTeamName)) {
				teamsWithOrder.put(actualTeamName, place++);
			}
		}
		for (NumberDateTeam teamPoitsPosition : pointsOrder) {
			String actualTeamName = teamPoitsPosition.getTeam();
			if (!teamsWithOrder.containsKey(actualTeamName)) {
				teamsWithOrder.put(actualTeamName, place++);
			}
		}
		
		NumberDateTeam points = teamsPoints.get(teamName);
		if (teamsWithOrder.containsKey(teamName)) {
				place = teamsWithOrder.get(teamName).intValue();
				StringBuilder sb = new StringBuilder();
				sb.append("Váš tým je aktuálně na ");
				sb.append(place);
				sb.append(". místě");
				if (points != null) {
					sb.append(" s ")
					.append(points.getNumber())
					.append(" ponožkami v ")
					.append(sdf.format(points.getDate()))
					.append(".");
				} else {
					NumberDateTeam linearStage = teamsInLinear.get(teamName);
					if (linearStage != null) {
						sb.append(" na stanovišti ")
						.append(linearStage.getStageDescription())
						.append(" v ")
						.append(sdf.format(linearStage.getDate()))
						.append(" (")
						.append(linearStage.getNumber() - linearStage.getNumberOfResults())
						.append(" vyluštěných šifer).");
					}
				}
				results.put(Integer.valueOf(-2), sb.toString());
		}
	
		TeamStage lastTeamStage = TeamStage.getLastTeamStage(teamName);
		NumberDateTeam numberDateTeam = points;
		int currentTeamPoints = -1;
		if (numberDateTeam != null) {
			currentTeamPoints = numberDateTeam.getNumber();
		}
		MessageToTeams messagesToTeams = new MessageToTeams();
		if (lastTeamStage != null) {
			List<MessageToTeams> messagesForStage = messagesToTeams.getList();
			
			order = -3;
			for (MessageToTeams messageToTeams : messagesForStage) {
				if ((messageToTeams.getFromStageNumber() <= lastTeamStage.getStageOrder()
						&& messageToTeams.getToStageNumber() >= lastTeamStage.getStageOrder()
						&& messageToTeams.getBranch() != null
						&& messageToTeams.getBranch().equals(lastTeamStage.getStageBranch()))
					||
					(messageToTeams.getFromStageNumber() <= currentTeamPoints
					&& messageToTeams.getToStageNumber() >= currentTeamPoints
					&& (messageToTeams.getBranch() == null || messageToTeams.getBranch().isEmpty()))
					) {
					results.put(Integer.valueOf(order--), messageToTeams.getMessage());
				}
			}
		
			Stage currentStage = new Stage();
			currentStage.setName(lastTeamStage.getStageName());
			currentStage.setBranch(lastTeamStage.getStageBranch());
			currentStage = currentStage.getList().get(0);
			
			double timeToResult = currentStage.getTimeToResult();
			String result = currentStage.getResult();
			if (result != null
					&& timeToResult > 0
					&& (teamStageHelp == null || 
								!(RESULT_CODE.equals(teamStageHelp.getHelp())
										&& teamStageHelp.getStageName().equals(lastTeamStage.getStageName())))) {
				if (isTimeAfterInterval(lastTeamStage.getStageDate(), timeToResult)) {
					
					
					StringBuilder sb = new StringBuilder();
					sb.append("Můžete si vzít řešení pro stanoviště ");
					sb.append(getCalculatedStageDescription(currentStage));
					results.put(order--, sb.toString());
				}
			}
		}
		
		
		
		order = -15;
		if (!teamsInLinear.containsKey(teamName)) {
			order = addResultAfterTime(order, results, teamName, teamsPositions, "A");
			order = addResultAfterTime(order, results, teamName, teamsPositions, "B");
			order = addResultAfterTime(order, results, teamName, teamsPositions, "C");
		}
		// TODO for linear branch the result can't be send after time without request order = addResultAfterTime(order, results, teamName, teamsPositions, "L");
		
		results.putAll(getStatistics(teamStages));
		
		results.put(200, "Čas posledního požadavku: " + sdf.format(new Date()));
		
		return results;
	}

	/**
	 * @param lastTeamStage
	 * @param timeInterval
	 * @return
	 */
	private boolean isTimeAfterInterval(Date stageDate,
			double timeInterval) {
		Calendar currentDate = Calendar.getInstance();
		Calendar date = Calendar.getInstance();
		date.clear();
		date.setTime(stageDate);
		date.add(Calendar.SECOND, (int) Math.round(timeInterval * 60));
		boolean isTimeAfterInterval = date.compareTo(currentDate) <= 0;
		return isTimeAfterInterval;
	}

	private int addResultAfterTime(int order, TreeMap<Integer, String> results,
			String teamName, Map<TeamBranch, NumberDateTeam> teamsPositions,
			String branch) {
		TeamBranch currentTeamBranch = new TeamBranch(branch, teamName);
		NumberDateTeam numberDateOnStage = teamsPositions.get(currentTeamBranch);
		if (numberDateOnStage != null) {
			Stage stage = new Stage();
			stage.setNumber(numberDateOnStage.getNumber());
			stage.setBranch(branch);
			Stage actualStage = stage.getList().get(0);
			String help = actualStage.getResult();
			double timeToHelp = actualStage.getTimeToResult();
			if (help != null
					&& timeToHelp > 0) {
				Calendar currentDate = Calendar.getInstance();
				Calendar date = Calendar.getInstance();
				date.clear();
				date.setTime(numberDateOnStage.getDate());
				date.add(Calendar.SECOND, (int) Math.round(timeToHelp * 60));
				if (date.compareTo(currentDate) <= 0) {
					StringBuilder sb = new StringBuilder();
					sb.append("Řešení ");
					sb.append(getCalculatedStageDescription(actualStage));
					sb.append(": ");
					sb.append(help);
					results.put(order--, sb.toString());
				}
			}
		}
		return order;
	}
	
	/* (non-Javadoc)
	 * @see cz.plsi.webInfo.actions.TeamStageActionInterface#getResults(java.lang.String)
	 */
	@Override
	public TreeSet<TeamStageClient> getTeamsByStageAndStageDate() {
		
		TeamStage teamStage = new TeamStage();
		List<TeamStage> teamStages = teamStage.getList();
		Map<TeamBranch, NumberDateTeam> teamsPositions = getTeamsPositions(teamStages);
		Map<String, NumberDateTeam> teamsPoints = new HashMap<>(50);
		Map<String, NumberDateTeam> teamsInLinear = new HashMap<>(30);
		getPointsAndLinearStages(teamsPositions, teamsPoints, teamsInLinear);
		
		TreeSet<NumberDateTeam> linearOrder = new TreeSet<>(Collections.reverseOrder());
		linearOrder.addAll(teamsInLinear.values());
		TreeSet<NumberDateTeam> pointsOrder = new TreeSet<>(Collections.reverseOrder());
		pointsOrder.addAll(teamsPoints.values());
		
		teamStage.setStageOrder(TeamStage.TEAM_ENDED_GAME);
		List<TeamStage> teamsThatEnded = teamStage.getList();
		HashSet<String> teamEndedNames = new HashSet<String>(15);
		for (TeamStage teamThatEnded : teamsThatEnded) {
			teamEndedNames.add(teamThatEnded.getTeamName());
		}
		
		Map<String, Integer> teamsWithOrder = new HashMap<>();
		int place = 1;
		TreeSet<TeamStageClient> sortedTeamStages = new TreeSet<>();
		for (NumberDateTeam teamPositionInLinear : linearOrder) {
			String actualTeamName = teamPositionInLinear.getTeam();
			if (!teamsWithOrder.containsKey(actualTeamName)) {
				teamsWithOrder.put(actualTeamName, place);
				Date date = new Date(teamPositionInLinear.getDate().getTime());
				TeamStageClient teamStageClient = new TeamStageClient(teamPositionInLinear.getTeam(),
															teamPositionInLinear.getStageDescription(),
															place,
															date);
				teamStageClient.setEnded(teamEndedNames.contains(teamStageClient.getTeamName()));
				teamStageClient.setNumberOfResults(teamPositionInLinear.getNumberOfResults());
				sortedTeamStages.add(teamStageClient);
				place++;
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		SimpleDateFormat sdfForParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("cs", "CZ"));
		sdfForParse.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		
		for (NumberDateTeam teamPoitsPosition : pointsOrder) {
			String actualTeamName = teamPoitsPosition.getTeam();
			if (!teamsWithOrder.containsKey(actualTeamName)) {
				teamsWithOrder.put(actualTeamName, place);
				Date date = new Date(teamPoitsPosition.getDate().getTime());
				StringBuilder sb = new StringBuilder();
				sb.append(teamPoitsPosition.getNumber());
				sb.append(" P ( ");
				NumberDateTeam numberBranch = teamsPositions.get(new TeamBranch("A", actualTeamName));
				if (numberBranch != null) {
					sb.append(numberBranch.getStageDescription());
					sb.append(" ");
				}
				numberBranch = teamsPositions.get(new TeamBranch("B", actualTeamName));
				if (numberBranch != null) {
					sb.append(numberBranch.getStageDescription());
					sb.append(" ");
				}
				numberBranch = teamsPositions.get(new TeamBranch("C", actualTeamName));
				if (numberBranch != null) {
					sb.append(numberBranch.getStageDescription());
					sb.append(" ");
				}
				sb.append(" )");
				TeamStageClient teamStageClient = new TeamStageClient(teamPoitsPosition.getTeam(),
															sb.toString() ,
															place,
															date);
				teamStageClient.setEnded(teamEndedNames.contains(teamStageClient.getTeamName()));
				sortedTeamStages.add(teamStageClient);
				place++;
			}
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
	public void addStage(int order, String name, String description, String help1, String help2, String result, String branch, int constraint, int timeToHelp, String message) {
		EMF.add(new Stage(name, order, description, help1, help2, result, branch, constraint, timeToHelp, message));
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
		
		String message = "Tým (" + teamCode + ") má aktuálně " + helpsCount + " nápovědu.";
		return CommonAction.addMessageToHistory(teamCode, null, null, message);
	}
	
	@Override
	public Map<Integer, String> getTeamMessageHistory(String teamCode) {
		int order = 0;
		TreeMap<Integer, String> results = new TreeMap<>(); 
		Team team = new Team();
		team.setCode(teamCode);
		List<Team> teamWithCode = team.getList();
		
		if (teamWithCode.size() == 0) {
			results.put(Integer.valueOf(order++), "Chyba: Nesprávný kód týmu.");
			return results;
		}
		
		
		TeamMessageHistory teamMessageHistory = new TeamMessageHistory();
		teamMessageHistory.setTeamCode(teamCode);
		List<TeamMessageHistory> teamMessageHistoryList = (List<TeamMessageHistory>) teamMessageHistory.getList();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", new Locale("cs", "CZ"));
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Prague"));
		for (TeamMessageHistory teamMessageHistoryCurrent : teamMessageHistoryList) {
			
			results.put(order++, 
					sdf.format(teamMessageHistoryCurrent.getMessageDate()) + " - " + teamMessageHistoryCurrent.getMessage());
		}
		return results;
	}
	
	
	@Override
	public String setMessageToTeams(String message, int messageFromStage, int messageToStage, String branch) {
		MessageToTeams messageToTeams = new MessageToTeams();
		messageToTeams.setMessage(message);
		messageToTeams.setFromStageNumber(messageFromStage);
		messageToTeams.setToStageNumber(messageToStage);
		messageToTeams.setBranch(branch);
		EMF.add(messageToTeams);
		return message;
	}
	
	public Map<Integer, String> getStatistics(List<TeamStage> teamStages) {
		
		//posčítat týmy podle větví
		Map<TeamBranch, NumberDateTeam> teamsPositions = getTeamsPositions(teamStages);
		
		Map<String, NumberDateTeam> teamsPoints = new HashMap<>(50);
		Map<String, NumberDateTeam> teamsInLinear = new HashMap<>(25);
		// pro větve A B C dát součet pro daný tým (klíč team + větev)
		getPointsAndLinearStages(teamsPositions, teamsPoints, teamsInLinear);
		
		Map<Integer, Integer> pointsWithCounts = new TreeMap<>(Collections.reverseOrder());  
		for (NumberDateTeam points : teamsPoints.values()) {
			if (pointsWithCounts.containsKey(points.getNumber())) {
				pointsWithCounts.put(points.getNumber(), pointsWithCounts.get(points.getNumber()) + 1);
			} else {
				pointsWithCounts.put(points.getNumber(), 1);
			}
		}
		
		Map<NumberWithDescription, Integer> linearWithCounts = new TreeMap<>(Collections.reverseOrder());  
		for (NumberDateTeam points : teamsInLinear.values()) {
			NumberWithDescription numberWithDescriptionKey = new NumberWithDescription(points.getNumber(), points.getStageDescription());
			if (linearWithCounts.containsKey(numberWithDescriptionKey)) {
				linearWithCounts.put(numberWithDescriptionKey, linearWithCounts.get(numberWithDescriptionKey) + 1);
			} else {
				linearWithCounts.put(numberWithDescriptionKey, 1);
			}
		}
		
		TreeMap<Integer, String> resultStats = new TreeMap<Integer, String>();
		int i = 19;
		if (!linearWithCounts.isEmpty())	{
			resultStats.put(i++, "Stanoviště : Počet týmů");
		}
		for (Entry<NumberWithDescription, Integer> orderAndCount : linearWithCounts.entrySet()) {
			
			NumberWithDescription stageWithNumber = orderAndCount.getKey();
			
			resultStats.put(i++,String.format("%s : %2d", stageWithNumber.getDescription(), orderAndCount.getValue()));
		}
		i = 39;
		if (!teamsPoints.isEmpty())	{
			resultStats.put(i++, "Počet ponožek : Počet týmů");
		}
		
		for (Entry<Integer, Integer> orderAndCount : pointsWithCounts.entrySet()) {
			
			resultStats.put(i++,String.format("%2d : %2d", orderAndCount.getKey(), orderAndCount.getValue()));
		}
		return resultStats;
	}

	private void getPointsAndLinearStages(
			Map<TeamBranch, NumberDateTeam> teamsPositions,
			Map<String, NumberDateTeam> teamsPoints, Map<String, NumberDateTeam> teamsInLinear) {
		for (TeamBranch teamBranch : teamsPositions.keySet()) {
			String team = teamBranch.getTeam();
			
			
			NumberDateTeam numberDateTeamCurrent = teamsPositions.get(teamBranch);
			if ("L".equals(teamBranch.getBranch())) {
				TeamStageHelp teamStageHelp = new TeamStageHelp();
				
				
				teamStageHelp.setHelp("reseni");
				teamStageHelp.setTeamName(teamBranch.getTeam());
				int numberOfResults = teamStageHelp.getList().size();
				teamStageHelp.setStageName(numberDateTeamCurrent.getStageName());
				
				if (teamStageHelp.getList().size() > 0) {
					numberOfResults--;
				}
				
				numberDateTeamCurrent.setNumberOfResults(numberOfResults);
				teamsInLinear.put(team, numberDateTeamCurrent);
			} else {
				NumberDateTeam numberDateTeamIn = teamsPoints.get(team);
				if (numberDateTeamIn == null) {
					teamsPoints.put(team, new NumberDateTeam(numberDateTeamCurrent.getNumber(), numberDateTeamCurrent.getDate(), numberDateTeamCurrent.getTeam()));
					continue;
				}
				
				if (numberDateTeamIn.getDate().compareTo(numberDateTeamCurrent.getDate()) < 0) {
					numberDateTeamIn.setDate(numberDateTeamCurrent.getDate());
				}
				numberDateTeamIn.setNumber(numberDateTeamIn.getNumber() + numberDateTeamCurrent.getNumber());
				teamsPoints.put(team, numberDateTeamIn);
			}
		}
		
		for (String team : teamsInLinear.keySet()) {
			teamsPoints.remove(team);
		}
	}

	public Map<TeamBranch, NumberDateTeam> getTeamsPositions(List<TeamStage> teamStages) {
		Map<TeamBranch, NumberDateTeam> teamBranchesCurrentPosition = new HashMap<>(200);
		
		
		for (TeamStage actualTeamStage : teamStages) {
			TeamBranch teamBranch = new TeamBranch(actualTeamStage.getStageBranch(), actualTeamStage.getTeamName());
			if (!teamBranchesCurrentPosition.containsKey(teamBranch)) {
				String stageDescription = actualTeamStage.getStageDescription();
				String stageName = actualTeamStage.getStageName();
				if (stageDescription == null
						|| stageDescription.isEmpty()) {
					stageDescription = actualTeamStage.getStageBranch() + "." + actualTeamStage.getStageOrder();
				}
				teamBranchesCurrentPosition.put(teamBranch, new NumberDateTeam(actualTeamStage.getStageOrder(), actualTeamStage.getStageDate(), actualTeamStage.getTeamName(), stageDescription, stageName));
			}	
		}
		
		return teamBranchesCurrentPosition;
	}
	
	
	
	
	
}