package cz.plsi.webInfo.client;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("teamStageAction")
public interface TeamStageActionInterface extends RemoteService {

	public abstract String getHelp(String teamCode, String helpName,
			String branch, List<String> errors);

	public abstract String nextStage(String teamCode, String stageName,
			List<String> errors);
	
	public abstract Map<Integer, String> getResults(String teamCode);

	public abstract void addTeam(String name, String code);

	public abstract void addStage(int order, String name, String description, String help1, String help2, String result, String branch, int constraint, int timeToHelp, String message);

	public abstract int loginTeam(String code);

	void addHelp(String help);

	String minusHelp(String teamCode);

	TreeSet<TeamStageClient> getTeamsByStageAndStageDate();

	String setMessageToTeams(String message, int messageFromStage,
			int messageToStage, String branch);

	Map<Integer, String> getTeamMessageHistory(String teamCode);

}