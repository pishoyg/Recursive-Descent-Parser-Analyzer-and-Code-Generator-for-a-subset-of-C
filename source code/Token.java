// this class represents a terminal

class Token
{
	// fields
	private TokenType Type;
	private String Lexim;
	private int DeclarationLineNumber;
	
	// constructors
	Token(TokenType type, String lexim, int declarationLineNumber)
	{
		this.Type = type;
		this.Lexim = lexim;
		this.DeclarationLineNumber = declarationLineNumber;
	}
	
	// getters
	public TokenType getType()
	{
		return this.Type;
	}
	public String getLexim()
	{
		return this.Lexim;
	}
	public int getDeclarationLineNumber()
	{
		return this.DeclarationLineNumber;
	}
}
