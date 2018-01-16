package application;

import java.net.URL;
import java.util.ResourceBundle;

import application_itamar.ApplicationMain;
import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class FMController implements ControllerIF{
	
    private ApplicationMain main;
    private Params params;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField newRate;

    @FXML
    private Button rateRequestButton;

    @FXML
    private Text grantedText;
    
    @FXML
    private Text deniedText;
    
    @FXML
    private ComboBox<String> dropDown;

    @FXML
    void newRateRequest(ActionEvent event) 
    {
    	System.out.println("Pressed request change rate");

    	Params loginParams = Params.getEmptyInstance();
    	loginParams.addParam("action", "requestChangePrice");
    	loginParams.addParam("price", newRate.getText());
    	loginParams.addParam("facID", params.getParam("facID"));
    	String type = "";
    	if(dropDown.getValue().equals("One Time Parking"))
    		type = "0";
    	if(dropDown.getValue().equals("Ordered One Time Parking"))
    		type = "1";
    	if(dropDown.getValue().equals("Routine Subscription - One Vehicle"))
    		type = "2";
    	if(dropDown.getValue().equals("Full Subscription"))
    		type = "3";
    	if(dropDown.getValue().equals("Routine Subscription"))
    		type = "4";
    	
    	loginParams.addParam("type", type);
    	
    	// send this JSON to server
    	TalkToServer.getInstance().send(loginParams.toString(), msg -> 
    	{
    		Params res = new Params(msg);
    		
    		if(true || res.getParam("status").equals("OK")){ //TODO: remove true ||, this is for dbg
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		
    	  	    		// code here 
    	  	    		int requestAnswer = Integer.parseInt(res.getParam("res"));
    	  	    		if(requestAnswer == 0) {
    	  	    			System.out.println("Something went wrong");
    	  	    		}
    	  	    		if(requestAnswer == 1) {
    	  	    			System.out.println("Request Sent!");
    	  	    		}
    	  	    			
    	  		      }
    	  	    });
    		}
    		
    	});
    }

    @FXML
    void initialize() {
    	assert deniedText != null : "fx:id=\"deniedText\" was not injected: check your FXML file 'SniffManager.fxml'.";
        assert dropDown != null : "fx:id=\"dropDown\" was not injected: check your FXML file 'SniffManager.fxml'.";
        assert grantedText != null : "fx:id=\"grantedText\" was not injected: check your FXML file 'SniffManager.fxml'.";
        assert newRate != null : "fx:id=\"newRate\" was not injected: check your FXML file 'SniffManager.fxml'.";
        assert rateRequestButton != null : "fx:id=\"rateRequestButton\" was not injected: check your FXML file 'SniffManager.fxml'.";
        dropDown.getItems().clear();
    	dropDown.getItems().addAll("One Time Parking","Ordered One Time Parking","Routine Subscription - One Vehicle","Full Subscription","Routine Subscription");
    }
    
	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
