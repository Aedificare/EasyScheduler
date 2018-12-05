package application.model;

import java.util.ArrayList;

public class OpenShifts {

	private static ArrayList<Shift> openShifts = new ArrayList<Shift>();
	
	public static ArrayList<Shift> getOpenShifts() {
		return openShifts;
	}

	public static void setOpenShifts(ArrayList<Shift> newOpenShifts) {
		openShifts = newOpenShifts;
	}
	
	public static void addOpenShift(Shift shift) {
		openShifts.add(shift);
	}
	public static void removeOpenShift(Shift shift) {
		for(Shift openShift : openShifts) {
			if(openShift.isSameShift(shift)) {
				openShifts.remove(openShift);
			return;
		}
		}
	}
}