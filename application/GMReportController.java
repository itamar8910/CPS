package application;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class GMReportController implements ControllerIF{
	
    private ApplicationMain main;
    private Params params;
    private JSONArray curList;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<String> parkingList;

    @FXML
    private Text report;

    @FXML
    private Button requestButton;


    @FXML
    void requestPressed(ActionEvent event) throws JSONException {
    	if(parkingList.getSelectionModel().getSelectedIndex() == -1)
    		return;
    	
    	System.out.println("request clicked");
        
        String facID = "";
        for (int i = 0; i < curList.length(); i++) {
  			if(parkingList.getSelectionModel().getSelectedIndex() == i) {
  				facID = curList.getJSONObject(i).getString("facID");
  				break;
  			}
  		}
        
        // send request to server for pdf creator
        Params serverRequest = Params.getEmptyInstance();
    	serverRequest.addParam("action", "requestCurrentParkingStatusReport");
    	serverRequest.addParam("facID", facID);
    	
    	final Params temp = Params.getEmptyInstance();

    	// send this JSON to server
    	TalkToServer.getInstance().send(serverRequest.toString(), msg -> {
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){ 
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		temp.addParam("data", res.getParam("data").toString());
    	  	    		temp.addParam("name", res.getParam("name").toString());
    	  	    		temp.addParam("dimension", res.getParam("dimension").toString());

    	  	    		PDFstatus pdf = new PDFstatus();
    	  	 	        pdf.createCurrentStatusPDF("currentStatus.pdf",temp.getParam("name").toString() ,Integer.parseInt(temp.getParam("dimension").toString()), temp.getParam("data").toString());
    	  	    		
    	  		      }});}});
    }

    @FXML
    void initialize() {
        assert parkingList != null : "fx:id=\"parkingList\" was not injected: check your FXML file 'requestReport.fxml'.";
        assert report != null : "fx:id=\"report\" was not injected: check your FXML file 'requestReport.fxml'.";
        assert requestButton != null : "fx:id=\"requestButton\" was not injected: check your FXML file 'requestReport.fxml'.";
        showList();
    }
    
    public void showList() {
    	System.out.println("Loading List");

    	Params refRequest = Params.getEmptyInstance();
    	refRequest.addParam("action", "requestParkings");
    	
    	if(parkingList.getItems()!=null)
    		parkingList.getItems().clear();
    	
    	// send this JSON to server
    	TalkToServer.getInstance().send(refRequest.toString(), msg -> 
    	{
    		Params res = new Params(msg);

    		if(true || res.getParam("status").equals("OK")){ 
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		final Stage dialog = new Stage();
    	  	    		// code here 
    	  	    		try {
							curList = new JSONArray(res.getParam("array"));
	    	  	    		for (int i = 0; i < curList.length(); i++) {
	    	  	    			parkingList.getItems().add(curList.getJSONObject(i).get("name").toString());
	    	  	    		}
	    	  	    		
						} catch (JSONException e) {
							e.printStackTrace();
						}
    	  	    		
    	  	      	System.out.println("List Loaded");

    	  		      }
    	  	    });
    		}
    		
    	});

    }
      
	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
