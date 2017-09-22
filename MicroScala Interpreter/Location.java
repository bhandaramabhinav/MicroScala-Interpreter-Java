// Location.java

// Location is a class to represent the location of variables in PL/0 programs.

public class Location {

  private int stackFrame;
  private int offset;
  private static int mainProgSize = 0;
  private static int maxStackFrame = 0;

  public Location () { this (0, 0); }

  public Location (int stackFrame, int offset) {
    this . stackFrame = stackFrame;
    this . offset     = offset;
  }

  public int stackFrame () { return stackFrame; }

  public int offset () { return offset; }

  public static int mainProgSize () { return mainProgSize; }

  public static int stackFrameSize () { return maxStackFrame + 1; }

  public Location nextLocation () { 
    Location nextLoc =  new Location (stackFrame, offset); 
    offset++;
    if (stackFrame == 0)
      mainProgSize = offset;
    return nextLoc;
  }

  public Location nextStackFrame () { 
    stackFrame = stackFrame + 1;
    if (stackFrame > maxStackFrame)
      maxStackFrame = stackFrame;
    return new Location (stackFrame, 0);
  }

  public String toString () {
    return stackFrame + ", " + offset;
  }

}