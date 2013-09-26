/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

import operatorPrecedens.operators.OperatorAdd;
import operatorPrecedens.operators.OperatorDivide;

/**
 *
 * @author Robin
 */
public class Main
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		BlockManager.loadBlocksFromFile("multiparam.p1");
		Interpretator.interpretBlock(BlockManager.getBaseBlock());
		System.out.println("(: Finished :)");
		//testDivision();
		//testVariableComplexity();
		//testValueConversion();
	}

	private static void testDivision()
	{
		Object[][] tests = 
		{
			{10, 3}, //integer division
			{10.0f, 3},
			{5.0f, 0}, //division by zero
			{10, 3.333333333333333},
			{10.00000000001111, 3.333333333333333},
			{true, 3.0f},
			{3, false}, //division by zero
			{true, false} //division by zero
		};
		
		for(Object[] test : tests)
		{
			Value a = new Value(test[0]);
			Value b = new Value(test[1]);
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException ex)
			{
			}
			System.out.println(a+" / "+b+" = "+OperatorDivide.instance().calculate(a, b));
		}
	}
	
	private static void testVariableComplexity()
	{
		Object[] inputValues =
		{
			true, 24, 6.254f, 0.8742345871056108465
		};

		for (Object inputA : inputValues)
		{
			for (Object inputB : inputValues)
			{
				Value a = new Value(inputA);
				Value b = new Value(inputB);
				System.out.println(a+" + "+b+" = "+OperatorAdd.instance().calculate(a, b));
			}
		}
	}

	private static void testValueConversion()
	{
		Object[] inputValues =
		{
			true, 24, 6.254f, 0.8742345871056108465
		};
		for (Object inputValue : inputValues)
		{
			Value value = new Value(inputValue);
			System.out.println("Input value: " + inputValue);
			System.out.println("Stored type: " + value.get().getClass().getName());
			System.out.println("as Boolean:  " + value.getAsBoolean());
			System.out.println("as Integer:  " + value.getAsInt());
			System.out.println("as Float:    " + value.getAsFloat());
			System.out.println("as Double:   " + value.getAsDouble());
			System.out.println("");
		}
	}
}
