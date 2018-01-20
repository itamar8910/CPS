package algorithm;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pojos.Car;

/**
 * Class to arrange the handling of the park
 * @author ~~ Etgar ~~ team,  Software Engineering course 2018
 * @version 1.0
 * @since 10.1.18
 */
public class Algorithm{

	private static class ArrayListCar extends ArrayList<Car>{

	}
	/**
	 * this is the number of cars in one row
	 */
	private int spotsInRow; // as an input from server. possible value: 4,5,6,7,8
	/**
	 * 3-dimension array holds list of cars in each cell
	 */
	private ArrayListCar[][][] park; // each spot has a list of all the cars belong to the spot
	/**
	 * 3-dimension array holds the status in each spot
	 */
	private char[][][] statusPark; // real time 3-dimensions array that has status for each spot
	// e - EMPTY, f - FULL, i - INVALID, s - SAVE, o - ordered
	
	/**
	 * default constructor which sets up an empty parking
	 */
	public Algorithm(){ // CONSTRUCTOR
		spotsInRow = 4; // spotsInRow = 4 as an initial value
		statusPark = new char[3][3][4];
		park = new ArrayListCar[3][3][4];

		int i, j, k;
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for(k = 0; k < 4; k++) {
					park[i][j][k] = new ArrayListCar(); // each cell has an empty list as an initial state
					statusPark[i][j][k] = 'e';
				}
		}
	
	/**
	 * 2nd constructor which sets up a parking with given number of cars in a row
	 * @param numSpotsInRow number of cars in a row
	 */
	public Algorithm(int numSpotsInRow){ // 2nd CONSTRUCTOR
		statusPark = new char[3][3][numSpotsInRow];
		park = new ArrayListCar[3][3][numSpotsInRow];
		spotsInRow = numSpotsInRow;

		int i, j, k;
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for (k = 0; k < numSpotsInRow; k++) {
					park[i][j][k] = new ArrayListCar();
					statusPark[i][j][k] = 'e';
				}
	}
	
	/**
	 * 3rd constructor which sets up the parking with given database from the server
	 * @param numSpotsInRow number of cars in one row
	 * @param db database of the parking as string
	 * @param si string describes the status in each spot in the park
	 */
	public Algorithm(int numSpotsInRow, String db, String si){ // 3rd CONSTRUCTOR
		this.spotsInRow = numSpotsInRow;
		this.park = generateParkFromString(db);
		this.statusPark = generateStatusPark(si);

		int i, j, k;
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for (k = 0; k < numSpotsInRow; k++)
					// sort by exit time. It also means it is sorted by entry time.
					Collections.sort(park[i][j][k]);
	}
	
	/**
	 * method creates the 3-dimensions array with the given string of status in each spot
	 * @param si status in each spot
	 * @return 3-dimensions char array of the given string
	 */
	private char[][][] generateStatusPark(String si){
		//TODO: make row width dynamic
		char[][][] tmpStatuses = new char[3][3][spotsInRow];
		try {
			System.out.println("-------------------------testing : " + si);

			JSONArray statuses = new JSONArray(si);
			for(int index = 0; index < statuses.length(); index++) {
				JSONObject current = statuses.getJSONObject(index);

				System.out.println("Using : " + current.toString());
				int i = current.getInt("i");
				int j = current.getInt("j");
				int k = current.getInt("k");

				tmpStatuses[i][j][k] = current.getString("status").charAt(0);


			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println("Fuckkinngg heereere");
			e.printStackTrace();
		}
		return tmpStatuses;
	}

	/**
	 * method creates 3-dimensions array of the park
	 * @param st string of the database
	 * @return 3-dimensions array of the park
	 */
	private ArrayListCar[][][] generateParkFromString(String st){
		ArrayListCar[][][] cars = new ArrayListCar[3][3][spotsInRow];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				for(int k = 0; k < spotsInRow; k++)
					cars[i][j][k] = new ArrayListCar();
		try {
			JSONArray arr = new JSONArray(st);
			for(int index = 0; index < arr.length(); index++) {
				JSONObject current = arr.getJSONObject(index);
				int i = current.getInt("i");
				int j = current.getInt("j");
				int k = current.getInt("k");
				String carID = current.getString("carID");
				long entryTime = current.getLong("entryTime");
				long exitTime = current.getLong("exitTime");
				if(cars[i][j][k] == null) {
					cars[i][j][k] = new ArrayListCar();
				}
				cars[i][j][k].add(new Car(carID, entryTime, exitTime));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cars;
	}

	//------------------------//

		/**
		 * function checks whether specific spot is empty in real time or not
		 * @param i depth in the park
		 * @param j floor in the park
		 * @param k width in the park
		 * @return true when the spot is empty in the current time, otherwise returns false
		 */
		private boolean isEmptySpotRealTime(int i, int j, int k){
			if (statusPark[i][j][k] == 's' || statusPark[i][j][k] == 'i')
				return false;

			long realTime = System.currentTimeMillis();
			int z;
			for (z = 0; z < park[i][j][k].size(); z++){
			if (realTime <= park[i][j][k].get(z).getExitTime() &&
				realTime >= park[i][j][k].get(z).getEntryTime())
					return false;
				}
			return true;
		}

		/**
		 * function checks whether specific spot is empty but has ordered cars belong to it
		 * @param i depth in the park
		 * @param j floor in the park
		 * @param k width in the park
		 * @return true when the specific spot is empty in real time but has at-least one ordered car
		 */
		private boolean isEmptyButOrderedRealTime(int i, int j, int k){
			if (!park[i][j][k].isEmpty() && isEmptySpotRealTime(i, j, k))
				return true;
			return false;
		}

		/**
		 * function gets specific list of cars that have any connection with a specific spot
		 * returns true if the specific spot is empty in the given times
		 * @param entryTime describes the beginning time of the interval
		 * @param exitTime describes the ending time of the interval
		 * @param list list of cars have any connection with the spot
		 * @return true when it is possible to insert car in the given interval times
		 */
		private boolean checkAvailableSpot(long entryTime, long exitTime, ArrayListCar list){
			// NO NEED TO CHECK 's' , 'i' CASES

			if (list.isEmpty()){
				return true;
			}
			else if (exitTime <= list.get(0).getEntryTime())
				return true;
			else if (entryTime >= list.get(list.size() - 1).getExitTime())
				return true;
			else{
				int z;
				for (z = 0; z < list.size() - 1; z++){
					if (entryTime >= list.get(z).getExitTime() && exitTime <= list.get(z+1).getEntryTime())
						return true;
				}
				return false;
			}
		}

		/**
		 * function checks whether the park is available to insertion command in the given times
		 * @param entryTime the beginning time of the interval
		 * @param exitTime the ending time of the interval
		 * @return true when it is possible to insert car in the given times into the park
		 */
		private boolean checkAvailablePark(long entryTime, long exitTime){
			int i, j, k;
			for (i = 0; i < 3; i++)
				for (j = 0; j < 3; j++)
					for (k = 0; k < spotsInRow; k++)
						if ((statusPark[i][j][k] != 's' && statusPark[i][j][k] != 'i') &&
								checkAvailableSpot(entryTime, exitTime, park[i][j][k]))
							return true;
			return false;
		}

		/**
		 * function returns spot's coordinates where a given car is located at
		 * @param carID identifier of the car
		 * @return location of the car in the park if was found, otherwise return {-1,-1,-1}
		 */
		private int[] locateCarSpot(String carID){
			int[] a = {-1, -1, -1}; // an array holds the coordinates of the car
			int i, j, k, z;

			for (i = 0; i < 3; i++){
				for (j = 0; j < 3; j++){
					for (k = 0; k < spotsInRow; k++){
						for(z = 0; z < park[i][j][k].size(); z++){
							if (park[i][j][k].get(z).getCarID().equals(carID)){
								a[0] = i; a[1] = j; a[2] = k;
								return a;
							}
						}
					}
				}
			}
			return a;
		}

		/**
		 * get the cars out of the park from a specific depth
		 * @param list a list which keeps the ejected cars
		 * @param i the depth in the park
		 * @param j the floor in the park
		 * @param k the width in the park
		 */
		private void ejectInDepth(ArrayListCar list, int i, int j, int k){
			int t, z;
			for (t = 0; t <= i; t++){
				if (statusPark[t][j][k] != 'i' && statusPark[t][j][k] != 's'){
					statusPark[t][j][k] = 'e'; // update the status because cars were ejected

					for (z = park[t][j][k].size() - 1; z >= 0; z--){
						list.add(park[t][j][k].get(z));
						park[t][j][k].remove(z);
					}
				}
			}
		}

		/**
		 * get the cars out of the park from a specific floor
		 * @param list a list which keeps the ejected cars
		 * @param j the floor in the park
		 * @param k the width in the park
		 */
		private void ejectInFloor(ArrayList<Car> list, int j, int k){
			int t, z;
			for (t = 0; t < j; t++){
				if (statusPark[0][t][k] != 'i' && statusPark[0][t][k] != 's'){
					statusPark[0][t][k] = 'e'; // update the status because cars we ejected

					for (z = park[0][t][k].size() - 1; z >= 0; z--){
						list.add(park[0][t][k].get(z));
						park[0][t][k].remove(z);
					}
				}
			}
		}

		/**
		 * find the optimal position for an entry command of a car
		 * @param entryTime beginning of the interval
		 * @param exitTime ending of the interval
		 * @return the coordinate in the park to plot in the car if possible, otherwise return {-1,-1,-1}
		 */
		private int[] locateOptimalSpot(long entryTime, long exitTime){
			int i, j, k, z, moves, minMoves = 6;
			int[] optimal = {-1, -1, -1};
			boolean st;

			for (k = 0; k < spotsInRow; k++){ // first loop on width
				for (j = 0; j < 3; j++){ // then loop on height

					moves = 0; // will be calculated with each iteration

					for (z = 0; z < j; z++){
						if (statusPark[0][z][k] != 'i' && statusPark[0][z][k] != 's'){
							st = checkAvailableSpot(entryTime, exitTime, park[0][z][k]);
							if (!st) // if the spot won't be empty in the given times we need to move another car
								moves++;
						}
					}

					for (i = 0; i < 3; i++){
						if (statusPark[i][j][k] != 'i' && statusPark[i][j][k] != 's'){
							st = checkAvailableSpot(entryTime, exitTime, park[i][j][k]);
							if (st)
								break;
							moves++;
						}
					}

					if (moves < minMoves){ // when we find lower number of moves we update
						minMoves = moves;
						optimal[0] = i; optimal[1] = j; optimal[2] = k;
					}
				}
			}
			return optimal;
		}

		/**
		 * function plots back the ejected cars (given in list) into the park, assume list is sorted by exit time
		 * @param list list of the cars to re-insert to the park
		 * @param i depth in the park
		 * @param j floor in the park
		 * @param k width in the park
		 */
		private void reEnterCars(ArrayListCar list, int i, int j, int k){
			long lastCTime;

			if (!list.isEmpty()){ // as long as we need to re-insert the ejected car
				int t = 0;
				for(t = 0; t < j; t++){
					if (statusPark[0][t][k] != 's' && statusPark[0][t][k] != 'i' && !list.isEmpty()){
						park[0][t][k].add(list.get(0));
						list.remove(0);

						while (!list.isEmpty()){
							lastCTime = park[0][t][k].get(park[0][t][k].size() - 1).getExitTime();
							if (list.get(0).getEntryTime() >= lastCTime){
								 park[0][t][k].add(list.get(0));
								 list.remove(0);
							}
							else
								break;
						}

						if (!isEmptySpotRealTime(0, t, k))
                            statusPark[0][t][k] = 'f';

                        if (isEmptyButOrderedRealTime(0, t, k))
                            statusPark[0][t][k] = 'o';
					}
				}


				t = 0;
				for(t = 0; t <= i; t++){
					if (statusPark[t][j][k] != 's' && statusPark[t][j][k] != 'i' && !list.isEmpty()){
						park[t][j][k].add(list.get(0));
						list.remove(0);

						while (!list.isEmpty()){
							lastCTime = park[t][j][k].get(park[t][j][k].size() - 1).getExitTime();
							if (list.get(0).getEntryTime() >= lastCTime){
								 park[t][j][k].add(list.get(0));
								 list.remove(0);
							}
							else
								break;
						}

						if (!isEmptySpotRealTime(t, j, k))
                            statusPark[t][j][k] = 'f';

                        if (isEmptyButOrderedRealTime(t, j, k))
                            statusPark[t][j][k] = 'o';
					}
				}
			}
		}
		
		/**
		 * function inserts into the park an ordered car
		 * @param car object of the car to insert into the park
		 */
		public void insertOrderedCar(Car car){
			boolean fullPark = !this.checkAvailablePark(car.getEntryTime(), car.getExitTime());
			if(fullPark){
				// Send message to server: insertion command failed
			}

			else{
				int[] a = locateOptimalSpot(car.getEntryTime(), car.getExitTime());
				park[a[0]][a[1]][a[2]].add(car);

				if(isEmptyButOrderedRealTime(a[0], a[1], a[2]))
					statusPark[a[0]][a[1]][a[2]] = 'o';
			}
		}
		
		/**
		 * function inserts car that immediately needs to be inserted into the park
		 * @param car object of the car to insert into the park
		 */
		public void insertCar(Car car){
			long entryTime = car.getEntryTime(), exitTime = car.getExitTime();

			boolean fullPark = !this.checkAvailablePark((int)entryTime, exitTime);
			if(fullPark){
				// Send message to server: insertion command failed
			}

			else{
				int[] a = locateOptimalSpot(entryTime, exitTime);
				park[a[0]][a[1]][a[2]].add(car);
				Collections.sort(park[a[0]][a[1]][a[2]]);
				statusPark[a[0]][a[1]][a[2]] = 'f';

				ArrayListCar list = new ArrayListCar();
				ejectInDepth(list, a[0] - 1, a[1], a[2]);
				ejectInFloor(list, a[1], a[2]);
				Collections.sort(list);

				reEnterCars(list, a[0] - 1, a[1], a[2]);
			}
		}
		
		/**
		 * function ejects car out of the park
		 * @param car object of the car to eject
		 */
		public void ejectCar(Car car){
			int[] a = locateCarSpot(car.getCarID());
			ArrayListCar list = new ArrayListCar();
			ejectInDepth(list, a[0] - 1, a[1], a[2]);
			ejectInFloor(list, a[1], a[2]);
			Collections.sort(list);
			int i;
			for (i = 0; i < park[a[0]][a[1]][a[2]].size(); i++){
				if (park[a[0]][a[1]][a[2]].get(i).getCarID().equals(car.getCarID())){
					park[a[0]][a[1]][a[2]].remove(i);
					break;
				}
			}
			Collections.sort(park[a[0]][a[1]][a[2]]);

			if (isEmptySpotRealTime(a[0], a[1], a[2]))
				statusPark[a[0]][a[1]][a[2]] = 'e';

			if (isEmptyButOrderedRealTime(a[0], a[1], a[2]))
				statusPark[a[0]][a[1]][a[2]] = 'o';


			reEnterCars(list, a[0] - 1, a[1], a[2]);
		}

	//------------------------//


	// FORMAT: <Calculation of converted convenient indexes> with <status in the spot>
		public String generateStatusStringOld(){
			String statusString = "";
			for(int i = 0 ; i < 3; i++){
				for(int j = 0; j < 3; j++){
					for(int k = 0; k < this.spotsInRow; k++){
						int value = i * 3 * this.spotsInRow + j * this.spotsInRow + k;
						statusString += Integer.toString(value);
						statusString += this.statusPark[i][j][k];
					}
				}
			}
			return statusString;
		}
		
		/**
		 * function generates string to describe the status in each spot of the park
		 * @return string includes status in each spot
		 */
		public String generateStatusString(){
			JSONArray statuses = new JSONArray();
			//String statusString = "";
			for(int i = 0 ; i < 3; i++){
				for(int j = 0; j < 3; j++){
					for(int k = 0; k < this.spotsInRow; k++){
						JSONObject current = new JSONObject();
						//System.out.println(i + "," + j + "," + k);
						try {
							//current.put("pos", i * 3 * this.spotsInRow + j * this.spotsInRow + k);
							current.put("i", i);
							current.put("j", j);
							current.put("k", k);
							current.put("status", String.valueOf(this.statusPark[i][j][k]));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						statuses.put(current);

					}
				}
			}
			//return statusString;
			return statuses.toString();
		}

		// FORMAT: <depth index,> <height index,> <width index,> <carID> <entry time of car,>
		// <exit time of car,> <#> <\n> ((EACH LINE DESCRIBES ONE SPOT))
		/**
		 * function generates string that describes the database in the park
		 * @return string includes description in each spot
		 */
		public String generateDBString(){
			JSONArray spots = new JSONArray();
			int i, j, k, z;
			//String DBString = "";
			for (i = 0; i < 3; i++){
				for(j = 0; j < 3; j++){
					for(k = 0; k < this.spotsInRow; k++){
						for (z = 0; z < park[i][j][k].size(); z++){

							JSONObject current = new JSONObject();
							try {
								current.put("i", i);
								current.put("j", j);
								current.put("k", k);
								current.put("carID", park[i][j][k].get(z).getCarID());
								current.put("entryTime", park[i][j][k].get(z).getEntryTime());
								current.put("exitTime", park[i][j][k].get(z).getExitTime());
								spots.put(current);
							}catch(Exception e) {
								e.printStackTrace();
							}

						}
					}
				}
			}
			//return DBString;
			return spots.toString();
		}




	public static void main(String args[]) {

		System.out.println("Calling ParkingAlgo with data:");
		String parkingData = "[{\"entryTime\":1516375932813,\"exitTime\":1516376292813,\"i\":0,\"j\":0,\"k\":0,\"carID\":\"pussyWagon\"}]\r\n";
		String statusData = "[{\"i\":0,\"j\":0,\"k\":0,\"status\":\"f\"},{\"i\":0,\"j\":0,\"k\":1,\"status\":\"e\"},{\"i\":0,\"j\":0,\"k\":2,\"status\":\"e\"},{\"i\":0,\"j\":0,\"k\":3,\"status\":\"e\"},{\"i\":0,\"j\":0,\"k\":4,\"status\":\"e\"},{\"i\":0,\"j\":0,\"k\":5,\"status\":\"e\"},{\"i\":0,\"j\":0,\"k\":6,\"status\":\"e\"},{\"i\":0,\"j\":0,\"k\":7,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":0,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":1,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":2,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":3,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":4,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":5,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":6,\"status\":\"e\"},{\"i\":0,\"j\":1,\"k\":7,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":0,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":1,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":2,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":3,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":4,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":5,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":6,\"status\":\"e\"},{\"i\":0,\"j\":2,\"k\":7,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":0,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":1,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":2,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":3,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":4,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":5,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":6,\"status\":\"e\"},{\"i\":1,\"j\":0,\"k\":7,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":0,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":1,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":2,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":3,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":4,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":5,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":6,\"status\":\"e\"},{\"i\":1,\"j\":1,\"k\":7,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":0,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":1,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":2,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":3,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":4,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":5,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":6,\"status\":\"e\"},{\"i\":1,\"j\":2,\"k\":7,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":0,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":1,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":2,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":3,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":4,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":5,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":6,\"status\":\"e\"},{\"i\":2,\"j\":0,\"k\":7,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":0,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":1,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":2,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":3,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":4,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":5,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":6,\"status\":\"e\"},{\"i\":2,\"j\":1,\"k\":7,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":0,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":1,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":2,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":3,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":4,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":5,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":6,\"status\":\"e\"},{\"i\":2,\"j\":2,\"k\":7,\"status\":\"e\"}]\r\n";
		//	System.out.println(data.getJSONArray("statusData").toString());
		Algorithm alg = new Algorithm(8, parkingData, statusData);
		//TODO: integrate insertion with status (in this case 'order')
		alg.insertCar(new Car("pussyWagon2", System.currentTimeMillis(), System.currentTimeMillis() + 1000 * 60 * 6));

		System.out.println(alg.generateDBString());
		System.out.println(alg.generateStatusString());

//		Algorithm alg = new Algorithm(4);
//
//		alg.statusPark[0][0][0] = 'i';
//
//		Car a1 = new Car("cesc", 2l, 9l);
//		Car b = new Car("roare", 9l, 11l);
//		Car c = new Car("roland", 11l, 12l);
//		Car d = new Car("obo", 13l, 16l);
//		Car e = new Car("sakho", 7l, 11l);
//		Car f = new Car("miki", 5l, 8l);
//		Car g = new Car("abcd",3l, 7l);
//		Car h = new Car("sm", 4l, 9l);
//		Car i = new Car("ppp", 8l, 11l);
//
//		alg.insertCar(a1);
//		alg.insertCar(b);
//		alg.insertCar(c);
//		alg.insertCar(d);
//		alg.insertCar(e);
//		alg.insertCar(f);
//		alg.insertOrderedCar(g);
//		alg.insertCar(h);
//		alg.insertCar(i);
//
//		int[] a = alg.locateCarSpot("cesc");
//		System.out.println("cesc car: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("roare");
//		System.out.println("roare car: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("roland");
//		System.out.println("roland car: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("obo");
//		System.out.println("obo car: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("sakho");
//		System.out.println("sakho: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("miki");
//		System.out.println("miki: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("abcd");
//		System.out.println("abcd: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("sm");
//		System.out.println("sm: " + a[0] + "," + a[1] + ","+a[2]);
//
//		a = alg.locateCarSpot("ppp");
//		System.out.println("ppp: " + a[0] + "," + a[1] + ","+a[2]);
//
//		 System.out.println(alg.generateStatusString());
//		 System.out.println(alg.generateDBString());
//
//
//		 //System.out.println(alg.park[0][0][0].get(0).getCarID());
//		 //System.out.println(alg.park[0][0][0].get(1).getCarID());
//
//		 alg.ejectCar(e);
//		 alg.ejectCar(b);
//		 alg.ejectCar(g);
//
//		 a = alg.locateCarSpot("cesc");
//			System.out.println("cesc car: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("roare");
//			System.out.println("roare car: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("roland");
//			System.out.println("roland car: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("obo");
//			System.out.println("obo car: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("sakho");
//			System.out.println("sakho: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("miki");
//			System.out.println("miki: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("abcd");
//			System.out.println("abcd: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("sm");
//			System.out.println("sm: " + a[0] + "," + a[1] + ","+a[2]);
//
//			a = alg.locateCarSpot("ppp");
//			System.out.println("ppp: " + a[0] + "," + a[1] + ","+a[2]);
//
//		 System.out.println(alg.generateDBString());
	}

}
