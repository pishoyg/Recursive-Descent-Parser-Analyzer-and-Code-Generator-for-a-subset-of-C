public class Instruction {
	/*
	 * This class represents a single instruction in the three-address code
	 * generated by the code generator It contains a type and up to 3 operands
	 */
	private InstructionType Type;
	private String Operand[] = new String[3];

	/*
	 * constructor is passed the instruction type, and the operands
	 */
	Instruction(InstructionType type, String op0) {
		this(type, op0, null, null);
	}

	Instruction(InstructionType type, String op0, String op1) {
		this(type, op0, op1, null);
	}

	Instruction(InstructionType type, String op0, String op1, String op2) {
		this.Type = type;
		this.Operand[0] = op0;
		this.Operand[1] = op1;
		this.Operand[2] = op2;
	}

	public InstructionType getType() {
		return this.Type;
	}
	public String getOperand(final int idx)
	{
		return this.Operand[idx];
	}
	/*
	 * print the instruction on a line in a readable format
	 */
	public void print() {
		if (this.getType() == InstructionType.Label) {
			System.out.println(this.Operand[0] + ":");
		} else {
			System.out.print(String.format("%-10s", this.Type.name()));
			System.out.print(String.format("%-5s", this.Operand[0]));
			if (this.Operand[1] != null)
				System.out.print(String.format("%-5s", this.Operand[1]));
			if (this.Operand[2] != null)
				System.out.print(String.format("%-5s", this.Operand[2]));
			System.out.println();
		}
	}
}
