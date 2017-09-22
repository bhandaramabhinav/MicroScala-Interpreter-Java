
//Author: Abhinav Bhandaram
//CSE Machine:cse01.cse.unt.edu
// This is the main class used for the syntax analyzing.

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.w3c.dom.stylesheets.LinkStyle;
import org.xml.sax.ext.LexicalHandler;

public class Parser {
	protected MicroScalaLexer lexer;
	protected Token token;
	private SyntaxTree programSyntaxTree;
	private Environment globalEnv = new Environment();

	public Environment GlobalEnv() {
		return this.globalEnv;
	}

	public SyntaxTree ProgramSyntaxTree() {
		return this.programSyntaxTree;
	}

	// Construtor for the analyzer takes a string input specifying the
	// //filename.
	public Parser(String fileName) throws java.io.IOException {
		lexer = new MicroScalaLexer(new FileReader(fileName));
		getToken();
	}

	// Method used to call the get token method of the lexer to
	// get the next legitimate token.
	private void getToken() throws java.io.IOException {
		token = lexer.nextToken();
	}

	// Main entry point function for the syntax analyzer.
	public void CompilationUnit() throws java.io.IOException {
		SyntaxTree MainTree = new SyntaxTree();
		if (token.symbol() != TokenClass.OBJECT) {
			ErrorMessage.print(lexer.position(), "OBJECT EXPECTED");
		} else {
			getToken();
			if (token.symbol() != TokenClass.ID) {
				ErrorMessage.print(lexer.position(), "Identifier EXPECTED");
			} else {
				getToken();
				if (token.symbol() != TokenClass.LFLOWER) {
					ErrorMessage.print(lexer.position(), "'{' EXPECTED");
				} else {
					getToken();
					MainTree = this.HandleDef();
					if (token.symbol() != TokenClass.RFLOWER) {
						ErrorMessage.print(lexer.position(), "'}' EXPECTED");

					}
				}
			}
		}
		this.programSyntaxTree = MainTree;
	}

	// Handling the scenario for multiple function definitions and var
	// definitions.
	public SyntaxTree HandleDef() throws java.io.IOException {
		SyntaxTree tree = null;
		while (token.symbol() == TokenClass.DEF || token.symbol() == TokenClass.VAR) {
			String procID;
			switch (token.symbol()) {
			case DEF:
				getToken();
				if (token.symbol() == TokenClass.ID) {
					procID = token.lexeme();
					this.Def(procID);
				} else if (token.symbol() == TokenClass.MAIN) {
					tree = this.MainDef();
				} else {
					ErrorMessage.print(lexer.position(), "Function definition or Main function definition EXPECTED");
				}
				break;
			case VAR:
				this.VarDef(null, null);
				//this.globalEnv.print("Global Environment");
				break;
			default:
				ErrorMessage.print(lexer.position(),
						"Function definition, Var Definition or Main function definition EXPECTED");
			}
		}
		return tree;
	}

	// Method to handle def non terminal grammar.
	public void Def(String ProcId) throws java.io.IOException {
		Environment procEnv = new Environment();
		LinkedHashMap<String,DenotableValue> paramenv=null;
		String paramId;
		TokenClass paramType;
		SyntaxTree statementTree, statement1Tree;
		this.globalEnv.updateEnvProc(ProcId);
		getToken();
		if (token.symbol() == TokenClass.LPAREN) {
			getToken();
			if (token.symbol() == TokenClass.ID) {
				paramenv=new LinkedHashMap<String,DenotableValue>();
				paramId=token.lexeme();
				getToken();
				if (token.symbol() == TokenClass.COLON) {
					getToken();
					paramType=token.symbol();
					if(paramType.ordinal()==10){
						paramenv.put(paramId, new DenotableValue(paramType.ordinal(), new ArrayList<Integer>()));
					}else{
						paramenv.put(paramId, new DenotableValue(paramType.ordinal(), 0));
					}
					this.Type();
					while (token.symbol() == TokenClass.COMMA) {
						getToken();
						if (token.symbol() == TokenClass.ID) {
							paramId=token.lexeme();
							getToken();
							if (token.symbol() == TokenClass.COLON) {
								getToken();
								paramType=token.symbol();
								if(paramType.ordinal()==10){
									paramenv.put(paramId, new DenotableValue(paramType.ordinal(), new ArrayList<Integer>()));
								}else{
									paramenv.put(paramId, new DenotableValue(paramType.ordinal(), 0));
								}
								this.Type();
							} else {
								ErrorMessage.print(lexer.position(), "':' EXPECTED");
							}
						} else {
							ErrorMessage.print(lexer.position(), "Identifier EXPECTED");
						}
					}
				} else {
					ErrorMessage.print(lexer.position(), "':' EXPECTED");
				}
			}
			if (token.symbol() == TokenClass.RPAREN) {
				getToken();
				if (token.symbol() == TokenClass.COLON) {
					getToken();
					int returnType=token.symbol().ordinal();
					this.Type();
					if (token.symbol() == TokenClass.ASSIGN) {
						getToken();
						if (token.symbol() == TokenClass.LFLOWER) {
							getToken();
							while (token.symbol() == TokenClass.VAR) {
								this.VarDef(ProcId, procEnv);
							}

							statementTree = this.Statement();
							// getToken();
							while (token.symbol() == TokenClass.PRINTLN || token.symbol() == TokenClass.LFLOWER
									|| token.symbol() == TokenClass.IF || token.symbol() == TokenClass.ID
									|| token.symbol() == TokenClass.WHILE) {
								// getToken();
								statement1Tree = this.Statement();
								statementTree = new SyntaxTree(":", statementTree, statement1Tree);

							}

							if (token.symbol() == TokenClass.RETURN) {
								getToken();
								SyntaxTree returnTree = this.ListExpr();
								returnTree = new SyntaxTree("return", returnTree);
								if (token.symbol() == TokenClass.SEMICOLON) {
									statementTree = new SyntaxTree(":", statementTree, returnTree);
									getToken();
									this.globalEnv.updateEnvProc(ProcId, statementTree, procEnv,paramenv,returnType);
									// statementTree.print(ProcId);
								} else {
									ErrorMessage.print(lexer.position(), "';' EXPECTED");
								}
							} else {

								ErrorMessage.print(lexer.position(), "Return statement EXPECTED");
							}
							if (token.symbol() == TokenClass.RFLOWER) {
								getToken();
							} else {
								ErrorMessage.print(lexer.position(), "'}' EXPECTED");
							}
						} else {
							ErrorMessage.print(lexer.position(), "'{' EXPECTED");
						}
					} else {
						ErrorMessage.print(lexer.position(), "'=' EXPECTED");
					}
				} else {
					ErrorMessage.print(lexer.position(), "':' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "Paramter Definition or '(' EXPECTED");
			}
		} else {
			ErrorMessage.print(lexer.position(), "'(' EXPECTED");
		}
	}

	// Function to handle grammar for MainDef
	public SyntaxTree MainDef() throws java.io.IOException {
		SyntaxTree statementTree = null, statement1tree;
		Environment mainEnv = new Environment();
		LinkedHashMap<String,DenotableValue> paramEnv=new LinkedHashMap<String,DenotableValue>();
		getToken();
		if (token.symbol() == TokenClass.LPAREN) {
			getToken();
			if (token.symbol() == TokenClass.ARGS) {
				getToken();
				if (token.symbol() == TokenClass.COLON) {
					getToken();
					if (token.symbol() == TokenClass.ARRAY) {
						getToken();
						if (token.symbol() == TokenClass.LSQUARE) {
							getToken();
							if (token.symbol() == TokenClass.STRING) {
								getToken();
								if (token.symbol() == TokenClass.RSQUARE) {
									getToken();
									if (token.symbol() == TokenClass.RPAREN) {
										getToken();
										// this.Statement();
										if (token.symbol() == TokenClass.LFLOWER) {
											this.globalEnv.updateEnvProc("main");
											getToken();
											paramEnv.put("args", new DenotableValue(10, new ArrayList<String>()));
											while (token.symbol() == TokenClass.VAR) {
												
												this.VarDef("main", mainEnv);
											}
											statementTree = this.Statement();
											// System.out.println(token.symbol());

											while (token.symbol() == TokenClass.PRINTLN
													|| token.symbol() == TokenClass.LFLOWER
													|| token.symbol() == TokenClass.IF
													|| token.symbol() == TokenClass.ID
													|| token.symbol() == TokenClass.WHILE) {
												// getToken();
												statement1tree = this.Statement();
												statementTree = new SyntaxTree(":", statementTree, statement1tree);

											}
											if (token.symbol() == TokenClass.RFLOWER) {
												getToken();
											} else {
												ErrorMessage.print(lexer.position(), "'}' EXPECTED");
											}
										} else {
											ErrorMessage.print(lexer.position(), "'{' EXPECTED");
										}
									} else {
										ErrorMessage.print(lexer.position(), "')' EXPECTED");
									}
								} else {
									ErrorMessage.print(lexer.position(), "']' EXPECTED");
								}
							} else {
								ErrorMessage.print(lexer.position(), "String Keyword EXPECTED");
							}
						} else {
							ErrorMessage.print(lexer.position(), "'[' EXPECTED");
						}
					} else {
						ErrorMessage.print(lexer.position(), "Array keyword EXPECTED");
					}

				} else {
					ErrorMessage.print(lexer.position(), "':' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "args keyword EXPECTED");
			}
		} else {
			ErrorMessage.print(lexer.position(), "'(' EXPECTED");
		}
		this.globalEnv.updateEnvMain("main", statementTree, mainEnv,paramEnv);
		return statementTree;
	}

	// Function to handle grammar for var def
	public Environment VarDef(String ProcId, Environment localEnv) throws java.io.IOException {
		getToken();
		// Environment localEnv=new Environment();
		// getToken();
		if (token.symbol() == TokenClass.ID) {
			String id;
			String value="";
			TokenClass type;
			id = token.lexeme();
			getToken();
			if (token.symbol() == TokenClass.COLON) {
				getToken();
				type = token.symbol();
				this.Type();
				if (token.symbol() == TokenClass.ASSIGN) {
					getToken();
					value = token.lexeme();
					this.Literal();
					if (token.symbol() != TokenClass.SEMICOLON) {
						ErrorMessage.print(lexer.position(), "';' EXPECTED");
					} else {
						if (ProcId == null) {
							if (type.ordinal() == 10) {
								globalEnv.updateEnv(id,new DenotableValue(type.ordinal(), new ArrayList<Integer>()));
							} else {
								globalEnv.updateEnv(id, new DenotableValue(type.ordinal(), value));

							}
						} else {
							if (type.ordinal() == 10) {
								localEnv.updateEnv(id, new DenotableValue(type.ordinal(), new ArrayList<Integer>()));
							} else {
								
								localEnv.updateEnv(id, new DenotableValue(type.ordinal(), value));

							}
						}
						getToken();

					}
				} else {
					ErrorMessage.print(lexer.position(), "'=' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "':' EXPECTED");
			}
		} else {
			ErrorMessage.print(lexer.position(), "Identifier EXPECTED");
		}
		return localEnv;

	}

	// Function to handle Type grammar
	public void Type() throws java.io.IOException {
		if (token.symbol() == TokenClass.INT) {
			getToken();
		} else if (token.symbol() == TokenClass.LIST) {
			getToken();
			if (token.symbol() == TokenClass.LSQUARE) {
				getToken();
				if (token.symbol() == TokenClass.INT) {
					getToken();
					if (token.symbol() == TokenClass.RSQUARE) {
						getToken();
					} else {
						ErrorMessage.print(lexer.position(), "']' EXPECTED");
					}
				} else {
					ErrorMessage.print(lexer.position(), "List data type 'INT' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "'[' EXPECTED");
			}
		} else {
			ErrorMessage.print(lexer.position(), "Type definition EXPECTED");
		}
	}

	// Function to handle Literal grammar
	public void Literal() throws java.io.IOException {
		if (token.symbol() == TokenClass.INTEGER || token.symbol() == TokenClass.NIL) {
			getToken();
		} else {
			ErrorMessage.print(lexer.position(), "Interger or NIL EXPECTED");
		}
	}

	// Function to handle Statement grammar.
	public SyntaxTree Statement() throws java.io.IOException {
		SyntaxTree ifCondTree, elseCondTree = null, whileConTree, idTree, expressionTree, statementTree = null,
				statement1Tree;
		if (token.symbol() == TokenClass.IF) {
			getToken();
			if (token.symbol() == TokenClass.LPAREN) {
				getToken();
				ifCondTree = this.Expr();
				if (token.symbol() == TokenClass.RPAREN) {
					getToken();
					statement1Tree = this.Statement();
					statementTree = new SyntaxTree("if", ifCondTree, statement1Tree);
					if (token.symbol() == TokenClass.ELSE) {
						getToken();
						elseCondTree = this.Statement();
						statementTree = new SyntaxTree("if", ifCondTree, statement1Tree, elseCondTree);
					}

				} else {
					ErrorMessage.print(lexer.position(), "')' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "'(' EXPECTED");
			}
		} else if (token.symbol() == TokenClass.WHILE) {
			getToken();
			if (token.symbol() == TokenClass.LPAREN) {
				getToken();
				whileConTree = this.Expr();

				if (token.symbol() == TokenClass.RPAREN) {
					getToken();
					statementTree = this.Statement();
					statementTree = new SyntaxTree("while", whileConTree, statementTree);
					return statementTree;
				} else {
					ErrorMessage.print(lexer.position(), "')' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "'(' EXPECTED");
			}
		} else if (token.symbol() == TokenClass.ID) {
			idTree = new SyntaxTree("id", new SyntaxTree(token.lexeme()));
			statementTree = idTree;
			getToken();

			if (token.symbol() == TokenClass.ASSIGN) {
				getToken();
				expressionTree = this.ListExpr();
				statementTree = new SyntaxTree("=", idTree, expressionTree);
				if (token.symbol() == TokenClass.SEMICOLON) {
					getToken();
				} else {
					ErrorMessage.print(lexer.position(), "';' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "'=' EXPECTED");
			}

		} else if (token.symbol() == TokenClass.PRINTLN) {

			getToken();
			if (token.symbol() == TokenClass.LPAREN) {
				getToken();

				expressionTree = this.ListExpr();
				if (token.symbol() == TokenClass.RPAREN) {
					getToken();
					statementTree = new SyntaxTree("println", expressionTree);
					if (token.symbol() == TokenClass.SEMICOLON) {
						getToken();
					} else {
						ErrorMessage.print(lexer.position(), "';' EXPECTED");
					}
				} else {
					ErrorMessage.print(lexer.position(), "')' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "'(' EXPECTED");
			}
		} else if (token.symbol() == TokenClass.LFLOWER) {
			getToken();
			statementTree = this.Statement();
			while (token.symbol() == TokenClass.PRINTLN || token.symbol() == TokenClass.LFLOWER
					|| token.symbol() == TokenClass.IF || token.symbol() == TokenClass.ID
					|| token.symbol() == TokenClass.WHILE) {

				// getToken();

				statement1Tree = this.Statement();
				statementTree = new SyntaxTree(":", statementTree, statement1Tree);
			}
			if (token.symbol() == TokenClass.RFLOWER) {
				getToken();
			} else {
				ErrorMessage.print(lexer.position(), "'}' EXPECTED");
			}

		}
		return statementTree;
	}

	// Function to handle Expr grammar.
	public SyntaxTree Expr() throws java.io.IOException {
		SyntaxTree andExprTree, expr1Tree, expr2Tree;
		expr1Tree = this.AndExpr();
		andExprTree = expr1Tree;
		while (token.symbol() == TokenClass.OR) {
			getToken();
			expr2Tree = this.AndExpr();
			andExprTree = new SyntaxTree("||", andExprTree, expr2Tree);
		}
		return andExprTree;
	};

	// Function to handle ListExpr grammar.
	public SyntaxTree ListExpr() throws java.io.IOException {
		String op;
		SyntaxTree expression1Tree, listTree = null, expression2Tree;
		expression1Tree = this.AddExpr();
		listTree = expression1Tree;
		if (token.symbol() == TokenClass.LISTOPR) {
			getToken();
			expression2Tree = this.ListExpr();
			listTree = new SyntaxTree("::", listTree, expression2Tree);
		}
		return listTree;

	}

	// Function to handle AddExpr grammar.
	public SyntaxTree AddExpr() throws java.io.IOException {
		SyntaxTree addTree, expr1Tree, expr2Tree;
		String AddOpr = "";
		expr1Tree = this.MulExpr();
		addTree = expr1Tree;
		while (token.symbol() == TokenClass.PLUS || token.symbol() == TokenClass.MINUS) {
			switch (token.symbol()) {
			case PLUS:
				AddOpr = "+";
				break;
			case MINUS:
				AddOpr = "-";
				break;
			}
			getToken();
			expr2Tree = this.MulExpr();
			addTree = new SyntaxTree(AddOpr, addTree, expr2Tree);
		}
		return addTree;
	}

	// Function to handle MulExpr grammar
	public SyntaxTree MulExpr() throws java.io.IOException {
		SyntaxTree mulTree, expr1Tree, expr2Tree;
		String mulOpr = "";
		expr1Tree = this.PreFixExpr();
		mulTree = expr1Tree;
		while (token.symbol() == TokenClass.TIMES || token.symbol() == TokenClass.SLASH) {
			switch (token.symbol()) {
			case TIMES:
				mulOpr = "*";
				break;
			case SLASH:
				mulOpr = "/";
				break;
			}
			getToken();
			expr2Tree = this.PreFixExpr();
			mulTree = new SyntaxTree(mulOpr, mulTree, expr2Tree);
		}
		return mulTree;
	}

	// Function to handle PreFixExpr grammar
	public SyntaxTree PreFixExpr() throws java.io.IOException {
		SyntaxTree preFixTree = null, expr1Tree, expr2Tree;
		String preFixOpr = "";
		if (token.symbol() == TokenClass.PLUS || token.symbol() == TokenClass.MINUS) {
			switch (token.symbol()) {
			case PLUS:
				preFixOpr = "+";
				break;
			case MINUS:
				preFixOpr = "-";
				break;
			}
			getToken();
			expr1Tree = this.simpleExpr();
			preFixTree = new SyntaxTree(preFixOpr, expr1Tree);
		}
		String listMethodOpr = "";
		expr1Tree = this.simpleExpr();
		preFixTree = expr1Tree;
		if (token.symbol() == TokenClass.PERIOD) {

			getToken();
			switch (token.symbol()) {
			case HEAD:
				listMethodOpr = "head";
				break;
			case TAIL:
				listMethodOpr = "tail";
				break;
			case ISEMPTY:
				listMethodOpr = "isempty";
				break;

			}
			preFixTree = new SyntaxTree(listMethodOpr, expr1Tree);
			if (token.symbol() == TokenClass.HEAD || token.symbol() == TokenClass.TAIL
					|| token.symbol() == TokenClass.ISEMPTY) {
				getToken();
			} else {
				ErrorMessage.print(lexer.position(), "head, tail, isEmpty keywords expected EXPECTED");
			}
		}

		return preFixTree;

	}

	// Function to handle SimpleExpr grammar
	public SyntaxTree simpleExpr() throws java.io.IOException {
		SyntaxTree simpleExprTree = null, idTree, intTree, expressionTree, param1Tree, param2Tree = null;
		String procID = "";
		if (token.symbol() == TokenClass.INTEGER) {
			simpleExprTree = new SyntaxTree("intValue", new SyntaxTree(token.lexeme()));
			getToken();

		} else if (token.symbol() == TokenClass.ID) {
			simpleExprTree = new SyntaxTree("id", new SyntaxTree(token.lexeme()));
			procID = token.lexeme();
			getToken();

			if (token.symbol() == TokenClass.LPAREN) {
				getToken();
				param1Tree = this.ListExpr();
				simpleExprTree = new SyntaxTree(",", param1Tree, null);
				while (token.symbol() == TokenClass.COMMA) {
					getToken();
					param2Tree = this.ListExpr();
					simpleExprTree = new SyntaxTree(",", param1Tree, param2Tree);
				}
				if (token.symbol() == TokenClass.RPAREN) {
					String apply = "apply " + procID;
					simpleExprTree = new SyntaxTree(apply, simpleExprTree);
					getToken();

				} else {
					ErrorMessage.print(lexer.position(), "')' EXPECTED");
				}
			}

		} else if (token.symbol() == TokenClass.LPAREN) {

			getToken();
			simpleExprTree = this.Expr();
			if (token.symbol() == TokenClass.RPAREN) {
				getToken();
			} else {
				ErrorMessage.print(lexer.position(), "')' EXPECTED");
			}
		} else if (token.symbol() == TokenClass.NIL) {
			getToken();
			simpleExprTree = new SyntaxTree("NIL");
		} else {
			ErrorMessage.print(lexer.position(), "Unexpected Symbol");
		}
		return simpleExprTree;
	}

	// Function to handle ANDExpr grammar
	public SyntaxTree AndExpr() throws java.io.IOException {
		SyntaxTree andExprTree, expr1Tree, expr2Tree;
		expr1Tree = this.RelExpr();
		andExprTree = expr1Tree;
		while (token.symbol() == TokenClass.AND) {
			getToken();
			expr2Tree = this.RelExpr();
			andExprTree = new SyntaxTree("&&", andExprTree, expr2Tree);
		}
		return andExprTree;
	}

	// Function to handle RelExpr grammar
	public SyntaxTree RelExpr() throws java.io.IOException {
		SyntaxTree relExprTree = null, expr1Tree, expr2Tree;
		String op = null;
		if (token.symbol() == TokenClass.NOT) {
			getToken();
			expr1Tree = this.ListExpr();
			op = "!";
			relExprTree = new SyntaxTree(op, expr1Tree);
		} else {

			expr1Tree = this.ListExpr();
			relExprTree = expr1Tree;
			if (token.symbol() == TokenClass.NE || token.symbol() == TokenClass.EQ || token.symbol() == TokenClass.GT
					|| token.symbol() == TokenClass.GE || token.symbol() == TokenClass.LT
					|| token.symbol() == TokenClass.LE) {
				switch (token.symbol()) {
				case EQ:
					op = "==";
					break;
				case LT:
					op = "<";
					break;
				case GT:
					op = ">";
					break;
				case NE:
					op = "!=";
					break;
				case LE:
					op = "<=";
					break;
				case GE:
					op = ">=";
					break;
				}
				getToken();
				expr2Tree = this.ListExpr();

				relExprTree = new SyntaxTree(op, expr1Tree, expr2Tree);
			}
		}
		return relExprTree;

	}

}
