//Author: Abhinav Bhandaram
//CSE Machine:cse01.cse.unt.edu
//The class defines the actions that need to be taken 
//when different Tokens are matched/.
public class Token {

  private TokenClass symbol;	// current token
  private String lexeme;	// lexeme

  public Token () { }

  public Token (TokenClass symbol) {
    this (symbol, null);
  }

  public Token (TokenClass symbol, String lexeme) {
    this . symbol = symbol;
    this . lexeme  = lexeme;
  }

  public TokenClass symbol () { return symbol; }

  public String lexeme () { return lexeme; }

  public String toString () {
    switch (symbol) {
      case OBJECT :     return "(keyword, object) ";
      case DEF :      return "(keyword, def) ";
      case MAIN :     return "(keyword, main) ";
      case ARGS :        return "(keyword, args) ";
      case RETURN :       return "(keyword, return) ";
      case IF :        return "(keyword, if) ";
      case ELSE :       return "(keyword, else) ";
      case ARRAY :      return "(keyword, Array) ";
      case STRING :      return "(keyword, String) ";
      case VAR :       return "(keyword, var) ";
      case WHILE :     return "(keyword, while) ";
      case INT:		 return "(keyword, Int)";
      case LIST:		 return "(keyword, List)";
      case PRINTLN:	 return "(keyword, println)";
      case HEAD:		 return "(keyword, head)";
      case TAIL:		 return "(keyword, tail)";
      case ISEMPTY:	 return "(keyword, isEmpty)";
      case NIL:		 return "(keyword, Nil)";

	 case ASSIGN :    return "(operator, = (Assign)) ";
      case PLUS :      return "(operator, + (Plus)) ";
      case MINUS :     return "(operator, - (Minus)) ";
      case TIMES :     return "(operator, * (Star)) ";
      case SLASH :     return "(operator, / (Slash)) ";
      case EQ :        return "(operator, == (Equal)) ";
      case LT :        return "(operator, < (Less Than)) ";
      case GT :        return "(operator, > (Greater Than)) ";
      case NE :        return "(operator, != (Not Equal)) ";
      case LE :        return "(operator, <= (Less Than or Equal To)) ";
      case GE :        return "(operator, >= (Greater Than or Equal To)) ";
      case AND :     return "(operator, && (And)) ";
      case OR :     return "(punctuation, || (Or)) ";
      case NOT :     return "(punctuation, ! (Not)) ";
      case LPAREN :    return "(operator, ( (Left Paranthesis)) ";
      case RPAREN :    return "(operator, ) (Right Paranthesis)) ";
	 case LSQUARE :    return "(operator, [ (Left Square Bracket)) ";
      case RSQUARE :    return "(operator, ] (Right Square Bracket)) ";
	case LFLOWER :    return "(operator, { (Left Curly Bracket)) ";
      case RFLOWER :    return "(operator, } (Right Curly Bracket)) ";

      case COMMA :     return "(punctuation, ,) ";
      case COLON :     return "(punctuation, :) ";
      case LISTOPR :     return "(operator, ::) ";
      case PERIOD :    return "(punctuation, .) ";
      case SEMICOLON : return "(punctuation, ;) ";
      case ID :        return "(identifier, " + lexeme + ") ";
      case INTEGER :   return "(integer, " + lexeme + ") ";
      case COMMENT :   return "(comment, " + lexeme + ") ";
      default : 
	ErrorMessage . print (0, "Unrecognized token");
        return null;
    }
  }

}
