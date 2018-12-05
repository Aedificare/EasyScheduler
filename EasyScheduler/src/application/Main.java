package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	public static Stage mainStage;
	// define the directory containing the employee schedule data
	public static final String DATA_DIR = "Data/EmployeeFiles";
	public static String EMPLOYEE_FILE = "default";
	@Override
	public void start(Stage primaryStage) {
		try {
			mainStage = primaryStage;
			Parent root = FXMLLoader.load(this.getClass().getResource("view/Welcome.fxml"));
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
