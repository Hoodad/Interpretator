/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpretator;

/**
 *
 * @author Robin
 */
public class Symbol {

	public static final int KIND_SIMPLE = 0;
	public static final int KIND_ARRAY = 1;
	public static final int KIND_FUNC = 2;
	public static final int KIND_FUNCVAL = 3;
	public static final int KIND_OBJECT = 4;
	public static final int KIND_REFERENCE = 5;
	
	protected int id;
	protected int type;
	protected int kind;
	protected int info1, info2, info3;
	protected int relativeAddress;
	protected String extnamn;
	protected String limits;
	protected String name;

	public Symbol(int id, int type, int kind, int info1, int info2, int info3, int relativeAddress, String extnamn, String limits, String name) {
		this.id = id;
		this.type = type;
		this.kind = kind;
		this.info1 = info1; //Antal parametrar
		this.info2 = info2; //Block index
		this.info3 = info3;  
		this.relativeAddress = relativeAddress;
		this.extnamn = extnamn;
		this.limits = limits;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public int getInfo1() {
		return info1;
	}

	public int getInfo2() {
		return info2;
	}

	public int getInfo3() {
		return info3;
	}

	public int getKind() {
		return kind;
	}

	public int getRelativeAddress() {
		return relativeAddress;
	}

	public int getType() {
		return type;
	}

	public String getExtnamn() {
		return extnamn;
	}

	public String getLimits() {
		return limits;
	}

	public String getName() {
		return name;
	}
}
