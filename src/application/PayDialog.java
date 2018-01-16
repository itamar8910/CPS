package application;

import common.Params;
import common.TalkToServer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PayDialog {

	public static void show(Stage stage, double payAmount) {
		Platform.runLater(new Runnable() {
		      @Override public void run() {
	    		 final Stage dialog = new Stage();
	             dialog.initModality(Modality.APPLICATION_MODAL);
	             dialog.initOwner(stage);
	             VBox dialogVbox = new VBox(20);
	             if(payAmount != 0) {
	            	 dialogVbox.getChildren().add(new Text("Please pay:" + payAmount));	            	 
	             }
	             TextField tfCreditCard = new TextField("Credit card #");
	             TextField tfUserID = new TextField("User ID");
	             TextField tfAmount = new TextField("Amount");

	             Button b = new Button("Pay");
	             dialogVbox.getChildren().add(tfUserID);
	             dialogVbox.getChildren().add(tfAmount);
	             dialogVbox.getChildren().add(tfCreditCard);
	             dialogVbox.getChildren().add(b);
	             b.setOnAction(e->{
	            	 Params params = Params.getEmptyInstance();
	            	 params.addParam("action", "pay");
	            	 params.addParam("amount", String.valueOf(tfAmount.getText()));
	            	 params.addParam("userID", tfUserID.getText());
	            	 TalkToServer.getInstance().send(params.toString(), msg -> {
	            		Params resp = new Params(msg);
	            			Platform.runLater(new Runnable() {
	          	  		      @Override public void run() {
	          	  	    		 final Stage dialog = new Stage();
	          	  	             dialog.initModality(Modality.APPLICATION_MODAL);
	          	  	             dialog.initOwner(stage);
	          	  	             VBox dialogVbox = new VBox(20);
	          	  	             dialogVbox.getChildren().add(new Text(resp.getParam("status").equals("OK") ? "Your payment was received" : "Your payment was declined"));
	          	  	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	          	  	             dialog.setScene(dialogScene);
	          	  	             dialog.show();
	          	  	             System.out.println("showed dialog");
	          	  		      }
	      	  		    	});
	            		
	            	 });
	             });
	             Scene dialogScene = new Scene(dialogVbox, 300, 200);
	             dialog.setScene(dialogScene);
	             dialog.show();
	             System.out.println("showed dialog");
		      }
	    	});
	}

}
