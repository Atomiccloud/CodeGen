import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CodeGen {
	//Global Variables
	static Scanner sc = null;
	static String curr_token = null;
	static String tempToken = null;
	static Line line;
	static int lineCounter = 0;
	static int paramCounter;
	static ArrayList<Line> list = new ArrayList<Line>();
	
 
		
	public static void main(String[] args) {
		File textFile = new File(args[0]);
		Lexer lexer = new Lexer(textFile);
		textFile = new File("tokens.txt");
		
		
		
		try {
			sc = new Scanner(textFile);
			curr_token = sc.nextLine();
			
			
			decList();		
			if(curr_token.equals("$")) {
				System.out.printf("%-15s %-15s %-15s %-15s %-15s\n", "index", "op", "opnd1", "opnd2", "result");
				System.out.println("-------------------------------------------------------------------------");
				for(int i=0;i<list.size();i++) {
					list.get(i).print();
				}
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
		String id;
		String returnType = curr_token.substring(3);
		typeSpecifier();
		id = curr_token.substring(4);
		checkID();
		declarationP(id,returnType);
	}

	private static void checkID() {
		if(curr_token.contains("ID: ")) {
			curr_token = sc.nextLine();
		} else {
			rej();
		}		
	}

	private static void declarationP(String id,String returnType) {
		if (curr_token.equals(";") || curr_token.equals("[")) {
			varDecP();
		} else if (curr_token.equals("(")) {
			funDecP(id,returnType);
		} else {
			rej();
		}
	}
	
	private static void funDecP(String id, String returnType) {
		line = new Line(lineCounter,"func",id,returnType,"");
		int temp = lineCounter;
		lineCounter++;
		if (curr_token.equals("(")) {
			curr_token = sc.nextLine();
			paramCounter = 0;
			list.add(line);
			params();
			list.get(temp).setResult(Integer.toString(paramCounter));
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
		String term = term();

		if(term != null && term.contains("C: ")) {
			Line lineAdd = new Line(lineCounter++,"call","","","");
			lineAdd.setResult(term);
		//	list.add(lineAdd);
		}
		
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

	private static String factor() {
		String ret = null;
		if (curr_token.equals("(")) {
			curr_token = sc.nextLine();
			expression();
			if (curr_token.equals(")")) {
				curr_token = sc.nextLine();
			}
		} else if (curr_token.contains("ID: ")) {
			String temp = curr_token.substring(4);
			checkID();
			ret = factorP();
			if(ret != null && ret.equals("call")) {
				ret = "C: " + temp;
			}
		} else if (curr_token.contains("INT: ") ) {
			checkNUM();
		} else {
			rej();
		}
		return ret;
	}

	private static String factorP() {
		String ret = null;
		if (curr_token.equals("(")) {
			ret = "call";
			callP();
		} else {
			varP();
		}
		return ret;
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

	private static String term() {
		String ret = null;
		ret = factor();
		termP();
		return ret;
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
		line = new Line(lineCounter, "alloc", "", "", "");
		typeSpecifier();
		if(curr_token.contains("ID: ")) {
			line.setResult(curr_token.substring(4));
		}
		checkID();
		if(curr_token.equals(";")) {
			line.setOpnd1("4");
			curr_token = sc.nextLine();
		} else if (curr_token.equals("[")) {
			curr_token = sc.nextLine();
			if(curr_token.contains("INT: ")) {
				int x = 4*(Integer.valueOf(curr_token.substring(5)));
				line.setOpnd1(Integer.toString(x));
			}
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
		list.add(line);
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
		line = new Line(lineCounter++,"param","","","");
		paramCounter++;
		typeSpecifier();
		if(curr_token.contains("ID: ")) {
			line.setResult(curr_token.substring(4));
		}
		list.add(line);
		line = new Line(lineCounter++, "alloc" ,"4" ,"" ,curr_token.substring(4));
		checkID();
		if(curr_token.equals("[")) {
			curr_token = sc.nextLine();
			if(curr_token.equals("]")) {
				curr_token = sc.nextLine();
			}
		} else {
			
		}
		list.add(line);
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
