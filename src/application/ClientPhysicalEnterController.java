package application;

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

public class ClientPhysicalEnterController implements ControllerIF{

    @FXML
    private TextField tfID;

    @FXML
    private Button bSubmit;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bSubmitClick(ActionEvent event) {
       	Params orderParams = Params.getEmptyInstance();
    	orderParams.addParam("action", "clientEnter");
    	final String ID = tfID.getText();
    	orderParams.addParam("ID", tfID.getText());

    	System.out.println("sending request to server");
    	TalkToServer.getInstance().send(orderParams.toString(), msg -> {
    		System.out.println("ClientPhysicalEnterController got msg from server:" + msg);

    		Params respParams = new Params(msg);

    		if(respParams.getParam("status").equals("OK")){
    			if(respParams.getParam("needsSubscriptionID").equals("Yes")){
    				Platform.runLater(new Runnable() {
        	  		      @Override public void run() {
        	  	    		 final Stage dialog = new Stage();
        	  	             dialog.initModality(Modality.APPLICATION_MODAL);
        	  	             dialog.initOwner(main.primaryStage);
        	  	             VBox dialogVbox = new VBox(20);
        	  	             dialogVbox.getChildren().add(new Text("Please enter your subscription ID"));
        	  	             final TextField tfSubID = new TextField("Please enter your subscription ID");
        	  	             dialogVbox.getChildren().add(tfSubID);
        	  	             final Button bSubmit = new Button("Submit");
        	  	             bSubmit.setOnAction(event->{
        	  	            	 String subscriptionID = tfSubID.getText();
        	  	            	 System.out.println("got subscription id:" + subscriptionID);
        	  	              	 Params orderParams = Params.getEmptyInstance();
        	  	            	 orderParams.addParam("action", "clientEnterWithSubscriptionID");
        	  	            	 orderParams.addParam("ID", ID);
        	  	            	 orderParams.addParam("subscriptionID", subscriptionID);
        	  	            	 TalkToServer.getInstance().send(orderParams.toString(), msg ->{
        	  	            		 System.out.println("ClientPhysicalEnterController with subscriptionID got msg from server:" + msg);
        	  	            		 Params respParams = new Params(msg);
        	  	            		if(respParams.getParam("status").equals("OK")){
        	  	            			Platform.runLater(new Runnable() {
        	  	          	  		      @Override public void run() {
        	  	          	  	    		 final Stage dialog = new Stage();
        	  	          	  	             dialog.initModality(Modality.APPLICATION_MODAL);
        	  	          	  	             dialog.initOwner(main.primaryStage);
        	  	          	  	             VBox dialogVbox = new VBox(20);
        	  	          	  	             dialogVbox.getChildren().add(new Text("Access granted, Please exit your vehicle"));
        	  	          	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
        	  	          	  	             dialog.setScene(dialogScene);
        	  	          	  	             dialog.show();
        	  	          	  	             System.out.println("showed dialog");
        	  	          	  		      }
        	  	      	  		    	});
        	  	            		}else{

        	  	            		}

        	  	            	 });
        	  	             });
        	  	           dialogVbox.getChildren().add(bSubmit);
        	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
        	  	             dialog.setScene(dialogScene);
        	  	             dialog.show();
        	  	             System.out.println("showed dialog");
        	  		      }
    	  		    	});
    			}else{
    				Platform.runLater(new Runnable() {
      	  		      @Override public void run() {
      	  	    		 final Stage dialog = new Stage();
      	  	             dialog.initModality(Modality.APPLICATION_MODAL);
      	  	             dialog.initOwner(main.primaryStage);
      	  	             VBox dialogVbox = new VBox(20);
      	  	             dialogVbox.getChildren().add(new Text("Access granted, Please exit your vehicle"));
      	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
      	  	             dialog.setScene(dialogScene);
      	  	             dialog.show();
      	  	             System.out.println("showed dialog");
      	  		      }
  	  		    	});
    			}

    		}else{
    			System.out.println("server returned BAD status");
    		}
    	});
    }

	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
