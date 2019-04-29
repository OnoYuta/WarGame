package warGame;

import java.io.Serializable;

public enum Num implements Serializable{
	TWO("2",1),
	THREE("3",2),
	FOUR("4",3),
	FIVE("5",4),
	SIX("6",5),
	SEVEN("7",6),
	EIGHT("8",7),
	NINE("9",8),
	TEN("10",9),
	J("J",10),
	Q("Q",11),
	K("K",12),
	A("A",13);
	private String disp;
	private int strength;

	private Num(String disp,int strength) {
		this.disp = disp;
		this.strength = strength;
	}

	public String getDisp() {
		return disp;
	}

	public int getStrength() {
		return strength;
	}
}
