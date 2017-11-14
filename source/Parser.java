import java.util.*;

public class Parser {
	// fields
	private ArrayList<Token> tokens; // list of tokens
	private int idx = 0; // index of terminal currently being parsed
	private Token token; // token currently being parsed

	public Node parse(ArrayList<Token> t) throws Exception {
		token = (tokens = t).get(idx = 0);
		return parseProgram();
	}

	// this should return a node representing the root of the whole prgoram
	private Node parseProgram() throws Exception {
		/*
		 * <program> -> <type> <identifier> (<parameters>) ”{” <declaration
		 * list> <compound statement> ”}”
		 */

		Node t = parseType();
		Node id = match(TokenType.identifier); // root
		Node lp = match(TokenType.left_parenthesis);
		Node params = parseParameters();
		Node rp = match(TokenType.right_parenthesis);
		Node lcb = match(TokenType.left_curly_bracket);
		Node dl = parseDeclarationList();
		Node cs = parseCompoundStatement();
		Node rcb = match(TokenType.right_curly_bracket);

		// make sure you have read all tokens
		if (token != null)
			throw new Exception("tokens found after end of program definition");

		id.addChild(t);			// 1st child is type of main program
		id.addChild(params);	// 2nd child is parameters
		id.addChild(dl);		// 3rd child is declaration list
		id.addChild(cs);		// 4th child is statements
		return id;
	}

	private Node parseType() throws Exception {
		switch (token.getType()) {
		case int_keyword:
			return match(TokenType.int_keyword);
		case float_keyword:
			return match(TokenType.float_keyword);
		default:
			throw new Exception(phraseException("type"));
		}
	}

	private Node parseDeclarationList() throws Exception {
		// <declaration list> -> {<declaration>}
		Node ans = parseVariableDeclaration();
		Node last = ans;
		while (token.getType() != TokenType.left_curly_bracket) {
			Node child = parseVariableDeclaration();
			last.addSibling(child);
			last = child;
		}
		return ans;
	}

	private Node parseVariableDeclaration() throws Exception {
		// <variable declaration> -> <type> <identifier> [ ”[” <integer literal
		// > ”]” ] ;
		Node t = parseType();
		Node id = match(TokenType.identifier);
		id.addChild(t); // the root of our return tree will contain the type
		switch (token.getType()) {
		case semicolon: {
			Node sc = match(TokenType.semicolon);
			return id;
		}
		case left_square_bracket: {
			Node lsb = match(TokenType.left_square_bracket);
			Node idx = match(TokenType.integer_literal);
			Node rsb = match(TokenType.right_square_bracket);
			Node sc = match(TokenType.semicolon);
			id.addChild(idx);
			return id;
		}
		default:
			throw new Exception("can not parse variable");
		}
	}

	private Node parseParameters() throws Exception {
		// <parameters> -> <parameter list> | void
		if (token.getType() == TokenType.void_keyword)
			return match(TokenType.void_keyword);
		else
			return parseParameterList();
	}

	private Node parseParameterList() throws Exception {
		// <parameter list > -> <parameter> {, <parameter> }
		Node ans = parseParameter();
		Node last = ans;
		while (token.getType() == TokenType.comma) {
			Node c = match(TokenType.comma);
			Node child = parseParameter();
			last.addSibling(child);
			last = child;
		}

		return ans;
	}

	private Node parseParameter() throws Exception {
		// <parameter> -> <type> <identifier> [ ”[ ]” ]
		Node t = parseType();
		Node id = match(TokenType.identifier);
		id.addChild(t);
		if (token.getType() == TokenType.left_square_bracket) {
			Node lsb = match(TokenType.left_square_bracket);
			Node rsb = match(TokenType.right_square_bracket);
			id.addChild(lsb);
			id.addChild(rsb);
		}
		return id;
	}

	private Node parseCompoundStatement() throws Exception {
		// <compound statement> -> ”{” <statement list> ”}”
		Node lcb = match(TokenType.left_curly_bracket);
		Node ans = parseStatement();
		Node last = ans;
		while (token.getType() != TokenType.right_curly_bracket) {
			Node sibling = parseStatement();
			last.addSibling(sibling);
			last = sibling;
			while(last.hasSibling())
				last = last.getSibling();
		}
		Node rcb = match(TokenType.right_curly_bracket);

		return ans;
	}

	private Node parseStatement() throws Exception {
		/*
		 * <statement> -> <assignment statement> | <compound statement> |
		 * <selection statement> | <iteration statement>
		 */
		switch (token.getType()) {
		case identifier:
			return parseAssignmentStatement();
		case if_keyword:
			return parseSelectionStatement();
		case while_keyword:
			return parseIterationStatement();
		case left_curly_bracket:
			return parseCompoundStatement();
		default:
			throw new Exception(phraseException("statement"));
		}
	}

	private Node parseSelectionStatement() throws Exception {
		/*
		 * <selection statement> -> if (<expression> ) <statement> [ else
		 * <statement> ]
		 */
		Node ans = match(TokenType.if_keyword);
		Node lp = match(TokenType.left_parenthesis);
		Node exp = parseExpression(); // left child is the condition
		Node rp = match(TokenType.right_parenthesis);
		Node stmt = parseStatement(); // right child is the statement

		ans.addChild(exp);
		ans.addChild(stmt);
		if (token.getType() == TokenType.else_keyword) {
			Node ek = match(TokenType.else_keyword);
			Node stmt2 = parseStatement();
			ans.addChild(stmt2);
		}

		return ans;
	}

	private Node parseIterationStatement() throws Exception {
		// <iteration statement> -> while( <expression> ) <statement>
		Node wk = match(TokenType.while_keyword);
		Node lp = match(TokenType.left_parenthesis);
		Node exp = parseExpression();
		Node rp = match(TokenType.right_parenthesis);
		Node st = parseStatement();
		wk.addChild(exp);
		wk.addChild(st);
		return wk;
	}

	private Node parseAssignmentStatement() throws Exception {
		// <assignment statement> -> <variable> = <expression>
		Node var = parseVariable();
		Node asso = match(TokenType.assignment_operator);
		Node exp = parseExpression();
		Node sc = match(TokenType.semicolon);
		asso.addChild(var);
		asso.addChild(exp);
		return asso;
	}

	private Node parseVariable() throws Exception {
		// <variable> -> <identifier> [ ”[” <expression> ”]” ]
		Node ans = match(TokenType.identifier);
		if (token.getType() == TokenType.left_square_bracket) {
			Node lsb = match(TokenType.left_square_bracket);
			Node id = ans;
			Node idx = match(TokenType.integer_literal);
			Node rsb = match(TokenType.right_square_bracket);
			ans = new Node(TokenType.square_bracket, lsb.getLexim()
					+ rsb.getLexim(), id.getDeclarationLineNumber());
			ans.addChild(id);
			ans.addChild(idx);
		}

		return ans;
	}

	private Node parseExpression() throws Exception {
		/*
		 * <expression> -> <addition expression> { <relational operator>
		 * <addition expression> }
		 */
		Node ans = parseAdditionExpression();
		while (token.getType() == TokenType.relational_operator) {
			Node relop = match(TokenType.relational_operator);
			Node left = ans;
			ans = relop;
			Node right = parseAdditionExpression();
			ans.addChild(left);
			ans.addChild(right);
		}
		return ans;
	}

	// the following procedure correctly implements left-associativity :D
	private Node parseAdditionExpression() throws Exception {
		// <addition expression> -> <term> { <addition operator> <term> }
		Node ans = parseTerm();
		while (token.getType() == TokenType.addition_operator) {
			Node addop = match(TokenType.addition_operator);
			Node left = ans; // previous term becomes left child
			ans = addop; // root of expression becomes the addition operator
			Node right = parseTerm(); // get the right child
			ans.addChild(left);
			ans.addChild(right);
		}
		return ans;
	}

	/*
	 * // a right-associative procedure would look something like: private
	 * static Node praseAdditionExpression() { Node ans = parseTerm(); Node op =
	 * match(TokenType.addition_operator, true); if (op == null) return
	 * ans; Node left = ans; Node right = parseAdditionExpression();
	 * op.addChild(left); op.addChild(right); return op; }
	 */

	private Node parseTerm() throws Exception {
		// <term> -> <factor> { <multiplication operator> <factor> }
		Node ans = parseFactor();
		while (token.getType() == TokenType.multiplication_operator) {
			Node mulop = match(TokenType.multiplication_operator);
			Node left = ans; // previous factor is now the left child!
			ans = mulop; // root of the term is now the multiplication operator
			ans.addChild(left);
			Node right = parseFactor();
			ans.addChild(right);
		}
		return ans;
	}

	private Node parseFactor() throws Exception {
		// <factor> -> ( <expression> ) | <variable> | <integer>
		switch (token.getType()) {
		case identifier:
			return parseVariable();
		case left_parenthesis: {
			Node lp = match(TokenType.left_parenthesis);
			Node e = parseExpression();
			Node rp = match(TokenType.right_parenthesis);
			return e;
		}
		case real_literal:
			return match(TokenType.real_literal);
		case integer_literal:
			return match(TokenType.integer_literal);
		default:
			throw new Exception(phraseException("factor"));
		}
	}

	private Node match(TokenType intendedType) throws Exception {
		if (token != null && token.getType().equals(intendedType)) {
			Node ans = new Node(token);
			++idx;
			token = idx >= tokens.size() ? null : tokens.get(idx);
			return ans;
		} else
			throw new Exception(phraseException(intendedType.name()));
	}

	private String phraseException(String expected) {
		return "Error at token #"
				+ Integer.toBinaryString(idx)
				+ " in line #"
				+ Integer.toString(token == null ? tokens
						.get(tokens.size() - 1).getDeclarationLineNumber()
						: token.getDeclarationLineNumber()) + ": expected "
				+ expected + "; found "
				+ (token == null ? "EOF" : token.getType()) + "("
				+ token.getLexim() + ");";
	}
}
