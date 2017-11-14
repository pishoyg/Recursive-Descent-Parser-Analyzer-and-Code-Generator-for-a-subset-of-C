import java.io.*;
import java.util.*;

public class Main
{
	private static ArrayList<Token> scan(String[] ScannerCommand) throws Exception
	{
		String ScannerPath = ScannerCommand[0];
		String ScannerInputFilePath = ScannerCommand[1];
		String ScannerOutputFilePath = ScannerCommand[2];
		String Split = "->";

		// make a system call to run the scanner
		Process p = Runtime.getRuntime().exec(
				ScannerPath + " " + ScannerInputFilePath + " "
						+ ScannerOutputFilePath);
		p.waitFor();
		// read the tokens (output of scanner)
		Scanner sc = new Scanner(new File(ScannerOutputFilePath));
		ArrayList<Token> ans = new ArrayList<Token>();
		while (sc.hasNext()) {
			String temp = sc.nextLine();
			if (!temp.isEmpty()) {
				String[] line = temp.split(Split);
				Token token = new Token(TokenType.getType(line[1]), line[2],
						Integer.parseInt(line[0]));
				if (token.getType() == TokenType.error)
					throw new Exception("Scanne error: unrecognized token: "
							+ token.getLexim());
				ans.add(token);
			}
		}
		return ans;

	}

	private static void printTokens(ArrayList<Token> tokens) {
		Integer counter = new Integer(0);
		for (Token t : tokens)
			System.out.println(String.format("%-2d: %-10s%-10s", counter++,
					t.getLexim(), t.getType()));
	}

	public static void main(String[] args)
	{
		/*
		 * the command to call the scanner should be provided in args
		 * args[0] should contain the scanner command. In my case, "scanner.out"
		 * args[1] should contain the input file path. In my case, "input.c"
		 * args[2] should contain the output file path. In my case, "output.txt"
		 */
		try
		{
			ArrayList<Token> tokens = scan(args);
			printTokens(tokens);
			Parser p = new Parser();
			// call the parser to generate the syntax tree
			Node syntaxTree = p.parse(tokens);
			// print the tree representation using indentation
			syntaxTree.DFS();
			// traverse the tree in preorder
			syntaxTree.preorderTraverse();
			System.out.println();
			
			Node declarationList = syntaxTree.getChild(2);
			Node statementList = syntaxTree.getChild(3);
			
			SymbolTable table = new SymbolTable(declarationList);
			table.printContent();

			SemanticAnalyzer sa = new SemanticAnalyzer(table, statementList);
			sa.checkTypeErrors();
			CodeGenerator cg = new CodeGenerator(table, statementList);
			
			ArrayList<Instruction> code = cg.generateCode();
			for (Instruction i : code)
				i.print();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
