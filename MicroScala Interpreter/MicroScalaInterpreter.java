//Author: Abhinav Bhandaram
//CSE Machine:cse01.cse.unt.edu
//This class is the main interpreter class, it parses the program and executes the output

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MicroScalaInterpreter {
	// public Store store;
	private SyntaxTree mainTree;
	private Environment globalEnv;

	public MicroScalaInterpreter(Environment globalEnv, SyntaxTree mainTree) {
		this.globalEnv = globalEnv;
		this.mainTree = mainTree;
		this.main();
	}

	public void main() {
		Procedure mainProc = (Procedure) globalEnv.accessEnv("main").value();
		Environment env = mainProc.env();
		statement(mainTree, env);
	}

	public void statement(SyntaxTree mainTree, Environment functionEnv) {
		String root = mainTree.root().toString();
		if (root.equals(":")) {
			statement(mainTree.left(), functionEnv);
			statement(mainTree.right(), functionEnv);
		} else if (root.equals("=")) {
			DenotableValue var = (DenotableValue) functionEnv.accessEnv(mainTree.left().left().toString(), globalEnv);
			functionEnv.updateEnvVarVal(mainTree.left().left().toString(),
					new DenotableValue(var.category(), expr(mainTree.right(), functionEnv)), globalEnv);

		} else if (root.equals("println")) {
			System.out.println(expr(mainTree.left(), functionEnv));
		} else if (root.equals("if")) {
			
			if (mainTree.center() == null) {				
				if (condition(mainTree.left(), functionEnv)) {
					statement(mainTree.right(), functionEnv);
				}

			} else {
				if (condition(mainTree.left(), functionEnv)) {

					statement(mainTree.center(), functionEnv);
				} else {
					statement(mainTree.right(), functionEnv);

				}
			}

		} else if (root.equals("else")) {
			statement(mainTree.right(), functionEnv);
		} else if (root.equals("while")) {
			if (condition(mainTree.left(), functionEnv)) {
				statement(mainTree.right(), functionEnv);
				// globalEnv.print("ab");
				statement(mainTree, functionEnv);
			}
		} else if (root.equals("return")) {
			Object val = expr(mainTree.left(), functionEnv);
			int type = functionEnv.accessEnv("return").category();
			functionEnv.updateEnvVarVal("return", new DenotableValue(type, val), globalEnv);
		}
	}

	public void funcActuals(SyntaxTree st, Environment env, List list) {
		if (st != null) {
			// System.out.println(st.toString());
			if (st.root() != ",") {
				list.add(expr(st, env));
			} else if (st.root().equals(",")) {
				funcActuals(st.left(), env, list);
				funcActuals(st.right(), env, list);

			}
		}
	}

	public boolean condition(SyntaxTree tree, Environment functionEnv) {
		try{
		if (tree.root().equals("==")) {
			return (Integer) expr(tree.left(), functionEnv) == (Integer) expr(tree.right(), functionEnv);
		}
		// return true;
		else if (tree.root().equals("<"))
			return (Integer) expr(tree.left(), functionEnv) < (Integer) expr(tree.right(), functionEnv);
		else if (tree.root().equals(">"))
			return (Integer) expr(tree.left(), functionEnv) > (Integer) expr(tree.right(), functionEnv);
		else if (tree.root().equals("!="))
			return (Integer) expr(tree.left(), functionEnv) != (Integer) expr(tree.right(), functionEnv);
		else if (tree.root().equals("<="))
			return (Integer) expr(tree.left(), functionEnv) <= (Integer) expr(tree.right(), functionEnv);
		else if (tree.root().equals(">="))
			return (Integer) expr(tree.left(), functionEnv) >= (Integer) expr(tree.right(), functionEnv);
		else if (tree.root().equals("!"))
			return !((Boolean) expr(tree.left(), functionEnv));
		else if (tree.root().equals("&&")) {
			return ((Boolean) condition(tree.left(), functionEnv) && (Boolean) condition(tree.right(), functionEnv));
		} else if (tree.root().equals("||")) {
			return ((Boolean) (condition(tree.left(), functionEnv)) && (Boolean) condition(tree.right(), functionEnv));
		} else if(tree.left().root()=="id"){
			return (Boolean)expr(tree,functionEnv);
		}
		else // syntaxTree . root () . equals ("odd")
			return (Integer) expr(tree.left(), functionEnv) % 2 == 1;}catch(Exception ex){
				ErrorMessage.print("Type Mismatch Error");
				return false;
			}
	}

	public Object expr(SyntaxTree syntaxTree, Environment localEnv) {
		try{
		if (syntaxTree.root().equals("+"))
			if (syntaxTree.right() == null)
				return expr(syntaxTree.left(), localEnv);
			else
				return (Integer) expr(syntaxTree.left(), localEnv) + (Integer) expr(syntaxTree.right(), localEnv);
		else if (syntaxTree.root() == "id") {
			DenotableValue value = localEnv.accessEnv(syntaxTree.left().root(), globalEnv);
			// System.out.println(value.value().toString());
			if (value.category() == 9) {
				return (Integer.parseInt(value.value().toString()));
			} else {
				return (value.value());
			}
		} else if (syntaxTree.root().equals("::"))

		{
			Integer leftValue = (Integer) expr(syntaxTree.left(), localEnv);
			List rightValue = (List) expr(syntaxTree.right(), localEnv);
			rightValue.add(0, leftValue);
			return rightValue;
		} else if (syntaxTree.root().equals("head"))

		{
			List leftVal = (List) expr(syntaxTree.left(), localEnv);
			if (leftVal.isEmpty()) {
				ErrorMessage.print("list empty");
				return null;
			} else
				return leftVal.get(0);
		} else if (syntaxTree.root() == "!") {
			return !(Boolean) expr(syntaxTree.left(), localEnv);
		} else if (syntaxTree.root().equals("tail"))

		{
			List leftVal = (List) expr(syntaxTree.left(), localEnv);
			if (!(leftVal.size() > 1)) {
				ErrorMessage.print("error");
				return null;
			} else
				return leftVal.subList(1, leftVal.size() - 1);
		} else if (syntaxTree.root().equals("isempty")) {
			List leftVal = (List) expr(syntaxTree.left(), localEnv);
			if (!(leftVal.size() > 1)) {
				return true;
			} else
				return false;
		} else if (syntaxTree.root().contains("apply"))

		{

			String[] idArray = syntaxTree.root().split(" ");
			String procId = idArray[1];
			Procedure proc = (Procedure) globalEnv.accessEnv(procId).value();
			Environment procMain = proc.env();
			HashMap<String, DenotableValue> paramEnv = proc.function_parameters();
			int returnType = proc.ReturnType();
			// String parameterList=syntaxTree.right().toString();
			List actuals = new ArrayList();
			if (syntaxTree.left().root().equals("id")) {
				actuals.add(expr(syntaxTree.left().left(), localEnv));
			} else {
				funcActuals(syntaxTree.left(), localEnv, actuals);
			}
			// System.out.println(actuals.get(0));
			Environment execFunc = new Environment();
			Iterator iterator = procMain.map().entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, DenotableValue> entry = (Map.Entry<String, DenotableValue>) iterator.next();
				String Id = (String) entry.getKey();
				execFunc.updateEnv(Id, new DenotableValue(entry.getValue().category(), entry.getValue().value()));
			}
			int i = 0;
			Iterator iterator1 = paramEnv.entrySet().iterator();
			while (iterator1.hasNext()) {
				Entry<String, DenotableValue> param = (Entry<String, DenotableValue>) iterator1.next();
				Object val = actuals.get(i);
				i++;
				execFunc.updateEnv(param.getKey(), new DenotableValue(param.getValue().category(), val));
			}
			execFunc.updateEnv("return", new DenotableValue(proc.ReturnType(), null));
			syntaxTree = proc.syntaxTree();
			statement(syntaxTree, execFunc);
			return execFunc.accessEnv("return").value();
		} else if (syntaxTree.root().equals("-"))
			if (syntaxTree.right() == null)
				return expr(syntaxTree.left(), localEnv);
			else
				return (Integer) expr(syntaxTree.left(), localEnv) - (Integer) expr(syntaxTree.right(), localEnv);
		else if (syntaxTree.root().equals("*"))
			return (Integer) (expr(syntaxTree.left(), localEnv)) * (Integer) expr(syntaxTree.right(), localEnv);
		else if (syntaxTree.root().equals("/")) {
			int divisor = (Integer) expr(syntaxTree.right(), localEnv);
			if (divisor == 0) {
				ErrorMessage.print("Division by zero");
				return 0; // will not happen as ErrorMessage . print exits
			} else
				return (Integer) expr(syntaxTree.left(), localEnv) / divisor;
		} else if (syntaxTree.root().equals("id")) {
			// e = store . access (syntaxTree . varLoc ());
			return 0;
		} else if (syntaxTree.root().toString() == "NIL") {
			return new ArrayList<Integer>();
		} else // syntaxTree . root () . equals ("int")
			return Integer.parseInt(syntaxTree.left().toString());
	
	}catch(Exception ex){
		ErrorMessage.print("Type Mismatch Error");
		return false;
	}
	}
}
