package mel;

/**
 * @author Warren S
 */
enum Func
{
	SIN(1), COS(1), TAN(1),
	ASIN(1), ACOS(1), ATAN(2),
	SQRT(1),
	ABS(1);

	private Func(int paramCount) { this.paramCount = paramCount; }

	private int paramCount;

	public String toString()
	{
		String begin = name().substring(0,1);
		String end = name().substring(1);

		return begin.toUpperCase()+end.toLowerCase();
	}

	public static Func fromString(String str)
	{
		for( Func f : values() )
		{
			if( f.toString().equals(str) )
			{
				return f;
			}
		}

		return null;
	}

	/** Returns the number of parameters needed by each function */
	public int paramCount() { return paramCount; }
}
