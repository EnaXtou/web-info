package cz.plsi.webInfo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebInfo implements EntryPoint {
	private VerticalPanel panel;
	private Widget currentPage;
	private HorizontalPanel line;

	@Override
	public void onModuleLoad() {

		line = new HorizontalPanel();
		panel = new VerticalPanel();
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("300", "100");

		
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
		panel.add(line);

		DecoratorPanel decoratorPanel = new DecoratorPanel();
		decoratorPanel.add(panel);
		RootPanel.get().add(decoratorPanel);

	}

	private void changePage(Widget page) {
		if (currentPage != null) {
			panel.remove(currentPage);
		}
		panel.add(page);
		currentPage = page;
	}

}
