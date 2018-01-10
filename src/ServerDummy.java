// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.Params;
import common.ParkingAlgo;
import common.Utils;
import ocsf.server.*;

/*TODOS
 * ORDER:
 * - Impl. etner
 * - Impl. leave
 * - Impl. cancel
 * - Impl. parking algo integration
 * 
 * 	TODO: add a cronjob that executes at midnight and adds all vehicles with subscription to the Vehicles table

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
    System.out.println("Tables:");
    TestDB.getInstance().printAllTables();
    //TestDB.getInstance().addRow("itamar", 100);
    //TestDB.getInstance().getBalanceOf("itamar");
  }


  //Instance methods ************************************************


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



  private void callParkingAlgo(String op, String vehicleID, String parkingLot, long startTime, long leaveTime) {
	  try{
	  final int width = TestDB.getInstance().getParkingLotWidth(parkingLot);
	  JSONArray data = TestDB.getInstance().getParkingLotJsonData(parkingLot);
	  String dataStr = "";

	  for(int i = 0; i < data.length(); i++){
		  JSONObject carJson = data.getJSONObject(i);
		  dataStr += carJson.getString("row") + ", " + carJson.getString("height") + ", " + carJson.getString("col") + ", " + carJson.getString("status") + ", "
				  + carJson.getString("enterTime") + ", " + carJson.getString("leaveTime") + ", " + carJson.getString("Vid") + "\n";
	  }

	  String opStr = "";
	  if(op.equals("Reserve")){
		  opStr = "Reserve " + startTime + ", " + leaveTime + ", " + vehicleID;
	  } else if(op.equals("Enter")){
		  opStr = "Enter " + startTime + ", " + leaveTime + ", " + vehicleID;
	  }else if(op.equals("Leave")){
		  opStr = "Leave " + vehicleID;
	  }

	  String newData = ParkingAlgo.doParking(dataStr, width, opStr);
	  JSONArray newDataJson = new JSONArray();

	  for(String line : newData.split("\n")){
		  String[] lineData = line.split(",");
		  JSONObject slotJson = new JSONObject();
		  try {
			slotJson.put("row", lineData[0]);
			slotJson.put("height", lineData[1]);
			slotJson.put("col", lineData[2]);
			slotJson.put("status", lineData[3]);
			slotJson.put("enterTime", lineData[4]);
			slotJson.put("leaveTime", lineData[5]);
			slotJson.put("Vid", lineData[6]);
			newDataJson.put(slotJson);
			//slots.add(slotJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  System.out.println("calling updateParkingLotData");
	  TestDB.getInstance().updateParkingLotData(parkingLot, newDataJson.toString());
	  System.out.println("called updateParkingLotData");

	  }catch(Exception e){
		  e.printStackTrace();
	  }
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

	  //final long startTime = System.currentTimeMillis();
	  //final long leaveTime = Utils.TimeToMillis(params.getParam("leaveTime"));

	  updateVehiclesForUser(params.getParam("ID"));//TestDB.getInstance().addVehicle(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);

	  //callParkingAlgo("Enter" , params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);

	  Params resp = Params.getEmptyInstance();
	  resp.addParam("status", "OK");
	  return resp;
  }

  private double calcParkingPriceUpfront(String param, String type, long parkingTimeInMillis) {
		return 100; //TODO: implement
	}

  private Params handleClientOneTimeOrder(Params params){
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

	  final long startTime = Utils.dateAndTimeToMillis(params.getParam("enterDate"), params.getParam("enterTime"));
	  final long leaveTime =Utils.dateAndTimeToMillis(params.getParam("leaveDate"), params.getParam("leaveTime"));

	  updateVehiclesForUser(params.getParam("ID"));//TestDB.getInstance().addVehicle(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);

	  //callParkingAlgo("Ordered" , params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, leaveTime);

	  double priceToPay = calcParkingPriceUpfront(params.getParam("parkingLot"), type, leaveTime-startTime);

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

	  updateVehiclesForUser(params.getParam("ID")); // adds vehicle to db if in the same day
	  
	  int subscriptionID = TestDB.getInstance().getIndexIDOfUser(params.getParam("ID"));
	  typeParams.addParam("subscriptionID", String.valueOf(subscriptionID));
	  TestDB.getInstance().updateUserType(params.getParam("ID"), typeParams.toString());
	  Params resp = Params.getEmptyInstance();
	  resp.addParam("status", "OK");
	  resp.addParam("subscriptionID", String.valueOf(subscriptionID));
	  return resp;
	  
  }

private Params handleFullSubscription(Params params) {
	  
	  //TODO: support of routine subscriber that wants to enter another parking lot one time so orders in a different way (currently will return BAD b.c there is already a user with the same ID)
	  boolean isInTable = TestDB.getInstance().isInTable("Users", "userID", params.getParam("ID"));
	  if(isInTable){
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "BAD");
		  return resp;
	  }
	  
	  Params typeParams = Params.getEmptyInstance();
	  typeParams.addParam("type", "fullSubscription");
	  typeParams.addParam("subscriptionStartMS", String.valueOf(Utils.dateToMillis(params.getParam("startDate"))));
	  final String type = typeParams.toString();
	  
	  TestDB.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), type);

	  updateVehiclesForUser(params.getParam("ID")); // adds vehicle to db if in the same day
	  
	  int subscriptionID = TestDB.getInstance().getIndexIDOfUser(params.getParam("ID"));
	  typeParams.addParam("subscriptionID", String.valueOf(subscriptionID));
	  TestDB.getInstance().updateUserType(params.getParam("ID"), typeParams.toString());
	  Params resp = Params.getEmptyInstance();
	  resp.addParam("status", "OK");
	  resp.addParam("subscriptionID", String.valueOf(subscriptionID));
	  return resp;
	  
  }

private void updateVehiclesForUser(String userID) {
	//TODO: impl. Adds vehicle to Vehicles Table based on user ID (handles orders, routine subscriptions, full subscription, etc.)
	//TODO call parking algo if parking is for today
}

private void updateVehiclesForAllUsersDaily(String param) {
	//TODO: impl. Adds all vehicles to Vehicles Table based on users (handles orders, routine subscriptions, full subscription, etc.)
	//TODO: also calls parkingAlgo for all vehicles of today
	//TODO: call routinely as a cron job. 
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
	    	if(params.getParam("action").equals("RoutineSubscription")){
	    		Params resp = handleRoutineSubscription(params);
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("FullSubscription")){
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
	    	else if(params.getParam("action").equals("clientLeave")){
	    		System.out.println("clientLeave");
	    		Params resp = Params.getEmptyInstance();
	    		resp.addParam("status", "OK");
	    		resp.addParam("payAmount", "123142");
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("clientEnter")){
	    		System.out.println("clientEnter");
	    		Params resp = Params.getEmptyInstance();
	    		resp.addParam("status", "OK");
	    		resp.addParam("needsSubscriptionID", "Yes");
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("clientEnterWithSubscriptionID")){
	    		System.out.println("clientEnterWithSubscriptionID");
	    		Params resp = Params.getEmptyInstance();
	    		resp.addParam("status", "OK");

	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("clientCancelOrder")){
	    		System.out.println("clientCancelOrder");
	    		Params resp = Params.getEmptyInstance();
	    		resp.addParam("status", "OK");
	    		resp.addParam("returnAmount", "42155");
	    		client.sendToClient(resp.toString());
	    	}else if(params.getParam("action").equals("clientContact")){
	    		System.out.println("clientContact");
	    		Params resp = Params.getEmptyInstance();
	    		resp.addParam("status", "OK");
	    		client.sendToClient(resp.toString());
	    	}else{
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
