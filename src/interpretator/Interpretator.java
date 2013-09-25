/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

import operatorPrecedens.Operand;
import java.util.Stack;
import operatorPrecedens.SymbolOperand;
import operatorPrecedens.ValueOperand;
import operatorPrecedens.operators.Operator;
import operatorPrecedens.operators.OperatorCodes;
import static operatorPrecedens.operators.OperatorCodes.*;

/**
 *
 * @author Robinerd
 */
public class Interpretator {

	public static final int I_BECOME = 0;
	public static final int I_ADDITION = 1;
	public static final int I_MULTIPLICATION = 2;
	public static final int I_COMPARE = 3;
	public static final int I_USERFUNCTION = 4;
	public static final int I_ARRAY = 5;
	public static final int I_PARAMETERSEPARATOR = 6;
	public static final int I_LEFTPAR = 7;
	public static final int I_RIGHTPAR = 8;
	public static final int I_FUNCTIONPAR = 9;
	public static final int I_ENDOFSTACK = 10;
	//S = store
	//? = unkown
	//E 
	//U 
	//F 
	//T 
	//L = load
	//A = 
	public final static char[][] ActionMatrix = {
		{'E', 'S', 'S', 'S', 'F', 'S', 'U', 'S', 'E', 'E', 'U'},
		{'E', 'U', 'S', 'U', 'F', 'S', 'U', 'S', 'U', 'E', 'U'},
		{'E', 'U', 'U', 'U', 'F', 'S', 'U', 'S', 'U', 'E', 'U'},
		{'E', 'S', 'S', 'E', 'F', 'S', 'U', 'S', 'U', 'E', 'U'},
		{'U', 'U', 'U', 'U', '?', '?', 'U', 'C', 'U', 'E', 'U'},
		{'U', 'U', 'U', 'U', '?', '?', 'U', 'S', 'U', 'E', 'U'},
		{'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E'},
		{'S', 'S', 'S', 'S', 'F', 'S', 'E', 'S', 'P', 'E', 'E'},
		{'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E'},
		{'S', 'S', 'S', 'S', 'F', 'S', 'T', 'S', 'L', 'E', 'E'},
		{'S', 'S', 'S', 'S', 'F', 'S', '?', 'S', 'E', 'E', 'A'},};

	public static void interpretBlock(Block block) {
		interpretBlock(block, null, null);
	}

	public static void interpretBlock(Block block, Memory staticEnvironment,
			Memory dynamicEnvironment) {
		TokenStream tokens = block.getTokens();

		tokens.resetCursor();
		Memory memory = new Memory(block.getSymbols(), staticEnvironment,
				dynamicEnvironment, block.getSize());

		int depth = 1;
		while (depth > 0 && tokens.hasNextToken()) {
			Token nextToken = tokens.peekToken();

			if (nextToken.isEnd()) {
				depth--;
				tokens.moveCursor();
			} else if (nextToken.isUserID()) {
				System.out.println("Found a variable named: " + nextToken.getText());
				interpretExpression(block, memory, tokens);
			} else if (nextToken.isCallblock()) {

				System.out.println("Found a Callblock!");

				Block callblock = BlockManager.getBlock(nextToken.getCode());

				//a callblock is either an inner block or a function
				if (callblock.getStaticFather() == block.getBlockID()) //if it's an inner block, use the memory of this block as static environment
				{
					interpretBlock(callblock, memory, memory);
				} else //TODO: this assumption is wrong since functions are allowed to be declared inside of other blocks
				//otherwise there is no static environment (no classes are permitted)
				{
					interpretBlock(callblock, null, memory);
				}

				tokens.moveCursor();
			} else if (nextToken.isRowNumber()) {
				System.out.println("#Line#");
				tokens.moveCursor();
			}
			else{
				System.err.println("Unkown token, don't know what to do");
			}
		}
	}

	private static void interpretExpression(Block currentBlock, Memory memory,
			TokenStream tokens) {
		Stack<Token> operators = new Stack<Token>();
		Stack<Operand> operands = new Stack<Operand>();
		boolean expressionComplete = false;
		Token hand;

		while (!expressionComplete) {
			hand = tokens.nextToken();

			if (!isOperator(currentBlock, hand)) {
				processOperand(hand, operands, memory);
			} else //is operator
			{
				char action;
				do {
					System.out.println("Hand contains: "+hand.getText());
					action = processOperator(currentBlock, operators, hand, operands);
					if (action == 'A') {
						expressionComplete = true;
					}
					System.out.println("Action: " + action);
				} while (action == 'U');
			}
		}
		System.out.println("##Expression complete##");
	}

	private static boolean isFunctionCall(Block currentBlock, Token token) {
		if (token.getType() == Token.TYPE_ID) {
			try{
			if (currentBlock.getSymbol(token.getCode()).getKind() == Symbol.KIND_FUNC) {
				return true;
			}
			}catch (Exception e){
				return false;
			}
		}
		
		return false;
	}
	
	private static boolean isOperator(Block currentBlock, Token token) {

		if (token.getType() == Token.TYPE_OPERATOR) {
			return true;
		}
		if(isFunctionCall(currentBlock, token)) {
			System.out.println("Function call detected");
			return true;
		}

		return false;
	}
	private static char processOperator(Block currentBlock, Stack<Token> operators, Token hand, Stack<Operand> operands) {
		char action;
		if (operators.isEmpty()) {
			int handIndex = getActionMatrixIndex(currentBlock, hand);

			action = getActionFromAM(I_ENDOFSTACK,handIndex);
		} else {
			action = getAction(currentBlock, operators.peek(), hand);
		}

		switch (action) {
			case 'S': // Stack push
				System.out.println("Operator "+hand.getText()+" pushed to stack");
				operators.push(hand);
				break;
			case 'U': // Utför
				Token operatorToken = operators.pop();
				Operator.executeOperator(operatorToken, operands);
				break;
			case 'A': //Avsluta
				break;
			case 'P': //Pop, läs nästa
				operators.pop();
				break;
			case 'F'://Nya op. stackas, dess prolog utförs, läs nästa
				//Prolog = föregående, därav samma funktions anrop som vid U
				//operatorToken = operators.pop();
				//Operator.executeOperator(operatorToken, operands);

				operators.push(hand);
				System.out.println("Function call pushed to operator stack");
				break;
			case 'C'://Nya op. är (, stacka funk.parentes, läs nästa
				//C kommer endast när ett F är föregående
				//Vet ej hur man ska stacka en funk.parantes.....
				Token funcPar = new Token(Token.TYPE_OPERATOR, OperatorCodes.OP_LEFT_PAR, "f(");
				funcPar.isFunctionPar = true;
				operators.push(funcPar);
				System.out.println("Function Parenthesis added to stack");
				break;
			case 'T'://Överför en parameter, läs nästa
				operands.pop();
				System.out.println("Transfer next parameter, not yet implemented");
				break;
			case 'L': //Överför sista parameter, läs nästa
				//F()
				//Dont do anything except read next
				//TODO: Ska föra övers till funktion anropet senare!!
				operands.pop();
				
				
				operators.pop();
				operators.pop();
				operands.push(new ValueOperand(new Value(100)));
				System.out.println("Transfer last parameter");
				break;
			case 'E':
				System.err.println("Syntax error in expression");
				break;
		}
		return action;
	}
	
	private static char getAction(Block currentBlock, Token stack, Token hand) {
		int stackIndex, handIndex;

		stackIndex = getActionMatrixIndex(currentBlock, stack);
		handIndex = getActionMatrixIndex(currentBlock, hand);

		return getActionFromAM(stackIndex,handIndex);

	}
	
	private static int getActionMatrixIndex(Block currentBlock, Token token) {
		if(isFunctionCall(currentBlock, token)) {
			return I_USERFUNCTION;
		}
		
		switch (token.getCode()) {
			case OP_SEMI:
				return I_ENDOFSTACK;
			case OP_LEFT_PAR:
				if(token.isFunctionPar)
					return I_FUNCTIONPAR;
				return I_LEFTPAR;
			case OP_RIGHT_PAR:
				return I_RIGHTPAR;

			case OP_COLON:
				//OBS! We dont know what this means
				System.err.println("Error: Colon operator not defined.");
				return -1;
			case OP_COMMA:
				return I_PARAMETERSEPARATOR;
			case OP_BECOME:
				return I_BECOME;
			case OP_EQUAL:
			case OP_NOT_EQUAL:
			case OP_LESS_THAN:
			case OP_LESS_OR_EQUAL:
			case OP_GREATER_THAN:
			case OP_GREATER_OR_EQUAL:
				return I_COMPARE;
			case OP_ADD:
			case OP_SUBTRACT:
				return I_ADDITION;
			case OP_MULTIPLY:
			case OP_DIVIDE:
				return I_MULTIPLICATION;
			default:
				//This return statement should never be used 
				return -1;
		}
	}

	

	private static void processOperand(Token hand, Stack<Operand> operands, Memory memory) {
		switch (hand.getType()) {
			case Token.TYPE_ID:
				operands.push(new SymbolOperand(hand.getCode(), memory));
				break;
			case Token.TYPE_INT:
				operands.push(new ValueOperand(new Value(hand.getCode())));
				break;
		}
		System.out.println("Add Operand to stack: " + hand.getText());
	}
	private static char getActionFromAM(int stackIndex, int handIndex){
		try{
			return ActionMatrix[stackIndex][handIndex];
		}catch(ArrayIndexOutOfBoundsException err){
			return '?';
		}
	}
}
