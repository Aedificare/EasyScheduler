package application.controller;

public class EmployeeLoginController extends LoginController {
	
	protected static String viewPath = "view/Employee.fxml";
	protected static String EmpAccFilePath = "Data/EmployeeAccountFiles";
	@Override
	public String getNextViewPath() {
		return viewPath;
	}

	@Override
	public String getAccountStoragePath() {
		return EmpAccFilePath;
	}
}
