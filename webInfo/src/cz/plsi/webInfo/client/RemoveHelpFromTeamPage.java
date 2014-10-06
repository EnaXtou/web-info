package cz.plsi.webInfo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RemoveHelpFromTeamPage extends Composite {
	private final TextBox code = new TextBox();
	private final Label codeLabel = new Label();
	private final Label status = new Label();

	private TeamStageActionInterfaceAsync teamStageAction = GWT.create(TeamStageActionInterface.class);

	/**
	 * create a custom click handler which will call onClick method when button
	 * is clicked.
	 */
	private class MyClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (teamStageAction == null) {
				teamStageAction = GWT.create(TeamStageActionInterface.class);
			}
			status.setText("");
			// Set up the callback object.
			AsyncCallback<String> callback = new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					status.setText("Chyba serveru. Zkontroluj db.");
				}

				public void onSuccess(String result) {
					code.setText("");
					status.setText(result);
				}
			};

			// Make the call to the stock price service.
			teamStageAction.minusHelp(code.getValue(), callback);
		}
	}

	public RemoveHelpFromTeamPage() {

		/*
		 * create button and attach click handler
		 */
		Button button = new Button("Odeber nápovědu od týmu");
		button.addClickHandler(new MyClickHandler());

		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel line = new HorizontalPanel();
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("300", "100");

		codeLabel.setText("Kód týmu: ");
		line.add(codeLabel);
		line.add(code);
		panel.add(line);
	
		panel.add(button);
		panel.add(status);

		initWidget(panel);
	}
	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		RemoveHelpFromTeamPage atp = new RemoveHelpFromTeamPage();
		RootPanel.get().add(atp);
	}

}
