package cz.plsi.webInfo.actions.helper;

public class TeamBranch implements Comparable<TeamBranch> {
	
	private String branch;
	private String team;
	
	public TeamBranch(String branch, String team) {
		this.branch = branch;
		this.team = team;
	}
	
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	@Override
	public int compareTo(TeamBranch tb) {
		
		int branchCompare = this.branch.compareTo(tb.getBranch());
		return branchCompare != 0 ? branchCompare : this.team.compareTo(tb.team);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamBranch other = (TeamBranch) obj;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		return true;
	}
	
	
	

}
