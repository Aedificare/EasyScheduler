package application.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Manager {
	private String name;
	private ArrayList<Employee> employeeRoster;
	private ArrayList<Shift> openShifts;
	public Manager(String name) {
		this.name = name;
		this.employeeRoster = new ArrayList<>();
		openShifts = new ArrayList<>();
	}
	public void addOpenShift(Shift shift) {
		this.openShifts.add(shift);
	}
	/**
	 * This method removes a shift from the openShifts list when a shift is covered by an employee
	 * @param shift
	 */
	public void shiftCovered(Shift shift) {
		for(Shift openShift : openShifts) {
			if(openShift.isSameShift(shift)) {
				openShifts.remove(openShift);
				return;
			}
		}
	}
	/**
	 * Loads all the employees in the Employeefiles directory
	 * @param dirName
	 */
	public void loadEmployees(String dirName) {
		File dir = new File(dirName);
		//Iterate over the list of all employees in the directory
		// Read each employee file
		for(File employeeFile : dir.listFiles()) {
		Employee employee = Employee.loadEmployee(employeeFile);
		if(employee != null) 
			this.employeeRoster.add(employee);
	}
	}
	public ArrayList<Employee> getRoster(){
		return this.employeeRoster;
	}
	public Employee getEmployee(String name) {
		for(Employee employee: employeeRoster)
			if(employee.getName().equals(name))
				return employee;
		return null;
	}
	public ArrayList<Employee> getEmployeesOnShift(Shift shift){
		ArrayList<Employee> retList = new ArrayList<>();
		for(Employee employee : this.employeeRoster) {
			if(employee.getWorkWeek().contains(shift)) {
				retList.add(employee);
				continue;
			}
		}
		return retList;
	}
	public ArrayList<String> getEmployeeNames(){
		ArrayList<String> retList = new ArrayList<>();
			for(Employee employee: this.employeeRoster) {
				retList.add(employee.getName());
			}
			return retList;
	}
	/**
	 * This method saves all of the employee data currently in employeeRoster to-
	 * the directory of employee files. Each employee has a file named with their name
	 * and containing their relevant information.
	 * 
	 * @param dirName - the directory containing all of the employee files
	 */
 	public void save(String dirName) {
		File dir = new File(dirName);
		// Iterate over the list of all employees
		for(Employee employee: this.employeeRoster) {
			employee.save(employee.getName());
		}
		
	}
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}
}
