package common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import client.Client;

public class TalkToServer implements ChatIF{


	private static TalkToServer instance;

	private Client client;
	boolean wait;
	StrCallbackIF currentCallback;

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
		currentCallback.handle(message);
		wait = false;
	}

	public void send(String message, StrCallbackIF callback){
		try {
			this.client.sendToServer(message);
			this.currentCallback = callback;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void sendAndWait(String message, StrCallbackIF callback){
		wait = true;
		try {
			this.client.sendToServer(message);
			this.currentCallback = callback;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(wait) {
			System.out.println("waiting in sendAndWait");
		}
	}


}
