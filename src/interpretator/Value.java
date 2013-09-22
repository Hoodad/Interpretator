/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

/**
 *
 * @author Robinerd
 */
public class Value
{

	private Object value;
	private static Class[] complexity =
	{
		Boolean.class, Integer.class, Float.class, Double.class
	};
	
	public Value(Object value)
	{
//		//special handling for booleans
//		//treat as a byte so that the numerical getAs functions don't fail
//		if (value instanceof Boolean)
//		{
//			if (Boolean.parseBoolean("" + value))
//			{
//				value = (byte) 1;
//			}
//			else
//			{
//				value = (byte) 0;
//			}
//		}

		this.value = value;
	}

	public void set(Value value)
	{
		this.value = value.get();
	}
	
	public void set(Object value)
	{
		this.value = value;
	}

	public Object get()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return "" + value;
	}

	public boolean getAsBoolean()
	{
		
		//if not an actual bool, convert to a double and interpret 0 as false, everything else as true
		double asNumber = 0;
		try
		{
			//if this is a bool, simply convert it
			if(isBoolean())
				return Boolean.parseBoolean(""+value);
			
			asNumber = Double.parseDouble("" + value);
		}
		catch (NumberFormatException ex2)
		{
			System.err.println("Error: Could not convert " + value.getClass().getName() + " to bool.");
		}
		return asNumber != 0;
	}

	public int getAsInt()
	{
		try
		{
			//special case if this is a boolean
			if (isBoolean())
				return booleanAsInt();
			
			return Integer.parseInt("" + value);
		}
		catch (NumberFormatException ex)
		{
			try
			{
				//if direct conversion didn't work, do it via Double
				return (int) Double.parseDouble("" + value);
			}
			catch (NumberFormatException ex2)
			{
				System.err.println("Error: Could not convert " + value.getClass().getName() + " to int.");
				return 0;
			}
		}
	}

	public float getAsFloat()
	{
		try
		{
			//special case if this is a boolean
			if (isBoolean())
				return booleanAsInt();
			
			return Float.parseFloat("" + value);
		}
		catch (NumberFormatException ex)
		{
			System.err.println("Error: Could not convert " + value.getClass().getName() + " to float.");
			return 0;
		}
	}

	public double getAsDouble()
	{
		try
		{
			//special case if this is a boolean
			if (isBoolean())
				return booleanAsInt();
			
			return Double.parseDouble("" + value);
		}
		catch (NumberFormatException ex)
		{
			System.err.println("Error: Could not convert " + value.getClass().getName() + " to double.");
			return 0;
		}
	}

	// help methods for boolean handling ---------------------------------------
	private boolean isBoolean()
	{
		return value.getClass().equals(Boolean.class);
	}

	private int booleanAsInt() throws NumberFormatException
	{
		return Boolean.parseBoolean("" + value) ? 1 : 0;
	}
	//--------------------------------------------------------------------------

	public static Class getMostComplexClass(Value... values)
	{
		Class mostComplex = null;
		for (Class type : complexity)
		{
			for (Value value : values)
			{
				if (type.isInstance(value.get()))
				{
					mostComplex = type;
				}
			}
		}
		return mostComplex;
	}
}
