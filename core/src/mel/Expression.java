package mel;

import java.io.*;
import java.util.*;

/**
 * Represents a parsed mathematical expression that can be evaluated
 * with {@link #evaluate()}.
 * @author Warren S
 */
public class Expression implements Serializable
{
	private static Map<String, Double> EMPTY_VALUES_MAP = new HashMap<>();
	private static Map<String, Double> CONSTANTS_MAP = new HashMap<>();

	static
	{
		CONSTANTS_MAP.put("pi", Math.PI);
		CONSTANTS_MAP.put("e", Math.E);
	}

	List<Node> nodes = new LinkedList<>();
	String str; //this expression's string form

	/**
	 * Compiles the given expression
	 * @param exp the mathematical expression to compile
	 * @return an Expression object representing the expression
	 * @throws CompilerException if there was an error while compiling the Expression
	 */
	public static Expression parse(String exp) throws CompilerException
	{
		return Compiler.compile(exp);
	}

	/** Creates a blank expression */
	Expression() {}

	/**
	 * Returns the value of the expression.
	 * This method can only be used if the expression has no variables.
	 * For expressions with variables, use {@link #evaluate(java.util.Map)} instead.
	 * @throws MECException if there was a problem evaluating the expression
	 */
	public double evaluate() { return evaluate(EMPTY_VALUES_MAP); }

	/**
	 * Returns the value of the expression
	 * @param values a map of variable names -> variable values for the given expression
	 * @throws MECException if there was a problem evaluating the expression
	 */
	public double evaluate(Map<String,Double> values)
	{
		LinkedList<Double> stack = new LinkedList<>();

		for( Node n : nodes )
		{
			switch( n.type )
			{
				case VALUE:
					stack.addFirst( n.value );
					break;
				case VARIABLE:
					{
						//check the map and the constants map for the value
						Double value = values.get( n.varname );
						if( value == null ) { value = CONSTANTS_MAP.get( n.varname ); }

						//if we are missing the value, throw an exception
						if( value == null ) { throw new MECException("Missing required variable: " +n.varname); }

						//add the value to the stack
						stack.addFirst(value);
					}

					break;
				case OPERATOR:
					if( stack.size() < 2 ) { throw new MECException("Not enough values for operator"); }

					double val1 = stack.removeFirst();
					double val2 = stack.removeFirst();

					switch( n.op )
					{
						case ADD:
							stack.addFirst( val1 + val2 );
							break;
						case SUB:
							stack.addFirst( val1 - val2 );
							break;
						case MUL:
							stack.addFirst( val1 * val2 );
							break;
						case DIV:
							stack.addFirst( val1 / val2 );
							break;
						case MOD:
							stack.addFirst( val1 % val2 );
							break;
						default:
							throw new RuntimeException("Unknown operator");
					}

					break;
				case FUNCTION:
					evalFunction(stack, n.func);
					break;
				default:
					throw new RuntimeException("Unknown node type");
			}
		}

		return stack.removeFirst();
	}

	private void evalFunction(LinkedList<Double> stack, Func func) throws MECException
	{
		//check the number of parameters
		if( stack.size() < func.paramCount() )
		{
			throw new MECException("Function " +func.toString()+ " does not have enough parameters");
		}

		//evaluate the function
		switch( func )
		{
			case SIN:
				{
					double angle = stack.removeFirst();
					stack.addFirst( Math.sin(angle) );
				}

				break;
			case COS:
				{
					double angle = stack.removeFirst();
					stack.addFirst( Math.cos(angle) );
				}

				break;
			case TAN:
				{
					double angle = stack.removeFirst();
					stack.addFirst( Math.tan(angle) );
				}

				break;
			case ASIN:
				{
					double a = stack.removeFirst();
					stack.addFirst( Math.asin(a) );
				}

				break;
			case ACOS:
				{
					double a = stack.removeFirst();
					stack.addFirst( Math.acos(a) );
				}

				break;
			case ATAN:
				{
					double y = stack.removeFirst();
					double x = stack.removeFirst();
					stack.addFirst( Math.atan2(y, x) );
				}

				break;
			case SQRT:
				{
					double value = stack.removeFirst();
					stack.addFirst( Math.sqrt(value) );
				}

				break;
			case ABS:
				{
					double value = stack.removeFirst();
					stack.addFirst( Math.abs(value) );
				}

				break;
		}
	}

	/**
	 * Returns the expression in string form.
	 */
	public String toString() { return str; }

	//these methods are used by Compiler to add nodes to the expression
	void addValueNode(double val)
	{
		Node n = new Node();
		nodes.add(n);

		n.type = NodeType.VALUE;
		n.value = val;
	}

	void addOperatorNode(OperatorType op)
	{
		Node n = new Node();
		nodes.add(n);

		n.type = NodeType.OPERATOR;
		n.op = op;
	}

	void addFunctionNode(Func func)
	{
		Node n = new Node();
		nodes.add(n);

		n.type = NodeType.FUNCTION;
		n.func = func;
	}

	void addVariableNode(String varname)
	{
		Node n = new Node();
		nodes.add(n);

		n.type = NodeType.VARIABLE;
		n.varname = varname;
	}

	/** Represents the type of nodes */
	private enum NodeType { VALUE, OPERATOR, FUNCTION, VARIABLE }

	/**
	 * Represents a single Value, Operator, Function, or Variable in the expression.
	 */
	private static class Node implements Serializable
	{
		private NodeType type;

		//VALUE:
		private double value;

		//OPERATOR:
		private OperatorType op;

		//FUNCTION:
		private Func func;

		//VARIABLE:
		private String varname;

		public String toString()
		{
			switch( type )
			{
				case VALUE:
					return "Value: " +value;
				case OPERATOR:
					return "Operator: " +op.toString();
				case FUNCTION:
					return "Function: " +func.toString();
				case VARIABLE:
					return "Var: " +varname;
				default:
					return null;
			}
		}
	}
}