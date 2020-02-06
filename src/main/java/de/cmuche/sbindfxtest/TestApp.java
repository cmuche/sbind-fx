package de.cmuche.sbindfxtest;

public class TestApp
{
  public static void main(String[] args) throws IllegalAccessException
  {
    TestController controller = new TestController();
    controller.initialize();
    System.out.println(controller.getDataValue("foo.bar.strField"));
  }
}
