package application;

import java.text.DecimalFormat;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientPhysicalLeaveController implements ControllerIF{

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfParkingLot;

    @FXML
    private Button bSubmit;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bBackClick(){
    	main.setScene("ClientPhysicalView.fxml", Params.getEmptyInstance());
    }

    @FXML
    void bSubmitClick(ActionEvent event) {
       	Params orderParams = Params.getEmptyInstance();
    	orderParams.addParam("action", "clientLeave");
    	orderParams.addParam("vehicleID", tfID.getText());
    	orderParams.addParam("parkingLot", tfParkingLot.getText());
    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), msg -> {
    		System.out.println("ClientPhysicalEnterController got msg from server:" + msg);

    		Params respParams = new Params(msg);

    		if(respParams.getParam("status").equals("OK")){
    			Platform.runLater(new Runnable() {
    	  		      @Override public void run() {
    	  	    		 final Stage dialog = new Stage();

    	  	             dialog.initModality(Modality.APPLICATION_MODAL);
    	  	             dialog.initOwner(main.primaryStage);
    	  	             VBox dialogVbox = new VBox(20);
    	  	             double payAmount = Double.valueOf(respParams.getParam("payAmount"));
    	  	             payAmount = Math.abs(payAmount);
    	  	             String amountNiceStr = "";
    	  	             try{
    	  	              amountNiceStr = new DecimalFormat("#.##").format(Double.valueOf(payAmount));
    	  	             }catch(Exception e){
    	  	            	 e.printStackTrace();
    	  	            	 amountNiceStr = respParams.getParam("payAmount");
    	  	             }
    	  	             dialogVbox.getChildren().add(new Text("Thanks for using CPS, you bill is: " + amountNiceStr));
    	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
    	  	             dialog.setScene(dialogScene);
    	  	             dialog.show();
    	  	             PayDialog.show(main.primaryStage, payAmount);
    	  	             System.out.println("showed dialog");
    	  		      }
	  		    });
    		}else{
    			System.out.println("server returned BAD status");
    			Platform.runLater(new Runnable() {
  	  		      @Override public void run() {
  	  	    		 final Stage dialog = new Stage();
  	  	             dialog.initModality(Modality.APPLICATION_MODAL);
  	  	             dialog.initOwner(main.primaryStage);
  	  	             VBox dialogVbox = new VBox(20);
  	  	             dialogVbox.getChildren().add(new Text("Sorry, your request could not be granted"));
  	  	             if(respParams.hasParam("message")) {
  	  	  	             dialogVbox.getChildren().add(new Text("Reason:" + respParams.getParam("message")));
  	  	             }
  	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
  	  	             dialog.setScene(dialogScene);
  	  	             dialog.show();
  	  	             System.out.println("showed dialog");
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
