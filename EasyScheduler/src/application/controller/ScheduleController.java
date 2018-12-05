package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import application.model.Employee;
import application.model.OpenShifts;
import application.model.Settings;
import application.model.Shift;
import application.model.ShiftTime;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public abstract class ScheduleController implements EventHandler<ActionEvent>, Initializable {
	@FXML
	Label warningLabel;
	@FXML
	GridPane weekView;
	@FXML
	Label titleLabel, helpLabel;
	@FXML
	ToggleGroup currentView;

	protected Employee employee;
	protected TextArea[][] tiles;

	protected Boolean inEditMode;
	protected boolean isSaved;
	protected boolean openShiftAction;
	
	protected int STATE;
	protected final int EDIT_STATE = 801, OPEN_SHIFT_STATE = 802;
	
	/**
	 * This method sets up the TextArea[][] for easier referencing 
	 */
	public void initialize() {
		Settings.loadSettings();
		inEditMode = false;
		openShiftAction = false;
		titleLabel.setText("Welcome "+LoginController.username);
		tiles = new TextArea[2][7];
		for (Node child : weekView.getChildren()) {
			Integer row = GridPane.getRowIndex(child);
			//Ignore the labels in the first row
			if (row == null)
				continue;
			Integer col = GridPane.getColumnIndex(child);
			// GridPane does not set 0 index, so treat null as zero index
			if (col == null)
				col = 0;

			tiles[row - 1][col] = (TextArea) child;
			child.setOnMouseClicked(e -> tileClicked(e));
			
		}
	}
public abstract void tileClicked(MouseEvent event);
	/**
	 * Displays an employee's schedule
	 * 
	 * @param event
	 */
public void showHelp(String help) {
	helpLabel.setText(help);
}
	public void displayEmployeeWorkWeek(ActionEvent event) {
		inEditMode = false;
		openShiftAction = false;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 7; j++) {
				ShiftTime shiftTime = new ShiftTime(i, j);
				if(employee.isWorking(shiftTime))
				tiles[i][j].setText(employee.getScheduledShift(shiftTime).getJobType() + "\n");
			}
		}
	}
	public void clearWeekTextAreas() {
		for (Node child : weekView.getChildren()) {
			if (child.getClass().equals(TextArea.class))
				((TextArea) child).clear();
		}
	}

	
	public void displayAvailableShifts(ActionEvent event) {
		displayAvailableShifts();
	}
	public void displayAvailableShifts(){
		STATE = OPEN_SHIFT_STATE;
		inEditMode = false;
		openShiftAction = true;
		clearWeekTextAreas();
	/*	if(OpenShifts.getOpenShifts().isEmpty())
			return;*/
		for(Shift openShift : OpenShifts.getOpenShifts()) {
			int row = ShiftTime.toRow(openShift.getTime());
			int col = ShiftTime.toCol(openShift.getDay());
			tiles[row][col].setText(tiles[row][col].getText()+openShift.getJobType()+"\n");
		}
	}
	
	/**
	 * Shows the availability for a given employee Requires another method to
	 * initialize Employee employee
	 * 
	 * @param event
	 */
	public void displayEmployeeAvailability(ActionEvent event) {
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 7; col++) {
				if (employee.getIsAvailable(new ShiftTime(row, col)))
					tiles[row][col].setText("Available\n");
				else
					tiles[row][col].setText("Unavailable\n");
			}
		}
	}

	/**
	 * This method saves the current data stored in the model to file
	 * 
	 * @param event
	 */
	public abstract void save(ActionEvent event);

	/**
	 * This method displays a warning/message to the user
	 * by populating the warningLabel in the view
	 * @param warning - the message to display
	 */
	public void showWarning(String warning) {
		warningLabel.setText(warning);
	}

	/**
	 * Returns the user to the main menu
	 * @param event
	 */
	public void logout(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("view/Welcome.fxml"));
			Main.mainStage.setScene(new Scene(root));
			Main.mainStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
