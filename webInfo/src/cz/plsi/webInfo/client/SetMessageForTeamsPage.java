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

public class SetMessageForTeamsPage extends Composite {
	private final TextBox message = new TextBox();
	private final Label messageLabel = new Label();
	private final TextBox messageFromStage = new TextBox();
	private final Label messageFromStageLabel = new Label();
	private final TextBox messageToStage = new TextBox();
	private final Label messageToStageLabel = new Label();
	private final TextBox branch = new TextBox();
	private final Label branchLabel = new Label();
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
					message.setText("");
					status.setText(result);
				}
			};

			// Make the call to the stock price service.
			teamStageAction.setMessageToTeams(message.getValue(), Integer.parseInt(messageFromStage.getValue()), Integer.parseInt(messageToStage.getValue()), branch.getValue(), callback);
		}
	}

	public SetMessageForTeamsPage() {

		/*
		 * create button and attach click handler
		 */
		Button button = new Button("Nastav zprávu pro týmy");
		button.addClickHandler(new MyClickHandler());

		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel line = new HorizontalPanel();
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("300", "100");

		messageLabel.setText("Zpráva: ");
		line.add(messageLabel);
		line.add(message);
		panel.add(line);
		
		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		messageFromStageLabel.setText("Stage od (včetně): ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(messageFromStageLabel);
		line.add(messageFromStage);
		panel.add(line);
		
		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		messageToStageLabel.setText("Stage do (včetně): ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(messageToStageLabel);
		line.add(messageToStage);
		panel.add(line);
	
		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		messageToStageLabel.setText("Branch: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(branch);
		line.add(branchLabel);
		panel.add(line);
		
		panel.add(button);
		panel.add(status);

		initWidget(panel);
	}
	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		SetMessageForTeamsPage atp = new SetMessageForTeamsPage();
		RootPanel.get().add(atp);
	}

}
