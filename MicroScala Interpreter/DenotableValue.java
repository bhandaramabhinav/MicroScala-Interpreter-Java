//Author: Abhinav Bhandaram
//CSE Machine:cse01.cse.unt.edu
//This class holds the denotable values and related information 


public class DenotableValue {

  private int category;
  private Object value;

  public DenotableValue (int category, Object value) {
    this . category = category;
    this . value = value;
  }

  public int category () { return category; }

  public Object value () { return value; }

  public String toStringDenote () {
    String printString = Category . toString (category);
    if (category == Category . CONSTANT || category == Category . VARIABLE || category==Category.INT||category==Category.LIST)
      printString = printString + "(" + value + ")";
    return printString;
  }

}