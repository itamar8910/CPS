// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.Params;
import ocsf.server.*;

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

  private void callParkingAlgo(String param, String param2, long startTime, long parseLong) {

  }

  private Params handleClientPhysicalOrder(Params params) {
	  boolean isInTable = TestDB.getInstance().isInTable("Users", "userID", params.getParam("ID"));
	  if(isInTable){
		  Params resp = Params.getEmptyInstance();
		  resp.addParam("status", "BAD");
		  return resp;
	  }

	  TestDB.getInstance().addUser(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("email"), "temp");

	  final long startTime = System.currentTimeMillis();

	  TestDB.getInstance().addVehicle(params.getParam("ID"), params.getParam("vehicleID"), params.getParam("parkingLot"), System.currentTimeMillis(), Long.parseLong(params.getParam("leaveTime")));

	  callParkingAlgo(params.getParam("vehicleID"), params.getParam("parkingLot"), startTime, Long.parseLong(params.getParam("leaveTime")));

	  Params resp = Params.getEmptyInstance();
	  resp.addParam("status", "OK");
	  return resp;
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
	    		Params resp = Params.getEmptyInstance();
	    		resp.addParam("status", "OK");
	    		resp.addParam("subscriptionID", "123");
	    		client.sendToClient(resp.toString());
	    	}
	    	if(params.getParam("action").equals("ClientPhysicalOrder")){
	    		Params resp = handleClientPhysicalOrder(params);
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
    Params params = Params.getEmptyInstance();
    params.addParam("action", "ClientPhysicalOrder");
    params.addParam("ID", "1243");
	System.out.println(sv.handleClientPhysicalOrder(params));
	System.exit(0);
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
