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

public class AddHelpPage extends Composite {
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
					code.setText("");
				}
			};

			// Make the call to the stock price service.
			teamStageAction.addHelp(code.getValue(), callback);
		}
	}

	public AddHelpPage() {

		/*
		 * create button and attach click handler
		 */
		Button button = new Button("Přidej heslo nápovědy");
		button.addClickHandler(new MyClickHandler());

		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel line = new HorizontalPanel();
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("300", "100");

		codeLabel.setText("Kód nápovědy: ");
		line.add(codeLabel);
		line.add(code);
		panel.add(line);
		panel.add(button);

		initWidget(panel);

	}

	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		AddHelpPage atp = new AddHelpPage();
		RootPanel.get().add(atp);
	}

}
