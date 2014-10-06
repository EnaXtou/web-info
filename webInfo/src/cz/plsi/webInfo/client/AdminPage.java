package cz.plsi.webInfo.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AdminPage extends Composite {
	private Widget currentPage;

	private VerticalPanel panel;
	private HorizontalPanel line;

	public AdminPage() {

		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		panel = new VerticalPanel();
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("100%", "100%");

		Button team;
		team = new Button("Přidej team");
		team.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				changePage(new AddTeamPage());
			}
		});

		line.add(team);

		Button stage = new Button("Přidej stage");
		stage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				changePage(new AddStagePage());
			}
		});

		line.add(stage);
		
		Button help = new Button("Přidej heslo pro nápovědu");
		help.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				changePage(new AddHelpPage());
			}
		});
		
		line.add(help);
		
		Button helpMinus = new Button("Odeber heslo pro nápovědu týmu");
		helpMinus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				changePage(new RemoveHelpFromTeamPage());
			}
		});
		
		line.add(helpMinus);
		panel.add(line);
		
		Button listStages = new Button("Zobraz stav hry");
		listStages.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				changePage(new ListTeamsOnStagesPage());
			}
		});
		
		line.add(listStages);
		panel.add(line);

		initWidget(panel);

	}

	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		AdminPage atp = new AdminPage();
		RootPanel.get().add(atp);
	}

	public void changePage(Widget page) {
		if (currentPage != null) {
			panel.remove(currentPage);
		}
		panel.add(page);
		currentPage = page;
	}

}
