/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

import java.util.LinkedList;
import operatorPrecedens.operators.OperatorCodes;

/**
 *
 * @author Robinerd
 */
public class TokenFuncPar extends Token
{
	private LinkedList<Value> parameters = new LinkedList<Value>();
	
	public TokenFuncPar() {
		super(Token.TYPE_OPERATOR, OperatorCodes.OP_LEFT_PAR, "f(");
	}
	
	@Override
	public boolean isFuncPar() {
		return true;
	}

	LinkedList<Value> getParameters()
	{
		return parameters;
	}

	void addParameter(Value value)
	{
		parameters.addLast(value);
	}
}
