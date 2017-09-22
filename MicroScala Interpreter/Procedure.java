import java.util.HashMap;
import java.util.LinkedHashMap;

// Procedure.java

// Procedure is a class to represent the components of a procedure.

class Procedure {

	private Environment env;
	private SyntaxTree syntaxTree;
	private LinkedHashMap<String,DenotableValue> function_parameters;
	private int ReturnType;

	public Procedure(Environment env,LinkedHashMap<String,DenotableValue> func_params,int ReturnType, SyntaxTree syntaxTree) {
		this.env = env;
		this.syntaxTree = syntaxTree;
		this.function_parameters = func_params;
		this.ReturnType=ReturnType;
	}

	public Environment env() {
		return env;
	}
	
	public int ReturnType(){
		return ReturnType;
	}
	public LinkedHashMap<String,DenotableValue> function_parameters(){
		return function_parameters;
	}
	public SyntaxTree syntaxTree() {
		return syntaxTree;
	}

	public void print(String procedureName) {
		syntaxTree.print(procedureName);
		env.print(procedureName);
		}

}