/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

/**
 *
 * @author Robin
 */
public class Token {

	public static final int TYPE_ID = 1;
	public static final int TYPE_INT = 2;
	public static final int TYPE_FLOAT = 3;
	public static final int TYPE_TEXT = 4;
	public static final int TYPE_OPERATOR = 5;
	public static final int TYPE_ERROR = 6;
	public static final int TYPE_ROWNR = 7;
	public static final int TYPE_CALL = 10;
	// <editor-fold defaultstate="collapsed" desc="constants">
	public static final int ID_BEGIN = 1;
	public static final int ID_END = 2;
	public static final int ID_ARRAY = 3;
	public static final int ID_FUNCTION = 4;
	public static final int ID_INTEGER = 5;
	public static final int ID_REAL = 6;
	public static final int ID_TEXT = 7;
	public static final int ID_WHILE = 8;
	public static final int ID_DO = 9;
	public static final int ID_IF = 10;
	public static final int ID_THEN = 11;
	public static final int ID_ELSE = 12;
	public static final int ID_IS = 13;
	public static final int ID_EXTERNAL = 14;
	public static final int ID_OBJECT = 15;
	public static final int ID_POINTER = 16;
	public static final int ID_NEW = 17;
	public static final int ID_DYN = 18;
	public static final int ID_MAX = 18;// </editor-fold>
	
	private int type;
	private int code;
	private String text;
	public Token next;

	public Token(int type, int code, String text) {
		this.code = code;
		this.type = type;
		this.text = text;
	}

	public boolean isFuncPar() {
		return false; //overridden in sub class TokenFuncPar
	}
	
	public boolean isUserID() {
		return type == TYPE_ID && code > ID_MAX;
	}

	public boolean isEnd() {
		return code == ID_END && type == TYPE_ID;
	}

	public boolean isSemicolon() {
		return type == TYPE_OPERATOR && text.equals(";");
	}

	public boolean isCallblock() {
		return type == TYPE_CALL;
	}

	public boolean isRowNumber() {
		return type == TYPE_ROWNR;
	}

	public int getType() {
		return type;
	}

	public int getCode() {
		return code;
	}

	public String getText() {
		return text;
	}

	boolean isIf() {
		return type == TYPE_ID && code == ID_IF;
	}

	boolean isThen() {
		return type == TYPE_ID && code == ID_THEN;
	}
	
	boolean isElse() {
		return type == TYPE_ID && code == ID_ELSE;
	}
}
