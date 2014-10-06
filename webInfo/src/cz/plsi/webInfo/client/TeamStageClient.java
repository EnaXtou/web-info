package cz.plsi.webInfo.client;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TeamStageClient implements Comparable<TeamStageClient>, Serializable, IsSerializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2902100656931860374L;

	private String teamName;
	
	private int stageOrder;
	
	private String stageName;
	
	private Date stageDate;

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public int getStageOrder() {
		return stageOrder;
	}

	public void setStageOrder(int stageOrder) {
		this.stageOrder = stageOrder;
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
						int stageOrder,
						Date stageDate) {
		this.teamName = teamName;
		this.stageName = stageName;
		this.stageOrder = stageOrder;
		this.stageDate = stageDate;
	}

	@Override
	public String toString() {
		return "TeamStageClient [teamName=" + teamName + ", stageOrder=" + stageOrder + ", stageName=" + stageName
				+ ", stageDate=" + stageDate + "]";
	}

	@Override
	public int compareTo(TeamStageClient o) {
		int result = this.stageOrder - o.stageOrder;
		if (result == 0) {
			result = this.stageDate.compareTo(o.stageDate);
		}
		return result;
	}
	
	
}
