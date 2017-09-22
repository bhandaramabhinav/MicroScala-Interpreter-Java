//Author: Abhinav Bhandaram
//CSE Machine:  cse01.cse.unt.edu
// TokenClass enumeration definition
// TokenClass is an enumeration to represent lexical token classes in the 
// PL/0 programming language.

public enum TokenClass {
  EOF, 
  // keywords
  OBJECT, DEF, MAIN, ARGS, RETURN, ARRAY, STRING, VAR, INT, LIST, IF, ELSE, WHILE, PRINTLN, HEAD, TAIL, ISEMPTY, NIL,
  // punctuation
  COMMA, PERIOD, SEMICOLON, COLON, LISTOPR,
  // operators
  LPAREN, RPAREN, LFLOWER, RFLOWER, RSQUARE, LSQUARE, ASSIGN, PLUS, MINUS, TIMES, SLASH, EQ, LT, LE, GT, GE, NE, NOT, AND, OR,
  
// ids,comments and integers
  ID, INTEGER, COMMENT

}
