package application;

import java.net.URL;
import javafx.application.Platform;
import java.util.ResourceBundle;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;



public class WorkerController implements ControllerIF{

    private ApplicationMain main;
    private Params params;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Text invalideText;

    @FXML
    private URL location;

    @FXML
    private PasswordField Password;

    @FXML
    private AnchorPane Screen;

    @FXML
    private Button loginBottun;

    @FXML
    private TextField userName;


    @FXML
    void pressedLogin(ActionEvent event) {
    	System.out.println("Pressed Login");
    	if(userName.getText() == "") {
    		Platform.runLater(new Runnable() {
	  		      @Override public void run() {
	  	    		 final Stage dialog = new Stage();
	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	  	             dialog.initOwner(main.primaryStage);
	  	             VBox dialogVbox = new VBox(20);
	  	             dialogVbox.getChildren().add(new Text("Enter Username"));
	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	  	             dialog.setScene(dialogScene);
	  	             dialog.show();
	  	             System.out.println("showed dialog");
	  		      }
		    	});
    		return;
    	}
    	if(Password.getText() == "") {
    		Platform.runLater(new Runnable() {
	  		      @Override public void run() {
	  	    		 final Stage dialog = new Stage();
	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	  	             dialog.initOwner(main.primaryStage);
	  	             VBox dialogVbox = new VBox(20);
	  	             dialogVbox.getChildren().add(new Text("Enter Password"));
	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	  	             dialog.setScene(dialogScene);
	  	             dialog.show();
	  	             System.out.println("showed dialog");
	  		      }
		    	});
    		return;
    	}

    	Params loginParams = Params.getEmptyInstance();
    	loginParams.addParam("UserName", userName.getText());
    	loginParams.addParam("Password", Password.getText());
    	loginParams.addParam("action", "employeLogin");

    	// send this JSON to server
    	TalkToServer.getInstance().send(loginParams.toString(), msg ->
    	{
    		Params res = new Params(msg);

        	Params userParams = Params.getEmptyInstance();
        	userParams.addParam("facID",res.getParam("facID"));
        	userParams.addParam("userID", res.getParam("userID"));


    		if(true || res.getParam("status").equals("OK")){ //TODO: remove true ||, this is for dbg
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();

    	  	    		int typeOfWorker = Integer.parseInt(res.getParam("type"));

    	  	        	switch(typeOfWorker)
    	  	        	{
    	  	        	case 1: // Facility Manager
    	  	        		System.out.println("user is a Facillity Manager");
    	  	        		main.setScene("FacilityManagerMain.fxml",userParams);
    	  	        		break;

    	  	        	case 2: // General Manager
    	  	        		System.out.println("user is a General Manager");
    	  	        		main.setScene("bigManagerOptions.fxml",userParams);
    	  	        		break;

    	  	        	case 3: // costumer service worker
    	  	        		main.setScene("ServiceEmployeeMain.fxml",userParams);
    	  	        		break;

    	  	        	case 4: // facility worker
    	  	        		main.setScene("FacilityWorkerMain.fxml",userParams);
    	  	        		break;

    	  	        	default:
    	  	        		invalideText.setVisible(true);
    	  	        	}
    	  		      }
	  		    });
    		}


    	});
    }

    @FXML
    void bBackClick(){
    	main.setScene("Welcome.fxml", Params.getEmptyInstance());
    }

    @FXML
    void initialize() {
        assert Screen != null : "fx:id=\"Screen\" was not injected: check your FXML file 'WorkerMainScene.fxml'.";
        assert loginBottun != null : "fx:id=\"loginBottun\" was not injected: check your FXML file 'WorkerMainScene.fxml'.";
        assert userName != null : "fx:id=\"userName\" was not injected: check your FXML file 'WorkerMainScene.fxml'.";
        assert invalideText != null : "fx:id=\"invalideText\" was not injected: check your FXML file 'WorkerMainScene.fxml'.";

    }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}
}