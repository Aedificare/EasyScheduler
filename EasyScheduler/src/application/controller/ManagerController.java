package application.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Main;
import application.model.Employee;
import application.model.Manager;
import application.model.OpenShifts;
import application.model.Settings;
import application.model.Shift;
import application.model.ShiftTime;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class ManagerController extends ScheduleController {

	private Manager manager;
	@FXML
	MenuButton singleEmployeeMenu, employeeViewButton, rosterMenuButton;
	@FXML
	RadioButton singleEmployeeRadioButton;
	@FXML
	Menu jobTitleMenu;
	@FXML
	MenuItem menuItem;
	@FXML
	HBox menuContainer, radioButtons;
	@FXML
	TextArea shiftReportArea;
	@FXML
	Label currentDisplayLabel;
	@FXML
	ToggleButton removeShiftMode;

	/**
	 * Constants used by this program
	 */
	private final String WARN_UNAVAILABLE = "Warning: Employee unavailable for selected shift: ";
	private final String WARN_ALREADY_SCHEDULED = "Warning: Employee is already scheduled for the shift: ";
	private final String ADDED_SHIFT = "Added employee to the schedule!";
	private final String WARN_EMPLOYEE_NOT_FOUND = "Warning: Could not find an employee by the name: ";
	private final String WARN_INVALID_JOB_TYPE = "Warning: That job type is not listed in your settings file!";
	private final String CANNOT_SAVE_IN_EDIT_STATE = "Warning: You cannot save the schedule while in edit mode";
	private final String USAGE_ALL_EMPLOYEE = "Usage: 'EmployeeName - JobTitle'";
	
	private final int SINGLE_EMPLOYEE_STATE = 901, ALL_EMPLOYEE_STATE = 902, SPECIFIC_EMPLOYEE_STATE = 903;

	private final String STYLE_WORKING = "-fx-background-color: rgba(0, 255, 0);";
	private final String STYLE_OFF = "-fx-background-color: rgba(255, 0, 0);";

	public final String SHIFT_REPORT_FORMAT = "Format:\nfullName - number of shifts working this week\n\n";
	// private Employee currentEmployeeName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize();
		this.manager = new Manager(LoginController.username);
		manager.loadEmployees(Main.DATA_DIR);
		shiftReportArea.visibleProperty().bind(weekView.visibleProperty().not());
		isSaved = true;
		inEditMode = false;
	}

	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
	}

	public void save(ActionEvent event) {
		Manager saveManager = this.manager;
		showWarning("");
		if(STATE == EDIT_STATE)
		{
			showWarning(CANNOT_SAVE_IN_EDIT_STATE);
			return;
		}
		
	//	if(STATE == OPEN_SHIFT_STATE) {
			ArrayList<Shift> openShifts = new ArrayList<Shift>();
		
		// Iterate over every textArea in the view
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 7; col++) {
				String area = tiles[row][col].getText();
				// If the tile contains no data, skip over it
				if (area.isEmpty())
					continue;

				ShiftTime shiftTime = new ShiftTime(row, col);
				//The current state defines the pattern to extract and verify data
				//from the current view
				switch (STATE) {

				case OPEN_SHIFT_STATE: {
					String[] lines = area.split("\n");
					for(String line : lines) {
						if(!Settings.getJobTitles().contains(line)) {
							showWarning(WARN_INVALID_JOB_TYPE);
							return;
						}
						Shift shift = new Shift(shiftTime, line);
						openShifts.add(shift);
					}
					break;
				}
				// A cell with data while in single employee view must be a single line
				// containing a job title and employeeName before an approstrophe
				case SINGLE_EMPLOYEE_STATE: {
					String currentEmployeeName = currentDisplayLabel.getText().substring(0,
							currentDisplayLabel.getText().indexOf('\''));

					// ShiftTime shiftTime = new ShiftTime(row, col);
					Shift shift = new Shift(shiftTime, area.trim());
					switch (saveManager.getEmployee(currentEmployeeName).addShift(shift)) {
					case Employee.NOT_AVAILABLE:
						showWarning(WARN_UNAVAILABLE + shiftTime.toString());
						break;
					case Employee.ALREADY_SCHEDULED:
						showWarning(WARN_ALREADY_SCHEDULED + shift.toString());
						break;
					case Employee.ADDED_SHIFT:
						showWarning(ADDED_SHIFT);
						break;
					case Employee.ON_SCHEDULE:
						break;
					default:
						continue;
					}
					break;
				}
				case SPECIFIC_EMPLOYEE_STATE: {
					String[] lines = area.split("\n");
					for (String employeeName : lines) {
						String token = currentDisplayLabel.getText();
						String currentJob = token.substring(token.indexOf("for") + 4, token.length());
						Shift shift = new Shift(shiftTime, currentJob);
						if (!saveManager.getEmployeeNames().contains(employeeName)) {
							showWarning(WARN_EMPLOYEE_NOT_FOUND + employeeName);
							return;
						}
						switch (saveManager.getEmployee(employeeName).addShift(shift)) {
						case Employee.NOT_AVAILABLE:
							showWarning(WARN_UNAVAILABLE + shiftTime.toString());
							break;
						case Employee.ON_SCHEDULE:
							break;
						case Employee.ALREADY_SCHEDULED:
							showWarning(WARN_ALREADY_SCHEDULED + shift.toString());
							break;
						case Employee.ADDED_SHIFT:
							showWarning(ADDED_SHIFT);
							break;
						
						default:
							break;
						}
					}
					break;
				}
				case ALL_EMPLOYEE_STATE: {
					String[] lines = area.split("\n");
					for (String line : lines) {
						String[] tokens = line.split(" - ");
						if(tokens.length<2) {
							showWarning(USAGE_ALL_EMPLOYEE);
							return;
						}
						if (!Settings.getJobTitles().contains(tokens[1])) {
							showWarning(WARN_INVALID_JOB_TYPE);
							return;
						}
						else if (!saveManager.getEmployeeNames().contains(tokens[0])) {
							showWarning(WARN_EMPLOYEE_NOT_FOUND + tokens[0]);
							return;
						}
						Shift shift = new Shift(shiftTime, tokens[1]);
						switch (saveManager.getEmployee(tokens[0]).addShift(shift)) {
						case Employee.NOT_AVAILABLE:
							showWarning(WARN_UNAVAILABLE + shiftTime.toString());
							break;
						case Employee.ALREADY_SCHEDULED:
							showWarning(WARN_ALREADY_SCHEDULED + shift.toString());
							break;
						case Employee.ADDED_SHIFT:
							showWarning(ADDED_SHIFT);
							break;
						case Employee.ON_SCHEDULE:
							break;
						default:
							continue;
						}
					} break;
				}
					
				}
			}
		}
		if(STATE == OPEN_SHIFT_STATE) {
			OpenShifts.setOpenShifts(openShifts);
			Settings.saveSettings();
			return;
		}
		manager = saveManager;
		manager.save(Main.DATA_DIR);
	}

	public void updateVisableMenus(MenuButton makeVisable) {
		hideMenus();
		makeVisable.setVisible(true);
	}

	public void hideMenus() {
		for (Node child : menuContainer.getChildren()) {
			// if (child.getClass().equals(MenuButton.class))
			// ((MenuButton) child).setVisible(false);
			child.setVisible(false);
		}
		currentDisplayLabel.setText("");
		showHelp("");
	}

	public void showEmployeeView(String employeeName) {
		// currentEmployeeName = employeeName;
		employee = manager.getEmployee(employeeName);
		employeeViewButton.setVisible(true);
		removeShiftMode.setVisible(true);
		currentDisplayLabel.setText(employeeName + "'s schedule");
	}

	public void reportsSelected(ActionEvent event) {
		hideMenus();
		clearPaint();
		weekView.visibleProperty().set(false);
		String areaText = SHIFT_REPORT_FORMAT;
		for (Employee employee : manager.getRoster()) {
			areaText += employee.getName() + " - " + String.valueOf(employee.getWorkWeek().size()) + "\n";
		}
		shiftReportArea.setText(areaText);
	}

	public void singleEmployeeSelected(ActionEvent event) {
		STATE = SINGLE_EMPLOYEE_STATE;
		clearWeekTextAreas();
		weekView.setVisible(true);

		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		for (String employeeName : manager.getEmployeeNames()) {
			MenuItem menuItem = new MenuItem(employeeName);
			menuItem.setOnAction(e -> showEmployeeView(employeeName));
			menuItems.add(menuItem);
		}
		singleEmployeeMenu.getItems().setAll(menuItems);
		updateVisableMenus(singleEmployeeMenu);
	}

	public void employeeRosterSelected(ActionEvent event) {
		clearWeekTextAreas();
		clearPaint();
		weekView.setVisible(true);
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		for (String jobTitle : Settings.getJobTitles()) {
			MenuItem menuItem = new MenuItem(jobTitle);
			menuItem.setOnAction(e -> displaySpecificTitle(jobTitle));
			menuItems.add(menuItem);
		}
		jobTitleMenu.getItems().setAll(menuItems);
		updateVisableMenus(rosterMenuButton);
	}

	public void displaySpecificTitle(String jobTitle) {
		STATE = SPECIFIC_EMPLOYEE_STATE;
		clearWeekTextAreas();
		for (Employee employee : manager.getRoster()) {
			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 7; col++) {
					ShiftTime timeSlot = new ShiftTime(row, col);
					if(!employee.isWorking(timeSlot))
						continue;
					String result = employee.getScheduledShift(timeSlot).getJobType();
					if (result.equals(jobTitle))
						tiles[row][col].setText(tiles[row][col].getText() + employee.getName() + "\n");
				}
			}
		}
		currentDisplayLabel.setText("Scheduled employees for " + jobTitle);
	}

	public void displayFullSchedule(ActionEvent event) {
		STATE = ALL_EMPLOYEE_STATE;
		clearWeekTextAreas();
		for (Employee employee : manager.getRoster()) {
			for (int row = 0; row < 2; row++) {
				for (int col = 0; col < 7; col++) {
					ShiftTime timeSlot = new ShiftTime(row, col);
					if(!employee.isWorking(timeSlot))
						continue;
					String result = employee.getScheduledShift(timeSlot).getJobType();
					if (!result.isEmpty())
						tiles.clone()[row][col]
								.setText(tiles[row][col].getText() + employee.getName() + " - " + result + "\n");
				}
			}
		}
		currentDisplayLabel.setText("Schedule for all employees");
	}

	public void clearPaint() {
		for (Node child : weekView.getChildren()) {
			if (child.getClass().equals(TextArea.class)) {
				child.setStyle("");
				((TextArea) child).clear();
			}
		}
	}

	public void removeShiftMode(ActionEvent event) {
		inEditMode = !inEditMode;
	
		if (!inEditMode) {
			STATE = SINGLE_EMPLOYEE_STATE;
			radioButtons.setDisable(false);
			clearPaint();
			showHelp("");
			showWarning("");
		} else
		{	
			showHelp("Click on a shift to remove from this employee's schedule");
			showWarning("");
			STATE = EDIT_STATE;
			radioButtons.setDisable(true);
			paintShifts();
		}
	}

	public void paintShifts() {

		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 7; col++) {
				ShiftTime shiftTime = new ShiftTime(row, col);
				// if(manager.getEmployee(employee.getName()).isWorking(shiftTime)) {
				if (employee.isWorking(shiftTime)) {
					tiles[row][col].setStyle(STYLE_WORKING);
					tiles[row][col].setText(employee.getScheduledShift(shiftTime).getJobType());
				} else {
					tiles[row][col].setStyle(STYLE_OFF);
					tiles[row][col].clear();
				}
			}
		}
	}

	public void tileClicked(MouseEvent event) {

		if (!removeShiftMode.isSelected())
			return;
		Integer row = GridPane.getRowIndex((Node) event.getSource());
		if (row == null)
			return;
		TextArea tile = (TextArea) event.getSource();
		Integer col = GridPane.getColumnIndex(tile);
		// GridPane does not set 0 index, so treat null as zero index
		if (col == null)
			col = 0;
		ShiftTime shiftTime = new ShiftTime(row - 1, col);
		// manager.getEmployee(employee.getName()).removeShift(shiftTime);
		employee.removeShift(shiftTime);
		OpenShifts.addOpenShift(new Shift(shiftTime, tile.getText()));
		paintShifts();
	}

	public void newOpenShift(MouseEvent event) {

	}
}