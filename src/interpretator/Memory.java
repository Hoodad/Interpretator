/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

import java.util.HashMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Robinerd
 */
public class Memory
{

	public static final int DYNAMIC_MEMORY_INHERITANCE	= 0;
	public static final int STATIC_MEMORY_INHERITANCE	= 1;
	//choose how variables are inherited, from static or dynamic father
	public static final int MEMORY_INHERITANCE = STATIC_MEMORY_INHERITANCE;
	
	private Value[] symbolValues;
	private Memory staticFather;
	private Memory dynamicFather;
	HashMap<Integer, Integer> symbolValueIndexMap;
	private int returnValueIndex = -1;

	public Memory(Symbol[] symbols, Memory staticFather, Memory dynamicFather, int size)
	{
		this.staticFather = staticFather;
		this.dynamicFather = dynamicFather;

		symbolValues = new Value[size];
		symbolValueIndexMap = new HashMap<Integer, Integer>(symbols.length);

		try
		{
			int memoryI = 0;
			for (int i = 0; i < symbols.length; i++)
			{
				switch(symbols[i].getKind())
				{
					case Symbol.KIND_FUNC:
						Interpretator.print("Function declaration found: "+symbols[i].getName());
						break;
					
					case Symbol.KIND_SIMPLE:
						Interpretator.print("Simple declaration found: "+symbols[i].getName());
						symbolValues[memoryI] = new Value(0);
						symbolValueIndexMap.put(symbols[i].getId(), memoryI);
						memoryI++;
						break;
						
					case Symbol.KIND_ARRAY:
						Interpretator.printError("Array declaration is not supported");
						throw new NotImplementedException();
						
					case Symbol.KIND_FUNCVAL:
						Interpretator.print("Function return value found: "+symbols[i].getName());
						symbolValues[memoryI] = new Value(0);
						symbolValueIndexMap.put(symbols[i].getId(), memoryI);
						returnValueIndex = memoryI;
						memoryI++;
						
						break;
					case Symbol.KIND_OBJECT:
						Interpretator.printError("Object declaration is not supported");
						throw new NotImplementedException();
						
					case Symbol.KIND_REFERENCE:
						Interpretator.printError("Reference declaration is not supported");
						throw new NotImplementedException();
					default:
						throw new IllegalArgumentException("Invalid declaration kind");
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			Interpretator.printError("Error: all symbols did not fit in memory block");
		}
	}

	public void setSymbolValue(int symbolID, Value newValue)
	{
		Integer valueIndex = symbolValueIndexMap.get(symbolID);
		if (valueIndex == null)
		{
			//the variable doesn't exist, check the inherited memory environment.

			Memory memoryEnvironment = getInheritedMemory();


			if (memoryEnvironment == null)
			{
				//if this is a root block (no environment), the variable doesn't exist
				Interpretator.printError("Error: accessing non-existing variable, not setting the value (id = " + symbolID + ")");
			}
			else
			{
				//check the parent block
				memoryEnvironment.setSymbolValue(symbolID, newValue);
			}
		}
                else
                {
                    symbolValues[valueIndex] = newValue;
                }
	}
	
	public Value getSymbolValue(int symbolID)
	{
		Integer valueIndex = symbolValueIndexMap.get(symbolID);
		if (valueIndex == null)
		{
			//the variable doesn't exist, check the inherited memory environment.

			Memory memoryEnvironment = getInheritedMemory();


			if (memoryEnvironment == null)
			{
				//if this is a root block (no environment), the variable doesn't exist
				Interpretator.printError("Error: accessing non-existing variable, using zero instead (id = " + symbolID + ")");
				return new Value(0);
			}
			else
			{
				//check the parent block
				return memoryEnvironment.getSymbolValue(symbolID);
			}
		}
		return symbolValues[valueIndex];
	}

	public void setParameter(int parameterIndex, Value value) {
		symbolValues[parameterIndex+1] = value;
	}
	
	public Value getReturnValue()
	{
		return symbolValues[returnValueIndex];
	}
	
	private Memory getInheritedMemory()
	{
		Memory father;
		switch (MEMORY_INHERITANCE)
		{
			case STATIC_MEMORY_INHERITANCE:
				father = staticFather;
				break;
			case DYNAMIC_MEMORY_INHERITANCE:
				father = dynamicFather;
				break;
			default:
				father = staticFather;
				Interpretator.printError("Error: illegal value set for memory inheritance, using static inheritance");
		}
		return father;
	}
	
}
