package application_itamar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.Client;
import common.ChatIF;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;

public class ClientController implements ChatIF{

	private Client client;

    @FXML
    private TextField tf;

    @FXML
    private Button bt;

    @FXML
    private ListView<String> lv;

    @FXML
    private ListView<String> lv2;

    private List<String> usernames, balances;

    private String lastRequest = "";



    @FXML
    void buttonPress(ActionEvent event) {
    	System.out.println("press");
    	if(tf.getText().equals("clear")) {
    		usernames.clear();
    		balances.clear();
    		tf.clear();
    		lv.setItems(FXCollections.observableArrayList(usernames));
    		lv2.setItems(FXCollections.observableArrayList(balances));
    		return;
    	}
    	lastRequest = tf.getText();
    	tf.clear();
    	client.handleMessageFromClientUI(lastRequest);
    	//lastRequest = "getAll";
    	//client.handleMessageFromClientUI("getAll");
    }

    @FXML
    void TFEnter(ActionEvent event) {
    	System.out.println("tf enter");
    	buttonPress(event);
    }

    public void initConnection(String host, int port) {
    	try
        {
          client= new Client(host, port, this);
        }
        catch(IOException exception)
        {
          System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
          System.exit(1);
        }
    }


    public void initialize() {
    	System.out.println("initialize");
    	usernames = new ArrayList<String>();
    	balances = new ArrayList<String>();

		lv.setItems(FXCollections.observableArrayList(usernames));
		lv2.setItems(FXCollections.observableArrayList(balances));

    }

	@Override
	public void display(String message) {
		// TODO Auto-generated method stub

		System.out.println("got message to display:" + message);
		System.out.println("last request is:" + lastRequest);
		if(lastRequest.startsWith("get ") && !message.equals("No such account")) {
			String user = lastRequest.substring("get ".length());
			System.out.println("got details of user:" + user);
			if(!usernames.contains(user)) {
				usernames.add(user);
				balances.add("0");
			}
			int userIndex = usernames.indexOf(user);
			if(balances.size() > 0)
				balances.remove(userIndex);
			balances.add(userIndex, message.substring("balance:".length()));
		}else if(lastRequest.equals("getAll") && !message.equals("")) {
			usernames.clear();
			balances.clear();
			String[] usersAndBalances = message.split("\n");
			for(String userAndBalance : usersAndBalances) {
				usernames.add(userAndBalance.substring(0, userAndBalance.indexOf(",")));
				balances.add(userAndBalance.substring(userAndBalance.indexOf(",")+1));
			}
		}
		System.out.println(usernames);
		System.out.println(balances);

		Platform.runLater(new Runnable() {
		      @Override public void run() {
		    	  lv.setItems(FXCollections.observableArrayList(usernames));
		  		lv2.setItems(FXCollections.observableArrayList(balances));
		      }
		    });



	}


}
