import java.util.ArrayList;

// this class represents a node of the syntax tree!
class Node
{
	// fields
	private Token T;
	private ArrayList<Node> Children = null;
	Node Sibling = null;
	// static fields
	static private StringBuilder indent = new StringBuilder("  ");
	
	// constructors
	public Node(Token t)
	{
		T = t;
	}
	public Node(TokenType type, String lexim, int declarationLineNumber)
	{
		this(new Token(type, lexim, declarationLineNumber));
	}
	// getters
	public String getLexim()
	{
		return this.T.getLexim();
	}
	public int getDeclarationLineNumber()
	{
		return this.T.getDeclarationLineNumber();
	}
	public TokenType getType()
	{
		return this.T.getType();
	}
	public Node getSibling()
	{
		return this.Sibling;
	}
	public Node getChild(final int idx)
	{
		return this.Children.get(idx);
	}
	public boolean hasSibling()
	{
		return this.Sibling != null;
	}
	public boolean hasChildren()
	{
		return this.Children != null;
	}
	public int getChildrenCount()
	{
		return this.Children.size();
	}
	// methods
	public void addChild(Node c)
	{
		// add a child node to this node
		if (this.Children == null)
			this.Children = new ArrayList<Node>();
		Children.add(c);
	}
	public void addSibling(Node s) throws Exception
	{
		if (this.Sibling != null)
			throw new Exception("Node already has a sibling");
		else this.Sibling = s;
	}
	public void preorderTraverse()
	{
		// print tree representation using indentation
		System.out.print(this.getLexim());
		if (this.Children != null)
		{
			for (int i = 0; i < this.Children.size(); ++i)
			{
				System.out.print(i == 0 ? '(' : ',');
				this.Children.get(i).preorderTraverse();
			}
			System.out.print(')');
		}
		if (this.Sibling != null)
		{
			System.out.print(',');
			this.Sibling.preorderTraverse();
		}
	}
		
	public void DFS()
	{
		// print tree representation using indentation
		System.out.println(indent + this.getLexim());
		indent.append("  ");
		if (this.Children != null)
		for (Node child : this.Children)
			child.DFS();
		indent.deleteCharAt(indent.length() - 1);
		indent.deleteCharAt(indent.length() - 1);
		if (this.Sibling != null)
			this.Sibling.DFS();
	}
}