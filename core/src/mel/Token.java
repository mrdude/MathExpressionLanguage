package mel;

/**
 * @author Warren S
 */
class Token
{
	enum Type { NUMBER, OPERATOR, OPEN_PAREN, CLOSE_PAREN, VARIABLE, FUNCTION, COMMA }

	int col,row; //where did we find this token?

	Type type;

	//NUMBER:
	double num;

	//FUNCTION:
	Func func;

	//VARIABLE:
	String varname;

	//OPERATOR:
	OperatorType optype;

	Token(String str, int col, int row)
	{
		this.col = col;
		this.row = row;

		switch( str.charAt(0) )
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
				try
				{
					num = Double.parseDouble(str);
				}
				catch(NumberFormatException nfe) { num = Double.NaN; } //TODO: deal with this better

				type = Type.NUMBER;
				break;
			case '+':
				optype = OperatorType.ADD;
				type = Type.OPERATOR;
				break;
			case '-':
				optype = OperatorType.SUB;
				type = Type.OPERATOR;
				break;
			case '*':
				optype = OperatorType.MUL;
				type = Type.OPERATOR;
				break;
			case '/':
				optype = OperatorType.DIV;
				type = Type.OPERATOR;
				break;
			case '%':
				optype = OperatorType.MOD;
				type = Type.OPERATOR;
				break;
			case '(':
				type = Type.OPEN_PAREN;
				break;
			case ')':
				type = Type.CLOSE_PAREN;
				break;
			case ',':
				type = Type.COMMA;
				break;
			default:
				//variables start with a lowercase letter; functions start with an uppercase
				if( startsWithUppercase(str) ) //this must be a function
				{
					type = Type.FUNCTION;
					func = Func.fromString(str);
				}
				else
				{
					type = Type.VARIABLE;
					varname = str;
				}
				break;
		}
	}

	/**
	 * Thanks StackOverflow for this function:
	 * http://stackoverflow.com/a/4452968
	 */
	private boolean startsWithUppercase(String s)
	{
		return Character.isUpperCase(s.codePointAt(0));
	}

	public String toString()
	{
		switch( type )
		{
			case NUMBER:
				return "Number: " +num;
			case OPERATOR:
				return "Operator: " +optype.toString();
			case OPEN_PAREN:
				return "Open Paren";
			case CLOSE_PAREN:
				return "Close Paren";
			case VARIABLE:
				return "Var: " +varname;
			case FUNCTION:
				return "Func: " +func.toString();
			case COMMA:
				return "COMMA";
			default:
				return null;
		}
	}
}