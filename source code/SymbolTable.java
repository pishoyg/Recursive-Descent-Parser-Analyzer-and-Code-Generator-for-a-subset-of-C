import java.util.HashMap;

public class SymbolTable {
	private HashMap<VariableName, Variable> Table
		= new HashMap<VariableName, Variable>();
	private int DataSize = 0;

	private final static String PrintFormat = "%-10s%-10s%-10s%-10s%-10s%-10s";

	/*
	 * The constructor of the symbol table is passed a node of the syntax
	 * representing the declaration list
	 * 
	 * The constructor stores all variables in the hash table, so they will be
	 * available for further retrieval
	 */
	SymbolTable(Node declarationList) throws Exception {
		for (Node declaration = declarationList; declaration != null;
				declaration = declaration.getSibling()) {
			this.declare(declaration,
					declaration.getChild(0),
					declaration.getChildrenCount() == 2 ?
							declaration.getChild(1)
							: null);
		}
	}

	/*
	 * return a variable defined by this name
	 */
	public Variable lookUp(String name) throws Exception {
		if (!this.isDefined(name))
			throw new Exception("Undefined variable: " + name);
		else return this.Table.get(new VariableName(name));
	}

	/*
	 * tell whether or not the symbol table contains a variable with this name
	 */
	public boolean isDefined(String name) {
		return this.Table.containsKey(new VariableName(name));
	}

	/*
	 * print the content of the symbol table in a readable format
	 */
	public void printContent() {
		System.out.println(String.format(PrintFormat, "line", "address",
				"type", "name", "category", "array size"));
		for (Variable v : this.Table.values())
			System.out.println(
				String.format(PrintFormat,
					v.getDeclarationLineNumber(),
					v.getRelativeAddress(),
					v.getType(),
					v.getName(),
					v.getCategory(),
					v.getArraySize()));
	}

	private void declare(Node identifier, Node type, Node arraySize)
			throws Exception {
		String name = identifier.getLexim();
		if (this.isDefined(name)) {
			throw new Exception(
				String.format(
					"redefinition of %s in line %d;" +
					"already defined in line %d;",
					name, identifier.getDeclarationLineNumber(),
					this.Table.get(new VariableName(name))
					.getDeclarationLineNumber()));
		} else {
			Variable v = new Variable(DataSize, identifier, type, arraySize);
			this.Table.put(new VariableName(name), v);
			DataSize += v.getSize();
		}
	}
}