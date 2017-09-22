
public class MicroScalaPars {
	public static void main (String args []) throws java.io.IOException {
		String fileName="test6.scala";
		System . out . println ("Source Program");
	    System . out . println ("--------------");
	    System . out . println ("");
	    Parser pl0 = new Parser (fileName);	    
	    pl0 . CompilationUnit();
	    SyntaxTree syntaxTree=new SyntaxTree();
	    syntaxTree=pl0.ProgramSyntaxTree();
	    System . out . println ("");
	    syntaxTree . print ("main program");	    
	  }
}
