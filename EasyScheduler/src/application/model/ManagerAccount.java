package application.model;

public class ManagerAccount {
	private String accountName, password;
	
	public ManagerAccount(String accountName, String password) {
	this.accountName = accountName;
	this.password = password;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
