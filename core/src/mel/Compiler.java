package mel;

import java.util.LinkedList;
import java.util.List;

/**
 * Compiles expressions into reverse-polish notation.
 * @author Warren S
 */
//TODO: Finish semantic checker 2
class Compiler
{
	static Expression compile(String exp) throws CompilerException
	{
		//run the 1st pass of the semantic checker
		semanticChecker1(exp.toCharArray());

		//run the lexer to convert the expression to a list of Tokens
		List<Token> tokenList = lexer(exp);

		//run the 2nd pass of the semantic checker
		semanticChecker2(tokenList);

		//run the parser
		Expression e = new Expression();
		e.str = exp;
		parser(e, tokenList);

		return e;
	}

	private static void error(String desc) throws CompilerException { throw new CompilerException(desc); }
	private static void error(String desc, int col, int row) throws CompilerException { throw new CompilerException(desc+ " @ col = " +col+ ", row = " +row); }
	
	private Compiler() {}

	//Semantic Checker (First pass)
	private static void semanticChecker1(char[] exp) throws CompilerException
	{
		//make sure that there are no mismatched parenthesis
		int depth = 0;

		for( char ch : exp )
		{
			switch( ch )
			{
				case '(':
					depth++;
					break;
				case ')':
					depth--;
					break;
			}
		}

		if( depth != 0 ) { error("Mismatched parenthesis"); }
	}
	
	//Lexer
	private static List<Token> lexer(String exp) throws CompilerException
	{
		int col = 1;
		int row = 1;

		List<Token> tokenList = new LinkedList<>();

		int tokenStart = 0;
		int tokenStartCol = 0, tokenStartRow = 0;
		ParsedCharType tokenType = getType( exp.charAt(0) );

		for( int x=1; x<exp.length(); x++ )
		{
			ParsedCharType type = getType( exp.charAt(x) );

			if( type != tokenType || tokenType.isSingleChar() )
			{
				if( tokenType != ParsedCharType.WHITESPACE )
				{
					Token t = new Token(exp.substring(tokenStart, x), tokenStartCol, tokenStartRow );
					tokenList.add(t);
				}

				tokenStart = x;
				tokenStartCol = col;
				tokenStartRow = row;
				tokenType = type;
			}

			//update column and row
			col++;
			if( exp.charAt(x) == '\n' )
			{
				row++;
				col = 1;
			}
		}

		Token t = new Token(exp.substring(tokenStart, exp.length()), tokenStartCol, tokenStartRow );
		tokenList.add(t);

		return tokenList;
	}

	private static ParsedCharType getType(char ch)
	{
		switch( ch )
		{
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '0':
			case '.':
				return ParsedCharType.NUMBER;
			case '+':
			case '-':
			case '*':
			case '/':
			case '%':
				return ParsedCharType.OPERATOR;
			case '(':
			case ')':
				return ParsedCharType.PARENTHESIS;
			case '\t':
			case ' ':
			case '\n':
				return ParsedCharType.WHITESPACE;
			case ',':
				return ParsedCharType.COMMA;
			default:
				return ParsedCharType.LETTER;
		}
	}

	//Semantic Checker (Second Pass)
	private static void semanticChecker2(List<Token> tokenList) throws CompilerException
	{
		//check for two operators next to each other
		for( int x=1; x<tokenList.size(); x++ )
		{
			Token prevToken = tokenList.get(x-1);
			Token curToken = tokenList.get(x);
			if( curToken.type == Token.Type.OPERATOR && prevToken.type == curToken.type )
			{
				error("Two operators can't be next to each other", tokenList.get(x).col, tokenList.get(x).row);
			}
		}

		//check for empty parenthesis
		for( int x=1; x<tokenList.size(); x++ )
		{
			Token prevToken = tokenList.get(x-1);
			Token curToken = tokenList.get(x);
			if( prevToken.type == Token.Type.OPEN_PAREN && curToken.type == Token.Type.CLOSE_PAREN )
			{
				error("Empty parenthesis", prevToken.col, prevToken.row);
			}
		}

		//make sure that all functions are valid
		for( Token t : tokenList )
		{
			if( t.type == Token.Type.FUNCTION )
			{
				if( t.func == null )
				{
					error("Unknown function", t.col, t.row);
				}
			}
		}

		//make sure that all commas are within a function's parenthesis
		//TODO

		//make sure that all functions have the correct amount of parameters
		//TODO
	}

	//Parser
	/**
	 * Uses the Shunting-Yard Algorithm to parse the token list into an Expression object.
	 * I basically copied the pseudocode from Wikipedia: https://en.wikipedia.org/wiki/Shunting_yard_algorithm
	 */
	private static void parser(Expression exp, List<Token> list) throws CompilerException
	{
		LIFOQueue<Token> stack = new LIFOQueue<>();

		for( Token t : list )
		{
			switch( t.type )
			{
				case NUMBER:
				case VARIABLE:
					addNode(exp, t);
					break;
				case FUNCTION:
					stack.push(t);
					break;
				case COMMA:
					while( !stack.isEmpty() && stack.peek().type != Token.Type.OPEN_PAREN )
					{
						addNode( exp, stack.pop() );
					}

					break;
				case OPERATOR:
					while( !stack.isEmpty() && stack.peek().type == Token.Type.OPERATOR )
					{
						if( t.optype.precedence() <= stack.peek().optype.precedence() )
						{
							addNode( exp, stack.pop() );
						}
						else
						{
							break;
						}
					}

					stack.push(t);

					break;
				case OPEN_PAREN:
					stack.push(t);
					break;
				case CLOSE_PAREN:
					while( !stack.isEmpty() && stack.peek().type != Token.Type.OPEN_PAREN )
					{
						addNode( exp, stack.pop() );
					}

					stack.pop(); //pop the OPEN_PAREN off the stack

					//if the token at the top of the stack is currently a function, pop it onto the output queue
					if( !stack.isEmpty() && stack.peek().type == Token.Type.FUNCTION )
					{
						addNode(exp, stack.pop());
					}
					break;
			}
		}

		//pop all tokens on the stack into the Expression.
		//NOTE: All of these tokens should be an operator
		while( !stack.isEmpty() )
		{
			if( stack.peek().type != Token.Type.OPERATOR ) { System.err.println("Wtf?"); }
			addNode(exp, stack.pop());
		}
	}

	private static void addNode(Expression exp, Token t) throws CompilerException
	{
		switch( t.type )
		{
			case NUMBER:
				exp.addValueNode(t.num);
				break;
			case OPERATOR:
				exp.addOperatorNode(t.optype);
				break;
			case FUNCTION:
				exp.addFunctionNode(t.func);
				break;
			case VARIABLE:
				exp.addVariableNode(t.varname);
				break;
			default:
				error("Misplaced token", t.col, t.row);
		}
	}
}