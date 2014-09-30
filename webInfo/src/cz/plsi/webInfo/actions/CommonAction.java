package cz.plsi.webInfo.actions;

import java.util.ArrayList;
import java.util.List;

public class CommonAction {
	
	public static void addError(String error, List<String> errors) {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		
		errors.add(error);
	}

}
