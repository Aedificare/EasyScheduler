package application.model;

public class ShiftTime{
	private String day, time;
	//constants representing the day of the week
	public static final int SUN = 0, MON = 1, TUE = 2
			, WED = 3, THU = 4, FRI = 5, SAT = 6;
	//constants representing the morning or night shift
	public static final int AM = 0, PM = 1;
	
	public ShiftTime(String day, String time) {
		this.day = day;
		this.time = time;
	}
	public ShiftTime(Integer time, Integer day) {
		switch (time){
		case AM:
			this.time = "AM";
			break;
		case PM:
			this.time = "PM";
			break;
		default:
			this.time = "ERR";
			break;
		}
		switch(day) {
		case SUN:
			this.day = "SUN";
			break;
		case MON:
			this.day = "MON";
			break;
		case TUE:
			this.day = "TUE";
			break;
		case WED:
			this.day = "WED";
			break;
		case THU:
			this.day = "THU";
			break;
		case FRI:
			this.day = "FRI";
			break;
		case SAT:
			this.day = "SAT";
			break;
		default:
			this.day = "ERR";
			break;
		}
	}
	public String getDay() {
		return this.day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getTime() {
		return this.time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public static int toRow(String time) {
		switch (time){
		case "AM":
			return AM;

		case "PM":
			return PM;

		default:
			return -1;

		}
	}
	public static int toCol(String day) {
		switch(day) {
		case "SUN":
			return SUN;
		case "MON":
			return MON;
		case "TUE":
			return TUE;
		case "WED":
			return WED;
		case "THU":
			return THU;
		case "FRI":
			return FRI;
		case "SAT":
			return SAT;
		default:
			return -1;
		}
	}
	public String toString() {
		return this.getDay() + " " + this.getTime();
	}
	public boolean isSameSlot(ShiftTime shiftTime) {
		if(this.day.equals(shiftTime.getDay()) &&
				this.time.equals(shiftTime.getTime()))
			return true;
		return false;
	}
}
