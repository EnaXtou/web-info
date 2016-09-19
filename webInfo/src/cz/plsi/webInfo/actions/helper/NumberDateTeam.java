package cz.plsi.webInfo.actions.helper;

import java.util.Date;

public class NumberDateTeam implements Comparable<NumberDateTeam> {
	
	private int number;
	private	Date date;
	private String team;
	
	public NumberDateTeam(int number, Date date, String team) {
		super();
		this.number = number;
		this.date = date;
		this.team = team;
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
		
		int numberCompare = Integer.compare(this.number , o.number);
		return numberCompare != 0 ? 
				numberCompare : 
				o.date.compareTo(this.date); // before is better therefore is "bigger"
	}
	
	
	
}
