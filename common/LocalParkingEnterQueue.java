package common;

import java.util.ArrayList;
import java.util.List;

/**
* Class that implements the singleton design pattern.
* This class is the parking lot enter queue (id's queue) --> each parking lot has one like this.
* Aggregates all data about parking lot enter queue --> queue of id's
*/

public class LocalParkingEnterQueue {

	private static LocalParkingEnterQueue enterQueue;

	private List<String> queue;

	public static LocalParkingEnterQueue getInstance(){
		if(enterQueue == null){
			enterQueue = new LocalParkingEnterQueue();
		}
		return enterQueue;
	}

	public LocalParkingEnterQueue(){
		queue = new ArrayList<String>();
	}

	public void add(String id){
		queue.add(id);
	}
	public String remove(){
		if(queue.size() == 0){
			return "";
		}
		String id = queue.remove(0);
		return id;
	}

}
