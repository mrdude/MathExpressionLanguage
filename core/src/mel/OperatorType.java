package mel;

/**
 * @author Warren S
 */
//All operators are left associative
enum OperatorType
{
	ADD, SUB,
	MUL, DIV,
	MOD;

	public int precedence()
	{
		switch( this )
		{
			case ADD:
			case SUB:
				return 0;
			case MUL:
			case DIV:
			case MOD:
				return 1;
			default:
				return 0;
		}
	}
}