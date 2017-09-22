//Author: Abhinav Bhandaram
//CSE Machine:cse01.cse.unt.edu
//This class consists the methods to generate the syntax tree.

class SyntaxTree {

	private Object node;
	private SyntaxTree left;
	private SyntaxTree right;
	private SyntaxTree center;

	// constructor functions

	public SyntaxTree() {
		this(null, null, null);
	}

	public SyntaxTree(Object nodeValue) {
		this(nodeValue, null, null);
	}

	public SyntaxTree(Object nodeValue, SyntaxTree leftTree) {
		this(nodeValue, leftTree, null);
	}

	public SyntaxTree(Object nodeValue, SyntaxTree leftTree, SyntaxTree rightTree) {
		node = nodeValue;
		left = leftTree;
		right = rightTree;
	}
	public SyntaxTree(Object nodeValue, SyntaxTree leftTree,SyntaxTree middleTree, SyntaxTree rightTree) {
		node = nodeValue;
		left = leftTree;
		right = rightTree;
		center=middleTree;
	}

	// selector functions

	public String root() {
		return node.toString();
	}

	public SyntaxTree left() {
		return left;
	}

	public SyntaxTree right() {
		return right;
	}
	public SyntaxTree center(){
		return center;
	}
	public int constValue() {
		return ((Integer) left().node).intValue();
	}

	// public Location varLoc () { return (Location) left () . left () . node; }
	public SyntaxTree procBody() {
		return left().left().left();
	}

	// print prints the tree in Cambridge Polish prefix notation with a heading.

	public void print(String block_name) {
		System.out.println("");
		System.out.println("Syntax Tree for " + block_name);
		System.out.print("----------------");
		for (int i = 0; i < block_name.length(); i++)
			System.out.print("-");
		System.out.println("");
		System.out.println(this);
	}

	// toString returns the tree in Cambridge Polish prefix notation.

	public String toString() {
		if (root().equals("int"))
			if (left().left() == null) // integer constant
				return left().root();
			else // constant identifier
				return left().left().root();
		if (left == null)
			return root();
		else if(root().equals(",")&&right==null){
			return "["+left+"]";
		}
		else if (right == null)
			return "(" + root() + " " + left + ")";
		else if (center != null)
			return "(" + root() + " " + left + " " + center + " " + right + ")";
		else if(root().equals(",")){
			return "["+left+root()+" "+right+"]";
		}
		else if(root().startsWith("apply")){
			return "("+ root()+left+ ")";
		}
		else
			return "(" + root() + " " + left + " " + right + ")";
	}

}