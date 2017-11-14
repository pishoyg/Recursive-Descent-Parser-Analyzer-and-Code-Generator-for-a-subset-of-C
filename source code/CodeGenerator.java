import java.util.*;

public class CodeGenerator {
	int labelCounter;
	Node StatementList;
	SymbolTable Table;
	ArrayList<Instruction> code;

	/*
	 * The constructor of the code generator is passed a reference to the symbol
	 * table, and a reference to the statement list of the program
	 */
	public CodeGenerator(SymbolTable table, Node statementList) {
		this.Table = table;
		this.StatementList = statementList;
	}

	/*
	 * return the code generated by traversing the statement list that was
	 * passed to the constructor
	 */
	public ArrayList<Instruction> generateCode() throws Exception {
		labelCounter = 0;
		code = new ArrayList<Instruction>();
		this.generateStatementCode(this.StatementList);
		return code;
	}

	private void generateStatementCode(Node statement) throws Exception {
		for (; statement != null; statement = statement.Sibling) {
			switch (statement.getType()) {
			case if_keyword: {
				/*
				 * generate code to evaluate the expression and
				 * store the result in t0
				 */
				int expressionRegNum = 0;
				generateExpressionCode(statement.getChild(0),
						expressionRegNum);
				Instruction exitIfLabel = newLabel();
				Instruction exitIf = new Instruction(
						InstructionType.JumpZ,
						tmpReg(expressionRegNum),
						exitIfLabel.getOperand(0));
				/*
				 * add a conditional jump to exit label
				 */
				code.add(exitIf);
				/*
				 * generate code for statements that are supposed
				 * to be executed when the expression evaluates
				 * to true
				 */
				generateStatementCode(statement.getChild(1));
				if (statement.getChildrenCount() == 3) {
					/*
					 * if the if statement has an else part,
					 * generate code for it
					 */
					Instruction exitElseLabel = newLabel();
					/*
					 * add a jump instruction (to skip the exit
					 * part in case the expression evaluated to
					 * true and the previous part was executed
					 */
					Instruction exitElse = new Instruction(
							InstructionType.Jump,
							exitElseLabel.getOperand(0));
					code.add(exitElse);
					/*
					 * place the 'if' exit label here (so the code
					 * will execute the else part if the expression
					 * evaluates to false and the jump is executed
					 */
					code.add(exitIfLabel);
					/*
					 * generate code for the else part
					 */
					generateStatementCode(statement.getChild(2));
					/*
					 * place the 'else' exit label here
					 */
					code.add(exitElseLabel);
				} else {
					/*
					 * if there is no else part, we just need to
					 * just place the 'if' exit label here
					 */
					code.add(exitIfLabel);
				}
				break;
			}
			case while_keyword: {
				/*
				 * code for iteration statement uses labels in a way
				 * similar to that for selection statement
				 */
				Instruction enterLabel = newLabel();
				Instruction exitLabel = newLabel();
				code.add(enterLabel);
				int expressionRegNum = 0;
				generateExpressionCode(statement.getChild(0),
						expressionRegNum);
				Instruction exitWhile = new Instruction(
						InstructionType.JumpZ,
						tmpReg(expressionRegNum),
						exitLabel.getOperand(0));
				code.add(exitWhile);
				generateStatementCode(statement.getChild(1));
				Instruction enterWhile = new Instruction(
						InstructionType.Jump,
						enterLabel.getOperand(0)); 
				code.add(enterWhile);
				code.add(exitLabel);
				break;
			}
			case assignment_operator: {
				/* generate code to evaluate expression on right hand
				 * side and store it in register #valueRegNum
				 */
				int valueRegNum = 0;
				generateExpressionCode(statement.getChild(1),
						valueRegNum);
				Node variable = statement.getChild(0);
				/* the following code finds the memory location
				 * of the variable on the left hand side, and
				 * adds a 'store' instruction to store the value
				 * computed previously in that location
				 */
				if (variable.getType() == TokenType.square_bracket) {
					int addressRegNum = 1;
					int offsetRegNum = 2;
					Node id = variable.getChild(0);
					Node expression = variable.getChild(1);
					/*
					 * generate code to compute the expression
					 * representing the array index, and store
					 * its value in register #offsetRegNum
					 */
					generateExpressionCode(expression, offsetRegNum);
					/*
					 * generate code to assign value of register
					 * #addressRegNum to the base address of the
					 * variable
					 * 
					 * base address is a static attribute that
					 * is obtained from the symbol table
					 */
					Instruction assign = new Instruction(
							InstructionType.Assign,
							tmpReg(addressRegNum),
							address(id)); 
					/*
					 * generate code to multiply offset by
					 * the size of an array element of this
					 * type
					 * 
					 * size of an array element is a static
					 * attribute that is obtained from the
					 * symbol table
					 */
					Instruction mul = new Instruction(
							InstructionType.Mul,
							tmpReg(offsetRegNum),
							tmpReg(offsetRegNum),
							size(id)
							);
					Instruction add = new Instruction(
							InstructionType.Add,
							tmpReg(addressRegNum),
							tmpReg(addressRegNum),
							tmpReg(offsetRegNum)); 
					Instruction store = new Instruction(
							InstructionType.Store,
							tmpReg(addressRegNum),
							tmpReg(valueRegNum));
					code.add(assign);
					code.add(mul);
					code.add(add);
					code.add(store);
				} else {
					Instruction store = new Instruction(
							InstructionType.Store,
							address(variable),
							tmpReg(valueRegNum));
					code.add(store);
				}
				break;
			}
			}
		}
	}

	/*
	 * this function generates code to be evaluate the expression
	 * represented by the passed syntax tree node, and stores its
	 * value in temporary register #returnRegNum
	 */
	private void generateExpressionCode(Node node, int returnRegNum)
			throws Exception {
		switch (node.getType()) {
		case addition_operator:
		case multiplication_operator:
		case relational_operator: {
			int leftValueRegNum = returnRegNum + 1;
			int rightValueRegNum = returnRegNum + 2;
			// if the expression is an operator, generate code
			// over the following 3 steps:
			// 1. generate code to store the value of the left child in
			// register #leftValueRegNum
			generateExpressionCode(node.getChild(0), leftValueRegNum);
			// 2. generate code to store the value of the right child in
			// register #rightValueRegNum
			generateExpressionCode(node.getChild(1), rightValueRegNum);
			// 3. add an instruction to combine the result of the left
			// child and right child; first find the operator type, and
			// then add the instruction
			InstructionType t = InstructionType.Add;
			switch (node.getLexim()) {
			case "+":
				t = InstructionType.Add;
				break;
			case "-":
				t = InstructionType.Sub;
				break;
			case "*":
				t = InstructionType.Mul;
				break;
			case "/":
				t = InstructionType.Div;
				break;
			case "<=":
				t = InstructionType.SetLE;
				break;
			case ">=":
				t = InstructionType.SetGE;
				break;
			case "==":
				t = InstructionType.SetE;
				break;
			case "!=":
				t = InstructionType.SetNE;
				break;
			case "<":
				t = InstructionType.SetL;
				break;
			case ">":
				t = InstructionType.SetG;
				break;
			}
			Instruction operation = new Instruction(t,
					tmpReg(returnRegNum),
					tmpReg(leftValueRegNum),
					tmpReg(rightValueRegNum));
			code.add(operation);
			break;
		}
		case integer_literal:
		case real_literal: {
			// if the expression is just a constant, directly
			// store its value in the register
			Instruction assign = new Instruction(
					InstructionType.Assign,
					tmpReg(returnRegNum),
					node.getLexim());
			code.add(assign);
			break;
		}
		case identifier: {
			// if the expression is a variable, load its value
			// from memory to the register
			Instruction load = new Instruction(InstructionType.Load,
					address(node),
					tmpReg(returnRegNum));
			code.add(load);
			break;
		}
		case square_bracket: {
			int addressRegNum = returnRegNum + 1;
			int offsetRegNum = returnRegNum + 2;
			// if the expression is an array element, compute
			// its address first and then load the value from
			// memory into the return register
			generateExpressionCode(node.getChild(1),
					offsetRegNum);
			Instruction assign = new Instruction(InstructionType.Assign,
					tmpReg(addressRegNum),
					address(node.getChild(0)));
			Instruction mul = new Instruction(InstructionType.Mul,
					tmpReg(offsetRegNum),
					tmpReg(offsetRegNum),
					size(node.getChild(0)));
			Instruction add = new Instruction(InstructionType.Add,
					tmpReg(addressRegNum),
					tmpReg(addressRegNum),
					tmpReg(offsetRegNum));
			Instruction load = new Instruction(InstructionType.Load,
					tmpReg(addressRegNum),
					tmpReg(returnRegNum));
			code.add(assign);// assign base address address
			code.add(mul);	// multiply offset by element size
			code.add(add);	// increment address
			code.add(load);	// load from memory
			break;
		}
		}
	}
	/*
	 * the following function creates a new label
	 */
	private Instruction newLabel()
	{
		return new Instruction(InstructionType.Label,
				"L" + Integer.toString(labelCounter++));
	}
	/*
	 * I have only used the following function to eliminate pieces of code that
	 * are repeated multiple times. This code just returns a string representing
	 * some type of operands of the instructions
	 */
	private String address(Node id) throws Exception {
		// passed a node representing a variable, return its address
		return Integer.toString(this.Table.lookUp(id.getLexim())
				.getRelativeAddress());
	}
	private String size(Node id) throws Exception {
		// passed a node representing a variable, return its entity size
		return Integer.toString(this.Table.lookUp(id.getLexim())
				.getType().getSize());
	}

	private String tmpReg(final int idx) {
		// return a string representing a temporary register
		return "t" + Integer.toString(idx);
	}
}