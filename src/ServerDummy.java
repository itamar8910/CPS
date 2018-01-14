// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithm.Algorithm;
import algorithm.Car;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Test;

import common.Params;
import common.User;
import common.Utils;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/*TODOS
 * - Impl. parking algo integration
 * 
 * - TODO: add to cronjob: alret to user when his subscription is about to be over ONE WEEK BEFORE
 * - TODO: remove user & if his subscription time is over in CRONJOB
 * - TODO: user "Follows" his order status (do some shity progress screen)
 */

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class ServerDummy extends AbstractServer
{
  //Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 6654;



  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public ServerDummy(int port)
  {
    super(port);
    //System.out.println("Tables:");
    //TestDB.getInstance().printAllTables();
    final boolean DO_CONJOBS = false; //TODO: remember to change this before submitting :)
    if(DO_CONJOBS) { 
	    new Thread(()-> {
	    	while(true) {
		    	System.out.println("CRON JOB 1 MIN");
		    	
		    	//do logic
		    	//check for all orders if are late
		    	List<User> allUsers = TestDB.getInstance().getAllUsers();
		    	System.out.println("Printing all users:");
		    	for(User user : allUsers) {
		    		if(user.getSubscriptionParams().getParam("type").equals("orderedOneTimeParking")) {
		    			long orderTimeUnix = Long.valueOf(user.getSubscriptionParams().getParam("enterTimeMS"));
		    			// if customer is late and this is the first minute shes is late in
		    			if(System.currentTimeMillis() > orderTimeUnix && System.currentTimeMillis() < orderTimeUnix + 60*1000) {
		    				//update stats
		    				addToStatistics(TestDB.getInstance().getVehicleParkingLot(user.getVehicleID()), 1, 0, 0, 0);
		    				//notify via email
		    				sendEmailToCostumerForBeginLate(user.getEmail(), "Hello, You are late for your order");
		    			}
		    		}
		    	}
		    	System.out.println("END");
		    	try {	    		
		    		//sleep till next minute
		    		Calendar c = Calendar.getInstance();
		    		c.setTimeInMillis(System.currentTimeMillis());
		    		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 1);
					System.out.println("calendar millis:" + c.getTimeInMillis());
		    		System.out.println("current millis :" + System.currentTimeMillis());
					long diff = c.getTimeInMillis() - System.currentTimeMillis();
		    		System.out.println("sleeping untill next minute for:" + diff);
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
	    }).start();
	    new Thread(()-> {
	    	while(true) {
	    		
	    		try {	    		
		    		//sleep till next day
		    		Calendar c = Calendar.getInstance();
		    		c.setTimeInMillis(System.currentTimeMillis());
		    		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)+1, 0,0);
					System.out.println("calendar millis:" + c.getTimeInMillis());
		    		System.out.println("current millis :" + System.currentTimeMillis());
					long diff = c.getTimeInMillis() - System.currentTimeMillis();
		    		System.out.println("sleeping untill next day for:" + diff);
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		System.out.println("CRON JOB START OF DAY");
	    		
	    		
	    		//do logic
	    		List<User> allUsers = TestDB.getInstance().getAllUsers();
		    	System.out.println("Printing all users:");
		    	for(User user : allUsers) {
		    		Params subscriptionParams = user.getSubscriptionParams();
		    		if(subscriptionParams.getParam("type").equals("routineSubscription") || subscriptionParams.getParam("type").equals("fullSubscription")) {
		    			handleCallParkingAlgoOrderForSubscription(user.getUserID(), user.getVehicleID(), TestDB.getInstance().getVehicleParkingLot(user.getVehicleID()), subscriptionParams);
		    		}	
		    	}
		    	
	    		/*
	    		 * for each user that's a subscriber
	    		 * call: handleCallParkingAlgoOrderForSubscription
	    		 */
	    		
	    	}
	    }).start();
    }
    //TestDB.getInstance().addRow("itamar", 100);
    //TestDB.getInstance().getBalanceOf("itamar");
  }


  //Instance methods ************************************************


  private void sendEmailToCostumerForBeginLate(String email, String string) {
	// TODO: impl.
	//sends email
	  
	//if response is she's still interested, charge additional 20%.
	
}


public JSONArray generateEmptyParkingLotDataJson(int rows, int height, int cols){

	  JSONArray slots = new JSONArray();
	  for(int r = 0; r < rows; r++){
		  for(int h = 0; h < height; h++){
			  for(int c = 0; c < cols; c++){
				  JSONObject slotJson = new JSONObject();
				  try {
					slotJson.put("row", r);
					slotJson.put("height", h);
					slotJson.put("col", c);
					slotJson.put("status", "f");
					slotJson.put("enterTime", 0);
					slotJson.put("leaveTime", 0);
					slotJson.put("Vid", -1);
					slots.put(slotJson);
					//slots.add(slotJson);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
		  }
	  }

	  return slots;
  }



//  private void callParkingAlgo(String op, String vehicleID, String parkingLot, long startTime, long leaveTime) {
//	  try{
//	  final int width = TestDB.getInstance().getParkingLotWidth(parkingLot);
//	  JSONArray data = TestDB.getInstance().getParkingLotJsonData(parkingLot);
//	  String dataStr = "";
//
//	  for(int i = 0; i < data.length(); i++){
//		  JSONObject carJson = data.getJSONObject(i);
//		  dataStr += carJson.getString("row") + ", " + carJson.getString("height") + ", " + carJson.getString("col") + ", " + carJson.getString("status") + ", "
//				  + carJson.getString("enterTime") + ", " + carJson.getString("leaveTime") + ", " + carJson.getString("Vid") + "\n";
//	  }
//
//	  String opStr = "";
//	  if(op.equals("Reserve")){
//		  opStr = "Reserve " + startTime + ", " + leaveTime + ", " + vehicleID;
//	  } else if(op.equals("Enter")){
//		  opStr = "Enter " + startTime + ", " + leaveTime + ", " + vehicleID;
//	  }else if(op.equals("Leave")){
//		  opStr = "Leave " + vehicleID;
//	  }
//
//	  String newData = ParkingAlgo.doParking(dataStr, width, opStr);
//	  JSONArray newDataJson = new JSONArray();
//
//	  for(String line : newData.split("\n")){
//		  String[] lineData = line.split(",");
//		  JSONObject slotJson = new JSONObject();
//		  try {
//			slotJson.put("row", lineData[0]);
//			slotJson.put("height", lineData[1]);
//			slotJson.put("col", lineData[2]);
//			slotJson.put("status", lineData[3]);
//			slotJson.put("enterTime", lineData[4]);
//			slotJson.put("leaveTime", lineData[5]);
//			slotJson.put("Vid", lineData[6]);
//			newDataJson.put(slotJson);
//			//slots.add(slotJson);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	  }
//	  System.out.println("calling updateParkingLotData");
//	  TestDB.getInstance().updateParkingLotData(parkingLot, newDataJson.toString());
//	  System.out.println("called updateParkingLotData");
//
//	  }catch(Exception e){
//		  e.printStackTrace();
//	  }
//  }
  
	final boolean CALL_ALGO = false;

  private void callParkingAlgoEnter(String parkingLot, String vehicleID, long leaveTime) {
	  if(!CALL_ALGO) {
		  return;
	  }
	  final int width = TestDB.getInstance().getParkingLotWidth(parkingLot);
	  JSONObject data = TestDB.getInstance().getParkingLotJsonData(parkingLot);
	  JSONArray start;
	try {
		start = data.getJSONArray("parkingData");
		Algorithm alg = new Algorithm(width, data.getJSONArray("parkingData").toString(), data.getJSONArray("statusData").toString());
		//TODO: integrate insertion with status (in this case 'order')
		alg.insertCar(new Car(vehicleID, leaveTime, System.currentTimeMillis()), leaveTime);
		//TODO: handle parking lot is full
		JSONObject result = new JSONObject();
		result.put("parkingData", new JSONArray(alg.generateDBString()));
		result.put("statsuData", new JSONArray(alg.generateStatusString()));
		TestDB.getInstance().updateParkingLotData(parkingLot, result.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

  }
  
 private void callParkingAlgoOrder(String parkingLot, String vehicleID, long entryTime, long leaveTime) {
	 if(!CALL_ALGO) {
		  return;
	  } 
	 final int width = TestDB.getInstance().getParkingLotWidth(parkingLot);
	  JSONObject data = TestDB.getInstance().getParkingLotJsonData(parkingLot);
	  JSONArray start;
	try {
		start = data.getJSONArray("parkingData");
		Algorithm alg = new Algorithm(width, data.getJSONArray("parkingData").toString(), data.getJSONArray("statusData").toString());
		//TODO: integrate insertion with status (in this case 'order')
		alg.insertOrderedCar(new Car(vehicleID, leaveTime, entryTime), entryTime, leaveTime);
		//TODO: handle parking lot is full
		JSONObject result = new JSONObject();
		result.put("parkingData", new JSONArray(alg.generateDBString()));
		result.put("statsuData", new JSONArray(alg.generateStatusString()));
		TestDB.getInstance().updateParkingLotData(parkingLot, result.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }
 
 private void callParkingAlgoLeave(String parkingLot, String vehicleID) {
	 if(!CALL_ALGO) {
		  return;
	  } 
	 final int width = TestDB.getInstance().getParkingLotWidth(parkingLot);
	  JSONObject data = TestDB.getInstance().getParkingLotJsonData(parkingLot);
	  JSONArray start;
	try {
		start = data.getJSONArray("parkingData");
		Algorithm alg = new Algorithm(width, data.getJSONArray("parkingData").toString(), data.getJSONArray("statusData").toString());
		//TODO: integrate insertion with status (in this case 'order')
		alg.ejectCar(new Car(vehicleID, System.currentTimeMillis(), System.currentTimeMillis()));
		//alg.insertOrderedCar(new Car(vehicleID, leaveTime, entryTime), entryTime, leaveTime);
		//TODO: handle parking lot is full
		JSONObject result = new JSONObject();
		result.put("parkingData", new JSONArray(alg.generateDBString()));
		result.put("statsuData", new JSONArray(alg.generateStatusString()));
		TestDB.getInstance().updateParkingLotData(parkingLot, result.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }

 private void initParkingLotData(String parkingLotName) {
	  final int width = TestDB.getInstance().getParkingLotWidth(parkingLotName);
	  //JSONObject data = TestDB.getInstance().getParkingLotJsonData(parkingLotName);
	  //JSONArray start;
	try {
		
	
		Algorithm alg = new Algorithm(width);
		JSONObject result = new JSONObject();
		result.put("parkingData", new JSONArray(alg.generateDBString()));
		result.put("statusData", new JSONArray(alg.generateStatusString()));
		TestDB.getInstance().updateParkingLotData(parkingLotName, result.toString());
		//TestDB.getInstance().updateParkingLotData(parkingLotName, result.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
}
 }
 
 private static class ThreeIndices implements Comparable{
	 int i,j,k;
	 String data;
	 public ThreeIndices(int ii, int jj, int kk, String data) {
		 i = ii;
		 j = jj;
		 k = kk;
		 this.data = data;
	 }
	@Override
	public int compareTo(Object o) {
		if((o instanceof ThreeIndices)) {
			ThreeIndices other = (ThreeIndices)o;
			if(this.i != other.i) {
				return this.i - other.i;
			}else if(this.j != other.j){
				return this.j - other.j;
			}else if(this.k != other.k) {
				return this.k - other.k;
			}else {
				return 0;
			}
		}
		return 0;
	}
	 
 }
 
 private String generateParkinglotDataForPDF(String parkingLotName) {
	 try {
		 JSONObject data = TestDB.getInstance().getParkingLotJsonData(parkingLotName);
		 JSONArray statusesJsonArr = data.getJSONArray("parkingStatus");
		 List<ThreeIndices> statuses = new ArrayList<ThreeIndices>();
		 for(int i = 0; i < data.length(); i++) {
			 JSONObject spotData = statusesJsonArr.getJSONObject(i);
			 statuses.add(new ThreeIndices(spotData.getInt("i"),spotData.getInt("j"),spotData.getInt("k"),String.valueOf((char)spotData.getInt("status"))));
		 }
		 Collections.sort(statuses);
		 String dataForPDF = "";
		 int index = 0;
		 for(ThreeIndices status : statuses) {
			 dataForPDF += String.valueOf(index++) + status.data; 
		 }
		 return dataForPDF;
		 
	 }catch(JSONException e) {
		 e.printStackTrace();
	 }
	 return "";
	
 }
 
 private void addToStatistics(String parkingLotName, int lateDelta, int cancelDelta, int arrivedDelta, int numDisabledDelta) {
	 int parkingLotID = TestDB.getInstance().getParkingLotIDByName(parkingLotName);
	 Calendar current = Calendar.getInstance();
	 current.set(Calendar.HOUR_OF_DAY, 0);
	 current.set(Calendar.MINUTE, 0);
	 current.set(Calendar.SECOND, 0);
	 current.set(Calendar.MILLISECOND, 0);
	 long todayUnixTime = current.getTimeInMillis();
	 TestDB.getInstance().initStatsIfDoesntExists(parkingLotID, todayUnixTime);
	 TestDB.getInstance().addToDailyStats(parkingLotID, todayUnixTime, lateDelta, cancelDelta, arrivedDelta, numDisabledDelta);
	 
 }
 
  private Params handleClientPhysicalOrder(Params params) {
	  boolean isInTable = TestDB.getInstance().isInTable("Users", "userID", params.getParam("ID"));
	  if(isInTable){
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "BAD");
		  return resp;
	  }
	  Params typeParams = Params.getEmptyInstance();
	  typeParams.addParam("type", "physicalOrder");
	  typeParams.addParam("parkingLot", params.getParam("parkingLot"));
	  typeParams.addParam("startTimeMS", String.valueOf(System.currentTimeMillis()));
	  typeParams.addParam("leaveTimeMS", String.valueOf(Utils.todayTimeToMillis(params.getParam("leaveTime"))));
	  final String type = typeParams.toString();
	  
	  TestDB.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

	  handleAddVehicleToDB(params.getParam("ID"), params.getParam("parkingLot"), params.getParam("vehicleID"));//TestDB.getInstance().addVehicle(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);
	  //TODO: handle if parking lot is full
	  //callParkingAlgoEnter(params.getParam("parkingLot"), params.getParam("vehicleID"), Utils.todayTimeToMillis(params.getParam("leaveTime")));
	  
	  // automatically calls handleClientEnter for physical order
	  Params enterInpParams = Params.getEmptyInstance();
	  enterInpParams.addParam("action", "clientEnter");
	  enterInpParams.addParam("vehicleID", params.getParam("vehicleID"));
	  enterInpParams.addParam("parkingLot", params.getParam("parkingLot"));
	  Params enterRespParams = handleClientEnter(enterInpParams);
	  if(enterRespParams.getParam("status").equals("BAD")) {
		  return enterRespParams;
	  }else {
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "OK");
		  return resp;
	  }
  }

  private double calcPriceUpfrontForOneTimeOrder(String parkingLot, Params subscriptionParams) {
		System.out.println("calcPriceUpfrontForOneTimeOrder");
		List<Integer> prices = TestDB.getInstance().getPrices(parkingLot);
		long startTime = Long.valueOf(subscriptionParams.getParam("enterTimeMS"));
		long leaveTime = Long.valueOf(subscriptionParams.getParam("leaveTimeMS"));
		long diff = leaveTime - startTime;
		System.out.println("diff:" + diff);
		double numHours = (diff / 1000.0 / 60.0 / 60.0);
		return numHours * prices.get(1);
  }

  private Params handleClientOneTimeOrder(Params params){
	  //TODO: automatically call Enter when there is a physical order
	  boolean isInTable = TestDB.getInstance().isInTable("Users", "userID", params.getParam("ID"));
	  if(isInTable){
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "BAD");
		  return resp;
	  }

	  Params typeParams = Params.getEmptyInstance();
	  typeParams.addParam("type", "orderedOneTimeParking");
	  typeParams.addParam("parkingLot", params.getParam("parkingLot"));
//	  typeParams.addParam("enterDate", params.getParam("enterDate"));
//	  typeParams.addParam("enterTime", params.getParam("enterTime"));
	  typeParams.addParam("enterTimeMS", String.valueOf(Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime"))));
//	  typeParams.addParam("leaveDate", params.getParam("leaveDate"));
//	  typeParams.addParam("leaveTime", params.getParam("leaveTime"));
	  typeParams.addParam("leaveTimeMS", String.valueOf(Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime"))));

	  final String type = typeParams.toString();

	  TestDB.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

//	  final long startTime = Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime"));
//	  final long leaveTime =Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime"));

	  handleAddVehicleToDB(params.getParam("ID"), params.getParam("parkingLot"), params.getParam("vehicleID"));//TestDB.getInstance().addVehicle(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);
	  callParkingAlgoOrder(params.getParam("parkingLot"), params.getParam("vehicleID"), Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime")),Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime")) );
	  //callParkingAlgo("Ordered" , params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);

	  double priceToPay = calcPriceUpfrontForOneTimeOrder(params.getParam("parkingLot"), typeParams);
	  addToUserMoney(params.getParam("ID"), -priceToPay);
	  Params resp = Params.getEmptyInstance();
	  resp.addParam("status", "OK");
	  resp.addParam("price", String.valueOf(priceToPay));
	  return resp;
  }
  
  private Params handleRoutineSubscription(Params params) {
	  
	  //TODO: support of routine subscriber that wants to enter another parking lot one time so orders in a different way (currently will return BAD b.c there is already a user with the same ID)
	  boolean isInTable = TestDB.getInstance().isInTable("Users", "userID", params.getParam("ID"));
	  if(isInTable){
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "BAD");
		  return resp;
	  }
	  
	  Params typeParams = Params.getEmptyInstance();
	  typeParams.addParam("type", "routineSubscription");
	  typeParams.addParam("subscriptionStartMS", String.valueOf(Utils.dateToMillis(params.getParam("startDate"))));
	  typeParams.addParam("enterTimeHHMM", params.getParam("enterTime"));
	  typeParams.addParam("leaveTimeHHMM", params.getParam("leaveTime"));
	  typeParams.addParam("parkingLot", params.getParam("parkingLot"));
	  final String type = typeParams.toString();
	  
	  TestDB.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

	  handleAddVehicleToDB(params.getParam("ID"), params.getParam("parkingLot"), params.getParam("vehicleID")); // adds vehicle to db if in the same day
	  
	  handleCallParkingAlgoOrderForSubscription(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), typeParams);

	  
	  int subscriptionID = TestDB.getInstance().getIndexIDOfUser(params.getParam("ID"));
	  typeParams.addParam("subscriptionID", String.valueOf(subscriptionID));
	  TestDB.getInstance().updateUserType(params.getParam("ID"), typeParams.toString());
	  Params resp = Params.getEmptyInstance();
	  resp.addParam("status", "OK");
	  resp.addParam("subscriptionID", String.valueOf(subscriptionID));
	  double price = calcSubscriptionPrice(params.getParam("parkingLot"), typeParams);
	  System.out.println("price:" + price);
	  addToUserMoney(params.getParam("ID"), -price);
	  resp.addParam("price", String.valueOf(price));
	  
	  return resp;
	  
  }

private void handleCallParkingAlgoOrderForSubscription(String userID, String vehicleID, String parkingLot, Params subscriptionParams) {
	if(!(subscriptionParams.equals("routineSubscription") || subscriptionParams.equals("fullSubscription"))) {
		System.out.println("ERR: called handleCallParkingAlgoOrderForSubscription with subscriptionType that are not of a subscription");
		return;
	}
	long subscriptionStartTimeMS = Long.valueOf(subscriptionParams.getParam("subscriptionStartMS"));
	//if subscription is valid
	if(System.currentTimeMillis() > subscriptionStartTimeMS && Utils.isInLastMonth(subscriptionStartTimeMS)) {
		//call parking algo for ordering a parking spot
		String endTimeHour = (subscriptionParams.equals("routineSubscription") ? subscriptionParams.getParam("leaveTimeHHMM") : "23:59");
		String startTimeHour = (subscriptionParams.equals("routineSubscription") ? subscriptionParams.getParam("enterTimeHHMM") : "00:01");
		if(parkingLot.equals("**ANY**")) { // if is full subscription, need to order in all parking lots
			List<String> allParkingLots = TestDB.getInstance().getAllParkingLotNames();
			for(String aParkingLot : allParkingLots) {
				callParkingAlgoOrder(aParkingLot, vehicleID, Utils.timeToMillis(startTimeHour),Utils.timeToMillis(endTimeHour));
			}
		}else { // if is routine subscription order only for specific parking lot
			callParkingAlgoOrder(parkingLot, vehicleID, Utils.timeToMillis(startTimeHour), Utils.timeToMillis(endTimeHour));			
		}
	}
	
}


private double calcSubscriptionPrice(String parkingLot, Params subscriptionParams) {
	List<Integer> prices = TestDB.getInstance().getPrices(parkingLot);

	if(subscriptionParams.getParam("type").equals("routineSubscription")) {
//		long enterTimeUnix = Long.valueOf(subscriptionParams.getParam("enterTimeHHMM"));
//		long leaveTimeUnix = Long.valueOf(subscriptionParams.getParam("enterTimeHHMM"));
//		long diff = leaveTimeUnix - enterTimeUnix; 
//		//TODO: update users money value in DB
//		//TODO: support multiple vehicles
//		double numHours = (int)(diff / 1000.0 / 60.0 / 60.0);
//		return (int)(numHours * prices.get(2) * prices.get(1));
		//TODO: update users money value in DB
		//TODO: support multiple vehicles
		System.out.println("calcing routine sub price");
		System.out.println(prices.get(2) + "," + prices.get(1));
		return (prices.get(2) * prices.get(1));
	}else if(subscriptionParams.getParam("type").equals("fullSubscription")) {
		prices = TestDB.getInstance().getPrices("Default");

		return (prices.get(4) * prices.get(1));
	}
	return 0;
}


private Params handleFullSubscription(Params params) {
	  
	  //TODO: support of routine subscriber that wants to enter another parking lot one time so orders in a different way (currently will return BAD b.c there is already a user with the same ID)
	  boolean isInTable = TestDB.getInstance().isInTable("Users", "userID", params.getParam("ID"));
	  if(isInTable){
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "BAD");
		  return resp;
	  }
	  
	  //TODO: handle max park time is 14 days
	  //TODO: can't park more than subscription unless the subscription is renewed
	  //TODO: system reminds user a week before subscription is over
	  
	  Params typeParams = Params.getEmptyInstance();
	  typeParams.addParam("type", "fullSubscription");
	  typeParams.addParam("subscriptionStartMS", String.valueOf(Utils.dateToMillis(params.getParam("startDate"))));
	  final String type = typeParams.toString();
	  
	  TestDB.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

	  handleAddVehicleToDB(params.getParam("ID"), "", params.getParam("vehicleID")); // adds vehicle to db if in the same day
	  handleCallParkingAlgoOrderForSubscription(params.getParam("ID"), params.getParam("vehicleID"), "**ANY**", typeParams);

	  int subscriptionID = TestDB.getInstance().getIndexIDOfUser(params.getParam("ID"));
	  typeParams.addParam("subscriptionID", String.valueOf(subscriptionID));
	  TestDB.getInstance().updateUserType(params.getParam("ID"), typeParams.toString());
	  Params resp = Params.getEmptyInstance();
	  resp.addParam("status", "OK");
	  resp.addParam("subscriptionID", String.valueOf(subscriptionID));
	  double price = calcSubscriptionPrice("", typeParams);
	  System.out.println("price:" + price);
	  addToUserMoney(params.getParam("ID"), -price);
	  resp.addParam("price", String.valueOf(price));
	  return resp;
	  
  }

/**
 * 
 * @param userID
 * @param parkingLot
 * @param command: Order or Enter
 */
private void handleAddVehicleToDB(String userID, String parkingLot, String vehicleID) {
	//first inserts vehicles to Vehicles table
	//TODO: support multiple vehicles
	//String vehicleID = TestDB.getInstance().getUserVehicleID(userID);
	TestDB.getInstance().addVehicle(userID, vehicleID, parkingLot, 0, 0, false);
	
	//TODO: also calls parkingAlgo for all vehicles of today
	//TODO: call routinely as a cron job. 

}

private void addToUserMoney(String userID, double amount) {
	TestDB.getInstance().addToUserMoney(userID, (amount));
}

private List<Integer> getPrices(String parkingLot){
	return TestDB.getInstance().getPrices(parkingLot);
}

private String updateVehicleEntered(String userID, String vehicleID, String parkingLot, Params subscriptionTypeParams) {
	boolean isInTable = TestDB.getInstance().isInTable("Vehicles", "vehicleID", vehicleID);
	if(!isInTable) {
		return "VehicleID is not in Vehicles table";
	}
	String currentUnixTime = String.valueOf(System.currentTimeMillis());
	String expectedLeaveTimeUnix = getExpectedLeaveTimeUnix(subscriptionTypeParams);
	TestDB.getInstance().updateVehicleStartLeaveTimes(vehicleID, currentUnixTime, expectedLeaveTimeUnix);
	TestDB.getInstance().updateVehicleIsParking(vehicleID, "true");
	//TODO: call car parking algo with 'Enter' command
	return "OK";
}

//private void updateVehiclesForAllUsersDaily(String param) {
//	//TODO: impl. Adds all vehicles to Vehicles Table based on users (handles orders, routine subscriptions, full subscription, etc.)
//}




private String getExpectedLeaveTimeUnix(Params subscriptionParams) {
	String subType = subscriptionParams.getParam("type");
	if(subType.equals("physicalOrder")) {
		return subscriptionParams.getParam("leaveTimeMS");
	}else if(subType.equals("orderedOneTimeParking")) {
		return subscriptionParams.getParam("leaveTimeMS");
	}else if(subType.equals("routineSubscription")) {
		return String.valueOf(Utils.todayTimeToMillis(subscriptionParams.getParam("leaveTimeHHMM")));
	}else if(subType.equals("fullSubscription")) {
		return "0";
	}
	return "0";
	
}


private Params handleClientEnter(Params params) {
	String vehicleID = params.getParam("vehicleID");
	String parkingLot = params.getParam("parkingLot");
	boolean isInTable = TestDB.getInstance().isInTable("Vehicles", "vehicleID", vehicleID);
	if(!isInTable) {
		Params resp = Params.getEmptyInstance();
		resp.addParam("status", "BAD");
		return resp;
	}
	String userID = TestDB.getInstance().getUserIdFromVehicleID(vehicleID);
	String subscriptionParamsStr = TestDB.getInstance().getUserSubscriptionTypeStr(userID);
	Params subscriptionParams = new Params(subscriptionParamsStr);
	if(needsSubscriptionID(subscriptionParams.getParam("type")) && !params.hasParam("subscriptionID")) {
		return Params.getEmptyInstance().addParam("status", "OK").addParam("needsSubscriptionID", "Yes");
	}
	String subscriptionID = needsSubscriptionID(subscriptionParams.getParam("type")) ? params.getParam("subscriptionID") : "";
	String canEnter = canEnterParking(userID, parkingLot, subscriptionID, subscriptionParams);
	if(canEnter.equals("OK")) {
		//add car to vehicles table
		updateVehicleEntered(userID, vehicleID, parkingLot, subscriptionParams);
		long leaveTime = getVehicleExpectedLeaveTime(userID, vehicleID, subscriptionParams);
		callParkingAlgoEnter(parkingLot, vehicleID, leaveTime);
//		int price = 0;
//		if(subscriptionParams.getParam("tpye").equals("orderedOneTimeParking")) {
//			price = calcPriceUpfrontForOneTimeOrder(subscriptionParams);
//		}
		if(subscriptionParams.getParam("type").equals("orderedOneTimeParking")) {
			//update stats
			addToStatistics(parkingLot, 0, 0, 1, 0);
		}
		return Params.getEmptyInstance().addParam("status", "OK").addParam("needsSubscriptionID", "No");
	}else {
		return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", canEnter);
	}
}



private long getVehicleExpectedLeaveTime(String userID, String vehicleID, Params subscriptionParams) {
	if(subscriptionParams.getParam("type").equals("physicalOrder")) {
		return Long.valueOf(subscriptionParams.getParam("startTimeMS"));
	}else if(subscriptionParams.getParam("type").equals("orderedOneTimeParking")) { //TODO: impl. for other types
		return Long.valueOf(subscriptionParams.getParam("leaveTimeMS"));
	}else if(subscriptionParams.getParam("type").equals("routineSubscription")) {
		return Long.valueOf(subscriptionParams.getParam("leaveTimeHHMM"));
	}else if(subscriptionParams.getParam("type").equals("fullSubscription")) {
		return Utils.timeToMillis("23:59");
	}else {
		System.out.println("ERR: getVehicleExpectedLeaveTime with unexpected subscription type");
	}
	return 0l;
}


private boolean needsSubscriptionID(String type) {
	return (type.equals("routineSubscription") || type.equals("fullSubscription"));
}


private String canEnterParking(String userID, String parkingLot, String subscriptionID, Params subscriptionParams) {
	String subType = subscriptionParams.getParam("type");
	if(subType.equals("physicalOrder")) {
		//TODO: check if parkingLot is full
		return "OK";
	}else if(subType.equals("orderedOneTimeParking")) {
		//check if same parking lot
		if(!parkingLot.equals(subscriptionParams.getParam("parkingLot"))) {
			return "Not same parking lot";
		}
		//check if arrived in time
		//TODO: handle case where user is late 
		//check if time matches
		if((Utils.isCurrentTimeAfter(subscriptionParams.getParam("enterTimeMS")) && !Utils.isCurrentTimeAfter(subscriptionParams.getParam("leaveTimeMS")))){
			return "Time doesn't match";
		}
		return "OK";
	}else if(subType.equals("routineSubscription") || subType.equals("fullSubscription")) {
		//check subscription id
		if(!subscriptionID.equals(subscriptionParams.getParam("subscriptionID"))) {
			return "Not same subscriptionID";
		}
		long subscriptionStartUnixtime = Long.valueOf(subscriptionParams.getParam("subscriptionStartMS"));
		System.out.println("subscription time ms:" + subscriptionStartUnixtime);
		if(!Utils.isInLastMonth(subscriptionStartUnixtime)) {
			//subscription expired
			return "Subscription expired";
		}
		if(subType.equals("routineSubscription")) {
			//check if same parking lot
			if(!parkingLot.equals(subscriptionParams.getParam("parkingLot"))) {
				return "Not same parking lot";
			}
			//check if not in weekend
			if(Utils.isCurrentlyWeekend()) {
				return "Cannot park with routine subscription in weekends!";
			}
			//check if time matches
			if(!Utils.isCurrentHourBetween(subscriptionParams.getParam("enterTimeHHMM"),subscriptionParams.getParam("leaveTimeHHMM"))) {
				return "Current hour not included in your subscription";
			}
			return "OK";
		}else { // full subscription
			return "OK";
		}
		}

	return "Invalid subscription type";

	}

private Params handleClientLeave(Params params) {
	String vehicleID = params.getParam("vehicleID");
	String parkingLot = params.getParam("parkingLot");
	boolean isInTable = TestDB.getInstance().isInTable("Vehicles", "vehicleID", vehicleID);
	if(!isInTable) {
		Params resp = Params.getEmptyInstance();
		resp.addParam("status", "BAD").addParam("message", "Vehicle not in parking");
		return resp;
	}
	boolean isVehicleInParking = TestDB.getInstance().getIsVehicleInParking(vehicleID);
	if(!isVehicleInParking) {
		return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", "Vehicle is not currently parked");
	}
	TestDB.getInstance().updateVehicleIsParking(vehicleID, "false");
	String userID = TestDB.getInstance().getUserIdFromVehicleID(vehicleID);
	String subscriptionParamsStr = TestDB.getInstance().getUserSubscriptionTypeStr(userID);
	Params subscriptionParams = new Params(subscriptionParamsStr);
	double priceToPay = calcPriceToPay(parkingLot, vehicleID, subscriptionParams);
	// remove from Users table if is physicalOrder or OneTimeOrder	
	if(subscriptionParams.getParam("type").equals("physicalOrder") || subscriptionParams.getParam("type").equals("orderedOneTimeParking")){
		TestDB.getInstance().removeUser(userID);		
	}
	//from vehicle from vehicles table
	TestDB.getInstance().removeVehicle(vehicleID);
	callParkingAlgoLeave(parkingLot, vehicleID);
	//returns response to user;
	return Params.getEmptyInstance().addParam("status", "OK").addParam("payAmount", String.valueOf(priceToPay));

}


private double calcPriceToPay(String parkingLot, String vehicleID, Params subscriptionParams) {
	List<Integer> prices = TestDB.getInstance().getPrices(parkingLot);
	if(subscriptionParams.getParam("type").equals("physicalOrder")) {
		System.out.println("inside pay for single order");
		long startParkTimeUnix = TestDB.getInstance().getVehicleStartParkTime(vehicleID);
		long deltaTimeUnix = System.currentTimeMillis() - startParkTimeUnix;
		System.out.println("deltaTime is:" + deltaTimeUnix);
		double numHours = (deltaTimeUnix / 1000.0 / 60.0 / 60.0);
		System.out.println("num hours:" + numHours);
		return (numHours * prices.get(0));
	}else if(subscriptionParams.getParam("type").equals("orderedOneTimeParking")) {
		// handle if client stayed parking more than she ordered
		long plannedLeaveTimeUnix = Long.valueOf(subscriptionParams.getParam("leaveTimeMS"));
		long deltaTimeUnix = System.currentTimeMillis() - plannedLeaveTimeUnix;
		double numHours = (deltaTimeUnix / 1000.0 / 60.0 / 60.0);
		final double LATE_TO_EXIT_PRICE_FACTOR = 2;
		return (numHours * prices.get(1) * LATE_TO_EXIT_PRICE_FACTOR);
	}
	
	return 0;
}


private Params handleClientCancelOrder(Params params) {
	String vehicleID = params.getParam("vehicleID");
	String userID = TestDB.getInstance().getUserIDByVehicleID(vehicleID);
	String parkingLot = TestDB.getInstance().getVehicleParkingLot(vehicleID);
	Params subscriptionParams = new Params(TestDB.getInstance().getUserSubscriptionTypeStr(userID));
	if (!subscriptionParams.getParam("type").equals("orderedOneTimeParking")) {
		return Params.getEmptyInstance().addParam("status", "BAD").addParam("message", "Your Order/subscription type cannot be canceled");
	}
	addToStatistics(parkingLot, 0, 1, 0, 0);
	double priceToPay = -1;
	double hoursDiff = Utils.getHoursDiff(subscriptionParams.getParam("enterTimeMS"));
	System.out.println("hours diff is:" + hoursDiff);
	double originalPrice = calcPriceUpfrontForOneTimeOrder(parkingLot, subscriptionParams);
	addToUserMoney(userID, originalPrice); // first refund to user his original payment
	//calculate how much use has to pay
	if(hoursDiff > 3.0) {
		priceToPay = originalPrice * 0.1;
	}else if(hoursDiff > 1.0) {
		priceToPay = originalPrice * 0.5;
	}else {
		priceToPay = originalPrice;
	}
		
	addToUserMoney(userID, -priceToPay); // user has to pay the fine for canceling
	return Params.getEmptyInstance().addParam("status", "OK").addParam("returnAmount", String.valueOf(originalPrice - priceToPay));

}




private Params handleGetParkingSlotStatus(Params params) {
	String parkingLotName = params.getParam("name");
	JSONObject data = TestDB.getInstance().getParkingLotJsonData(parkingLotName);
	Params resp = Params.getEmptyInstance();
	resp.addParam("status", "OK");
	try {
		resp.addParam("array", data.getJSONArray("statusData").toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return resp;
}

private Params handleGNumOfSubscribers(Params params) {
	int parkingLotID = Integer.valueOf(params.getParam("facID"));
	String parkingLotName = TestDB.getInstance().getParkingLotNameByID(parkingLotID);
	List<User> users = TestDB.getInstance().getAllUsers();
	int count = 0;
	for(User user : users) {
		if(!TestDB.getInstance().getVehicleParkingLot(user.getVehicleID()).equals(parkingLotName)) {
			continue;
		}
		if(user.getSubscriptionParams().getParam("type").equals("routineSubscription") || user.getSubscriptionParams().getParam("type").equals("fullSubscription")) {
			count++;
		}
	}
	return Params.getEmptyInstance().addParam("status", "OK").addParam("num", String.valueOf(count));
}

private Params handleGNumOfSubscribersWithMoreThanOneCar(Params params) {
	int parkingLotID = Integer.valueOf(params.getParam("facID"));
	String parkingLotName = TestDB.getInstance().getParkingLotNameByID(parkingLotID);
	List<User> users = TestDB.getInstance().getAllUsers();
	Map<String, List<String>> userToVehicle = new HashMap<String,List<String>>();
	int count = 0;
	for(User user : users) {
		if(!TestDB.getInstance().getVehicleParkingLot(user.getVehicleID()).equals(parkingLotName)) {
			continue;
		}
		if(user.getSubscriptionParams().getParam("type").equals("routineSubscription") || user.getSubscriptionParams().getParam("type").equals("fullSubscription")) {
			if(!userToVehicle.containsKey(user.getUserID())){
				userToVehicle.put(user.getUserID(), new ArrayList<String>());
			}
			if(userToVehicle.get(user.getUserID()).size() > 0){
				if(!userToVehicle.get(user.getUserID()).contains(user.getVehicleID())){
					count += 1;
				}
			}
			userToVehicle.get(user.getUserID()).add(user.getUserID());
		}
	}
	return Params.getEmptyInstance().addParam("status", "OK").addParam("num", String.valueOf(count));
}

private Params getSubscriptionStats(Params params) {
	Params respNumSubs = handleGNumOfSubscribers(params);
	Params resNumsSubsMoreThanOneVehicle = handleGNumOfSubscribersWithMoreThanOneCar(params);
	return Params.getEmptyInstance().addParam("monthly", respNumSubs.getParam("num")).addParam("monthlyWithMoreCars", resNumsSubsMoreThanOneVehicle.getParam("num"));

}

private Params toggleDisableSpot(Params params) {
	//status:true/false. floor, position, name
	try {
		//int facID = Integer.parseInt(params.getParam("faceID"));
		int floor = Integer.parseInt(params.getParam("floor"));
		int position = Integer.parseInt(params.getParam("position"));
		String parkingLotName = params.getParam("name");
		String value = params.getParam("status"); //true/false
				
		int numCols = TestDB.getInstance().getParkingLotWidth(parkingLotName);
		JSONObject parkingData = TestDB.getInstance().getParkingLotJsonData(parkingLotName);
		JSONArray statusData = new JSONArray(parkingData.getString("statusData"));
		int i = floor;
		int j = position / numCols;
		int k = position % numCols;
		JSONArray newStatusData = new JSONArray();
		for(int index = 0; index < statusData.length(); index++) {
			JSONObject spotStatus = statusData.getJSONObject(index);
			if(spotStatus.getInt("i") == i && spotStatus.getInt("j") == j && spotStatus.getInt("k") == k) {
				spotStatus.put("status", (value=="true") ? 'i' : spotStatus.get("status"));
			}
			newStatusData.put(spotStatus);
		}
		parkingData.put("statusData", newStatusData.toString());
		TestDB.getInstance().updateParkingLotData(parkingLotName, parkingData.toString());
		return Params.getEmptyInstance().addParam("status", "OK");
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return Params.getEmptyInstance().addParam("status", "BAD");
}


private Params reserveSpot(Params params) {
	//"floor" , "position":1D, name
	
	try {
		//int facID = Integer.parseInt(params.getParam("faceID"));
		int floor = Integer.parseInt(params.getParam("floor"));
		int position = Integer.parseInt(params.getParam("position"));
		String parkingLotName = params.getParam("name");
		int numCols = TestDB.getInstance().getParkingLotWidth(parkingLotName);
		JSONObject parkingData = TestDB.getInstance().getParkingLotJsonData(parkingLotName);
		JSONArray statusData = new JSONArray(parkingData.getString("statusData"));
		int i = floor;
		int j = position / numCols;
		int k = position % numCols;
		JSONArray newStatusData = new JSONArray();
		for(int index = 0; index < statusData.length(); index++) {
			JSONObject spotStatus = statusData.getJSONObject(index);
			if(spotStatus.getInt("i") == i && spotStatus.getInt("j") == j && spotStatus.getInt("k") == k) {
				spotStatus.put("status", 's');
			}
			newStatusData.put(spotStatus);
		}
		parkingData.put("statusData", newStatusData.toString());
		TestDB.getInstance().updateParkingLotData(parkingLotName, parkingData.toString());
		return Params.getEmptyInstance().addParam("status", "OK");
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return Params.getEmptyInstance().addParam("status", "BAD");
}



private Params getSpotStatus(Params params) {

	try {
		//int facID = Integer.parseInt(params.getParam("faceID"));
		int floor = Integer.parseInt(params.getParam("floor"));
		int position = Integer.parseInt(params.getParam("position"));
		String parkingLotName = params.getParam("name");
		int numCols = TestDB.getInstance().getParkingLotWidth(parkingLotName);
		JSONObject parkingData = TestDB.getInstance().getParkingLotJsonData(parkingLotName);
		JSONArray statusData = new JSONArray(parkingData.getString("statusData"));
		int i = floor;
		int j = position / numCols;
		int k = position % numCols;
		JSONArray newStatusData = new JSONArray();
		for(int index = 0; index < statusData.length(); index++) {
			JSONObject spotStatus = statusData.getJSONObject(index);
			if(spotStatus.getInt("i") == i && spotStatus.getInt("j") == j && spotStatus.getInt("k") == k) {
				return Params.getEmptyInstance().addParam("status", "OK")
						.addParam("status", spotStatus.getString("status"))
						.addParam("isDisabled", String.valueOf(spotStatus.getString("status").equals("i")))
						.addParam("isReserved", String.valueOf(spotStatus.getString("status").equals("s")));
			}
			newStatusData.put(spotStatus);
		}
		parkingData.put("statusData", newStatusData.toString());
		TestDB.getInstance().updateParkingLotData(parkingLotName, parkingData.toString());
		return Params.getEmptyInstance().addParam("status", "OK");
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return Params.getEmptyInstance().addParam("status", "BAD");
}


/**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client){
	    System.out.println("dummy server got message:" + msg);
	    Params params = new Params(msg.toString());
	    try {
	    	if(params.getParam("action").equals("RoutineSubscription")){ // -V
	    		Params resp = handleRoutineSubscription(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("FullSubscription")){// -V
	    		Params resp = handleFullSubscription(params);
	    		client.sendToClient(resp.toString());
	    	}
	    	else if(params.getParam("action").equals("ClientPhysicalOrder")){ // - V
	    		Params resp = handleClientPhysicalOrder(params);
	    		client.sendToClient(resp.toString());
	    	}
	    	else if(params.getParam("action").equals("clientOneTimeOrder")){ // - V
	    		Params resp = handleClientOneTimeOrder(params);
	    		client.sendToClient(resp.toString());
	    	}
	    	else if(params.getParam("action").equals("clientLeave")){ // -V
	    		Params resp = handleClientLeave(params);
	    		client.sendToClient(resp.toString());	
	    	}else if(params.getParam("action").equals("clientEnter")){ // -V
	    		Params resp = handleClientEnter(params);
	    		client.sendToClient(resp.toString());	    	
	    	}else if(params.getParam("action").equals("clientEnterWithSubscriptionID")){ // -V
	    		System.out.println("clientEnterWithSubscriptionID");
	    		Params resp = handleClientEnter(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("clientCancelOrder")){ // -V
	    		System.out.println("clientCancelOrder");
	    		Params resp = handleClientCancelOrder(params);
	    		client.sendToClient(resp.toString());
	    		//resp.addParam("status", "OK");
//	    		resp.addParam("returnAmount", "42155");
//	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("clientContact")){ // - In Gil's code
//	    		System.out.println("clientContact");
//	    		Params resp = Params.getEmptyInstance();
//	    		resp.addParam("status", "OK");
//	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("getParkingSlotStatus")) {
	    		System.out.println("getParkingSlotStatus");
	    		Params resp = handleGetParkingSlotStatus(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("getNumOfSubscribers")) {
	    		System.out.println("getNumOfSubscribers");
	    		Params resp = handleGNumOfSubscribers(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("getNumOfSubscribersWithMoreThanOneCar")) {
	    		System.out.println("getNumOfSubscribersWithMoreThanOneCar");
	    		Params resp = handleGNumOfSubscribersWithMoreThanOneCar(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("getSubscriptionStats")) {
	    		System.out.println("getSubscriptionStats");
	    		Params resp = getSubscriptionStats(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("reserveSpot")) {
	    		System.out.println("reserveSpot");
	    		Params resp = reserveSpot(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("spotDisabled")) {
	    		System.out.println("sportDisabled");
	    		Params resp = toggleDisableSpot(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("getSpotStatus")) {
	    		System.out.println("getSpotStatus");
	    		Params resp = getSpotStatus(params);
	    		client.sendToClient(resp.toString());
	    	}
	    	else{
	    		client.sendToClient("{}");
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

  


/**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */
  public static void main(String[] args)
  {


    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }


    ServerDummy sv = new ServerDummy(port);
    TestDB.getInstance(); //  init db
//    sv.initParkingLotData("misgavParking");
//    System.exit(0);
//    sv.addToStatistics("A", 1, 2, 3, 4);
//    System.exit(0);
//    System.out.println(sv.generateParkinglotDataForPDF("A"));
//    System.exit(0);
//    TestDB.getInstance().addParkingLot("A", sv.generateEmptyParkingLotDataJson(3, 4, 5).toString(), 5);
//	System.exit(0);
//    Params params = Params.getEmptyInstance();
//    params.addParam("action", "clientOneTimeOrder");
//    params.addParam("ID", "253532");
//    params.addParam("parkingLot", "A");
//    params.addParam("vehicleID", "317288");
//    params.addParam("leaveTime", "123123");
//    params.addParam("leaveDate", "123123");
//    params.addParam("enterTime", "123123");
//    params.addParam("enterDate", "123123");
//    params.addParam("email", "gaga123");
//	System.out.println(sv.handleClientOneTimeOrder(params));
//	System.exit(0);
    try
    {
      sv.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
