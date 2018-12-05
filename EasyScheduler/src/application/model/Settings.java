package application.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import application.Main;

public class Settings {

	private static ArrayList<String> jobTitles;
	private static int state;
	private static final int STATE_JOB_TITLES = 2, STATE_OPEN_SHIFTS = 3;
	public static final File file = new File("Settings/settings");
	
	public static void loadSettings() {
		jobTitles = new ArrayList<String>();
		ArrayList<Shift> newOpenShifts = new ArrayList<Shift>();
		Scanner scan = null;
		try {
			scan = new Scanner(file);
			while(scan.hasNextLine()) {
				
				String line = scan.nextLine();
				
				if(line.equals("$JOB TITLES:")) {
					state = STATE_JOB_TITLES;
					continue;
				}
				else if(line.equals("$OPEN SHIFTS:")) {
					state = STATE_OPEN_SHIFTS;
					continue;
				}
				switch(state){
				case STATE_JOB_TITLES:
					jobTitles.add(line);	
					break;
				case STATE_OPEN_SHIFTS:
					String[] tokens = line.split(" ");
					newOpenShifts.add(new Shift(new ShiftTime(tokens[0], tokens[1]), tokens[2]));
					break;
				default:
					return;
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			scan.close();
		}
		OpenShifts.setOpenShifts(newOpenShifts);
	}
	public static void saveSettings() {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("$JOB TITLES:\n");
			for(String job : jobTitles) {
				writer.write(job+"\n");
			}
			writer.write("$OPEN SHIFTS:\n");
			for(Shift shift: OpenShifts.getOpenShifts()) {
				writer.write(shift.toString()+"\n");
			}
			writer.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static ArrayList<String> getJobTitles(){
		return jobTitles;
	}
}
