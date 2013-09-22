/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package operatorPrecedens;

import interpretator.Value;

/**
 *
 * @author Robinerd
 */
public class ValueOperand implements Operand
{
	Value value;

	public ValueOperand(Value value)
	{
		this.value = value;
	}

	public Value getValue()
	{
		return value;
	}
}
