package application.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import application.Main;

public class Employee {
	
private String name;
private ArrayList<ShiftTime> availability;
private ArrayList<Shift> schedule;
private ArrayList<String> jobTitles;
private Boolean isFullTime;
public static final int ADDED_SHIFT = 701;
public static final int ALREADY_SCHEDULED = 702;
public static final int NOT_AVAILABLE = 703;
public static final int ON_SCHEDULE = 704;

public Employee(String name,ArrayList<ShiftTime> availability) {
	this.name = name;
	this.availability = availability;
	this.schedule = new ArrayList<Shift>(14);
}

public Employee(File file) {
	Employee.loadEmployee(file);
}
public boolean isWorking(ShiftTime shiftTime) {
	for(Shift shift: this.schedule) {
		if(shift.getShiftTime().isSameSlot(shiftTime)) {
			return true;
		}
	}
	return false;
}
public int addShift(Shift shift) {
	for(Shift scheduledShift : this.schedule) {
		if(scheduledShift.isSameShift(shift)) {
			return ON_SCHEDULE;
		}
	else if(scheduledShift.getShiftTime().isSameSlot(shift.getShiftTime()))
			return ALREADY_SCHEDULED;
	}
	for(ShiftTime shiftTime: this.availability) {
		if(shiftTime.isSameSlot(shift.getShiftTime())) {
			this.schedule.add(shift);
			return ADDED_SHIFT;
		}
	}
	return NOT_AVAILABLE;
}


/**
 * This method iterates over the availability list
 * It returns "available" if the shiftTime is in the list
 * @param shiftTime
 * @return
 */
public boolean getIsAvailable(ShiftTime shiftTime) {
	for(ShiftTime availableShiftTime : this.availability) {
		if(availableShiftTime.isSameSlot(shiftTime))
			return true;
	}
	return false;
}

public Shift getScheduledShift(ShiftTime shiftTime) {
	for(Shift shift : this.schedule) {
		if(shift.getShiftTime().isSameSlot(shiftTime))
			return shift;
	}
	return null;
}
/**
 * This method laods an employee from an employee file formatted as:
 * 
 * 	Job Titles:
 * 	Availability:
 * 	Schedule:
 * Where a list of : 
 * 			job title(s) is required
 * 			Availability is required
 * 			Schedule is optional(Employees can exist without being on the schedule)
 * 
 * 
 * @param file
 * @return
 */
public static Employee loadEmployee(File file) {
	Scanner scan = null;
	try {
		scan = new Scanner(file);

		ArrayList<ShiftTime> availability = new ArrayList<>();
		ArrayList<Shift> schedule = new ArrayList<>();
		ArrayList<String> jobTitles = new ArrayList<>();
		//First section to read from is Job Titles:
	/*	if(!scan.nextLine().equals("Job Titles:")) {
			//del
			System.out.println("something went wrong");}*/
		
		//Ignore first line of the file
		scan.nextLine();
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			//end of job titles, go on to availability
			if(line.equals("Availability:"))
				break;
			jobTitles.add(line);
		}
		//Begin reading in availability
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			//End of availability, go on to schedule
			if(line.equals("Schedule:")) 
				break;
			String[] tokens = line.split(" ");
			if (tokens.length == 2){
				ShiftTime shiftTime = new ShiftTime(tokens[0], tokens[1]);
				availability.add(shiftTime);
			}	
		}
			//Begin reading schedule
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] scheduleTokens = line.split(" ");
				if(scheduleTokens.length == 3) {
	/*		del		if(!jobTitles.contains(scheduleTokens[2])) {
						System.out.println("Error, employee scheduled for a job they are not trained for");
					}*/
					schedule.add(new Shift(new ShiftTime(scheduleTokens[0],scheduleTokens[1]), scheduleTokens[2]));
				}
			}
			Employee employee = new Employee(file.getName(),availability);
			employee.setWorkWeek(schedule);
			employee.setJobTitles(jobTitles);
			return employee;
		
	} catch(IOException e) {
		e.printStackTrace();
		return null;
	}finally {
		scan.close();
	}
}
/**
 * This method takes a string fileName which is the name of the file to save the current data to
 * employee files are named after the employees they describe
 * Employee files are formatted into three sections:
 * 
 * Job Titles:
 * ******
 * ******
 * Availability:
 * ******
 * ******
 * ******
 * Schedule:
 * ******
 * ******
 * ******
 * ------
 * 
 * @param fileName
 */
public void save(String fileName) {
	File file = new File(Main.DATA_DIR+"/"+fileName);
	FileWriter writer;
	try {
		//Open the file to write to
		writer = new FileWriter(file);
		//Write the titles first
		writer.write("Job Titles:\n");
		for(String title : this.jobTitles) {
			writer.write(title+"\n");
		}
		//Write the availability next
		writer.write("Availability:\n");
		for(ShiftTime shiftTime: this.availability) {
			writer.write(shiftTime.toString()+"\n");
		}
		// Lastly write the schedule if it is not empty
		if(!this.schedule.isEmpty()) {
		writer.write("Schedule:\n");
		for(Shift shift :this.schedule) {
			writer.write(shift.toString()+"\n");
		}
		writer.close();
		}
	}catch(IOException e) {
		e.printStackTrace();
	}
}
public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public ArrayList<ShiftTime> getAvailability() {
	return availability;
}
public synchronized void toggleAvailableShiftTime(ShiftTime shiftTime) {
	//If this shift is in availability, remove it
	for(ShiftTime available : this.availability) {
		if(available.isSameSlot(shiftTime)) {
			availability.remove(available);
			return;
		}
	}
	//Not found in availability, add it to the list
	this.availability.add(shiftTime);
}
public void setAvailability(ArrayList<ShiftTime> availability) {
	this.availability = availability;
}

public ArrayList<Shift> getWorkWeek() {
	return schedule;
}

public void setWorkWeek(ArrayList<Shift> workWeek) {
	this.schedule = workWeek;
}

public ArrayList<String> getJobTitles() {
	return jobTitles;
}

public void setJobTitles(ArrayList<String> jobTitles) {
	this.jobTitles = jobTitles;
}

public Boolean getIsFullTime() {
	return isFullTime;
}

public void setIsFullTime(Boolean isFullTime) {
	this.isFullTime = isFullTime;
}

/**
 * This method represents an employee being trained for a new job (cross training)
 * @param jobTitle - the new title the employee is permitted to work
 */
public void completedTrainingFor(String jobTitle) {
	this.jobTitles.add(jobTitle);
}

public void removeShift(ShiftTime shiftTime) {
	for(Shift shift : this.schedule) {
		if(shift.getShiftTime().isSameSlot(shiftTime)) {
			this.schedule.remove(shift);
			return;
		}
	}
}
}
