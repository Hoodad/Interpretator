package operatorPrecedens.operators;

/**
 *
 * @author Robinerd
 */
public class OperatorAdd extends ArithmeticOperator
{
	
	private static OperatorAdd instance = null;

	public static OperatorAdd instance()
	{
		if(instance == null)
			instance = new OperatorAdd();
		return instance;
	}
	
	@Override
	protected int calculate(int a, int b)
	{
		return a + b;
	}

	@Override
	protected float calculate(float a, float b)
	{
		return a + b;
	}

	@Override
	protected int calculate(boolean a, boolean b)
	{
		return (a ? 1 : 0) + (b ? 1 : 0);
	}

	@Override
	protected double calculate(double a, double b)
	{
		return a + b;
	}

	
}
