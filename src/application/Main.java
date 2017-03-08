package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.opencv.core.Core;

public class Main extends Application
{
	public static Stage cpy;
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("UI.fxml"));BorderPane rootElement = (BorderPane) loader.load();Scene scene = new Scene(rootElement, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());primaryStage.setTitle("Histogram Normalizer");
			primaryStage.setScene(scene);
			primaryStage.show();
			cpy=new Stage();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
}