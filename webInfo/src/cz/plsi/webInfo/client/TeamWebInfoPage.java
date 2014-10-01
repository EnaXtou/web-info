package cz.plsi.webInfo.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TeamWebInfoPage extends Composite {

	private VerticalPanel panel;
	private HorizontalPanel line;
	private HorizontalPanel helpPanel = new HorizontalPanel();

	private Label helpCodeLabel = new Label("Zadej kód pro nápovědu: ");
	private TextBox helpCode = new TextBox();

	private Label stageCodeLabel = new Label("Zadej kód pro stanoviště: ");
	private TextBox stageCode = new TextBox();

	private Hidden teamCodeHidden = new Hidden("teamCode");

	private TeamStageActionInterfaceAsync teamStageAction = GWT.create(TeamStageActionInterface.class);

	public TeamWebInfoPage(String teamCode) {
		teamCodeHidden.setValue(teamCode);

		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		panel = new VerticalPanel();
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("100%", "100%");

		line.add(helpCodeLabel);
		line.add(helpCode);
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		line.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Button helpButton;
		helpButton = new Button("Nápověda");
		helpButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (teamStageAction == null) {
					teamStageAction = GWT.create(TeamStageActionInterface.class);
				}

				// Set up the callback object.
				AsyncCallback<String> callback = new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// TODO: Do something with errors.
					}

					public void onSuccess(String result) {
						helpCode.setText("");
						if (helpPanel.getWidgetCount() > 0) {
							helpPanel.remove(0);
						}
						helpPanel.add(new Label(result));
						
					}
				};

				teamStageAction.getHelp(teamCodeHidden.getValue(), helpCode.getValue(), new ArrayList<String>(), callback);

			}

		});

		line.add(helpButton);
		panel.add(line);
		
		line = new HorizontalPanel();
		line.add(stageCodeLabel);
		line.add(stageCode);
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		line.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Button stageButton;
		stageButton = new Button("Stanoviště");
		stageButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (teamStageAction == null) {
					teamStageAction = GWT.create(TeamStageActionInterface.class);
				}
				
				// Set up the callback object.
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						// TODO: Do something with errors.
					}
					
					public void onSuccess(Boolean result) {
						
					}
				};
				
				teamStageAction.nextStage(teamCodeHidden.getValue(), stageCode.getValue(), new ArrayList<String>(), callback);
				
			}
			
		});
		
		line.add(stageButton);
		panel.add(line);
		panel.add(helpPanel);

		initWidget(panel);

	}

	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		TeamWebInfoPage atp = new TeamWebInfoPage("");
		RootPanel.get().add(atp);
	}
}
