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

public class AddTeamPage extends Composite {
	private final TextBox name = new TextBox();
	private final Label nameLabel = new Label();
	private final TextBox code = new TextBox();
	private final Label codeLabel = new Label();

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

			// Set up the callback object.
			AsyncCallback<Void> callback = new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					// TODO: Do something with errors.
				}

				public void onSuccess(Void result) {
					name.setText("");
				}
			};

			// Make the call to the stock price service.
			teamStageAction.addTeam(name.getValue(), code.getValue(), callback);
		}
	}

	public AddTeamPage() {

		/*
		 * create button and attach click handler
		 */
		Button button = new Button("Přidej tým");
		button.addClickHandler(new MyClickHandler());

		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel line = new HorizontalPanel();
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("300", "100");

		nameLabel.setText("Název týmu: ");
		name.setTitle("Název týmu");
		panel.add(line);
		line.add(nameLabel);
		line.add(name);

		line = new HorizontalPanel();
		codeLabel.setText("Kód týmu: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(codeLabel);
		line.add(code);
		panel.add(line);
		panel.add(button);

		initWidget(panel);

	}

	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		AddTeamPage atp = new AddTeamPage();
		RootPanel.get().add(atp);
	}

}
