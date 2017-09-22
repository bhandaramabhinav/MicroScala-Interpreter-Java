//Author: Abhinav Bhandaram
//CSE Machine:cse01.cse.unt.edu
//The class is the main script to 
//Run the Interpreter for Micro Scala.

class MicroScalaSyn {

	public static void main(String args[]) throws java.io.IOException {
		// Taking the file name, to be parsed as an input from
		// command line arguments
		// String fileName=args[0];
		String fileName = args[0];
		System.out.println("Source Program");
		System.out.println("--------------");
		System.out.println("");
		
		Environment globalEnv=new Environment();
		Parser MicroScala = new Parser(fileName);
		// Calling the main execution unit of the analyzer.
		MicroScala.CompilationUnit();
		Environment env=MicroScala.GlobalEnv();
		System.out.println("");
		System.out.println("");
		System.out.println("The output of the Interpreter is: ");
		MicroScalaInterpreter interpreter=new MicroScalaInterpreter(env, MicroScala.ProgramSyntaxTree());
		System.out.println("");
		System.out.println("");
		// Message printed if the program is parsed successfuly.
		System.out.println("PARSE SUCCESSFUL");
		System.out.println("");
		System.out.println("");
		env.print("Global Environment");


	}

}