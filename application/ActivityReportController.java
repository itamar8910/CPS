package application;

import org.json.JSONArray;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import common.ControllerIF;
import common.Params;;

public class ActivityReportController implements ControllerIF{
	
    private ApplicationMain main;
    private Params params;
    private JSONArray curList;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;


    @FXML
    void initialize() {


    }
    
    
	@Override
	public void init(ApplicationMain main, Params params) {
		this.main = main;
		this.params = params;
	}







}

