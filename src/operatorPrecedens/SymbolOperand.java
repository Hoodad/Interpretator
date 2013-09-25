/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package operatorPrecedens;

import interpretator.Memory;
import interpretator.Value;

/**
 *
 * @author Robinerd
 */
public class SymbolOperand implements Operand
{
	public int symbolID;
	public Memory memory;

	public SymbolOperand(int symbolID, Memory memory)
	{
		this.symbolID = symbolID;
		this.memory = memory;
	}

	public Value getValue()
	{
//		switch(symbol.getKind())
//		{
//			case Symbol.KIND_SIMPLE:
				return memory.getSymbolValue(symbolID);
//			default:
//				System.err.println("Unsupported symbol kind: " + symbol.getKind());
//				return null;
//		}
	}
	
	
	
}
