package operatorPrecedens.operators;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Robinerd
 */
public class OperatorDivide extends ArithmeticOperator
{

	private static OperatorDivide instance = null;

	public static OperatorDivide instance()
	{
		if (instance == null)
			instance = new OperatorDivide();
		return instance;
	}

	@Override
	protected int calculate(int a, int b)
	{
		if (b == 0)
			throw new ArithmeticException("Division by zero");

		return a / b;
	}

	@Override
	protected float calculate(float a, float b)
	{
		if (b == 0)
			throw new ArithmeticException("Division by zero");

		return a / b;
	}

	@Override
	protected int calculate(boolean a, boolean b)
	{
		if(b == false)
			throw new ArithmeticException("Division by zero");
		
		//b == true == 1
		return (a ? 1 : 0);
	}

	@Override
	protected double calculate(double a, double b)
	{
		if (b == 0)
			throw new ArithmeticException("Division by zero");

		return a / b;
	}

}
