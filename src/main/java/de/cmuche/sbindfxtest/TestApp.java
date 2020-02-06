package de.cmuche.sbindfxtest;

import javafx.application.Application;
import javafx.stage.Stage;

public class TestApp extends Application
{
  public static void main(String[] args)
  {
    TestController controller = new TestController();
    controller.initialize();
    System.out.println(controller.getDataValue("foo.bar.strField"));
    controller.changed();
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {

  }
}
