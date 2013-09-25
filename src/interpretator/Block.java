/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

/**
 *
 * @author Robin
 */
public class Block {

	private int blockID;
	private int staticFather;
	private int size;
	private int nrOfDeclarations;
	private int nrOfTokens;
	private Symbol symbols[];
	private TokenStream tokens;

	public Block(int blockID, int staticFather, int size, int nrOfDeclarations, int nrOfTokens, Symbol[] symbols, Token[] tokens) {
		this.blockID = blockID;
		this.staticFather = staticFather;
		this.size = size;
		this.nrOfDeclarations = nrOfDeclarations;
		this.nrOfTokens = nrOfTokens;
		this.symbols = symbols;
		this.tokens = new TokenStream(tokens);
	}

	public int getBlockID() {
		return blockID;
	}

	public int getSize() {
		return size;
	}

	public Symbol[] getSymbols() {
		return symbols;
	}

	public Symbol getFunctionSymbol(int symbolID) {
		for (Symbol symbol : symbols) {
			if (symbol.getId() == symbolID) {
				if(symbol.getKind() != Symbol.KIND_FUNCVAL) {
					return symbol;
				}
			}
		}

		if (staticFather >= 0) {
			return BlockManager.getBlock(staticFather).getSymbol(symbolID);
		}
		return null;		
	}
	
	public Symbol getSymbol(int symbolID) {
		for (Symbol symbol : symbols) {
			if (symbol.getId() == symbolID) {
				return symbol;
			}
		}

		if (staticFather >= 0) {
			return BlockManager.getBlock(staticFather).getSymbol(symbolID);
		}
		return null;
	}

	public int getStaticFather() {
		return staticFather;
	}

	public TokenStream getTokens() {
		return tokens;
	}
}
