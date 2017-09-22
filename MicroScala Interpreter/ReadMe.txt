1. Executing Script Name: MicroScalaSyn.java: The script file is used to execute the .scala programs, it is given as uder input.
2. Interpreter: MicroScalaInterpreter.java: The main interpreter file responsible for interpreting and executing the scala programs.
3. Parser: Parser.java: Parser class creates the static environment of the program.
4. Environment: Environment.java
3. Error Message: ErrorMessage.java
4. Token Enumeration: TokenClass.java
5. Token Actions: Token.java
6. Output files: TestNOutput.txt where 'N' is the number of the output file given (1-6).

Example execution Statment: java MicroScalaLex TestN.scala

Where 'N' is the number of the input file given (1-6).

Output placed in text file using:

java MicroScalaLex TestN.scala > TestNOutput.txt

Where 'N' is the number of the input file given (1-6).
