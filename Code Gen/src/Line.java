public class Line {
	int index;
	String op;
	String opnd1;
	String opnd2;
	String result;
	
	
	public Line(int index, String op, String opnd1, String opnd2, String result) {
		this.index = index;
		this.op = op;
		this.opnd1 = opnd1;
		this.opnd2 = opnd2;
		this.result = result;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public String getOp() {
		return op;
	}


	public void setOp(String op) {
		this.op = op;
	}


	public String getOpnd1() {
		return opnd1;
	}


	public void setOpnd1(String opnd1) {
		this.opnd1 = opnd1;
	}


	public String getOpnd2() {
		return opnd2;
	}


	public void setOpnd2(String opnd2) {
		this.opnd2 = opnd2;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}
	
	public void print(){
		System.out.printf("%-15d %-15s %-15s %-15s %-15s\n", index, op, opnd1, opnd2, result);
		//System.out.println(index + "\t\t" + op + "\t\t" + opnd1 + "\t\t" + opnd2 + "\t\t" + result);
	}
}
