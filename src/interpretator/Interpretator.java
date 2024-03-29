/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

import java.util.LinkedList;
import operatorPrecedens.Operand;
import java.util.Stack;
import operatorPrecedens.SymbolOperand;
import operatorPrecedens.ValueOperand;
import operatorPrecedens.operators.Operator;
import static operatorPrecedens.operators.OperatorCodes.*;

/**
 *
 * @author Robinerd
 */
public class Interpretator {
	
	//used by if statements
	private static enum BranchingStatus {
		NONE, THEN, ELSE
	}
	
	//Used for formating the debug output
	private static int callDepth = 0;
	
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
		interpretBlock(block, null, null, null);
	}

	public static void interpretBlock(Block block, Memory staticEnvironment,
			Memory dynamicEnvironment, Memory p_memory) {
		TokenStream tokens = block.getTokens();
		tokens.resetCursor();

		Memory memory = p_memory;
		if (memory == null) {
			memory = new Memory(block.getSymbols(), staticEnvironment,
					dynamicEnvironment, block.getSize());
		}

		callDepth++;
		
		int depth = 1;
		while (depth > 0 && tokens.hasNextToken()) {
			Token nextToken = tokens.peekToken();

			if (nextToken.isEnd()) {
				depth--;
				tokens.moveCursor();
			} else if (nextToken.isUserID()) {
				print("Found a variable named: " + nextToken.getText());
				interpretExpression(block, memory, tokens);
			} else if (nextToken.isCallblock()) {

				print("Found a Callblock!");

				Block callblock = BlockManager.getBlock(nextToken.getCode());

				//a callblock is either an inner block or a function
				if (callblock.getStaticFather() == block.getBlockID()) //if it's an inner block, use the memory of this block as static environment
				{
					interpretBlock(callblock, memory, memory, null);
				} else //TODO: this assumption is wrong since functions are allowed to be declared inside of other blocks
				//otherwise there is no static environment (no classes are permitted)
				{
					interpretBlock(callblock, null, memory, null);
				}

				tokens.moveCursor();
			} else if (nextToken.isRowNumber()) {
				print("#Empty Line#");
				tokens.moveCursor();
			} else if (nextToken.isIf()) {
				print("If-statement ");
				tokens.moveCursor();
			} else {
				tokens.moveCursor();
				printError("Unkown token: "+nextToken.getText());
			}
		}
		callDepth--;
	}
	private static void interpretExpression(Block currentBlock, Memory memory,
			TokenStream tokens) {

		print("##Begin Expression##");
		Stack<Token> operators = new Stack<Token>();
		Stack<Operand> operands = new Stack<Operand>();
		BranchingStatus branchingStatus = BranchingStatus.NONE;
		boolean ifResult = true;
		
		boolean expressionComplete = false;
		Token hand = null;
		while (!expressionComplete) {
			hand = tokens.nextToken();
			hand.next = tokens.peekToken();
			switch (branchingStatus) {
				case THEN: {
					boolean avoidSkip = false;
					if (hand.isElse()) {
						branchingStatus = BranchingStatus.ELSE;
					}
					
					if (hand.isSemicolon()) {
						branchingStatus = BranchingStatus.NONE;
						avoidSkip = true;
					}
					
					if (!ifResult && !avoidSkip) {
						continue;
					}
					break;
				}
				case ELSE: {
					boolean avoidSkip = false;
					if (hand.isSemicolon()) {
						branchingStatus = BranchingStatus.NONE;
						avoidSkip = true;
					}
					if (ifResult && !avoidSkip) {
						continue;
					}
					break;
				}
				case NONE:
					break;
			}

			if (isOperand(currentBlock, hand)) {
				processOperand(hand, operands, memory);
			} 
			else //is operator
			{
				char action;
				do {
					print("Hand contains: " + hand.getText());
					action = processOperator(memory, currentBlock, operators, hand, operands);
					print("Action: " + action);
					if (action == 'A') {
						if(hand.isThen())
						{
							ifResult = operands.pop().getValue().getAsBoolean();
							branchingStatus = BranchingStatus.THEN;
							print("Condition evaluated to: " + ifResult);
							if(ifResult){
								print("## THEN");
							}
							else{
								print("ELSE ##");
							}
						}
						else if(hand.isElse())
						{
							//expression NOT complete
						}
						else
						{
							expressionComplete = true;
						}
					}
				} while (action == 'U');
			}
		}
		
		print(
				"##Expression complete##");
	}

	private static boolean isFunctionCall(Block currentBlock, Token token) {
		if (token.getType() == Token.TYPE_ID) {
			try {
				if (currentBlock.getSymbol(token.getCode()).getKind() == Symbol.KIND_FUNC) {
					return true;
				}
				if (currentBlock.getSymbol(token.getCode()).getKind() == Symbol.KIND_FUNCVAL) {
					if (token.next.getCode() == OP_LEFT_PAR) {
						return true;
					}
				}
			} catch(NullPointerException ex) {
				printError("Error evaluating isFunctionCall for token: "+token.getText());
				throw ex;
			}
		}

		return false;
	}

	private static boolean isOperand(Block currentBlock, Token token) {

		if (token.getType() == Token.TYPE_OPERATOR) {
			return false;
		}
		if (token.isThen()) {
			return false;
		}
		if (token.isElse()) {
			return false;
		}
		if (isFunctionCall(currentBlock, token)) {
			print("Function call detected");
			return false;
		}

		return true;
	}

	private static char processOperator(Memory currentMemory, Block currentBlock, Stack<Token> operators, Token hand, Stack<Operand> operands) {
		char action;

		if (operators.isEmpty()) {
			int handIndex = getActionMatrixIndex(currentBlock, hand);

			action = getActionFromAM(I_ENDOFSTACK, handIndex);
		} else {
			action = getAction(currentBlock, operators.peek(), hand);
		}

		switch (action) {
			case 'S': // Stack push
				print("Operator " + hand.getText() + " pushed to stack");
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
				print("Function call pushed to operator stack");
				break;
			case 'C': {
				//Nya op. är (, stacka funk.parentes, läs nästa
				//C kommer endast när ett F är föregående
				//Vet ej hur man ska stacka en funk.parantes.....
				Token funcPar = new TokenFuncPar();
				operators.push(funcPar);
				print("Function Parenthesis added to stack");
				break;
			}
			case 'T': { 
				//Överför en parameter, läs nästa
				try {
					TokenFuncPar funcPar = (TokenFuncPar) operators.peek();
					funcPar.addParameter(operands.pop().getValue());
				} catch(ClassCastException ex) {
					printError("Error when transfering next parameter: top of stack is not a function parenthesis");
				}
				print("Transfer next parameter, not yet implemented");
				break;
			}
			case 'L': //Överför sista parameter, läs nästa
				//F()
				//Dont do anything except read next
				TokenFuncPar funcPar = null;
				try {
					funcPar = (TokenFuncPar) operators.pop();
					funcPar.addParameter(operands.pop().getValue());
				} catch(ClassCastException ex) {
					printError("Error when transfering function parameters: top of stack is not a function parenthesis");
				}
				Token functionCall = operators.pop();
				Symbol functionSymbol = currentBlock.getFunctionSymbol(functionCall.getCode());
				Block functionBlock = BlockManager.getBlock(functionSymbol.getInfo2());
				Memory functionMemory = new Memory(functionBlock.getSymbols(),
						null, currentMemory, functionBlock.getSize());

				String paramsDebug = "";
				if(funcPar != null) {
					LinkedList<Value> parameters = funcPar.getParameters();
					int parameterIndex = 0;
					for(Value param : parameters) {
						functionMemory.setParameter(parameterIndex, param);
						paramsDebug += param+(parameterIndex+1 == parameters.size() ? "" : ",");
						parameterIndex++;
					}
				}

				print("Calling function "+functionSymbol.getName()+"("+paramsDebug+")");
				
				int calledFromToken = currentBlock.getTokens().getCursorPos();
				interpretBlock(functionBlock, null, null, functionMemory);
				currentBlock.getTokens().setCursorPos(calledFromToken);
				
				operands.push(new ValueOperand(functionMemory.getReturnValue()));
				print("Function returned "+functionMemory.getReturnValue().getAsInt());
				break;
			case 'E':
				printError("Syntax error in expression");
				break;
		}
		return action;
	}

	private static char getAction(Block currentBlock, Token stack, Token hand) {
		int stackIndex, handIndex;

		stackIndex = getActionMatrixIndex(currentBlock, stack);
		handIndex = getActionMatrixIndex(currentBlock, hand);

		return getActionFromAM(stackIndex, handIndex);

	}

	private static int getActionMatrixIndex(Block currentBlock, Token token) {
		if (token.isThen()) {
			return I_ENDOFSTACK;
		}
		if (token.isElse()) {
			return I_ENDOFSTACK;
		}
		if (isFunctionCall(currentBlock, token)) {
			return I_USERFUNCTION;
		}
		switch (token.getCode()) {
			case OP_SEMI:
				return I_ENDOFSTACK;
			case OP_LEFT_PAR:
				if (token.isFuncPar()) {
					return I_FUNCTIONPAR;
				}
				return I_LEFTPAR;
			case OP_RIGHT_PAR:
				return I_RIGHTPAR;

			case OP_COLON:
				//OBS! We dont know what this means
				printError("Error: Colon operator not defined.");
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
		String appendDebugText = "";
		switch (hand.getType()) {
			case Token.TYPE_ID:
				operands.push(new SymbolOperand(hand.getCode(), memory));
				appendDebugText = "(="+operands.peek().getValue()+")";
				break;
			case Token.TYPE_INT:
				operands.push(new ValueOperand(new Value(hand.getCode())));
				break;
			default:
				printError("Invalid operand type: "+hand.getType());
		}
		print("Add Operand to stack: " + hand.getText()+ " " + appendDebugText);
	}

	private static char getActionFromAM(int stackIndex, int handIndex) {
		try {
			return ActionMatrix[stackIndex][handIndex];
		} catch (ArrayIndexOutOfBoundsException err) {
			return '?';
		}
	}
	public static void print(String text) {
		for(int i=1; i<callDepth; i++)
		{
			System.out.print("    ");
		}
		System.out.println(text);
	}
	public static void printError(String text) {
		System.out.flush();
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException ex)
		{
		}
		System.err.println(text);
		
	}
}
