package cz.plsi.webInfo.client;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TeamStageClient implements Comparable<TeamStageClient>, Serializable, IsSerializable  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8141619283175202966L;

	private String teamName;
	
	private int order;
	
	private String stageName;
	
	private Date stageDate;
	
	private boolean ended = false;

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public Date getStageDate() {
		return stageDate;
	}

	public void setStageDate(Date stageDate) {
		this.stageDate = stageDate;
	}
	
	public TeamStageClient() {
		super();
	}
	
	public TeamStageClient(String teamName, 
						String stageName,
						int order,
						Date stageDate) {
		this.teamName = teamName;
		this.stageName = stageName;
		this.order = order;
		this.stageDate = stageDate;
	}

	@Override
	public String toString() {
		return "TeamStageClient [teamName=" + teamName + ", stageOrder=" + order + ", stageName=" + stageName
				+ ", stageDate=" + stageDate + "]";
	}

	@Override
	public int compareTo(TeamStageClient o) {
		int result = this.order - o.order;
		if (result == 0) {
			result = o.stageDate.compareTo(this.stageDate);
		}
		return result;
	}

	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	
	
}
