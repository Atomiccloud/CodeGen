import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CodeGen {
	//Global Variables
	static Scanner sc = null;
	static String curr_token = null;
	static String tempToken = null;
	
 
		
	public static void main(String[] args) {
		File textFile = new File(args[0]);
		Lexer lexer = new Lexer(textFile);
		textFile = new File("tokens.txt");
		
		try {
			sc = new Scanner(textFile);
			curr_token = sc.nextLine();
			
			
			decList();		
			if(curr_token.equals("$")) {
				System.out.println("ACCEPT");
			} else {
				rej();
			}
		} catch (FileNotFoundException e) {			
			System.out.println("File Not Found!");
		}
		
		sc.close();

	}
	
	private static void decList() {	
		declaration();
		decListP();
	}
	
	private static void decListP() {
		if (curr_token.contains("K: ")) {
			declaration();
			decListP();
		}
	}

	private static void declaration() {
		
		typeSpecifier();
		checkID();
		declarationP();
	}

	private static void checkID() {
		if(curr_token.contains("ID: ")) {
			curr_token = sc.nextLine();
		} else {
			rej();
		}		
	}

	private static void declarationP() {
		if (curr_token.equals(";") || curr_token.equals("[")) {
			varDecP();
		} else if (curr_token.equals("(")) {
			funDecP();
		} else {
			rej();
		}
	}
	
	private static void funDecP() {
		if (curr_token.equals("(")) {
			curr_token = sc.nextLine();
			params();
			if (curr_token.equals(")")) {
				curr_token = sc.nextLine();
				compoundStmt();
			} else {
				rej();
			}
		} else {
			rej();
		}
	}

	private static void compoundStmt() {
		if(curr_token.equals("{")) {
			curr_token = sc.nextLine();
			localDec();
			stmtList();
			if(curr_token.equals("}")) {
				curr_token = sc.nextLine();
			} else {
				rej();
			}
		}
	}

	private static void stmtList() {
		if (curr_token.equals("(") || curr_token.equals("{") || curr_token.contains("ID: ") || curr_token.contains("K: ") || curr_token.contains("INT: ") 
				|| curr_token.equals(";") ) {
			statement();
			stmtList();
		}
	}

	private static void statement() {
		if (curr_token.equals("{")) {
			compoundStmt();
		} else if (curr_token.equals("K: if")) {
			selectionStmt();
		} else if (curr_token.equals("K: while")) {
			iterationStmt();
		} else if (curr_token.equals("K: return")) {
			returnStmt();
		} else {
			expressionStmt();
		}
	}

	private static void expressionStmt() {
		if (curr_token.equals(";")) {
			curr_token = sc.nextLine();			
		} else {
			expression();
			if(curr_token.equals(";")) {
				curr_token = sc.nextLine();				
			} else {
				rej();
			}
		}
	}

	private static void expression() {
		addExpression();
		if(curr_token.equals("<=") || curr_token.equals("<") || curr_token.equals(">") || curr_token.equals(">=") || curr_token.equals("==") || curr_token.equals("!=")) {
			relop();
			addExpression();
		}
	}

	private static void relop() {
		curr_token = sc.nextLine();
	}

	private static void addExpression() {
		term();
		addExpressionP();
	}

	private static void addExpressionP() {
		if (curr_token.equals("+") || curr_token.equals("-")) {
			addop();
			term();
			addExpressionP();
		}
	}

	private static void termP() {
		if (curr_token.equals("*") || curr_token.equals("/")) {
			mulop();
			factor();
			termP();
		}
	}

	private static void mulop() {
		curr_token = sc.nextLine();
	}

	private static void addop() {
		curr_token = sc.nextLine();
	}

	private static void factor() {
		if (curr_token.equals("(")) {
			curr_token = sc.nextLine();
			expression();
			if (curr_token.equals(")")) {
				curr_token = sc.nextLine();
			}
		} else if (curr_token.contains("ID: ")) {
			checkID();
			factorP();
		} else if (curr_token.contains("INT: ") ) {
			checkNUM();
		} else {
			rej();
		}
	}

	private static void factorP() {
		if (curr_token.equals("(")) {
			callP();
		} else {
			varP();
		}
	}

	private static void varP() {
		if (curr_token.equals("[")) {
			curr_token = sc.nextLine();
			expression();
			if (!curr_token.equals("]")) {
				rej();
			} else {
				curr_token = sc.nextLine();
				if (curr_token.equals("=")) {
					curr_token = sc.nextLine();
					if(curr_token.equals("(") || curr_token.contains("ID: ") || curr_token.contains("INT: ") ) {
						expression();
					} else {
						checkID();
						if (curr_token.equals("[")) {
							curr_token = sc.nextLine();
							expression();
							if (curr_token.equals("]")) {
								curr_token = sc.nextLine();
							} else {
								rej();
							}
						} else {						
					}
						rej();
					}
				}
			}
		} else if (curr_token.equals("=")) {
			curr_token = sc.nextLine();
			expression();
		}
	}

	private static void callP() {
		curr_token = sc.nextLine();
		args();
		if (curr_token.equals(")")) {
			curr_token = sc.nextLine();
		} else {
			rej();
		}
	}

	private static void args() {
		if (curr_token.equals("(") || curr_token.contains("ID: ") || curr_token.contains("INT: ") ) {
			argList();
		}
	}

	private static void argList() {
		expression();
		argListP();
	}

	private static void argListP() {
		if(curr_token.equals(",")) {
			curr_token = sc.nextLine();
			expression();	
			argListP();
		}	
	}

	private static void term() {
		factor();
		termP();
	}

	private static void returnStmt() {
		curr_token = sc.nextLine();
		if (curr_token.equals(";")) {
			curr_token = sc.nextLine();
		} else {
			expression();
			if (curr_token.equals(";")) {
				curr_token = sc.nextLine();
			} else {
				rej();
			}
		}
	}

	private static void iterationStmt() {
		curr_token = sc.nextLine();
		if(curr_token.equals("(")) {
			curr_token = sc.nextLine();
			expression();
			if (curr_token.equals(")")) {
				curr_token = sc.nextLine();
				statement();
			} else {
				rej();
			}
		} else {
			rej();
		}
	}

	private static void selectionStmt() {
		curr_token = sc.nextLine();
		if (curr_token.equals("(")) {
			curr_token = sc.nextLine();
			expression();
			if (curr_token.equals(")")) {
				curr_token = sc.nextLine();
				statement();
				if (curr_token.equals("K: else")) {
					curr_token = sc.nextLine();
					statement();
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
	}

	private static void localDec() {
		if (curr_token.equals("K: int") || curr_token.equals("K: void")) {
			varDec();
			localDec();
		}
	}

	private static void varDec() {
		typeSpecifier();
		checkID();
		if(curr_token.equals(";")) {
			curr_token = sc.nextLine();
		} else if (curr_token.equals("[")) {
			curr_token = sc.nextLine();
			checkNUM();
			if(!curr_token.equals("]")) {
				rej();
			}
			curr_token = sc.nextLine();
			if(!curr_token.equals(";")) {
				rej();
			}
			curr_token = sc.nextLine();
		} else {
			rej();
		}
		
	}

	private static void checkNUM() {
		if (curr_token.contains("INT: ") ) {
			curr_token = sc.nextLine();
		} else {
			rej();
		}
	}

	private static void params() {
		if(curr_token.equals("K: void")) {
			curr_token = sc.nextLine();
		} else {
			paramsList();
		}
	}

	private static void paramsList() {
		param();
		paramsListP();
	}

	private static void paramsListP() {
		if(curr_token.equals(",")) {
			curr_token = sc.nextLine();
			param();
			paramsListP();
		}
	}

	private static void param() {
		typeSpecifier();
		checkID();
		if(curr_token.equals("[")) {
			curr_token = sc.nextLine();
			if(curr_token.equals("]")) {
				curr_token = sc.nextLine();
			}
		}
	}

	private static void varDecP() {
		if(curr_token.equals(";")) {
			curr_token = sc.nextLine();
		} else if (curr_token.equals("[")) {
			curr_token = sc.nextLine();
			checkNUM();
			if(!curr_token.equals("]")) {
				rej();
			}
			curr_token = sc.nextLine();
			if(!curr_token.equals(";")) {
				rej();
			}
			curr_token = sc.nextLine();
		} else {
			rej();
		}
	}

	private static void typeSpecifier() {
		if(curr_token.equals("K: int")){		
			curr_token = sc.nextLine();
		} else if(curr_token.equals("K: void")){		
			curr_token = sc.nextLine();
		} else {
			rej();
		}
	}	
	
	private static void rej() {
		System.out.println("REJECT");
		System.exit(0);
	}
}
