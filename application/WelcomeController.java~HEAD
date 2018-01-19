package application;

import java.io.IOException;

import common.Client;
import common.ControllerIF;
import common.Params;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class WelcomeController implements ControllerIF{

    @FXML
    private Button bClient;

    @FXML
    private Button bEmployee;

    private ApplicationMain main;
    private Params params;

    @FXML
    void bClientClick(ActionEvent event) {
    	System.out.println("bClientClick" + main);
    	System.out.println(this.main);
    	main.setScene("ClientEnterView.fxml", params);
    }

    @FXML
    void bEmployeeClick(ActionEvent event) {
    	System.out.println("bEmployeeClick");
    }




	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}

}
