package application.model;
/**
 * A Shift is an aggregate of a shift time(ShiftTime) and a job type(JobType)
 * @author Kenny
 *
 */
public class Shift {
	private ShiftTime shiftTime;
	private String type;


	public Shift(ShiftTime shiftTime, String type) {
		this.shiftTime = shiftTime;
		this.type = type;
	}
	
	/**
	 * Get the day of the week this shift lands on
	 * @return "SUN", "MON", "TUE", etc.
	 */
	public String getDay() {
		return shiftTime.getDay();
	}
	public ShiftTime getShiftTime() {
		return this.shiftTime;
	}
	public boolean isSameShift(Shift shift) {
		if(this.shiftTime.isSameSlot(shift.getShiftTime()))
			if(this.type.equals(shift.getJobType()))
				return true;
		return false;
	}
	/**
	 * Shift time is either AM or PM
	 * @return - "AM" or "PM"
	 */
	public String getTime() {
		return shiftTime.getTime();
	}
	/**
	 * Employees must have the job title for the shift they are working
	 * @return - the title required to work this shift
	 */
	public String getJobType() {
		return this.type;
	}
	public String toString() {
		return this.shiftTime.toString()+" "+this.getJobType();
	}
}
