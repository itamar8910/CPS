package algorithm;

public class Car implements Comparable{
	private String carID;
	private long entryTime;
	private long exitTime;
	
	@Override
	public int compareTo(Object o){
		return (int) (this.entryTime - ((Car)o).getEntryTime());
	}
	
	Car(){
		carID = "";
		entryTime = 0;
		exitTime = 0;
	}
	
	public Car(String carID, long exitTime, long entryTime){
		this.carID = carID;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
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
	
	public void setCarID(String carID){
		this.carID = carID;
	}
	
	public void setEntryTime(int entryTime){
		this.entryTime = entryTime;
	}
	
	public void setExitTime(int exitTime){
		this.exitTime = exitTime;
	}
}