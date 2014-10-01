package cz.plsi.webInfo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebInfo implements EntryPoint {
	private final TextBox code = new TextBox();
	private final Label codeLabel = new Label("Zadej kód týmu pro přihlášení: ");
	
	private VerticalPanel panel;
	private Widget currentPage;
	private HorizontalPanel line;
	private TeamStageActionInterfaceAsync teamStageAction = GWT.create(TeamStageActionInterface.class);

	@Override
	public void onModuleLoad() {
		panel = new VerticalPanel();
		panel.ensureDebugId("mainPanelWebInfo");
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("100%", "100%");

		
		
		line = new HorizontalPanel();
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		line.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		line.ensureDebugId("lineCodeInfo");
		line.setSize("100%", "100%");
		line.add(codeLabel);
		line.add(code);
		
		Button loginButton = new Button("Login");
		loginButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (teamStageAction == null) {
					teamStageAction = GWT.create(TeamStageActionInterface.class);
				}

				// Set up the callback object.
				AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						// TODO: Do something with errors.
					}

					public void onSuccess(Integer result) {
						if (result.equals(0)) {
							changePage(new AdminPage());
						}
						if (result.equals(1)) {
							changePage(new TeamWebInfoPage(code.getValue()));
						}
					}
				};

				// Make the call to the stock price service.
				teamStageAction.loginTeam(code.getValue(), callback);
			}
			
		});
		
		line.ensureDebugId("lineloginButtonInfo");
		line.add(loginButton);
		panel.add(line);

		DecoratorPanel decoratorPanel = new DecoratorPanel();
		decoratorPanel.ensureDebugId("decorateInfo");
		decoratorPanel.add(panel);
		RootPanel.get().add(decoratorPanel);

	}

	public void changePage(Widget page) {
		if (currentPage != null) {
			panel.remove(currentPage);
		}
		panel.add(page);
		currentPage = page;
	}

}
