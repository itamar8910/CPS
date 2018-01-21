package common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

/**
* Talk To Server
* Implemented by the Singleton design pattern
* Responsible to client-server communication callbacks for the display
* of objects onto the client UI.
*/

public class TalkToServer implements ChatIF{


	private static TalkToServer instance;

	private Client client;
	boolean wait;
	//StrCallbackIF currentCallback;
	Queue<StrCallbackIF> callbacks;

	/**
	* returns instance of TalkToServre corresponding to given params
	* @param ip Ip of sever
	* @param port Port the server listens on
	*/
	public static TalkToServer getInstance(String ip, int port){
		if(instance == null){
			instance = new TalkToServer(ip, port);
		}

        return instance;
	}

	/**
	* returns the instance of TalkToServre (singleton design pattern)
	*/
	public static TalkToServer getInstance(){
		if(instance == null){
			return null;
		}
        return instance;
	}

	/**
	* Constructor - constructs new object with given params
	* @param ip Server's ip
	* @param port Port server listens on
	*/
	private TalkToServer(String ip, int port){
		callbacks = new LinkedList<StrCallbackIF>();
		try
        {
          client = new Client(ip, port, this);
        }
        catch(IOException exception)
        {
          System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
          System.exit(1);
        }
	}

	@Override
	public void display(String message) {
		System.out.println("display was called");
		callbacks.poll().handle(message);
		wait = false;
	}

	/**
	* Send a message to server, with corresponding callback
	* @param message Message to be sent to server
	* @param callback Action corresponding to the message
	*/
	public void send(String message, StrCallbackIF callback){
		System.out.println("sending:" + message);
		try {
			this.client.sendToServer(message);
			//this.currentCallback = callback;
			callbacks.add(callback);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	* Send a message to server, with corresponding callback, wait for ans
	* @param message Message to be sent to server
	* @param callback
	*/
	public void sendAndWait(String message, StrCallbackIF callback){
		wait = true;
		try {
			this.client.sendToServer(message);
			callbacks.add(callback);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(wait) {
			System.out.println("waiting in sendAndWait");
		}
	}


}
