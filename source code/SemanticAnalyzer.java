public class SemanticAnalyzer {
	SymbolTable Table;
	Node StatementList;

	/*
	 * The constructor of the semantic analyzer is passed a reference to the
	 * symbol table and one to the node representing the statement list of the
	 * syntax tree generated by the parse
	 */
	public SemanticAnalyzer(SymbolTable table, Node statementList) {
		this.Table = table;
		this.StatementList = statementList;
	}

	/*
	 * check if there are any semantic errors
	 */
	public void checkTypeErrors() throws Exception {
		checkStatement(this.StatementList);
	}

	/*
	 * The following function loops over the statements in the statement list
	 * passed, and checks for semantic errors
	 */
	private void checkStatement(Node statement) throws Exception {
		for (; statement != null; statement = statement.getSibling()) {
			switch (statement.getType()) {
			case if_keyword: {
				getExpressionType(statement.getChild(0));
				checkStatement(statement.getChild(1));
				if (statement.getChildrenCount() == 3)
					checkStatement(statement.getChild(2));
				break;
			}
			case while_keyword: {
				getExpressionType(statement.getChild(0));
				checkStatement(statement.getChild(1));
				break;
			}
			case assignment_operator: {
				// check for any type mismatch
				getExpressionType(statement);
				break;
			}
			}
		}
	}

	/*
	 * the following function is passed an expression and returns its type
	 * 
	 * It checks for the following errors:
	 * 
	 * 1. mismatching of the types of the two operands of any operator
	 * (assignment, addition, multiplication, relational)
	 * 
	 * 2. use of any undeclared variables
	 */
	private DataType getExpressionType(Node node) throws Exception {
		switch (node.getType()) {
		case integer_literal:
			return DataType.integer;
		case real_literal:
			return DataType.real;
		case assignment_operator:
		case addition_operator:
		case relational_operator:
		case multiplication_operator: {
			DataType leftType = getExpressionType(node.getChild(0));
			DataType rightType = getExpressionType(node.getChild(1));
			if (leftType != rightType)
				throw new Exception(
					"type mismatch between operands of "
					+ node.getLexim() + " on line "
					+ Integer.toString(
						node.getDeclarationLineNumber()));
			else
				return leftType;
		}
		case identifier:
			return this.Table.lookUp(node.getLexim()).getType();
		case square_bracket:
			return getExpressionType(node.getChild(0));
		default:
			throw new Exception("unexpected token " + node.getLexim());
		}
	}
}
