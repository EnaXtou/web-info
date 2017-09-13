package cz.plsi.webInfo.actions.helper;

import java.util.Date;

public class NumberDateTeam implements Comparable<NumberDateTeam> {
	
	private int number;
	private	Date date;
	private String stageDescription;
	private String team;
	private int numberOfResults;
	private String stageName ;
	
	public NumberDateTeam(int number, Date date, String team) {
		super();
		this.number = number;
		this.date = date;
		this.team = team;
	}
	
	public NumberDateTeam(int number, Date date, String team, String stageDescription, String stageName) {
		this(number, date, team);
		this.stageDescription = stageDescription;
		this.setStageName(stageName);
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	@Override
	public int compareTo(NumberDateTeam o) {
		
		int numberOfSolvedStagesCompare = Integer.compare(this.number - this.numberOfResults , o.number - o.numberOfResults);
		
		int stageNumberCompare = Integer.compare(this.number, o.number); 
		if (numberOfSolvedStagesCompare != 0) {
			return numberOfSolvedStagesCompare;
		} else if (stageNumberCompare != 0) {
			return stageNumberCompare;
		} else {
			return o.date.compareTo(this.date); // before is better therefore is "bigger"
		}				 
	}
	public String getStageDescription() {
		return stageDescription;
	}
	public void setStageDescription(String stageDescription) {
		this.stageDescription = stageDescription;
	}
	
	@Override
	public int hashCode() {
		return stageDescription.hashCode();
	}

	public int getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
}
