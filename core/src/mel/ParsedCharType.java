package mel;

/**
 * Is used by the Lexer
 * @author Warren S
 */
enum ParsedCharType
{
	LETTER,
	NUMBER,
	OPERATOR,
	PARENTHESIS,
	WHITESPACE,
	COMMA;

	/** Returns true if this parsed char type will only take up one char */
	public boolean isSingleChar()
	{
		switch(this)
		{
			case OPERATOR:
			case PARENTHESIS:
			case COMMA:
				return true;
			default:
				return false;
		}
	}
}