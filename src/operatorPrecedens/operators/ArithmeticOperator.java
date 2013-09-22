/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package operatorPrecedens.operators;

import interpretator.Value;

/**
 *
 * @author Robinerd
 */
public abstract class ArithmeticOperator
{

	protected abstract int calculate(boolean a, boolean b); // return int since this is a numerical operator, not logical

	protected abstract int calculate(int a, int b);

	protected abstract float calculate(float a, float b);

	protected abstract double calculate(double a, double b);

	public Value calculate(Value a, Value b)
	{
		try
		{
			Value result = null;
			Class newType = Value.getMostComplexClass(a, b);
			if (newType.equals(Integer.class))
			{
				result = new Value(calculate(a.getAsInt(), b.getAsInt()));
			}
			else if (newType.equals(Float.class))
			{
				result = new Value(calculate(a.getAsFloat(), b.getAsFloat()));
			}
			else if (newType.equals(Double.class))
			{
				result = new Value(calculate(a.getAsDouble(), b.getAsDouble()));
			}
			else if (newType.equals(Boolean.class))
			{
				result = new Value(calculate(a.getAsBoolean(), b.getAsBoolean()));
			}
			else
			{
				System.err.println("Error: unsupported value type: " + newType);
			}
			return result;
		}
		catch(ArithmeticException ex)
		{
			System.err.println("Error: Arithmetic exception: "+ex.getLocalizedMessage());
			return new Value(0);
		}
	}
}
