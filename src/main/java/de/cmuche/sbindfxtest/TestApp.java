package de.cmuche.sbindfxtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestApp extends Application
{
  public static void main(String[] args)
  {
    /*TestController controller = new TestController();
    controller.initialize();
    System.out.println(controller.getDataValue("foo.bar.strField"));
    controller.changed();*/
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fx/TestView.fxml"));
    Parent root = loader.load();

    Stage stage = new Stage();
    stage.setTitle("sBind FX");
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
  }
}
