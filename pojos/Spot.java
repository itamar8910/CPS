package pojos;

/**
* Spot class containing all Spot properties needed
* Implements Comparable, meaning Spot objects can be compared (compared by exitTime)
* Properties (of each spot): status, carID, entryTime, exitTime
*/

public class Spot implements Comparable{
	private char status;
	private String carID;
	private long entryTime;
	private long exitTime;
	
	@Override
	public int compareTo(Object o) {
		return (this.exitTime > ((Spot)o).getExitTime()) ? 1 : -1;
	}
	
	Spot(){
		status = 'e'; // e - EMPTY, f - FULL, i - INVALID, s - SAVE, o - ORDERED
		carID = "";
		entryTime = 0;
		exitTime = 0;
	}
	
	Spot(char status, String carID, long exitTime, long entryTime){
		this.status = status;
		this.carID = carID;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
	}
	
	public int getStatus(){
		return status;
	}
	
	public char getStatusChar() {
		return (char)status;
	}
	
	public String getCarID(){
		return carID;
	}
	
	public long getEntryTime(){
		return entryTime;
	}
	
	public long getExitTime(){
		return exitTime;
	}
	
	public void setStatus(char status){
		this.status = status;
	}
	
	public void setcarID(String carID){
		this.carID = carID;
	}
	
	public void setEntryTime(long entryTime){
		this.entryTime = entryTime;
	}
	
	public void setExitTime(long exitTime){
		this.exitTime = exitTime;
	}
}