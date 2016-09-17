package cz.plsi.webInfo.client;

import java.util.ArrayList;
import java.util.Map;

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

public class TeamWebInfoPage extends Composite {

	private static final String BRANCH_LINEAR = "L";
	private static final String CODE_REQUIRED = "Chyba: Zadej kód.";
	private static final String ANSWER_ERROR = "Chyba: Server není dostupný zkuste to znovu.";
	private VerticalPanel panel;
	private HorizontalPanel line;
	private HorizontalPanel helpStagePanel = new HorizontalPanel();
	private VerticalPanel messages = new VerticalPanel();

	private Label codeLabel = new Label("Kód: ");
	private TextBox code = new TextBox();

	private Hidden teamCodeHidden = new Hidden("teamCode");

	private TeamStageActionInterfaceAsync teamStageAction = GWT.create(TeamStageActionInterface.class);

	private void addAnswerFromWebInfo(String result) {
		helpStagePanel.clear();
		Label label = new Label(result);
		label.setStyleName("statusBar");
		helpStagePanel.add(label);
	}

	public TeamWebInfoPage(String teamCode) {
		teamCodeHidden.setValue(teamCode);

		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		panel = new VerticalPanel();
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("100%", "100%");

		line.setSize("100%", "100%");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		line.add(codeLabel);
		line.add(code);
		Button helpButton;
		helpButton = new Button("Nápověda");
		helpButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (teamStageAction == null) {
					teamStageAction = GWT.create(TeamStageActionInterface.class);
				}
				
				if (code.getValue().isEmpty()) {
					addAnswerFromWebInfo(CODE_REQUIRED);
					return;
				}
				// Set up the callback object.
				AsyncCallback<String> callback = new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						addAnswerFromWebInfo(ANSWER_ERROR);
					}

					public void onSuccess(String result) {
						code.setText("");
						addAnswerFromWebInfo(result);
						fillMessages();

					}

				};

				teamStageAction.getHelp(teamCodeHidden.getValue(), code.getValue().toLowerCase(), BRANCH_LINEAR, new ArrayList<String>(),
						callback);

			}

		});

		panel.add(line);

		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		line.add(helpButton);
		
		Button stageButton = new Button("Stanoviště");
		stageButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (teamStageAction == null) {
					teamStageAction = GWT.create(TeamStageActionInterface.class);
				}
				
				if (code.getValue().isEmpty()) {
					addAnswerFromWebInfo(CODE_REQUIRED);
					return;
				}

				// Set up the callback object.
				AsyncCallback<String> callback = new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						addAnswerFromWebInfo(ANSWER_ERROR);
					}

					public void onSuccess(String result) {
						code.setText("");
						addAnswerFromWebInfo(result);
						fillMessages();
					}
				};

				teamStageAction.nextStage(teamCodeHidden.getValue(), code.getValue().toLowerCase(), new ArrayList<String>(),
						callback);
				

			}

		});
		line.add(stageButton);
		
		Button refreshInfoButton = new Button("Zjisti info");
		refreshInfoButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (teamStageAction == null) {
					teamStageAction = GWT.create(TeamStageActionInterface.class);
				}
				fillMessages();
			}
			
		});
		line.add(refreshInfoButton);
		
		panel.add(line);
		panel.add(helpStagePanel);
		messages.setSpacing(10);
		panel.add(messages);
		initWidget(panel);

	}

	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		TeamWebInfoPage atp = new TeamWebInfoPage("");
		RootPanel.get().add(atp);
	}

	private void addMessages(Map<Integer, String> results) {
		messages.clear();
		for (String mesage : results.values()) {
			messages.add(new Label(mesage));
		}
	}

	private void fillMessages() {
		// Set up the callback object.
		AsyncCallback<Map<Integer, String>> callbackResults = new AsyncCallback<Map<Integer, String>>() {
			public void onFailure(Throwable caught) {
				addAnswerFromWebInfo(ANSWER_ERROR);
			}
			
			public void onSuccess(Map<Integer, String> results) {
				addMessages(results);
				
			}
		};
		teamStageAction.getResults(teamCodeHidden.getValue(), callbackResults);
	}
}
