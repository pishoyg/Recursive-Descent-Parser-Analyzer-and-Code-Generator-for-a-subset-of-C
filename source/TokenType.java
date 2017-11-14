import java.util.HashMap;

enum TokenType
{
	integer_literal,
	real_literal,

	addition_operator,
	multiplication_operator,
	relational_operator,
	assignment_operator,

	left_parenthesis,
	right_parenthesis,
	parenthesis,
	left_curly_bracket,
	right_curly_bracket,
	curly_bracket,
	left_square_bracket,
	right_square_bracket,
	square_bracket,
	
	semicolon,
	comma,

	if_keyword,
	else_keyword,
	while_keyword,
	return_keyword,
	
	void_keyword,
	int_keyword,
	float_keyword,
	
	identifier,
	
	error;
	
	// a map from a token type string to an enum
	private static HashMap<String, TokenType> GetType = new HashMap<String, TokenType> ();	
	static
	{
		for (TokenType t : TokenType.values())
			GetType.put(t.name(), t);
	}
	
	public static TokenType getType(String s)
	{
		return GetType.get(s);
	}
}

