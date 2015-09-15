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

public class AddStagePage extends Composite {
	private final TextBox order = new TextBox();
	private final Label orderLabel = new Label();
	private final TextBox name = new TextBox();
	private final Label nameLabel = new Label();
	private final TextBox description = new TextBox();
	private final Label descriptionLabel = new Label();
	private final TextBox help1 = new TextBox();
	private final Label help1Label = new Label();
	private final TextBox help2 = new TextBox();
	private final Label help2Label = new Label();
	private final TextBox result = new TextBox();
	private final Label resultLabel = new Label();

	private TeamStageActionInterfaceAsync teamStageAction = GWT
			.create(TeamStageActionInterface.class);

	public AddStagePage() {

		/*
		 * create button and attach click handler
		 */
		Button button = new Button("Přidej stage");
		button.addClickHandler(new ClickHandler() {

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
				teamStageAction.addStage(Integer.parseInt(order.getValue()),
						name.getValue(),
						description.getValue(),
						help1.getValue(),
						help2.getValue(),
						result.getValue(),
						callback);

			}
		});

		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSize("100%", "100%");

		HorizontalPanel line;

		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		orderLabel.setText("Pořadí stage: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(orderLabel);
		line.add(order);
		panel.add(line);

		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		nameLabel.setText("Název stage: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(nameLabel);
		line.add(name);
		panel.add(line);

		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		descriptionLabel.setText("Popis stage: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(descriptionLabel);
		line.add(description);
		panel.add(line);
		
		line = new HorizontalPanel();
		line.setSize("100%", "100%");
		help1Label.setText("Nápověda 1: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(help1Label);
		line.add(help1);
		panel.add(line);

		line = new HorizontalPanel();
		help2Label.setText("Nápověda 2: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(help2Label);
		line.add(help2);
		panel.add(line);

		line = new HorizontalPanel();
		resultLabel.setText("Řešení: ");
		line.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		line.add(resultLabel);
		line.add(result);
		panel.add(line);

		panel.add(button);
		initWidget(panel);
	}

	public void onModuleLoad() {
		// Create an optional text box and add it to the root panel.
		AddStagePage atp = new AddStagePage();
		RootPanel.get().add(atp);
	}

}
