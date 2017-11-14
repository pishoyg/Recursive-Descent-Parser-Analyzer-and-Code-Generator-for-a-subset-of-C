public class Variable {
	// fields
	private String Name;
	private DataType Type;
	private int DeclarationLineNumber;

	private VariableCategory Category;
	private int ArraySize;
	private int RelativeAddress;

	// constructors
	Variable(int relativeAddress, Node identifier, Node type, Node arraySize)
			throws Exception {
		// set name
		this.Name = identifier.getLexim();
		// set declaration line number
		this.DeclarationLineNumber = identifier.getDeclarationLineNumber();
		// set type
		switch (type.getType()) {
		case int_keyword:
			this.Type = DataType.integer;
			break;
		case float_keyword:
			this.Type = DataType.real;
			break;
		default:
			throw new Exception("unrecognized type token: " + type.getLexim());
		}
		// set relative address
		this.RelativeAddress = relativeAddress;
		// set category and array size
		if (arraySize == null) {
			this.Category = VariableCategory.simple;
			this.ArraySize = 0;
		} else {
			this.Category = VariableCategory.array;
			this.ArraySize = Integer.parseInt(arraySize.getLexim());
		}
	}

	// getters
	public String getName() {
		return this.Name;
	}

	public boolean isArray() {
		return this.getCategory() == VariableCategory.array;
	}

	public VariableCategory getCategory() {
		return this.Category;
	}

	public DataType getType() {
		return this.Type;
	}

	public int getArraySize() {
		return this.ArraySize;
	}

	public int getRelativeAddress() {
		return this.RelativeAddress;
	}

	public int getDeclarationLineNumber() {
		return this.DeclarationLineNumber;
	}
	public int getSize() throws Exception {
		return
			this.getType().getSize()
			* (this.isArray() ? this.getArraySize() : 1);
	}
}
