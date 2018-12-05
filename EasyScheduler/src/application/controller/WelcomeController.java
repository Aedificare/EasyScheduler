package application.controller;

import java.io.IOException;
import application.Main;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;

public class WelcomeController implements EventHandler<ActionEvent>{

	@Override
	/**
	 * Help button
	 * Shows basic usage for the application
	 */
	public void handle(ActionEvent event) {
		
	}
	public void employeePressed(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("view/EmployeeLogin.fxml"));
			Main.mainStage.setScene(new Scene(root));
			Main.mainStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void managerPressed(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("view/ManagerLogin.fxml"));
		Main.mainStage.setScene(new Scene(root));
		Main.mainStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
