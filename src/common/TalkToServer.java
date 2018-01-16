package common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class TalkToServer implements ChatIF{


	private static TalkToServer instance;

	private Client client;
	boolean wait;
	//StrCallbackIF currentCallback;
	Queue<StrCallbackIF> callbacks;
	
	public static TalkToServer getInstance(String ip, int port){
		if(instance == null){
			instance = new TalkToServer(ip, port);
		}
        return instance;
	}

	public static TalkToServer getInstance(){
		if(instance == null){
			return null;
		}
        return instance;
	}

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
		
		callbacks.poll().handle(message);
		wait = false;
	}

	public void send(String message, StrCallbackIF callback){
		try {
			this.client.sendToServer(message);
			//this.currentCallback = callback;
			callbacks.add(callback);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
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
