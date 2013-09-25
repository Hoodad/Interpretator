/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package operatorPrecedens.operators;

import interpretator.Value;

/**
 *
 * @author Hoodad
 */
public interface BaseOperator {
	public Value calculate(Value a, Value b);
}
