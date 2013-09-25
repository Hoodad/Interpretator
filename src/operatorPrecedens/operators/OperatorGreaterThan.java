package operatorPrecedens.operators;

/**
 *
 * @author Robinerd
 */
public class OperatorGreaterThan extends LogicOperator
{
	
	private static OperatorGreaterThan instance = null;

	public static OperatorGreaterThan instance()
	{
		if(instance == null)
			instance = new OperatorGreaterThan();
		return instance;
	}
	
	@Override
	protected boolean calculate(int a, int b)
	{
		return a > b;
	}

	@Override
	protected boolean calculate(float a, float b)
	{
		return a > b;
	}

	@Override
	protected boolean calculate(boolean a, boolean b)
	{
		return (a ? 1 : 0) > (b ? 1 : 0);
	}

	@Override
	protected boolean calculate(double a, double b)
	{
		return a > b;
	}

	
}
