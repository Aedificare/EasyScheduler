package application.controller;


public class ManagerLoginController extends LoginController{
	protected static String nextView = "view/Manager.fxml", storagePath = "Data/ManagerAccountFiles";

	@Override
	public String getNextViewPath() {
		return nextView;
	}

	@Override
	public String getAccountStoragePath() {
		return storagePath;
	}
}
