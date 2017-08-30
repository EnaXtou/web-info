package cz.plsi.webInfo.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebInfo implements EntryPoint {
	private static final String TEAM_CODE_COOKIE_NAME = "PlSiWI_TEAM_CODE";
	private final TextBox code = new TextBox();
	private final Label codeLabel = new Label("Kód týmu: ");
	
	private VerticalPanel panel;
	private Widget currentPage;
	private HorizontalPanel line;
	private TeamStageActionInterfaceAsync teamStageAction = GWT.create(TeamStageActionInterface.class);

	@Override
	public void onModuleLoad() {
		panel = new VerticalPanel();
		panel.ensureDebugId("mainPanelWebInfo");
		panel.setSpacing(8);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panel.setSize("100%", "100%");
		
		
		line = new HorizontalPanel();
		//
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		line.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		line.ensureDebugId("lineCodeInfo");
		line.setSize("100%", "100%");
		code.setSize("6em", "1em");
		//
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
						changeTabToTeam(result);
					}
				};

				teamStageAction.loginTeam(code.getValue().toLowerCase(), callback);
			}
			
		});
		
		line.ensureDebugId("lineloginButtonInfo");
		line.add(loginButton);
		panel.add(line);

		DecoratorPanel decoratorPanel = new DecoratorPanel();
		decoratorPanel.ensureDebugId("decorateInfo");	
		decoratorPanel.add(panel);
		int clientWidth = Window.getClientWidth();
		
//		Window.addResizeHandler(new WindowResizeHandler())
		RootPanel.get().setSize(clientWidth + "px", "95%");
		RootPanel.get().add(decoratorPanel);
		
		String teamCodeFromCookie = Cookies.getCookie(TEAM_CODE_COOKIE_NAME);
		if (teamCodeFromCookie != null) {
			//TODO better security
			code.setText(teamCodeFromCookie);
			changeTabToTeam("rudolfove".equals(teamCodeFromCookie)? 0 : 1);
		}

	}

	public void changePage(Widget page) {
		if (currentPage != null) {
			panel.remove(currentPage);
		}
		panel.add(page);
		currentPage = page;
	}

	private void changeTabToTeam(Integer result) {
		Date tomorrow = new Date();
		CalendarUtil.addDaysToDate(tomorrow, 1);
		if (result.equals(0)) {
			changePage(new AdminPage());
			Cookies.setCookie(TEAM_CODE_COOKIE_NAME, code.getValue().toLowerCase(), tomorrow);
		} else if (result.equals(1)) {
			changePage(new TeamWebInfoPage(code.getValue().toLowerCase()));
			Cookies.setCookie(TEAM_CODE_COOKIE_NAME, code.getValue().toLowerCase(), tomorrow);
			
		} else {
			changePage(new Label("Chyba: Neznámý tým."));
		}
	}
	
	

}
