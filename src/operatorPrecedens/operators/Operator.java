/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package operatorPrecedens.operators;

import interpretator.Token;
import interpretator.Value;
import java.util.Stack;
import operatorPrecedens.Operand;
import operatorPrecedens.SymbolOperand;
import operatorPrecedens.ValueOperand;
import static operatorPrecedens.operators.OperatorCodes.*;
/**
 *
 * @author Robinerd
 */
public class Operator
{
	public static void executeOperator(Token operatorToken, Stack<Operand> operands) {
		switch(operatorToken.getCode()) {
			case OP_BECOME:
				Operand valueOp = operands.pop();
				SymbolOperand variableOp = (SymbolOperand) operands.pop();
				
				Value value = valueOp.getValue();
				variableOp.memory.setSymbolValue(variableOp.symbolID, value);
				
				System.out.println("Variable " + variableOp.symbolID + " = " + value);
				break;
			case OP_ADD:
				executeArithmeticOperator(OperatorAdd.instance(), operands);
				break;
			case OP_SUBTRACT:
				executeArithmeticOperator(OperatorSubtract.instance(), operands);
				break;
			case OP_MULTIPLY:
				executeArithmeticOperator(OperatorMultiply.instance(), operands);
				break;
			
		}
	}
	
	private static void executeArithmeticOperator(ArithmeticOperator operator, Stack<Operand> operands) {
		Operand valueRight = operands.pop();
		Operand valueLeft = operands.pop();

		Value result = operator.calculate(valueLeft.getValue(), valueRight.getValue());
		operands.push(new ValueOperand(result));
	}
}
