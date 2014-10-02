package cz.plsi.webInfo.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TeamStageActionInterfaceAsync {

	void addTeam(String name, String code, AsyncCallback<Void> callback);

	void getHelp(String teamCode, String helpName, List<String> errors,
			AsyncCallback<String> callback);

	void getResults(String teamCode,
			AsyncCallback<Map<Integer, String>> callback);

	void nextStage(String teamCode, String stageName, List<String> errors, AsyncCallback<String> callback);

	void addStage(int order, String name, String help1, String help2, String result, AsyncCallback<Void> callback);

	void loginTeam(String code, AsyncCallback<Integer> callback);

	void addHelp(String help, AsyncCallback<Void> callback);

}
