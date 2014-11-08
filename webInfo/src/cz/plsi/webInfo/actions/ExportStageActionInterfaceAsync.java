package cz.plsi.webInfo.actions;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExportStageActionInterfaceAsync {

	void exportTeamsOnStages(AsyncCallback<List<String[]>> callback);

}
