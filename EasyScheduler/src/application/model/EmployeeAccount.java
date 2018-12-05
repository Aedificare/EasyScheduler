package application.model;

import java.io.*;
import java.util.Scanner;

public class EmployeeAccount {
	private Employee employee;
	private String accountName, password;

	public EmployeeAccount(String accountName, String password) {
		this.accountName = accountName;
		this.password = password;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
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

	/**
	 * Loads an employee's data from the data file sharing the same name Calling
	 * methods should pass in the employee's name
	 * 
	 * @param fileName
	 */
	public void loadEmployeeFromFile(String fileName) {
		File file = new File(fileName);
		Scanner scan = null;
		try {
			scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] tokens = line.split(",");
				if (tokens.length == 1) {
					switch (tokens[0]) {
					case "Availability":
						System.out.println("TODO");
					case "Schedule":
						System.out.println("TODO");
					default:
						System.out.println("handle error: data format in file");
					}
				}
				// handle the data in the file
				else if (tokens.length > 1) {

				} else
					// EOF
					System.out.println("EOF");
				;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}

	public void saveEmployeeToFile(String fileName) {
		File file = new File(fileName);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write("");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
