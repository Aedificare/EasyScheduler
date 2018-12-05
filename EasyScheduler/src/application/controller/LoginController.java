package application.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

public abstract class LoginController implements EventHandler<ActionEvent>,Initializable {

	@FXML
	PasswordField passwordField;
	/*@FXML
	PasswordField passwordField;*/
	@FXML
	TextField usernameField;
	@FXML
	Label warningLabel;
	@FXML
	Button homeButton;

	private final String WARN_EMPTY_PASSWORD = "Please enter your password!";
	private final String WARN_EMPTY_USERNAME = "Please enter your username!";
	private final String WARN_WRONG_PASSWORD = "Password is invalid, please try again";
	private final String WARN_ACCOUNT_NOT_FOUND = "We couldn't find that account, please try again";
	public static String username = "";

	
	/**
	 * This method handles the LOGIN button being pressed It evaluates the username
	 * against a list of files in the appropriate account folder In each account
	 * folder is the password for that account If the password entered matches,
	 * grant access
	 */
	public void handle(ActionEvent event) {
		
		if (usernameField.getText().isEmpty()) {
			showWarning(WARN_EMPTY_USERNAME);
		} else if (passwordField.getText().isEmpty()) {
			showWarning(WARN_EMPTY_PASSWORD);
		}
		File dir = new File(getAccountStoragePath());
		// check that is dir
		if (!dir.isDirectory()) {
			System.out.println("fatal error");
			return;
		}
		for (File accountFile : dir.listFiles()) {
			if (accountFile.getName().equals(this.usernameField.getText())) {
				// username matches, therefore account exists
				// now verify password
				Scanner scan = null;
				try {
					scan = new Scanner(accountFile);
					if (scan.nextLine().equals(passwordField.getText())) {
						username = usernameField.getText();
						// Authenticate and proceed to next view
						try {
							Parent root = FXMLLoader.load(Main.class.getResource(getNextViewPath()));
							Main.mainStage.setScene(new Scene(root));
							Main.mainStage.show();

						} catch (IOException e) {
							e.printStackTrace();
						}
					} else
						showWarning(WARN_WRONG_PASSWORD);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				showWarning(WARN_ACCOUNT_NOT_FOUND);
			}
		}

	}

	/**
	 * This method is called by the authenticator It must be implemented by the type
	 * of loginController extending this class
	 * 
	 * @return - Path to load the next view
	 */
	public abstract String getNextViewPath();

	/**
	 * This method is called by the authenticator It must be implemented by the type
	 * of loginController extending this class
	 * 
	 * @return - the directory containing account files
	 */
	public abstract String getAccountStoragePath();

	/**
	 * This method populates the warning Label with text The label is used to
	 * communicate important information to the user
	 * 
	 * @param warning
	 *            - string to be displayed
	 */
	public void showWarning(String warning) {
		warningLabel.setText(warning);
	}

	/**
	 * Returns the user to the welcome screen
	 * 
	 * @param event
	 */
	public void previousScreen(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("view/Welcome.fxml"));
			Main.mainStage.setScene(new Scene(root));
			Main.mainStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
