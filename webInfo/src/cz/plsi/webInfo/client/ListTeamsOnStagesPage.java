package cz.plsi.webInfo.client;

import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ListTeamsOnStagesPage extends Composite {
	private Label status = new Label();
	private FlexTable teamsTable = new FlexTable();

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
			AsyncCallback<TreeSet<TeamStageClient>> callback = new AsyncCallback<TreeSet<TeamStageClient>>() {
				public void onFailure(Throwable caught) {
					status.setText("Chyba serveru. Zkontroluj db.");
				}

				public void onSuccess(TreeSet<TeamStageClient> result) {
					status.setText("Výpis týmů úspěšný.");
					int row = 0;
					teamsTable.setBorderWidth(1);
					teamsTable.setText(row, 0, "Pořadí");
					teamsTable.setText(row, 1, "Pozice");
					teamsTable.setText(row, 2, "Čas příchodu");
					teamsTable.setText(row, 3, "Název týmu");
					teamsTable.setText(row, 4, "Vzdali");
					++row;
					for (TeamStageClient teamStageClient : result) {
						teamsTable.setText(row, 0, String.valueOf(teamStageClient.getOrder()));
						teamsTable.setText(row, 1, teamStageClient.getStageName());
						String stageDate = DateTimeFormat.getFormat(PredefinedFormat.HOUR24_MINUTE_SECOND).format(teamStageClient.getStageDate());
						teamsTable.setText(row, 2, stageDate);
						teamsTable.setText(row, 3, teamStageClient.getTeamName());
						if (teamStageClient.isEnded()) {
							teamsTable.setText(row, 4,  "Ano");
							teamsTable.getRowFormatter().addStyleName(row, "teamEnded");
						} else {
							teamsTable.setText(row, 4,  "Ne");
							
						}
						++row;
					}
				}
			};

			// Make the call to the stock price service.
			teamStageAction.getTeamsByStageAndStageDate(callback);;
		}
	}

	public ListTeamsOnStagesPage() {

		/*
		 * create button and attach click handler
		 */
		Button button = new Button("Vypiš týmy");
		button.addClickHandler(new MyClickHandler());

		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel line = new HorizontalPanel();
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("300", "100");

		panel.add(button);
		panel.add(status);
		panel.add(teamsTable);
		

		initWidget(panel);
	}
	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		ListTeamsOnStagesPage atp = new ListTeamsOnStagesPage();
		RootPanel.get().add(atp);
	}

}
