/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

/**
 *
 * @author Robinerd
 */
public class TokenStream
{
	private Token tokens[];
	private int tokenCursor;
	
	public TokenStream(Token[] tokens)
	{
		this.tokens = tokens;
		tokenCursor = 0;
	}
	
	
	public void moveCursor()
	{
		tokenCursor++;
	}

	public boolean hasNextToken()
	{
		return tokenCursor < tokens.length;
	}

	public void resetCursor()
	{
		tokenCursor = 0;
	}

	public Token peekToken()
	{
		if (!hasNextToken())
			return null;

		return tokens[tokenCursor];
	}

	public Token nextToken()
	{
		if (!hasNextToken())
			return null;

		return tokens[tokenCursor++];
	}
}
