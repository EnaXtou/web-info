package cz.plsi.webInfo.actions;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface ExportStageActionInterface  extends RemoteService {

	List<String[]> exportTeamsOnStages();

}
