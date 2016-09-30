package cz.plsi.webInfo.actions.helper;

public class NumberWithDescription implements Comparable<NumberWithDescription> {
	
	private int number;
	
	public NumberWithDescription(int number, String description) {
		this.number = number;
		this.description = description;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	private String description;

	@Override
	public int compareTo(NumberWithDescription o) {
		return Integer.compare(this.number , o.number);
		
	}

}
