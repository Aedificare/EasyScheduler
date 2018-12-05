package application.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import application.model.Employee;
import application.model.OpenShifts;
import application.model.Settings;
import application.model.Shift;
import application.model.ShiftTime;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmployeeController extends ScheduleController {

	private final String STYLE_AVAILABLE = "-fx-background-color: rgba(0, 255, 0);";
	private final String STYLE_UNAVAILABLE = "-fx-background-color: rgba(255, 0, 0);";
	
	private final String WARN_UNAVAILABLE = "You have specified that you are unavailable on: ";
	private final String WARN_ALREADY_SCHEDULED = "You are already on the schedule for: ";
	
	private String result;

	@Override
	public void handle(ActionEvent event) {
	}

	/**
	 * This method calls the super to populate the Text[][] This method populates
	 * the model with data from the appropriate file It displays a welcome message
	 * to the employee with their name
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize();
		File file = new File(Main.DATA_DIR + "/" + LoginController.username);
		employee = Employee.loadEmployee(file);
	}

	/**
	 * Passes the filename to the model to save the data to file
	 */
	public void save(ActionEvent event) {
		employee.save(LoginController.username);
		Settings.saveSettings();
	}

	/**
	 * This method enters edit mode, an interactive state where an employee user can
	 * change their availability. It applys css and enables the user to simply click
	 * on a tile to toggle between available and unavailable Tiles are painted green
	 * and red for available and unavailable respectively
	 * 
	 * @param event
	 */
	public void editAvailability(ActionEvent event) {
		// Once edit mode is entered, create a listener that clear the css upon exiting
		currentView.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				for (Node child : weekView.getChildren()) {
					if (child.getClass().equals(TextArea.class)) {
						child.setStyle("");
						showHelp("");
					}
				}
			}

		});
		inEditMode = true;
		showHelp("Click on the shifts you'd like to change.\n"
				+ "Green tiles are available.\nRed tiles are unavailable");
		paintAvailability();
	}

	/**
	 * This method is called while in edit mode It toggles availability and calls
	 * paintAvailability to update the view
	 * 
	 * @param event
	 *            - the source of the event is the TextArea clicked by the user
	 */
	public void tileClicked(MouseEvent event) {
		showWarning("");
		if (!(inEditMode || openShiftAction))
			return;
		Integer row = GridPane.getRowIndex((Node) event.getSource());
		Integer col = GridPane.getColumnIndex((Node) event.getSource());
		// GridPane does not set 0 index, so treat null as zero index
		if (row == null)
			return;
		if (col == null)
			col = 0;
		// TextArea currentArea = weekView.getChildren()

		ShiftTime shiftTime = new ShiftTime(row - 1, col);
		if (inEditMode) {
			employee.toggleAvailableShiftTime(shiftTime);
			// TextArea currentArea = ( TextArea ) event.getSource();
			paintAvailability();
		} else {
			// If the user clicked on an empty box, ignore it
			String line = ((TextArea) event.getSource()).getText();
			if (line.isEmpty())
				return;
			// If there is more than one job type in the textArea, display popup window and
			// choose from the available options
			if (line.matches("(?s).*[\n\r].+")) {

				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.initOwner(Main.mainStage);
				VBox dialogVbox = new VBox(12);
				dialogVbox.getChildren()
						.add(new Text("Please confirm one of the available job types for this shift time slot"));
				String[] jobs = line.split("\n");
				ToggleGroup group = new ToggleGroup();
				for (String job : jobs) {
					RadioButton option = new RadioButton();
					option.setText(job);
					group.getToggles().add(option);
					dialogVbox.getChildren().add(option);
				}
				Button closeButton = new Button();
				closeButton.setText("Choose");
				closeButton.setOnAction(e -> closePopup(dialog, group.getSelectedToggle()));
				dialogVbox.getChildren().add(closeButton);
				Scene dialogScene = new Scene(dialogVbox, 400, 300);
				dialog.setScene(dialogScene);
				dialog.showAndWait();

				if (result == null) {
					showWarning("No selection made!");
				}
				// Add the shift to the employee's schedule
				Shift shift = new Shift(shiftTime, result);
				if(!scheduleEmployeeFor(shift))
					return;
				// remove the shift from the list of open shifts
				OpenShifts.removeOpenShift(shift);
				displayAvailableShifts();
			}
			// User selected a box with only one job title, so add it to their schedule
			else {
				// Add the shift to the employee's schedule
				Shift shift = new Shift(shiftTime, line);
				if(!scheduleEmployeeFor(shift))
					return;
				// remove the shift from the list of open shifts
				OpenShifts.removeOpenShift(shift);
				displayAvailableShifts();
			}

		}
	}

	public boolean scheduleEmployeeFor(Shift shift) {
		switch (employee.addShift(shift)) {
		case Employee.NOT_AVAILABLE:
			showWarning(WARN_UNAVAILABLE + shift.getShiftTime().toString());
			return false;
		case Employee.ALREADY_SCHEDULED:
			showWarning(WARN_ALREADY_SCHEDULED+employee.getScheduledShift(shift.getShiftTime()));
			return false;
		case Employee.ADDED_SHIFT:
			return true;
		case Employee.ON_SCHEDULE:
			return false;
		default:
			return false;
		}
	}
	public void closePopup(Stage popUp, Toggle selection) {
		result = ((RadioButton) selection).getText();
		popUp.close();

	}

	/**
	 * This method is called when in edit mode This method applies css to the
	 * TextAreas to make setting availability simple for the user The method pulls
	 * the availability currently stored in the model and paints the respective
	 * tiles green and the unavailable tiles red
	 */
	public void paintAvailability() {
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 7; col++) {
				/*
				 * for (Node child : weekView.getChildren()) { Integer row =
				 * GridPane.getRowIndex(child); if (row == null) continue; Integer col =
				 * GridPane.getColumnIndex(child); // GridPane does not set 0 index, so treat
				 * null as zero index if (col == null) col = 0;
				 */
				TextArea currentArea = tiles[row][col];
				ShiftTime shiftTime = new ShiftTime(row, col);
				if (employee.getIsAvailable(shiftTime)) {
					currentArea.setStyle(STYLE_AVAILABLE);
					currentArea.setText("Available");
				} else {
					currentArea.setStyle(STYLE_UNAVAILABLE);
					currentArea.setText("Unavailable");
				}
			}
		}
	}
	/*
	 * public void displayAvailableShifts(ActionEvent event) { // inEditMode =
	 * false; // openShiftAction = true; super.displayAvailableShifts(event);
	 * 
	 * }
	 */

}
