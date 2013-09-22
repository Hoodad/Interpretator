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
import static operatorPrecedens.operators.OperatorCodes.*;

/**
 *
 * @author Robinerd
 */
public class Interpretator {

    private static final int I_BECOME = 0;
    private static final int I_ADDITION = 1;
    private static final int I_MULTIPLICATION = 2;
    private static final int I_COMPARE = 3;
    private static final int I_USERFUNCTION = 4;
    private static final int I_ARRAY = 5;
    private static final int I_PARAMETERSEPARATOR = 6;
    private static final int I_LEFTPAR = 7;
    private static final int I_RIGHTPAR = 8;
    private static final int I_FUNCTIONPAR = 9;
    private static final int I_ENDOFSTACK = 10;
    private static char[][] ActionMatrix = {
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
            } else if (nextToken.isBegin()) {
                depth++;
                tokens.moveCursor();
            } else if (nextToken.isUserID()) {
                //Value val = memory.getSymbolValue(nextToken.getCode());
                //TODO: implement operator precedence
                //val.set(OperatorAdd.instance().calculate(val, new Value(0.5f)));
                //System.out.println(nextToken.getText() + " = " + val);
                interpretExpression(block, memory, tokens);
            } else if (nextToken.isCallblock()) {
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
            } else {
                tokens.moveCursor();
            }
        }
    }

    private static void interpretExpression(Block currentBlock, Memory memory,
            TokenStream tokens) {
        //TODO: implement with operator precedence
        Stack<Token> operators = new Stack<Token>();
        Stack<Operand> operands = new Stack<Operand>();
        boolean expressionComplete = false;
        Token hand = null;

        while (!expressionComplete) {
            hand = tokens.nextToken();

            switch (hand.getType()) {
                case Token.TYPE_ID:
                    operands.push(new SymbolOperand(hand.getCode(), memory));
                    break;
                case Token.TYPE_INT:
                    operands.push(new ValueOperand(new Value(hand.getCode())));
                    break;
                case Token.TYPE_OPERATOR:
                    char action;
                    do {
                        if (operators.isEmpty()) {
                            int handIndex = getActionMatrixIndex(hand);

                            try {
                                action = ActionMatrix[I_ENDOFSTACK][handIndex];
                            } catch (ArrayIndexOutOfBoundsException err) {
                                action = '?';
                            }
                        } else {
                            action = getAction(operators.peek(), hand);
                        }

                        switch (action) {
                            case 'S':
                                operators.push(hand);
                                break;

                            case 'U':
                                Token operatorToken = operators.pop();
                                Operator.executeOperator(operatorToken, operands);
                                break;

                            case 'A':
                                expressionComplete = true;
                                break;

                            case 'F':
                                //Nya op. stackas, dess prolog utförs, läs nästa

                                //Prolog = föregående, därav samma funktions anrop som vid U
                                operatorToken = operators.pop();
                                Operator.executeOperator(operatorToken, operands);

                                operators.push(hand);
                                break;

                            case 'C':
                                //Nya op. är (, stacka funk.parentes, läs nästa

                                //C kommer endast när ett F är föregående
                                //Vet ej hur man ska stacka en funk.parantes.....
                                operators.push(hand);
                                break;

                            case 'T':
                                //Överför en parameter, läs nästa

                                //Fixas senare
                                System.err.println("Not yet implemented");
                                break;

                            case 'L':
                                //Överför sista parameter, läs nästa

                                //F()
                                //Dont do anything except read next
                                break;

                            case 'E':
                                System.err.println("Syntax error in expression");
                                break;
                        }
                    } while (action == 'U');
                    break;
            }
        }
    }

    private static char getAction(Token stack, Token hand) {
        int stackIndex, handIndex;

        stackIndex = getActionMatrixIndex(stack);
        handIndex = getActionMatrixIndex(hand);

        try {
            return ActionMatrix[stackIndex][handIndex];
        } catch (ArrayIndexOutOfBoundsException err) {
            return '?';
        }

    }

    private static int getActionMatrixIndex(Token token) {
        switch (token.getCode()) {
            case OP_SEMI:
                return I_ENDOFSTACK;
            case OP_LEFT_PAR:
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
}
