package common;

/**
* Class responsible for the parking algorithm of new inserted cars to parking lot
* --> When a new car is inserted to the parking lot the algorithm finds re-order all cars if needed and finds a place to the new car
*/

public class ParkingAlgo {

	public static String doParking(String parkingLotData, int parkingLotWidth, String operation){
		System.out.println("doParking called with:" + parkingLotData + "," + parkingLotWidth + "," + operation);
		return parkingLotData;
	}

}
