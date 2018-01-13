package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.Params;

public class Algorithm{
	private int spotsInRow; // as an input from server. range: 4,5,6,7,8
	private Spot[][][] park; // the park itself - receive from server
	
	Algorithm(){ // CONSTRUCTOR
		// spotsInRow = 4 as an initial value
		int i, j, t;
		park = new Spot[3][3][4];
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for(t = 0; t < 4; t++)
					park[i][j][t] = new Spot();
	}
	
	/**
	 * 
	 * @param state: item = [{'depth':i,'height':j,'col':t,'vehicleID':000, 'entryTime':000,'exitTime':000, 'status':''}]
	 */
	public Algorithm(JSONArray state, int spotsInRow){ // CONSTRUCTOR
		this.spotsInRow = spotsInRow;
		// spotsInRow = 4 as an initial value
		int i, j, t;
		park = new Spot[3][3][4];
		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for(t = 0; t < 4; t++)
					park[i][j][t] = new Spot();
		for(int index = 0; index < state.length(); index++) {
			try {
				JSONObject spotJson = state.getJSONObject(index);
				char statusChar = 'f'; ///TODO: support other statuses
				System.out.println(spotJson);
				Spot spot = new Spot(statusChar,spotJson.getString("vehicleID"), spotJson.getLong("entryTime"), spotJson.getLong("exitTime"));
				park[spotJson.getInt("depth")][spotJson.getInt("height")][spotJson.getInt("col")] = spot;
			}catch(JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	Algorithm(int numSpotsInRow){ // 2nd CONSTRUCTOR
		int i, j, t;
		park = new Spot[3][3][numSpotsInRow];
		for (i = 0; i < 3; i++){
			for (j = 0; j < 3; j++){
				for(t = 0; t < numSpotsInRow; t++){
					park[i][j][t] = new Spot();
				}
			}
		}
	}
	
	Algorithm(int numSpotsInRow, Spot[][][] parkArray){ // 3rd CONSTRUCTOR
		spotsInRow = numSpotsInRow;
		park = parkArray;
	}
	
	private boolean checkAvailable(long exitTime){ // checking whether insert command is executable
		int i, j, k;
		for (i = 0; i < 3; i++){
			for (j = 0; j < 3; j++){
				for (k = 0; k < spotsInRow; k++){
					// check if there is any spot in the park which is empty or ordered with the following condition:
					// if there is an ordered spot with entry time higher than the current exit time, it is fine
					if(park[i][j][k].getStatus() == 'e'
							|| (park[i][j][k].getStatus() == 'o' && exitTime <= park[i][j][k].getEntryTime()))
						return true;
				}
			}
		}
		return false;
	}
	
	private int[] locateCarSpot(String carID){
		int i, j, k;
		int[] a = {-1, -1, -1}; // a array holds the coordinates of the car
		for (i = 0; i < 3; i++){
			for (j = 0; j < 3; j++){
				for (k = 0; k < this.spotsInRow; k++){
					if(park[i][j][k].getCarID().equals(carID)){ // if we locate this car then update a and finish
						a[0] = i; a[1] = j; a[2] = k;
						return a;
					}
				}
			}
		}
		return a;
	}
	
	// get the cars out of the park from a specific depth
	// al is a list which keeps the ejected cars
	private void ejectInDepth(ArrayList<Spot> al, int i, int j, int k){
		int t;
		for (t = 0; t < i; t++){
			if (park[t][j][k].getStatus() == 'f'){ // if the spot is full - then there is a car to eject
				al.add(park[t][j][k]);
				park[t][j][k].setStatus('e'); // update the spot as empty..
			}
		}	
	}
	
	// get the cars out of the park from a specific floor
	// al is a list which keeps the ejected cars
	private void ejectInFloor(ArrayList<Spot> al, int i, int j, int k){
		int t;
		for (t = 0; t < j; t++){
			if (park[i][t][k].getStatus() == 'f'){ // if the spot is full - then there is a car to eject
				al.add(park[i][t][k]);
				park[i][t][k].setStatus('e'); // update the spot as empty
			}
		}
	}
	
	// find the optimal position for an entry command of a car
	private int[] locateOptimalPosition(long exitTime){
		int i, j, k, moves, minMoves = 6;
		int[] optimal = new int[3];
		for (k = 0; k < spotsInRow; k++){ // first loop on width
			for (j = 0; j < 3; j++){ // then loop on height
				moves = 0; // will be calculated with each iteration
				for (i = 0; i < 3; i++){
					if (park[i][j][k].getStatus() == 'e' || // if we found an empty spot
							// or we found an ordered spot which we can enter into the car because the
							// current exit time is less than the spot's entry time
							(park[i][j][k].getStatus() == 'o' && exitTime <= park[i][j][k].getEntryTime()))
						break;
					moves++;
				}
				if (moves < minMoves){ // when we find lower number of moves we update
					minMoves = moves;
					optimal[0] = i; optimal[1] = j; optimal[2] = k;
				}
			}
		}
		return optimal;
	}
	
	// FORMAT: <Calculation of converted convenient indexes> with <status in the spot>
	public String generateStatusString(){
		String statusString = "";
		for(int i = 0 ; i < 3; i++){
			for(int j = 0; j < 3; j++){
				for(int k = 0; k < this.spotsInRow; k++){
					int value = i * 3 * this.spotsInRow + j * this.spotsInRow + k;
					statusString += Integer.toString(value);
					statusString += this.park[i][j][k].getStatus();
				}
			}
		}
		return statusString;
	}
	
	// FORMAT: <depth index,> <height index,> <width index,> <status in spot> <entry time in spot>
	// <exit time in spot> <\n to end a line> ((EACH LINE DESCRIBES ONE SPOT))
	public String generateDBString(){
		int i, j, k;
		String DBString = "";
		for (i = 0; i < 3; i++){
			for(j = 0; j < 3; j++){
				for(k = 0; k < this.spotsInRow; k++){
					DBString += Integer.toString(i) + "," + Integer.toString(j) + "," + Integer.toString(k);
					DBString += ",";
					DBString += this.park[i][j][k].getStatus() + ",";
					DBString += Long.toString(this.park[i][j][k].getEntryTime()) + ",";
					DBString += Long.toString(this.park[i][j][k].getExitTime()) + ",";
					if (i != 2 && j != 2 && k != this.spotsInRow - 1)
						DBString += this.park[i][j][k].getCarID() + "\n";
				}
			}
		}
		return DBString;
	}
	
	public JSONArray generateDBJsonArray(){
		JSONArray res = new JSONArray();
		int i, j, k;
		//String DBString = "";
		for (i = 0; i < 3; i++){
			for(j = 0; j < 3; j++){
				for(k = 0; k < this.spotsInRow; k++){
					//System.out.println(i + "," + j +"," + k);
					//[{'depth':i,'height':j,'col':t,'vehicleID':000, 'entryTime':000,'exitTime':000, 'status':''}]
					try {
						JSONObject spotJson = new JSONObject();
						spotJson.put("depth", i);
						spotJson.put("height", j);
						spotJson.put("col", k);
						spotJson.put("status", this.park[i][j][k].getStatusChar());
						if(this.park[i][j][k].getStatusChar() != 'e' ) {
							System.out.println(i + "," + j +"," + k);
						}
						spotJson.put("entryTime", this.park[i][j][k].getEntryTime());
						spotJson.put("exitTime", this.park[i][j][k].getExitTime());
						
						spotJson.put("vehicleID", this.park[i][j][k].getCarID()); //TODO: check if this line is OK
						
						//DBString += Integer.toString(i) + "," + Integer.toString(j) + "," + Integer.toString(k);
						//DBString += ",";
						//DBString += this.park[i][j][k].getStatus() + ",";
//						DBString += Long.toString(this.park[i][j][k].getEntryTime()) + ",";
//						DBString += Long.toString(this.park[i][j][k].getExitTime()) + ",";
						if (i != 2 && j != 2 && k != this.spotsInRow - 1) {
							spotJson.put("vehicleID", this.park[i][j][k].getCarID());

							//DBString += this.park[i][j][k].getCarID() + "\n";
						}
						res.put(spotJson);
					}catch(JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return res;
	}
	
	public void insertCar(String carID, long exitTime){
		boolean fullPark = !this.checkAvailable(exitTime);
		if(fullPark){
			// Send message to server: insertion command failed
		}
		
		else{
			int[] a = locateOptimalPosition(exitTime);
			System.out.println("insert to:" + Arrays.toString(a));
			ArrayList<Spot> list = new ArrayList<Spot>();
			ejectInDepth(list, a[0], a[1], a[2]);
			ejectInFloor(list, a[0], a[1], a[2]);
			Collections.sort(list);
			
			for (int counter = 0; counter < list.size(); counter++){
				int t = 0;
				while(t != a[1]){
					if(park[a[0]][t][a[2]].getStatus() == 'e'){
						park[a[0]][t][a[2]] = list.get(counter);
						park[a[0]][t][a[2]].setStatus('f');
						break;
					}
					
					if(park[a[0]][t][a[2]].getStatus() == 'o' && list.get(counter).getExitTime() <= park[a[0]][t][a[2]].getEntryTime()){
						park[a[0]][t][a[2]] = list.get(counter);
						park[a[0]][t][a[2]].setStatus('f');
						break;
					}
					t++;
				}
				
				t = 0;
				while(t != a[0]){
					if(park[t][a[1]][a[2]].getStatus() == 'e'){
						park[t][a[1]][a[2]] = list.get(counter);
						park[t][a[1]][a[2]].setStatus('f');
						break;
					}
					
					if(park[t][a[1]][a[2]].getStatus() == 'o' && list.get(counter).getExitTime() <= park[t][a[1]][a[2]].getEntryTime()){
						park[t][a[1]][a[2]] = list.get(counter);
						park[t][a[1]][a[2]].setStatus('f');
						break;
					}
					t++;
				}
			}
			System.out.println("after insert:" + park[a[0]][a[1]][a[2]].getCarID());
		}
	}
	
	public void ejectCar(String carID){
		int[] a = locateCarSpot(carID);
		ArrayList<Spot> list = new ArrayList<Spot>();
		ejectInDepth(list, a[0], a[1], a[2]);
		ejectInFloor(list, a[0], a[1], a[2]);
		Collections.sort(list);
		
		for (int counter = 0; counter < list.size(); counter++){
			int t = 0;
			while(t != a[1]){
				if(park[a[0]][t][a[2]].getStatus() == 'e'){
					park[a[0]][t][a[2]] = list.get(counter);
					park[a[0]][t][a[2]].setStatus('f');
					break;
				}
				
				if(park[a[0]][t][a[2]].getStatus() == 'o' && list.get(counter).getExitTime() <= park[a[0]][t][a[2]].getEntryTime()){
					park[a[0]][t][a[2]] = list.get(counter);
					park[a[0]][t][a[2]].setStatus('f');
					break;
				}
				t++;
			}
			
			t = 0;
			while(t != a[0]){
				if(park[t][a[1]][a[2]].getStatus() == 'e'){
					park[t][a[1]][a[2]] = list.get(counter);
					park[t][a[1]][a[2]].setStatus('f');
					break;
				}
				
				if(park[t][a[1]][a[2]].getStatus() == 'o' && list.get(counter).getExitTime() <= park[t][a[1]][a[2]].getEntryTime()){
					park[t][a[1]][a[2]] = list.get(counter);
					park[t][a[1]][a[2]].setStatus('f');
					break;
				}
				t++;
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			JSONArray start = new JSONArray("[]");
			Algorithm alg = new Algorithm(start, 4);
			alg.insertCar("1", 44121l);
			System.out.println(alg.generateDBJsonArray());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}