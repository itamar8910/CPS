package algorithm;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Algorithm{
	
	private static class ArrayListCar extends ArrayList<Car>{
		
	}
	
	private int spotsInRow; // as an input from server. possible value: 4,5,6,7,8
	private ArrayListCar[][][] park; // each spot has a list of all the cars belong to the spot
	private char[][][] statusPark; // real time 3-dimensions array that has status for each spot
	// e - EMPTY, f - FULL, i - INVALID, s - SAVE, o - ordered
	
	Algorithm(){ // CONSTRUCTOR
		this.spotsInRow = 4; // spotsInRow = 4 as an initial value
		int i, j, k;
		statusPark = new char[3][3][4];
		park = new ArrayListCar[3][3][4];
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for(k = 0; k < 4; k++) {
					park[i][j][k] = new ArrayListCar(); // each cell has an empty list as an initial state
					statusPark[i][j][k] = 'e';
				}
		}
	
	public Algorithm(int numSpotsInRow){ // 2nd CONSTRUCTOR
		int i, j, k;
		statusPark = new char[3][3][numSpotsInRow];
		park = new ArrayListCar[3][3][numSpotsInRow];
		this.spotsInRow = numSpotsInRow;
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for(k = 0; k < numSpotsInRow; k++) {
					park[i][j][k] = new ArrayListCar();
					statusPark[i][j][k] = 'e';
				}
		
	}
	
	public Algorithm(int numSpotsInRow, String db, String si){ // 3rd CONSTRUCTOR
		this.spotsInRow = numSpotsInRow;
		this.park = generateParkFromString(db);
		this.statusPark = generateStatusPark(si);
		int i, j, k;
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for (k = 0; k < numSpotsInRow; k++)
					// sort by entry time. It also means it is sorted by exit time.
					Collections.sort(park[i][j][k]);
	}
	
	private char[][][] generateStatusPark(String si){
		//TODO: make row width dynamic
		char[][][] tmpStatuses = new char[3][3][4];
		try {
			JSONArray statuses = new JSONArray(si);
			for(int index = 0; index < statuses.length(); index++) {
				JSONObject current = statuses.getJSONObject(index);
				int i = current.getInt("i");
				int j = current.getInt("j");
				int k = current.getInt("k");
				tmpStatuses[i][j][k] = current.getString("status").charAt(0);
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmpStatuses;
	}
	
	private ArrayListCar[][][] generateParkFromString(String st){
		ArrayListCar[][][] cars = new ArrayListCar[3][3][4];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				for(int k = 0; k < 4; k++)
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
				cars[i][j][k].add(new Car(carID, exitTime, entryTime));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	//------------------------//
	
	// function checks whether specific spot is empty in real time
		private boolean isEmptySpotRealTime(int i, int j, int k){
			if (statusPark[i][j][k] == 's' || statusPark[i][j][k] == 'i')
				return false;
			long realTime = System.currentTimeMillis();
			for (int z = 0; z < park[i][j][k].size(); z++){
				if (realTime <= park[i][j][k].get(z).getExitTime() &&
						realTime >= park[i][j][k].get(z).getEntryTime())
					return false;
			}
			return true;
		}

		private boolean isEmptyButOrderedRealTime(int i, int j, int k){
			if (!park[i][j][k].isEmpty() && isEmptySpotRealTime(i, j, k) == true)
				return true;
			return false;
		}
		
		// function gets specific list of cars that have any connection with the specific spot
		// returns true if the specific spot is empty in the given times
		private boolean checkAvailableSpot(long entryTime, long exitTime, ArrayList<Car> list){
			if (list.isEmpty()) // meaning there are no cars belong to the spot
				return true;
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
		
		// the function tries to find an empty interval with the given parameters
		private boolean checkAvailablePark(long entryTime, long exitTime){			
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					for (int k = 0; k < spotsInRow; k++)
						if ((statusPark[i][j][k] == 's' || statusPark[i][j][k] == 'i') &&
								checkAvailableSpot(entryTime, exitTime, park[i][j][k]))
							return true;
			return false;
		}
		
		private int[] locateCarSpot(String carID){
			int i, j, k, z;
			int[] a = {-1, -1, -1}; // an array holds the coordinates of the car
			
			for (i = 0; i < 3; i++){
				for (j = 0; j < 3; j++){
					for (k = 0; k < this.spotsInRow; k++){
						for(z = 0; z < park[i][j][k].size(); z++){
							System.out.println("**");
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
		
		// get the cars out of the park from a specific depth
		// al is a list which keeps the ejected cars
		private void ejectInDepth(ArrayList<Car> al, int i, int j, int k){
			for (int t = 0; t < i; t++){
				if (statusPark[t][j][k] != 'i' && statusPark[t][j][k] != 's'){
					statusPark[t][j][k] = 'e';
					for (int z = park[t][j][k].size() - 1; z >= 0; z--){
						al.add(park[t][j][k].get(z));
						park[t][j][k].remove(z);
					}
				}
			}
		}
		
		// get the cars out of the park from a specific floor
		// al is a list which keeps the ejected cars
		private void ejectInFloor(ArrayList<Car> al, int i, int j, int k){
			for (int t = 0; t < j; t++){
				if (statusPark[i][t][k] != 'i' && statusPark[i][t][k] != 's'){
					statusPark[i][t][k] = 'e';
					for (int z = park[i][t][k].size() - 1; z >= 0; z--){
						al.add(park[i][t][k].get(z));
						park[i][t][k].remove(z);
					}
				}
			}
		}
		
		// find the optimal position for an entry command of a car
		private int[] locateOptimalPosition(long entryTime, long exitTime){
			int i, j, k, z, moves, minMoves = 5;
			int[] optimal = new int[3];
			boolean st;
			
			for (k = 0; k < spotsInRow; k++){ // first loop on width
				
				for (j = 0; j < 3; j++){ // then loop on height
					
					moves = 0; // will be calculated with each iteration
					
					for (z = 0; z < j; z++){
						if (statusPark[0][z][k] != 'i' && statusPark[0][z][k] != 's'){
							st = checkAvailableSpot(entryTime, exitTime, park[0][z][k]);
							if (!st)
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
		
		public void insertOrderedCar(Car car, long entryTime, long exitTime){
			boolean fullPark = !this.checkAvailablePark(entryTime, exitTime);
			if(fullPark){
				// Send message to server: insertion command failed
			}
			
			else{
				int[] a = locateOptimalPosition(entryTime, exitTime);
				park[a[0]][a[1]][a[2]].add(car);
				
				if(isEmptyButOrderedRealTime(a[0], a[1], a[2]))
					statusPark[a[0]][a[1]][a[2]] = 'o';
			}
		}
		
		public void insertCar(Car car, long exitTime){
			long entryTime = System.currentTimeMillis();
			boolean fullPark = !this.checkAvailablePark((int)entryTime, exitTime);
			if(fullPark){
				// Send message to server: insertion command failed
			}
			
			else{
				int[] a = locateOptimalPosition(entryTime, exitTime);
				ArrayList<Car> list = new ArrayList<Car>();
				ejectInDepth(list, a[0], a[1], a[2]);
				ejectInFloor(list, a[0], a[1], a[2]);
				list.add(car);
				Collections.sort(list);
				
				while (!list.isEmpty()){ // as long as we need to re-insert the ejected car
					int t = 0;
					while (t < a[1]){
						if (statusPark[0][t][a[2]] != 's' && statusPark[0][t][a[2]] != 'i'){
							park[0][t][a[2]].add(list.get(0));
							
							while (list.get(0).getEntryTime() >=
									park[0][t][a[2]].get(park[0][t][a[2]].size() - 1).getExitTime()){
								
										park[0][t][a[2]].add(list.get(0));
										list.remove(0);
										
									}
							if (!isEmptySpotRealTime(0, t, a[2]))
								statusPark[0][t][a[2]] = 'f';
							
							if (isEmptyButOrderedRealTime(0, t, a[2]))
								statusPark[a[0]][t][a[2]] = 'o';
						}
						t++;
					}
					
					t = 0;
					while (t <= a[0]){
						if (statusPark[t][a[1]][a[2]] != 's' && statusPark[t][a[1]][a[2]] != 'i'){
							park[t][a[1]][a[2]].add(list.get(0));
							
							while (list.get(0).getEntryTime() >=
									park[t][a[1]][a[2]].get(park[t][a[1]][a[2]].size() - 1).getExitTime()){
								
										park[t][a[1]][a[2]].add(list.get(0));
										list.remove(0);
										
									}
							
							if (!isEmptySpotRealTime(t, a[1], a[2]))
								statusPark[t][a[1]][a[2]] = 'f';

							if (isEmptyButOrderedRealTime(t, a[1], a[2]))
								statusPark[t][a[1]][a[2]] = 'o';
						}
						t++;
					}
				}
			}
		}
		
		public void ejectCar(Car car){
			int[] a = locateCarSpot(car.getCarID());
			ArrayList<Car> list = new ArrayList<Car>();
			ejectInDepth(list, a[0], a[1], a[2]);
			ejectInFloor(list, a[0], a[1], a[2]);
			int i;
			for (i = 0; i < list.size(); i++){
				if (list.get(i).getCarID().equals(car.getCarID())){
					list.remove(i);
					break;
				}
			}
			Collections.sort(list);
			
			while (!list.isEmpty()){ // as long as we need to re-insert the ejected car
				int t = 0;
				while (t < a[1]){
					if (statusPark[0][t][a[2]] != 's' && statusPark[0][t][a[2]] != 'i'){
						park[0][t][a[2]].add(list.get(0));
						
						while (list.get(0).getEntryTime() >=
								park[0][t][a[2]].get(park[0][t][a[2]].size() - 1).getExitTime()){
							
									park[0][t][a[2]].add(list.get(0));
									list.remove(0);
									
								}
						if (!isEmptySpotRealTime(0, t, a[2]))
							statusPark[0][t][a[2]] = 'f';
						
						if (isEmptyButOrderedRealTime(0, t, a[2]))
							statusPark[a[0]][t][a[2]] = 'o';
					}
					t++;
				}
				
				t = 0;
				while (t <= a[0]){
					if (statusPark[t][a[1]][a[2]] != 's' && statusPark[t][a[1]][a[2]] != 'i'){
						park[t][a[1]][a[2]].add(list.get(0));
						
						while (list.get(0).getEntryTime() >=
								park[t][a[1]][a[2]].get(park[t][a[1]][a[2]].size() - 1).getExitTime()){
							
									park[t][a[1]][a[2]].add(list.get(0));
									list.remove(0);
									
								}
						
						if (!isEmptySpotRealTime(t, a[1], a[2]))
							statusPark[t][a[1]][a[2]] = 'f';

						if (isEmptyButOrderedRealTime(t, a[1], a[2]))
							statusPark[t][a[1]][a[2]] = 'o';
					}
					t++;
				}
			}
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
		 Algorithm alg = new Algorithm(4);
		 alg.insertCar(new Car("Afsa",2l,1l), 2l);
		 System.out.println(alg.locateCarSpot("Afas")[0]);
		 System.out.println(alg.generateStatusString());
		 System.out.println(alg.generateDBString());
	}
	
}