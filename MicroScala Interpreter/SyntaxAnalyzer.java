//Author: Abhinav Bhandaram
//CSE Machine:cse01.cse.unt.edu
// This is the main class used for the syntax analyzing.

import java.io.BufferedReader;
import java.io.FileReader;
import org.xml.sax.ext.LexicalHandler;

public class SyntaxAnalyzer {
	protected MicroScalaLexer lexer;
	protected Token token;
//Construtor for the analyzer takes a string input specifying the //filename.
	public SyntaxAnalyzer(String fileName) throws java.io.IOException {
		lexer = new MicroScalaLexer(new FileReader(fileName));
		getToken();
	}
//Method used to call the get token method of the lexer to
//get the next legitimate token.
	private void getToken() throws java.io.IOException {
		token = lexer.nextToken();
	}
//Main entry point function for the syntax analyzer.
	public void CompilationUnit() throws java.io.IOException {

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
					// System.out.println(token.symbol());
					this.HandleDef();

					while (token.symbol() == TokenClass.DEF || token.symbol() == TokenClass.VAR) {

						this.HandleDef();
					}
					if (token.symbol() != TokenClass.RFLOWER) {
						ErrorMessage.print(lexer.position(), "'}' EXPECTED");

					}
				}
			}
		}
	}
//Handling the scenario for multiple function definitions and var definitions.
	public void HandleDef() throws java.io.IOException {

		switch (token.symbol()) {
		case DEF:
			getToken();
			if (token.symbol() == TokenClass.ID) {
				this.Def();
			} else if (token.symbol() == TokenClass.MAIN) {
				this.MainDef();
			} else {
				ErrorMessage.print(lexer.position(), "Function definition or Main function definition EXPECTED");
			}
			break;
		case VAR:
			this.VarDef();
			break;
		default:
			ErrorMessage.print(lexer.position(),
					"Function definition, Var Definition or Main function definition EXPECTED");
		}
		// getToken();
	}
//Method to handle def non terminal grammar.
	public void Def() throws java.io.IOException {
		getToken();
		// System.out.println(token.symbol());
		if (token.symbol() == TokenClass.LPAREN) {
			getToken();
			if (token.symbol() == TokenClass.ID) {
				getToken();
				if (token.symbol() == TokenClass.COLON) {
					getToken();
					this.Type();
					while (token.symbol() == TokenClass.COMMA) {
						getToken();
						if (token.symbol() == TokenClass.ID) {
							getToken();
							if (token.symbol() == TokenClass.COLON) {
								getToken();
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
					this.Type();
					if (token.symbol() == TokenClass.ASSIGN) {
						getToken();
						if (token.symbol() == TokenClass.LFLOWER) {
							getToken();
							while (token.symbol() == TokenClass.VAR) {
								this.VarDef();
							}

							this.Statement();
							while (token.symbol() == TokenClass.SEMICOLON) {
								getToken();
								this.Statement();
							}
							if (token.symbol() == TokenClass.RETURN) {
								getToken();
								this.ListExpr();
								if (token.symbol() == TokenClass.SEMICOLON) {
									getToken();
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
//Function to handle grammar for MainDef
	public void MainDef() throws java.io.IOException {
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
										if (token.symbol() == TokenClass.LFLOWER) {
											getToken();
											while (token.symbol() == TokenClass.VAR) {
												this.VarDef();
											}
											this.Statement();
											while(token.symbol()==TokenClass.SEMICOLON){
												getToken();
												this.Statement();
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
	}
//Function to handle grammar for var def
	public void VarDef() throws java.io.IOException {
		getToken();
		if (token.symbol() == TokenClass.ID) {
			getToken();
			if (token.symbol() == TokenClass.COLON) {
				getToken();
				this.Type();
				if (token.symbol() == TokenClass.ASSIGN) {
					getToken();
					this.Literal();
					if (token.symbol() != TokenClass.SEMICOLON) {
						ErrorMessage.print(lexer.position(), "';' EXPECTED");
					} else {
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
	}
//Function to handle Type grammar 
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
//Function to handle Literal grammar
	public void Literal() throws java.io.IOException {
		if (token.symbol() == TokenClass.INTEGER || token.symbol() == TokenClass.NIL) {
			getToken();
		} else {
			ErrorMessage.print(lexer.position(), "Interger or NIL EXPECTED");
		}
	}
//Function to handle Statement grammar.
	public void Statement() throws java.io.IOException {
		boolean ifBType = false;
		boolean elseBType = false;
		if (token.symbol() == TokenClass.IF) {
			getToken();
			if (token.symbol() == TokenClass.LPAREN) {
				getToken();
				this.Expr();
				// System.out.println("here with"+token.symbol());
				if (token.symbol() == TokenClass.RPAREN) {
					getToken();
					if (token.symbol() == TokenClass.LFLOWER) {
						// ifBType=true;
						getToken();
						this.Statement();
						System.out.println(token.symbol());
						while (token.symbol() == TokenClass.SEMICOLON) {
							getToken();
							this.Statement();
						}

						if (token.symbol() == TokenClass.RFLOWER) {
							getToken();
							if (token.symbol() == TokenClass.ELSE) {
								getToken();
								if (token.symbol() == TokenClass.LFLOWER) {
									elseBType = true;
									getToken();
									this.Statement();
									while (token.symbol() == TokenClass.SEMICOLON) {
										getToken();
										this.Statement();

									}
									if (token.symbol() == TokenClass.RFLOWER) {
										getToken();
									} else {
										ErrorMessage.print(lexer.position(), "'}' EXPECTED");
									}
								} else {
									this.Statement();
									if (token.symbol() != TokenClass.SEMICOLON) {
										ErrorMessage.print(lexer.position(), "';' EXPECTED");
									}
									getToken();
									if(token.symbol()==TokenClass.RFLOWER){
										ErrorMessage.print(lexer.position(), "'}' UNEXPECTED");
									}

								}

							}
						} else {
							ErrorMessage.print(lexer.position(), "'}' EXPECTED");
						}
					} else {
						// getToken();

						this.Statement();
						//System.out.println(token.symbol());
						if (token.symbol() == TokenClass.SEMICOLON) {
							// System.out.println("here with"+token.symbol());
							// if (token.symbol() == TokenClass.SEMICOLON) {
							getToken();
							if (token.symbol() == TokenClass.RFLOWER) {
								ErrorMessage.print(lexer.position(), "'}' UNEXPECTED token");
							}
							if (token.symbol() == TokenClass.ELSE) {
								getToken();
								// System.out.println("here with" +
								// token.symbol());
								if (token.symbol() == TokenClass.LFLOWER) {
									getToken();
									this.Statement();
									while (token.symbol() == TokenClass.SEMICOLON) {
										getToken();
										this.Statement();

									}
									if (token.symbol() == TokenClass.RFLOWER) {
										getToken();
									} else {
										ErrorMessage.print(lexer.position(), "'}' EXPECTED");
									}
								} else {
									this.Statement();
									if (token.symbol() != TokenClass.SEMICOLON) {
										ErrorMessage.print(lexer.position(), "';' EXPECTED");
									}
									getToken();
									
									if(token.symbol()==TokenClass.RFLOWER){
										ErrorMessage.print(lexer.position(), "'}' UNEXPECTED");
									}
									this.Statement();
								}

							}

						}else{
							ErrorMessage.print(lexer.position(), "';' EXPECTED");
						}
					}
				} else {
					ErrorMessage.print(lexer.position(), "here ')' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "'(' EXPECTED");
			}
		}
		if (token.symbol() == TokenClass.WHILE) {
			getToken();
			if (token.symbol() == TokenClass.LPAREN) {
				getToken();
				this.Expr();
				if (token.symbol() == TokenClass.RPAREN) {
					getToken();
					if (token.symbol() == TokenClass.LFLOWER) {
						getToken();
						this.Statement();
						System.out.println(token.symbol());
						while(token.symbol()==TokenClass.SEMICOLON){
							getToken();
							this.Statement();
						}
						// System.out.println(token.symbol());
						if (token.symbol() == TokenClass.RFLOWER) {
							getToken();
							this.Statement();
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
				ErrorMessage.print(lexer.position(), "'(' EXPECTED");
			}
		}
		if (token.symbol() == TokenClass.ID) {
			getToken();
			if (token.symbol() == TokenClass.ASSIGN) {
				getToken();
				// System.out.println("Coming here correctly");
				this.ListExpr();

			} else {
				ErrorMessage.print(lexer.position(), "'=' EXPECTED");
			}
		}
		if (token.symbol() == TokenClass.PRINTLN) {
			// System.out.println("here with"+token.symbol());
			getToken();
			if (token.symbol() == TokenClass.LPAREN) {
				getToken();
				// System.out.println(token.symbol());
				this.ListExpr();
				if (token.symbol() == TokenClass.RPAREN) {
					getToken();
				} else {
					ErrorMessage.print(lexer.position(), "')' EXPECTED");
				}
			} else {
				ErrorMessage.print(lexer.position(), "'(' EXPECTED");
			}
		}
		if (token.symbol() == TokenClass.LFLOWER) {
			getToken();
			this.Statement();
			while (token.symbol() == TokenClass.SEMICOLON) {
				this.Statement();
			}
			if (token.symbol() == TokenClass.RFLOWER) {
				getToken();
			} else {
				ErrorMessage.print(lexer.position(), "'}' EXPECTED");
			}
		}
	}
//Function to handle Expr grammar.
	public void Expr() throws java.io.IOException {
		this.AndExpr();
		// System.out.println(token.symbol());
		while (token.symbol() == TokenClass.OR) {
			getToken();
			this.AndExpr();
		}
		// getToken();
	};
//Function to handle ListExpr grammar.
	public void ListExpr() throws java.io.IOException {
		// System.out.println(token.symbol());
		this.AddExpr();
		if (token.symbol() == TokenClass.LISTOPR) {
			getToken();
			this.ListExpr();
		}
		// getToken();

	}
//Function to handle AddExpr grammar.
	public void AddExpr() throws java.io.IOException {

		this.MulExpr();
		while (token.symbol() == TokenClass.PLUS || token.symbol() == TokenClass.MINUS) {
			getToken();
			this.MulExpr();
		}

	}
//Function to handle MulExpr grammar
	public void MulExpr() throws java.io.IOException {

		this.PreFixExpr();
		while (token.symbol() == TokenClass.TIMES || token.symbol() == TokenClass.SLASH) {
			getToken();
			this.PreFixExpr();
		}

	}
//Function to handle PreFixExpr grammar
	public void PreFixExpr() throws java.io.IOException {

		if (token.symbol() == TokenClass.PLUS || token.symbol() == TokenClass.MINUS) {
			getToken();
			this.simpleExpr();
		} else {

			this.simpleExpr();
			if (token.symbol() == TokenClass.PERIOD) {
				// System.out.println("here");
				getToken();

				if (token.symbol() == TokenClass.HEAD || token.symbol() == TokenClass.TAIL
						|| token.symbol() == TokenClass.ISEMPTY) {
					getToken();
				} else {
					ErrorMessage.print(lexer.position(), "head, tail, isEmpty kywords expected EXPECTED");
				}
			}

		}
	}
//Function to handle SimpleExpr grammar
	public void simpleExpr() throws java.io.IOException {
		// System.out.println("simple expr"+token.symbol()+token.lexeme());
		if (token.symbol() == TokenClass.INTEGER) {

			getToken();

		} else if (token.symbol() == TokenClass.ID) {

			getToken();
			// System.out.println("here with symbol"+token.symbol());
			if (token.symbol() == TokenClass.LPAREN) {
				getToken();
				this.ListExpr();
				while (token.symbol() == TokenClass.COMMA) {
					getToken();
					this.ListExpr();
				}
				if (token.symbol() == TokenClass.RPAREN) {
					getToken();

				} else {
					ErrorMessage.print(lexer.position(), "')' EXPECTED");
				}
			}

		} else if (token.symbol() == TokenClass.LPAREN) {
			getToken();
			this.Expr();
			if (token.symbol() == TokenClass.RPAREN) {
				getToken();
			} else {
				ErrorMessage.print(lexer.position(), "')' EXPECTED");
			}
		} else if (token.symbol() == TokenClass.NIL) {
			getToken();
		} else {
			ErrorMessage.print(lexer.position(), "Unexpected Symbol");
		}
	}
//Function to handle ANDExpr grammar
	public void AndExpr() throws java.io.IOException {

		this.RelExpr();

		while (token.symbol() == TokenClass.AND) {
			getToken();
			this.RelExpr();
		}

	}
//Function to handle RelExpr grammar
	public void RelExpr() throws java.io.IOException {
		if (token.symbol() == TokenClass.NOT) {
			getToken();
			this.ListExpr();
		} else {
			// System.out.println("here with symbo"+token.symbol());
			this.ListExpr();
			if (token.symbol() == TokenClass.NE || token.symbol() == TokenClass.EQ || token.symbol() == TokenClass.GT
					|| token.symbol() == TokenClass.GE || token.symbol() == TokenClass.LT
					|| token.symbol() == TokenClass.LE) {
				getToken();
				this.ListExpr();
			}
		}

	}

}
