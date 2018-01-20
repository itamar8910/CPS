package application;

import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientContactController implements ControllerIF{

    @FXML
    private TextArea taComplaint;

    @FXML
    private TextField tfID;
    
    @FXML
    private TextField tfParkingName;

    @FXML
    private Button bSubmit;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bBackClick(){
    	main.setScene("ClientOnlineView.fxml", Params.getEmptyInstance());
    }
    
    @FXML
    void bSubmitClick(ActionEvent event) {
    	Params orderParams = Params.getEmptyInstance();

    	orderParams.addParam("action", "clientContact");
    	orderParams.addParam("ID", tfID.getText());
    	orderParams.addParam("text", taComplaint.getText());
    	orderParams.addParam("facName", tfParkingName.getText());

    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), msg -> {
    		Params respParams = new Params(msg);
    		if(respParams.getParam("status").equals("OK")){
    			Platform.runLater(new Runnable() {
		  		      @Override public void run() {
		  	    		 final Stage dialog = new Stage();

		  	             dialog.initModality(Modality.APPLICATION_MODAL);
		  	             dialog.initOwner(main.primaryStage);
		  	             VBox dialogVbox = new VBox(20);
		  	             dialogVbox.getChildren().add(new Text("Thanks for contancting us, we'll respond to you within the next 24  hours"));
		  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
		  	             dialog.setScene(dialogScene);
		  	             dialog.show();
		  	             System.out.println("showed dialog");
		  		      }
			    });
    		}else {
    				//respParams
    			Platform.runLater(new Runnable() {
		  		      @Override public void run() {
		  	    		 final Stage dialog = new Stage();

		  	             dialog.initModality(Modality.APPLICATION_MODAL);
		  	             dialog.initOwner(main.primaryStage);
		  	             VBox dialogVbox = new VBox(20);
		  	             dialogVbox.getChildren().add(new Text("Your contact request is invalid"));
		  	             if(respParams.hasParam("message")) {
		  	            	 dialogVbox.getChildren().add(new Text("message:" + respParams.getParam("message")));
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
