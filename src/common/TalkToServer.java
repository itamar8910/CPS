package common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import client.Client;

public class TalkToServer implements ChatIF{


	private static TalkToServer instance;

	private Client client;

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
	}

	public void send(String message, StrCallbackIF callback){
		this.currentCallback = callback;

	}


}
