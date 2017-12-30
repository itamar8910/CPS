// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;
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
public class Server extends AbstractServer
{
  //Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;



  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public Server(int port)
  {
    super(port);
    System.out.println("Tables:");
    TestDB.getInstance().printAllTables();
    //TestDB.getInstance().addRow("itamar", 100);
    //TestDB.getInstance().getBalanceOf("itamar");
  }


  //Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	    System.out.println("Message received: " + msg + " from " + client);
	    //this.sendToAllClients(msg);
	    String msgStr = msg.toString();
	    if(msgStr.startsWith("add")){
	    	System.out.println("got add command");
	    	try{
	    	//System.out.println(msgStr.indexOf("add ") + "add ".length());
	    	//System.out.println("add ".length() + msgStr.substring(msgStr.indexOf("add ") + "add ".length()).indexOf(" "));
	    	String name = msgStr.substring(msgStr.indexOf("add ") + "add ".length(), "add ".length() + msgStr.substring(msgStr.indexOf("add ") + "add ".length()).indexOf(" "));
	    	int num = Integer.valueOf(msgStr.substring(msgStr.indexOf(name) + name.length() + 1));
	    	System.out.println(name);
	    	System.out.println(num);
	    	boolean success = TestDB.getInstance().addRow(name, num);
	    	if(success){
	    		this.sendToAllClients("Added");
	    	}else{
	    		this.sendToAllClients("client already exists");
	    	}
	    	}catch(Exception e){
	    		System.out.println("ERR:" + e.getMessage());
	    		this.sendToAllClients("ERR");
	    	}
	    }else if(msgStr.startsWith("get")){
	    	System.out.println("got get command");
	    	String name = msgStr.substring("get ".length());
	    	int balance = TestDB.getInstance().getBalanceOf(name);
	    	if(balance == -1){
	    		this.sendToAllClients("No such account");
	    	}else{
	    		this.sendToAllClients("Balance:" + balance);
	    	}
	    }else if(msgStr.startsWith("update")){
	    	System.out.println("got update command");
	    	try{
	    	//System.out.println(msgStr.indexOf("add ") + "add ".length());
	    	//System.out.println("add ".length() + msgStr.substring(msgStr.indexOf("add ") + "add ".length()).indexOf(" "));
	    	String name = msgStr.substring(msgStr.indexOf("update ") + "update ".length(), "update ".length() + msgStr.substring(msgStr.indexOf("update ") + "update ".length()).indexOf(" "));
	    	int num = Integer.valueOf(msgStr.substring(msgStr.indexOf(name) + name.length() + 1));
	    	System.out.println(name);
	    	System.out.println(num);
	    	TestDB.getInstance().updateRow(name, num);
	    	this.sendToAllClients("Updated");
	    	}catch(Exception e){
	    		System.out.println("ERR:" + e.getMessage());
	    		this.sendToAllClients("ERR");
	    	}
	    }
	    else{
	    	this.sendToAllClients("Invalid query");
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

    Server sv = new Server(port);

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
