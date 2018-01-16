package application;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import common.Client;
import common.ControllerIF;
import common.Params;
import common.TalkToServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class ApplicationMain extends Application {
	public Stage primaryStage;
	final int DEFAULT_PORT = 55560;//6654;//55560;
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
		List<String> args = getParameters().getRaw();
		String host = "localhost";//"192.168.43.39";
		int port = DEFAULT_PORT;
		if(args.size() >= 2) {
			host = args.get(0);
			port = Integer.parseInt(args.get(1));
		}
		TalkToServer.getInstance(host, port);

		this.setScene("Welcome.fxml", Params.getEmptyInstance());


	}

	public void setScene(String urlStr, Params params){
		// constructing our scene
		try {

			URL url = getClass().getResource(urlStr);


			FXMLLoader fxmlLoader = new FXMLLoader(url);

			AnchorPane pane = (AnchorPane)fxmlLoader.load();
			ControllerIF controller = fxmlLoader.<ControllerIF>getController();



			List<String> args = getParameters().getRaw();
//			String host = "localhost";
//			int port = 5555;
//			if(args.size() >= 2) {
//				host = args.get(0);
//				port = Integer.parseInt(args.get(1));
//			}
			controller.init(this, params);

			//AnchorPane pane = FXMLLoader.load( url );
			Scene scene = new Scene( pane );

			// setting the stage
			primaryStage.setScene( scene );
			primaryStage.setTitle( "Main view" );
			primaryStage.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		launch(args);
	}



}
